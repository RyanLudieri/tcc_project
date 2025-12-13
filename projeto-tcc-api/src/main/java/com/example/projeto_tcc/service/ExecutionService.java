package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.enums.VariableType;
import com.example.projeto_tcc.repository.GlobalSimulationResultRepository;
import com.example.projeto_tcc.repository.RoleConfigRepository;
import com.example.projeto_tcc.util.ParsedResourceUsage;
import com.example.projeto_tcc.util.SimulationRunContext;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.codehaus.janino.SimpleCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import simula.manager.QueueEntry;
import simula.manager.SimulationManager;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExecutionService {

    @Lazy
    @Autowired
    private SimulationGenerationService simulationGenerationService;

    @Autowired
    private GlobalSimulationResultRepository globalRepository;

    @Autowired
    private SimulationCacheManager cacheManager;

    @Autowired
    private RoleConfigRepository roleConfigRepository;

    private String buildHeader(SimulationRunContext runContext) {
        List<Double> daysPerReplication = runContext.getDaysPerReplication();
        double sumDays = 0;
        for (double d : daysPerReplication) {
            sumDays += d;
        }
        double avgDays = daysPerReplication.size() > 0 ? sumDays / daysPerReplication.size() : 0;

        StandardDeviation sd = new StandardDeviation();
        double[] arr = daysPerReplication.stream().mapToDouble(d -> d).toArray();
        double stdDev = (arr.length > 1) ? sd.evaluate(arr) : 0.0;

        return "\n............   NUMBER OF SIMULATION RUNS, MEAN(SD) OF PROJECT DURATION...   ............\n" +
                "\nNumber of simulation runs......................:  " + daysPerReplication.size() + "\n" +
                "Number of days.....................................:  " + String.format("%.2f", avgDays) +
                "(" + String.format("%.4f", stdDev) + ")\n";
    }

    private String printResultadosGlobalTextArea(int[][] matriz, Map<String, VariableType> mapConfig, List<String> queueNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n............................   PRINTING THE MEAN AND STANDARD DEVIATION OF ENTITIES IN EACH QUEUE CONFIGURED AS 'DEPENDENT' FOR EACH REPLICATION   ............................ \n");

        StandardDeviation sd = new StandardDeviation();

        for (int j = 0; j < matriz[0].length; j++) {
            String fila = queueNames.get(j);
            if (mapConfig.containsKey(fila)) {
                sb.append("\nQueue: ").append(fila);
                double soma = 0.0;
                double[] vals = new double[matriz.length];

                for (int i = 0; i < matriz.length; i++) {
                    sb.append("\n\tquantity in replication ").append(i + 1).append("..: ").append(matriz[i][j]);
                    vals[i] = matriz[i][j];
                    soma += matriz[i][j];
                }
                double mean = soma / matriz.length;
                double stdDev = (vals.length > 1) ? sd.evaluate(vals) : 0.0;

                sb.append("\n\tmean of entities in queue ").append(fila).append("..:").append(String.format("%.2f", mean));
                sb.append("\tStandard deviation..: ").append(String.format("%.2f", stdDev)).append("\n");
            }
        }
        return sb.toString();
    }

    public String getFilteredResults(List<WorkProductConfig> configList) {
        Long processId = getActiveProcessId();
        SimulationRunContext runContext = (processId != null) ? cacheManager.getRunContext(processId) : null;

        if (runContext == null || runContext.getDaysPerReplication().isEmpty()) {
            return "Nenhuma simulação executada ou resultados não encontrados no cache.";
        }
        if (configList == null || configList.isEmpty()) {
            return "Configuração de variáveis dependentes vazia.";
        }

        String header = buildHeader(runContext);

        Map<String, VariableType> mapQueueVariableType = new HashMap<>();
        for (WorkProductConfig row : configList) {
            if (row.getVariableType() == VariableType.DEPENDENT) {
                mapQueueVariableType.put(row.getQueue_name(), row.getVariableType());
            }
        }

        if (mapQueueVariableType.isEmpty()) {
            return header + "\n\n(Nenhuma variável dependente selecionada para o relatório de resultados globais)";
        }

        Set<String> keys = runContext.getResultadoGlobal().keySet();
        if (keys.isEmpty()) {
            return header;
        }

        List<String> sortedRunKeys = new ArrayList<>(keys);
        sortedRunKeys.sort(Comparator.comparing(s -> Integer.parseInt(s.replaceAll("\\D+", ""))));

        HashMap hm = (HashMap) runContext.getResultadoGlobal().get(sortedRunKeys.iterator().next());
        List<String> listaNomesFilas = new ArrayList<>(hm.keySet());

        int numReplications = sortedRunKeys.size();
        int[][] matrizResultados = new int[numReplications][listaNomesFilas.size()];

        int i = 0;
        for (String keyExp : sortedRunKeys) {
            HashMap secondHash = (HashMap) runContext.getResultadoGlobal().get(keyExp);
            for (int j = 0; j < listaNomesFilas.size(); j++) {
                String qName = listaNomesFilas.get(j);
                Object obj = secondHash.get(qName);
                matrizResultados[i][j] = (obj instanceof QueueEntry) ? ((QueueEntry) obj).deadState.getCount() : 0;
            }
            i++;
        }

        return header + printResultadosGlobalTextArea(matrizResultados, mapQueueVariableType, listaNomesFilas);
    }

    public String generateCodeForPreview(Long processId) {
        try {
            String tempAcdId = "Preview_" + processId + "_" + System.currentTimeMillis();
            Path generatedFilePath = simulationGenerationService.generateSimulation(processId, tempAcdId);

            if (Files.exists(generatedFilePath)) {
                return new String(Files.readAllBytes(generatedFilePath));
            } else {
                return "Erro: O arquivo de código não foi gerado corretamente.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao gerar preview do código: " + e.getMessage();
        }
    }

    public void compile(String javaCode, String fullClassName, Long processId) throws Exception {
        cacheManager.clearCacheForProcess(processId);
        cacheManager.putGeneratedJavaCode(processId, javaCode);
        cacheManager.setActiveProcess(processId);

        Path outputDir = Paths.get("output");
        if (!Files.exists(outputDir)) Files.createDirectories(outputDir);

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.cook(javaCode);
        ClassLoader classLoader = compiler.getClassLoader();
        Class<?> compiledClass = classLoader.loadClass(fullClassName);
        cacheManager.putCompiledClass(processId, compiledClass);
    }

    public Long getActiveProcessId() {
        return cacheManager.getActiveProcessId();
    }

    public String getGeneratedJavaCode() {
        Long currentProcessId = cacheManager.getActiveProcessId();
        if (currentProcessId == null) return null;
        return cacheManager.getGeneratedJavaCode(currentProcessId);
    }

    public GlobalSimulationResult executeSimulation(
            Long processIdToExecute,
            float duration,
            Integer replications,
            List<WorkProductConfig> configList) throws Exception {

        cacheManager.setActiveProcess(processIdToExecute);
        SimulationRunContext runContext = new SimulationRunContext();

        Class<?> compiledSimClass = cacheManager.getCompiledClass(processIdToExecute);
        if (compiledSimClass == null)
            throw new IllegalStateException("Simulação não compilada para o processo ID: " + processIdToExecute);

        if (replications == null || replications < 1) replications = 1;

        ClassLoader newClassLoader = compiledSimClass.getClassLoader();
        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();

        Method getObsReport = null;
        try {
            getObsReport = compiledSimClass.getMethod("getObserverReport");
        } catch (NoSuchMethodException ignored) {}

        // Tenta obter o método estruturado para eventos de uso de recursos
        Method getResourceUsageMapMethod = null;
        try {
            // Tentativa: getResourceUsageMap() no SimulationManager
            getResourceUsageMapMethod = compiledSimClass.getMethod("getResourceUsageMap");
        } catch (NoSuchMethodException ignored) {}


        try {
            currentThread.setContextClassLoader(newClassLoader);

            for (int i = 1; i <= replications; i++) {
                hardResetLibrary(newClassLoader);

                Object simInstance = compiledSimClass.getDeclaredConstructor().newInstance();
                Method setDur = compiledSimClass.getMethod("setSimulationDuration", float.class);
                Method exec = compiledSimClass.getMethod("execute", float.class);
                Method getMan = compiledSimClass.getMethod("getSimulationManager");

                setDur.invoke(simInstance, duration);
                exec.invoke(simInstance, duration);

                SimulationManager man = (SimulationManager) getMan.invoke(simInstance);
                double clock = man.getScheduler().GetClock();

                runContext.getDaysPerReplication().add(clock / 480.0);

                if (getObsReport != null) {
                    String observerReport = (String) getObsReport.invoke(simInstance);
                    runContext.getHistoryObserverReports().put(i, observerReport);
                }

                runContext.getHistoryActivityResults().put(i, man.getScheduler().getMapWithActivityResults());
                runContext.getHistoryPhaseResults().put(i, man.getScheduler().getMapWithPhaseResults());
                runContext.getHistoryMilestoneResults().put(i, man.getScheduler().getMapWithMilestoneResults());
                runContext.getHistoryIterationResults().put(i, man.getScheduler().getMapWithIterationResults());
                runContext.getResultadoGlobal().put("run #" + i, man.getQueues());

                man.getScheduler().Stop();
                man.getScheduler().Clear();
            }

        } finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }

        cacheManager.putRunContext(processIdToExecute, runContext);
        GlobalSimulationResult savedResult = persistSimulationData(processIdToExecute, configList, runContext);
        cacheManager.removeCompiledClass(processIdToExecute);
        System.gc();

        return savedResult;
    }

    private GlobalSimulationResult persistSimulationData(
            Long processIdToPersist,
            List<WorkProductConfig> configList, SimulationRunContext runContext) {

        if (runContext.getDaysPerReplication().isEmpty()) return null;

        try {
            Set<String> dependentQueues = new HashSet<>();
            if (configList != null && !configList.isEmpty()) {
                for (WorkProductConfig cfg : configList) {
                    if (cfg.getVariableType() == VariableType.DEPENDENT) {
                        dependentQueues.add(cfg.getQueue_name());
                    }
                }
            }

            Map<String, String> queueToTaskName = new HashMap<>();
            if (configList != null) {
                for (WorkProductConfig cfg : configList) {
                    queueToTaskName.put(cfg.getQueue_name(), cfg.getTask_name());
                }
            }

            List<RoleConfig> roles = roleConfigRepository.findByDeliveryProcessId(processIdToPersist);
            int totalRoles = roles.size();

            GlobalSimulationResult global = new GlobalSimulationResult();
            global.setProcessId(processIdToPersist);
            global.setExecutionDate(LocalDateTime.now());
            global.setTotalReplications(runContext.getDaysPerReplication().size());
            global.setTotalRolesUsed(totalRoles);

            double sumDays = runContext.getDaysPerReplication().stream().mapToDouble(d -> d).sum();
            global.setAverageDuration(sumDays / runContext.getDaysPerReplication().size());

            StandardDeviation sd = new StandardDeviation();
            double[] arrDays = runContext.getDaysPerReplication().stream().mapToDouble(d -> d).toArray();
            global.setDurationStdDev((arrDays.length > 1) ? sd.evaluate(arrDays) : 0.0);

            Map<String, List<Double>> tempQueueValues = new HashMap<>();
            Map<String, List<Double>> tempResourceUtilization = new HashMap<>(); // NOVO: Para acumular a utilização

            for (int i = 1; i <= runContext.getDaysPerReplication().size(); i++) {
                ReplicationResult rep = new ReplicationResult();
                rep.setReplicationNumber(i);
                rep.setDuration(runContext.getDaysPerReplication().get(i - 1));

                // População de Filas (Lógica Existente)
                HashMap queuesMap = runContext.getResultadoGlobal().get("run #" + i);
                if (queuesMap != null) {
                    for (Object keyObj : queuesMap.keySet()) {
                        String qName = keyObj.toString();
                        boolean shouldSave = dependentQueues.isEmpty() || dependentQueues.contains(qName);

                        if (shouldSave) {
                            Object val = queuesMap.get(keyObj);
                            int count = (val instanceof QueueEntry) ? ((QueueEntry) val).deadState.getCount() : 0;
                            rep.getQueueFinalCounts().put(qName, count);
                            tempQueueValues.computeIfAbsent(qName, k -> new ArrayList<>()).add((double) count);
                        }
                    }
                }
                rep.setGlobalResult(global);
                global.getReplicationResults().add(rep);
            }

            // CÁLCULO DAS ESTATÍSTICAS GLOBAIS DE FILAS (QUEUE STATS) - MANTIDO
            for (Map.Entry<String, List<Double>> entry : tempQueueValues.entrySet()) {
                String qName = entry.getKey();
                List<Double> values = entry.getValue();
                double[] valuesArr = values.stream().mapToDouble(d -> d).toArray();
                double mean = values.stream().mapToDouble(d -> d).average().orElse(0.0);
                double stdDev = (valuesArr.length > 1) ? sd.evaluate(valuesArr) : 0.0;

                GlobalQueueStat stat = new GlobalQueueStat();
                stat.setQueueName(qName);
                stat.setTaskName(queueToTaskName.get(qName));
                stat.setAverageCount(mean);
                stat.setStdDevCount(stdDev);
                stat.setGlobalResult(global);
                global.getQueueStats().add(stat);
            }

            return globalRepository.save(global);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar dados: " + e.getMessage(), e);
        }
    }

    // *****************************************************************
    // FUNÇÃO DE PARSING DE LOG FINAL (MANTIDA COMO FALLBACK)
    // *****************************************************************
    private List<GlobalResourceStat> parseResourceStatsFromLog(String logText) {
        List<GlobalResourceStat> stats = new ArrayList<>();
        if (logText == null) return stats;

        String regex = "Report from observer (.*?) queue Observer \\d+\\s+Statistics summary:\\s+Number of resources permanencing in queue:\\s+Average: ([\\d\\.\\-]+E?\\d*)\\s+StdDev: ([\\d\\.\\-]+E?\\d*)\\s+";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(logText);

        while (matcher.find()) {
            try {
                String resourceName = matcher.group(1).trim();

                if (resourceName.endsWith(" queue")) {
                    resourceName = resourceName.substring(0, resourceName.lastIndexOf(" queue")).trim();
                }

                double average = Double.parseDouble(matcher.group(2).trim());
                double stdDev = Double.parseDouble(matcher.group(3).trim());

                GlobalResourceStat stat = new GlobalResourceStat();
                stat.setResourceName(resourceName);
                stat.setAverageUtilization(average);
                stat.setStdDevUtilization(stdDev);

                stats.add(stat);
            } catch (Exception e) {
                System.err.println("Erro ao parsear estatísticas de recurso (fallback): " + e.getMessage());
            }
        }
        return stats;
    }
    // *****************************************************************

    private List<ParsedResourceUsage> parseObserverLog(String logText) {
        List<ParsedResourceUsage> list = new ArrayList<>();
        if (logText == null) return list;

        String[] lines = logText.split("\n");
        for (String line : lines) {
            if (line.contains("Released") && line.contains("resources")) {
                try {
                    String activity = line.substring(0, line.indexOf(":")).trim();
                    String qtyText = line.substring(line.indexOf("Released") + 8).trim();
                    qtyText = qtyText.substring(0, qtyText.indexOf(" ")).trim();
                    int qty = Integer.parseInt(qtyText);
                    String role = line.substring(line.indexOf("to") + 2, line.indexOf("queue")).trim();

                    ParsedResourceUsage p = new ParsedResourceUsage();
                    p.activityName = activity;
                    p.roleName = role;
                    p.quantityUsed = qty;
                    list.add(p);
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    private void hardResetLibrary(ClassLoader loader) throws Exception {
        // optional: forcibly reset static variables if needed
    }

    public String getDetailedSimulationLog() {
        return "Simulação completa com logs detalhados gerados.";
    }
}