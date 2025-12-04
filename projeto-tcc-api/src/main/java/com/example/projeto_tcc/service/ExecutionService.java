package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.GlobalQueueStat;
import com.example.projeto_tcc.entity.GlobalSimulationResult;
import com.example.projeto_tcc.entity.ReplicationResult;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.enums.VariableType;
import com.example.projeto_tcc.repository.GlobalSimulationResultRepository;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.codehaus.janino.SimpleCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import simula.manager.QueueEntry;
import simula.manager.SimulationManager;
import simulator.spem.xacdml.results.ActivityResults;
import simulator.spem.xacdml.results.IterationResults;
import simulator.spem.xacdml.results.MilestoneResults;
import simulator.spem.xacdml.results.PhaseResults;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
//@SessionScope
public class ExecutionService {

    @Lazy
    @Autowired
    private SimulationGenerationService simulationGenerationService;

    @Autowired
    private GlobalSimulationResultRepository globalRepository;

    @Autowired // NOVO: Injetar o Cache Manager
    private SimulationCacheManager cacheManager;
//    private String generatedJavaCode;
//    private Long activeProcessId;
//    private Class<?> compiledSimClass;

    // Variáveis de Estado (Armazenam os dados da última execução)
    private int totalReplicationsRun = 0;
    private List<Double> daysPerReplication = new ArrayList<>();

    // Histórico detalhado para o LOG (Chave: Nº da Replicação)
    private Map<Integer, Map<String, List<ActivityResults>>> historyActivityResults = new HashMap<>();
    private Map<Integer, Map<String, List<PhaseResults>>> historyPhaseResults = new HashMap<>();
    private Map<Integer, Map<String, List<MilestoneResults>>> historyMilestoneResults = new HashMap<>();
    private Map<Integer, Map<String, List<IterationResults>>> historyIterationResults = new HashMap<>();

    // Armazena as filas de cada rodada (para Log Detalhado e Global)
    private Map<String, HashMap> resultadoGlobal = new TreeMap<>();

    public String generateCodeForPreview(Long processId) {
        try {
            // Cria um ID temporário para não sobrescrever arquivos importantes
            String tempAcdId = "Preview_" + processId + "_" + System.currentTimeMillis();

            // Chama o serviço que cria o XACDML e aplica o XSLT
            // (Gera o arquivo físico no disco)
            Path generatedFilePath = simulationGenerationService.generateSimulation(processId, tempAcdId);

            // Lê o conteúdo do arquivo e retorna como String
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

    // --- COMPILAÇÃO ---
// ExecutionService.java

    // --- COMPILAÇÃO ---
    public void compile(String javaCode, String fullClassName, Long processId) throws Exception {
        System.out.println("Iniciando compilação...");

        // Armazena o código e o ID no cache
        cacheManager.putGeneratedJavaCode(processId, javaCode);
        cacheManager.setActiveProcess(processId);

        resetData(); // Limpa as variáveis de *instância* do service

        Path outputDir = Paths.get("output");
        if (!Files.exists(outputDir)) Files.createDirectories(outputDir);

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(ExecutionService.class.getClassLoader());
        compiler.cook(javaCode);
        Class<?> compiledClass = compiler.getClassLoader().loadClass(fullClassName);

        // Armazena a classe compilada no cache compartilhado
        cacheManager.putCompiledClass(processId, compiledClass);

        System.out.println("Compilação OK.");
        System.out.println("ExecutionService instance = " + this.hashCode());
    }
    private void resetData() {
        this.totalReplicationsRun = 0;
        this.daysPerReplication.clear();
        this.resultadoGlobal.clear();
        this.historyActivityResults.clear();
        this.historyPhaseResults.clear();
        this.historyMilestoneResults.clear();
        this.historyIterationResults.clear();
    }

    // --- GETTERS NECESSÁRIOS ---
    // ExecutionService.java

    // --- GETTERS NECESSÁRIOS ---
    public Long getActiveProcessId() {
        return cacheManager.getActiveProcessId(); // Buscar do Cache
    }

    public String getGeneratedJavaCode() {
        Long currentProcessId = cacheManager.getActiveProcessId();
        if (currentProcessId == null) return null;
        return cacheManager.getGeneratedJavaCode(currentProcessId); // Buscar do Cache
    }

    // ================================================================================
    // 1. EXECUÇÃO (SILENCIOSA - Apenas guarda dados)
    // ================================================================================
// ExecutionService.java

    // ================================================================================
    // 1. EXECUÇÃO (SILENCIOSA - Apenas guarda dados)
    // ================================================================================
    public GlobalSimulationResult executeSimulation(float duration, Integer replications, List<WorkProductConfig> configList) throws Exception {
        System.out.println("ExecutionService instance = " + this.hashCode());

        Long currentProcessId = cacheManager.getActiveProcessId();

        // 🟢 Busca a classe compilada do Cache
        Class<?> compiledSimClass = cacheManager.getCompiledClass(currentProcessId);

        if (compiledSimClass == null) throw new IllegalStateException("Simulação não compilada.");

        if (replications == null || replications < 1) replications = 1;

        resetData();
        this.totalReplicationsRun = replications;

        System.out.println("Executando " + replications + " replicações...");

        for (int i = 1; i <= replications; i++) {
            hardResetLibrary();

            // Usa a classe obtida do Cache
            Object simInstance = compiledSimClass.getDeclaredConstructor().newInstance();
            Method setDur = compiledSimClass.getMethod("setSimulationDuration", float.class);
            Method exec = compiledSimClass.getMethod("execute", float.class);
            Method getMan = compiledSimClass.getMethod("getSimulationManager");

            setDur.invoke(simInstance, duration);

            exec.invoke(simInstance, duration);



            SimulationManager man = (SimulationManager) getMan.invoke(simInstance);



            double clock = man.getScheduler().GetClock();

            this.daysPerReplication.add(clock / 480.0);



            this.historyActivityResults.put(i, man.getScheduler().getMapWithActivityResults());

            this.historyPhaseResults.put(i, man.getScheduler().getMapWithPhaseResults());

            this.historyMilestoneResults.put(i, man.getScheduler().getMapWithMilestoneResults());

            this.historyIterationResults.put(i, man.getScheduler().getMapWithIterationResults());



            this.resultadoGlobal.put("run #" + i, man.getQueues());



            man.getScheduler().Stop();

            man.getScheduler().Clear();
        }

        GlobalSimulationResult savedResult = persistSimulationData(configList);
        System.out.println("Fim da execução.");
        return savedResult;
    }
// ExecutionService.java

    private GlobalSimulationResult persistSimulationData(List<WorkProductConfig> configList) {
        if (this.totalReplicationsRun == 0) return null;

        try {
            Long currentProcessId = cacheManager.getActiveProcessId();

            if (currentProcessId == null) {
                throw new IllegalStateException("Processo ativo não encontrado no cache. A simulação não foi compilada.");
            }

            Set<String> dependentQueues = new HashSet<>();
            if (configList != null && !configList.isEmpty()) {
                for (WorkProductConfig cfg : configList) {
                    if (cfg.getVariableType() == VariableType.DEPENDENT) {
                        dependentQueues.add(cfg.getQueue_name());
                    }
                }
            }

            GlobalSimulationResult global = new GlobalSimulationResult();

            // 🛠️ CORREÇÃO: Usa o 'currentProcessId' obtido do cache
            global.setProcessId(currentProcessId);

            global.setExecutionDate(LocalDateTime.now());
            global.setTotalReplications(this.totalReplicationsRun);

            double sumDays = this.daysPerReplication.stream().mapToDouble(d -> d).sum();
            global.setAverageDuration(sumDays / this.totalReplicationsRun);

            StandardDeviation sd = new StandardDeviation();
            double[] arrDays = daysPerReplication.stream().mapToDouble(d -> d).toArray();
            global.setDurationStdDev((arrDays.length > 1) ? sd.evaluate(arrDays) : 0.0);

            Map<String, List<Double>> tempQueueValues = new HashMap<>();

            for (int i = 1; i <= this.totalReplicationsRun; i++) {
                ReplicationResult rep = new ReplicationResult();
                rep.setReplicationNumber(i);

                if ((i - 1) < daysPerReplication.size()) {
                    rep.setDuration(daysPerReplication.get(i - 1));
                }

                HashMap queuesMap = this.resultadoGlobal.get("run #" + i);
                if (queuesMap != null) {
                    for (Object keyObj : queuesMap.keySet()) {
                        String qName = keyObj.toString();
                        boolean shouldSave = dependentQueues.isEmpty() || dependentQueues.contains(qName);

                        if (shouldSave) {
                            Object val = queuesMap.get(keyObj);
                            int count = 0;
                            if (val instanceof QueueEntry) {
                                count = ((QueueEntry) val).deadState.getCount();
                            }

                            rep.getQueueFinalCounts().put(qName, count);
                            tempQueueValues.computeIfAbsent(qName, k -> new ArrayList<>()).add((double) count);
                        }
                    }
                }
                rep.setGlobalResult(global);
                global.getReplicationResults().add(rep);
            }

            for (Map.Entry<String, List<Double>> entry : tempQueueValues.entrySet()) {
                String qName = entry.getKey();
                List<Double> values = entry.getValue();
                double[] valuesArr = values.stream().mapToDouble(d -> d).toArray();
                double mean = values.stream().mapToDouble(d -> d).average().orElse(0.0);
                double stdDev = (valuesArr.length > 1) ? sd.evaluate(valuesArr) : 0.0;

                GlobalQueueStat stat = new GlobalQueueStat();
                stat.setQueueName(qName);
                stat.setAverageCount(mean);
                stat.setStdDevCount(stdDev);
                stat.setGlobalResult(global);
                global.getQueueStats().add(stat);
            }

            return globalRepository.save(global);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar dados: " + e.getMessage());
        }
    }
    private void hardResetLibrary() {
        // Incluímos Scheduler e InternalActiveEntry na lista
        Class<?>[] classesToClean = {
                simula.manager.QueueEntry.class,
                simula.manager.ResourceEntry.class,
                simula.manager.ActiveEntry.class,
                simula.manager.InternalActiveEntry.class,
                simula.manager.SimulationManager.class,
                simula.Scheduler.class,
                simula.Activity.class
        };

        for (Class<?> clazz : classesToClean) {
            wipeStaticFields(clazz);
        }

        // Reseta o flag principal
        try {
            Field f = simula.Activity.class.getField("isBeginOfSimulation");
            f.set(null, true);
        } catch (Exception e) {}
    }

    private void wipeStaticFields(Class<?> clazz) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    boolean isFinal = Modifier.isFinal(field.getModifiers());

                    try {
                        // 1. Mapas e Listas (Limpamos o CONTEÚDO, mesmo se for final)
                        if (java.util.Map.class.isAssignableFrom(type)) {
                            Object mapObj = field.get(null);
                            if (mapObj != null) {
                                ((java.util.Map<?, ?>) mapObj).clear();
                                // Debug para confirmar limpeza
                                // System.out.println("Limpo Map: " + clazz.getSimpleName() + "." + field.getName());
                            }
                        }
                        else if (java.util.Collection.class.isAssignableFrom(type)) { // List, Set, Vector...
                            Object colObj = field.get(null);
                            if (colObj != null) {
                                ((java.util.Collection<?>) colObj).clear();
                                // System.out.println("Limpo Collection: " + clazz.getSimpleName() + "." + field.getName());
                            }
                        }
                        // 2. Números (Só zeramos se NÃO for final)
                        else if (!isFinal && (type == int.class || type == Integer.class || type == long.class)) {
                            field.set(null, 0);
                        }
                        // 3. Booleanos (Só resetamos se NÃO for final)
                        else if (!isFinal && (type == boolean.class || type == Boolean.class)) {
                            field.set(null, false);
                        }

                    } catch (Exception e) {
                        // Ignora campos específicos que derem erro
                    }
                }
            }
        } catch (Throwable t) {
            System.out.println("Erro ao limpar classe " + clazz.getSimpleName());
        }
    }

    // ================================================================================
    // 2. LOG DETALHADO (Texto para o Modal/TextArea)
    // ================================================================================
    public String getDetailedSimulationLog() {
        if (this.totalReplicationsRun == 0) return "Nenhuma simulação executada.";

        StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------- DETAILED RESULTS BY REPLICATION -------------------------------------- \n");

        for (int i = 1; i <= this.totalReplicationsRun; i++) {
            sb.append("\n------------------------------------------------------------------------\n");
            sb.append("Replication (execution) #").append(i).append("\n");
            sb.append("------------------------------------------------------------------------\n");

            double days = this.daysPerReplication.get(i-1);
            sb.append("\tProject duration: ").append(String.format("%.2f", days)).append(" days\n");

            // Dead States Snapshot
            sb.append("\n\t[Dead states snapshot] - Printing the number of entities in each dead state at the end of the replication\n");
            HashMap queues = this.resultadoGlobal.get("run #" + i);
            if (queues != null && !queues.isEmpty()) {
                List<Object> sortedKeys = new ArrayList<>(queues.keySet());
                sortedKeys.sort(Comparator.comparing(Object::toString));

                for (Object queueName : sortedKeys) {
                    QueueEntry qe = (QueueEntry) queues.get(queueName);
                    sb.append("\n\t\tQueue name : ").append(queueName);
                    sb.append("\n\t\tNunber of entities in queue via getCount: ").append(qe.deadState.getCount());
                }
            }
            sb.append("\n");

            // Elementos (Com cabeçalhos [Activities], etc.)
            sb.append(formatActivityResults(this.historyActivityResults.get(i)));
            sb.append(formatPhaseResults(this.historyPhaseResults.get(i)));
            sb.append(formatMilestoneResults(this.historyMilestoneResults.get(i)));
            sb.append(formatIterationResults(this.historyIterationResults.get(i)));
        }
        return sb.toString();
    }

    // ================================================================================
    // 3. RESULTADOS GLOBAIS (Média e Desvio Padrão)
    // ================================================================================
    public String getFilteredResults(List<WorkProductConfig> configList) {
        if (this.totalReplicationsRun == 0) return "";
        if (configList == null || configList.isEmpty()) return "Configuração vazia.";

        String header = buildHeader();

        Map<String, VariableType> mapQueueVariableType = new HashMap<>();
        for (WorkProductConfig row : configList) {
            if (row.getVariableType() == VariableType.DEPENDENT) {
                mapQueueVariableType.put(row.getQueue_name(), row.getVariableType());
            }
        }

        if (mapQueueVariableType.isEmpty()) return header + "\n\n(Nenhuma variável dependente selecionada)";

        Set<String> keys = resultadoGlobal.keySet();
        if (keys.isEmpty()) return header;

        HashMap hm = (HashMap) resultadoGlobal.get(keys.iterator().next());
        List<String> listaNomesFilas = new ArrayList<>(hm.keySet());

        int[][] matrizResultados = new int[keys.size()][listaNomesFilas.size()];

        int i = 0;
        for (String keyExp : keys) {
            HashMap secondHash = (HashMap) resultadoGlobal.get(keyExp);
            for (int j = 0; j < listaNomesFilas.size(); j++) {
                String qName = listaNomesFilas.get(j);
                Object obj = secondHash.get(qName);
                matrizResultados[i][j] = (obj instanceof QueueEntry) ? ((QueueEntry) obj).deadState.getCount() : 0;
            }
            i++;
        }

        return header + printResultadosGlobalTextArea(matrizResultados, mapQueueVariableType, listaNomesFilas);
    }

    private String buildHeader() {
        double sumDays = 0;
        for (double d : this.daysPerReplication) sumDays += d;
        double avgDays = totalReplicationsRun > 0 ? sumDays / totalReplicationsRun : 0;

        StandardDeviation sd = new StandardDeviation();
        double[] arr = daysPerReplication.stream().mapToDouble(d -> d).toArray();
        double stdDev = (arr.length > 1) ? sd.evaluate(arr) : 0.0;

        return "\n............   NUMBER OF SIMULATION RUNS, MEAN(SD) OF PROJECT DURATION...   ............\n" +
                "\nNumber of simulation runs......................:  " + totalReplicationsRun + "\n" +
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

    // --- HELPERS DE FORMATAÇÃO (AGORA COM CABEÇALHOS) ---

    private String formatActivityResults(Map<String, List<ActivityResults>> map) {
        if (map == null || map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t[Activities]\n");
        for (String key : map.keySet()) {
            List<ActivityResults> list = map.get(key);
            String name = key.contains("_") ? key.split("_")[1] : key;
            sb.append("\t\tThe activity named '").append(name).append("' was executed ").append(list.size()).append(" times");
            for (int k = 0; k < list.size(); k++) {
                ActivityResults ac = list.get(k);
                sb.append("\n\t\t\t - In the execution # ").append(k+1)
                        .append(", it started at (day) : ").append((int) ac.getTimeWorkBreakdownElementStarted()/480 + 1)
                        .append(" and finished at (day) : ").append((int) ac.getTimeWorkBreakdownElementFinished()/480 + 1);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String formatPhaseResults(Map<String, List<PhaseResults>> map) {
        if (map == null || map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t[Phases]\n");
        for (String key : map.keySet()) {
            List<PhaseResults> list = map.get(key);
            String name = key.contains("_") ? key.split("_")[1] : key;
            sb.append("\t\tThe phase named '").append(name).append("' was executed ").append(list.size()).append(" times");
            for (int k = 0; k < list.size(); k++) {
                PhaseResults r = list.get(k);
                sb.append("\n\t\t\t - In the execution # ").append(k+1)
                        .append(", it started at (day) : ").append((int)r.getTimeWorkBreakdownElementStarted()/480 + 1)
                        .append(" and finished at (day) : ").append((int)r.getTimeWorkBreakdownElementFinished()/480 + 1);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String formatMilestoneResults(Map<String, List<MilestoneResults>> map) {
        if (map == null || map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t[Milestones]\n");
        for (String key : map.keySet()) {
            List<MilestoneResults> list = map.get(key);
            sb.append("\t\tMilestone ").append(key).append(" reached !");
            for (MilestoneResults r : list) {
                sb.append("\n\t\t\t it was reached at (day) : ").append((int)r.getTimeReached()/480 + 1);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String formatIterationResults(Map<String, List<IterationResults>> map) {
        if (map == null || map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t[Iterations]\n");
        for (String key : map.keySet()) {
            List<IterationResults> list = map.get(key);
            String name = key.contains("_") ? key.split("_")[1] : key;
            sb.append("\t\tThe iteration named '").append(name).append("' was executed ").append(list.size()).append(" times");
            for (int k = 0; k < list.size(); k++) {
                IterationResults r = list.get(k);
                sb.append("\n\t\t\t - In the execution # ").append(k+1)
                        .append(", it started at (day) : ").append((int)r.getTimeWorkBreakdownElementStarted()/480 + 1)
                        .append(" and finished at (day) : ").append((int)r.getTimeWorkBreakdownElementFinished()/480 + 1);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}