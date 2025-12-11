package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.enums.VariableType;
import com.example.projeto_tcc.repository.GlobalSimulationResultRepository;
import com.example.projeto_tcc.repository.RoleConfigRepository;
import com.example.projeto_tcc.util.SimulationRunContext;
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
public class ExecutionService {

    @Lazy
    @Autowired
    private SimulationGenerationService simulationGenerationService;

    @Autowired
    private GlobalSimulationResultRepository globalRepository;

    @Autowired
    private SimulationCacheManager cacheManager;

    @Autowired
    private RoleConfigRepository roleConfigRepository; // ✅ NOVO: Para contar os roles

    // CAMPOS DE ESTADO FORAM REMOVIDOS DESTA CLASSE

    // Adicione estes dois métodos auxiliares dentro da classe ExecutionService

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

    // --- COMPILAÇÃO ---
    public void compile(String javaCode, String fullClassName, Long processId) throws Exception {
        System.out.println("🔵 ========================================");
        System.out.println("🔵 INICIANDO COMPILAÇÃO");
        System.out.println("🔵 ProcessId: " + processId);
        System.out.println("🔵 Thread: " + Thread.currentThread().getId());
        System.out.println("🔵 ExecutionService instance: " + this.hashCode());
        System.out.println("🔵 ========================================");

        // ✅ PASSO 1: Limpar completamente o cache do processo anterior
        cacheManager.clearCacheForProcess(processId);

        // ✅ PASSO 2: Armazenar o novo código
        cacheManager.putGeneratedJavaCode(processId, javaCode);

        // ✅ PASSO 3: Definir este processo como ativo na thread atual
        cacheManager.setActiveProcess(processId);

        Path outputDir = Paths.get("output");
        if (!Files.exists(outputDir)) Files.createDirectories(outputDir);

        // ✅ PASSO 4: Criar um NOVO SimpleCompiler para cada compilação
        // Isso garante que cada compilação tenha seu próprio ClassLoader isolado
        SimpleCompiler compiler = new SimpleCompiler();

        System.out.println("🟡 Compilando código... (tamanho: " + javaCode.length() + " chars)");
        compiler.cook(javaCode);

        // ✅ PASSO 5: Obter o ClassLoader ÚNICO desta compilação
        ClassLoader classLoader = compiler.getClassLoader();
        Class<?> compiledClass = classLoader.loadClass(fullClassName); // ✅ USANDO O NOME DA CLASSE PASSADO COMO PARÂMETRO

        System.out.println("🟢 Classe compilada: " + compiledClass.getName());
        System.out.println("🟢 ClassLoader hash: " + classLoader.hashCode());
        System.out.println("🟢 ClassLoader: " + classLoader.getClass().getName());

        // ✅ PASSO 6: Armazenar a classe compilada no cache
        cacheManager.putCompiledClass(processId, compiledClass);

        System.out.println("🟢 ========================================");
        System.out.println("🟢 COMPILAÇÃO CONCLUÍDA COM SUCESSO!");
        System.out.println("🟢 ProcessId: " + processId);
        System.out.println("🟢 ========================================");
    }


    // --- GETTERS NECESSÁRIOS ---
    public Long getActiveProcessId() {
        return cacheManager.getActiveProcessId();
    }

    public String getGeneratedJavaCode() {
        Long currentProcessId = cacheManager.getActiveProcessId();
        if (currentProcessId == null) return null;
        return cacheManager.getGeneratedJavaCode(currentProcessId);
    }

    // ================================================================================
    // 1. EXECUÇÃO (Agora cria um contexto para cada execução)
    // ================================================================================
    public GlobalSimulationResult executeSimulation(
            Long processIdToExecute,
            float duration,
            Integer replications,
            List<WorkProductConfig> configList) throws Exception {

        System.out.println("🔵 ========================================");
        System.out.println("🔵 INICIANDO EXECUÇÃO");
        System.out.println("🔵 ProcessId: " + processIdToExecute);
        System.out.println("🔵 Thread: " + Thread.currentThread().getId());
        System.out.println("🔵 ExecutionService instance: " + this.hashCode());
        System.out.println("🔵 Replicações: " + replications);
        System.out.println("🔵 Duração: " + duration);
        System.out.println("🔵 ========================================");

        // ✅ DEFINE O PROCESSO ATIVO PARA ESTA THREAD
        cacheManager.setActiveProcess(processIdToExecute);

        // ✅ CRIA UM NOVO CONTEXTO PARA ESTA EXECUÇÃO, GARANTINDO ISOLAMENTO
        SimulationRunContext runContext = new SimulationRunContext();

        Class<?> compiledSimClass = cacheManager.getCompiledClass(processIdToExecute);

        System.out.println("🟡 Buscando classe compilada para processId: " + processIdToExecute);

        if (compiledSimClass == null) {
            System.out.println("🔴 ERRO: Classe compilada NÃO ENCONTRADA para processId: " + processIdToExecute);
            throw new IllegalStateException("Simulação não compilada para o processo ID: " + processIdToExecute);
        }

        System.out.println("🟢 Classe compilada ENCONTRADA!");
        System.out.println("🟢 Classe: " + compiledSimClass.getName());
        System.out.println("🟢 ClassLoader hash: " + compiledSimClass.getClassLoader().hashCode());

        if (replications == null || replications < 1) replications = 1;

        // A lógica agora usa o contexto local, sempre começando do zero.
        int startingReplication = 1;
        int currentTotalReplications = replications;

        System.out.println("Executando " + replications + " replicações...");

        ClassLoader newClassLoader = compiledSimClass.getClassLoader();
        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();

        Method getObsReport = null;
        try {
            getObsReport = compiledSimClass.getMethod("getObserverReport");
        } catch (NoSuchMethodException e) {
            System.out.println("Atenção: Método getObserverReport() não encontrado.");
        }

        try {
            currentThread.setContextClassLoader(newClassLoader);

            for (int i = startingReplication; i <= currentTotalReplications; i++) {
                System.out.println("🟡 Executando replicação #" + i + " de " + currentTotalReplications);
                hardResetLibrary(newClassLoader); // Essencial para limpar estado estático da biblioteca de simulação

                System.out.println("🟡 Criando instância da classe: " + compiledSimClass.getName());
                Object simInstance = compiledSimClass.getDeclaredConstructor().newInstance();
                Method setDur = compiledSimClass.getMethod("setSimulationDuration", float.class);
                Method exec = compiledSimClass.getMethod("execute", float.class);
                Method getMan = compiledSimClass.getMethod("getSimulationManager");

                setDur.invoke(simInstance, duration);
                exec.invoke(simInstance, duration);

                SimulationManager man = (SimulationManager) getMan.invoke(simInstance);
                double clock = man.getScheduler().GetClock();

                // ✅ ARMAZENA OS RESULTADOS NO CONTEXTO, NÃO EM "this"
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

        // ✅ Armazena o contexto no cache para que os métodos de log possam usá-lo
        cacheManager.putRunContext(processIdToExecute, runContext);

        System.out.println("🟢 Persistindo resultados da simulação...");
        GlobalSimulationResult savedResult = persistSimulationData(
                processIdToExecute, // <--- PASSE ESTE ID!
                configList,
                runContext
        );

        System.out.println("🟢 ========================================");
        System.out.println("🟢 EXECUÇÃO CONCLUÍDA COM SUCESSO!");
        System.out.println("🟢 ProcessId: " + processIdToExecute);
        System.out.println("🟢 Total de replicações: " + runContext.getDaysPerReplication().size());
        System.out.println("🟢 ========================================");

        cacheManager.removeCompiledClass(processIdToExecute);
        System.gc();


        return savedResult;

//        GlobalSimulationResult savedResult = persistSimulationData(configList, runContext);
//        System.out.println("Fim da execução.");
//        return savedResult;
    }

    private GlobalSimulationResult persistSimulationData(
            Long processIdToPersist,
            List<WorkProductConfig> configList, SimulationRunContext runContext) {
        if (runContext.getDaysPerReplication().isEmpty()) return null;



        try {
//            Long currentProcessId = cacheManager.getActiveProcessId();
//            if (currentProcessId == null) {
//                throw new IllegalStateException("Processo ativo não encontrado no cache.");
//            }

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

            // ✅ Contagem de Roles
            List<RoleConfig> roles = roleConfigRepository.findByDeliveryProcessId(processIdToPersist);
            int totalRoles = roles.size();

            GlobalSimulationResult global = new GlobalSimulationResult();
            global.setProcessId(processIdToPersist);
            global.setExecutionDate(LocalDateTime.now());
            global.setTotalReplications(runContext.getDaysPerReplication().size());
            global.setTotalRolesUsed(totalRoles); // ✅ NOVO: Salva a contagem de roles

            double sumDays = runContext.getDaysPerReplication().stream().mapToDouble(d -> d).sum();
            global.setAverageDuration(sumDays / runContext.getDaysPerReplication().size());

            StandardDeviation sd = new StandardDeviation();
            double[] arrDays = runContext.getDaysPerReplication().stream().mapToDouble(d -> d).toArray();
            global.setDurationStdDev((arrDays.length > 1) ? sd.evaluate(arrDays) : 0.0);

            Map<String, List<Double>> tempQueueValues = new HashMap<>();

            for (int i = 1; i <= runContext.getDaysPerReplication().size(); i++) {
                ReplicationResult rep = new ReplicationResult();
                rep.setReplicationNumber(i);
                rep.setDuration(runContext.getDaysPerReplication().get(i - 1));

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
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private void hardResetLibrary(ClassLoader classLoader) {

        System.out.println("Iniciando hard reset agressivo via Reflection...");

        try {
            // 🚨 PASSO 1: Mude este nome de classe para a CLASSE PRINCIPAL DA SUA BIBLIOTECA SIMULA
            Class<?> simManagerClass = classLoader.loadClass("simula.manager.SimulationManager");
            Class<?> schedulerClass = classLoader.loadClass("simula.manager.Scheduler");

            // 🚨 PASSO 2: Mude estes nomes de campos para os CAMPOS ESTÁTICOS CORRETOS que retêm o estado/instância.
            String[] staticFieldsToReset = {
                    "LAST_INSTANCE",
                    "instance",
                    "CURRENT_TIME",
                    "OUTPUT_FILE_PATH",
                    "currentSimulation", // Adicionado: Campo comum para reter a simulação ativa
                    // Adicione aqui outros campos estáticos que podem reter resultados (ex: 'GlobalResultCache')
            };

            // 🚨 PASSO 3: Adicione aqui os nomes de campos ESTÁTICOS que são COLEÇÕES (Map, List, Set) e precisam ser limpos (clear())
            String[] staticCollectionsToClear = {
                    "globalQueueList", // Adicionado: Coleção comum de filas
                    "globalEventList", // Adicionado: Coleção comum de eventos
                    "allActivities", // Adicionado: Coleção comum de atividades
                    "allWorkProducts", // Adicionado: Coleção comum de produtos de trabalho
                    // Ex: "globalQueueList", "globalEventList"
            };

            for (String fieldName : staticFieldsToReset) {
                try {
                    Field staticField = simManagerClass.getDeclaredField(fieldName);
                    staticField.setAccessible(true);

                    // Zera o campo estático
                    staticField.set(null, null);
                    System.out.println("   -> Campo estático zerado: " + fieldName);

                } catch (NoSuchFieldException e) {
                    // Ignore se o campo não for encontrado
                    continue;
                }
            }

            // 🚨 Resetando campos estáticos do Scheduler
            String[] schedulerStaticFieldsToReset = {
                    "instance", // Se o Scheduler for um Singleton
                    "CURRENT_TIME", // Se o Scheduler tiver seu próprio relógio estático
                    "eventList", // Adicionado: Lista de eventos do Scheduler
                    "currentEvent", // Adicionado: Evento atual
                    // Adicione outros campos estáticos do Scheduler aqui
            };

            for (String fieldName : schedulerStaticFieldsToReset) {
                try {
                    Field staticField = schedulerClass.getDeclaredField(fieldName);
                    staticField.setAccessible(true);

                    // Zera o campo estático
                    staticField.set(null, null);
                    System.out.println("   -> Campo estático do Scheduler zerado: " + fieldName);

                } catch (NoSuchFieldException e) {
                    // Ignore se o campo não for encontrado
                    continue;
                }
            }

            for (String fieldName : staticCollectionsToClear) {
                try {
                    Field staticField = simManagerClass.getDeclaredField(fieldName);
                    staticField.setAccessible(true);
                    Object collection = staticField.get(null);

                    if (collection instanceof java.util.Collection) {
                        ((java.util.Collection<?>) collection).clear();
                        System.out.println("   -> Coleção estática limpa: " + fieldName);
                    } else if (collection instanceof java.util.Map) {
                        ((java.util.Map<?, ?>) collection).clear();
                        System.out.println("   -> Mapa estático limpo: " + fieldName);
                    }

                } catch (NoSuchFieldException e) {
                    // Ignore se o campo não for encontrado
                    continue;
                }
            }

            // 🚨 Resetando coleções estáticas do Scheduler
            String[] schedulerStaticCollectionsToClear = {
                    "eventList", // Limpeza de coleções do Scheduler
                    // Adicione outras coleções estáticas do Scheduler aqui
            };

            for (String fieldName : schedulerStaticCollectionsToClear) {
                try {
                    Field staticField = schedulerClass.getDeclaredField(fieldName);
                    staticField.setAccessible(true);
                    Object collection = staticField.get(null);

                    if (collection instanceof java.util.Collection) {
                        ((java.util.Collection<?>) collection).clear();
                        System.out.println("   -> Coleção estática do Scheduler limpa: " + fieldName);
                    } else if (collection instanceof java.util.Map) {
                        ((java.util.Map<?, ?>) collection).clear();
                        System.out.println("   -> Mapa estático do Scheduler limpo: " + fieldName);
                    }

                } catch (NoSuchFieldException e) {
                    // Ignore se o campo não for encontrado
                    continue;
                }
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Aviso: Classe da biblioteca de simulação não encontrada no ClassLoader. Ignorando hard reset de Reflection.");
        } catch (Exception e) {
            System.err.println("Erro ao resetar o estado estático via Reflection: " + e.getMessage());
            e.printStackTrace();
        }

        // Você pode manter sua lógica de reset original aqui, se houver.
        // ...

        System.out.println("Hard reset concluído.");


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
                        if (java.util.Map.class.isAssignableFrom(type)) {
                            Object mapObj = field.get(null);
                            if (mapObj != null) ((java.util.Map<?, ?>) mapObj).clear();
                        } else if (java.util.Collection.class.isAssignableFrom(type)) {
                            Object colObj = field.get(null);
                            if (colObj != null) ((java.util.Collection<?>) colObj).clear();
                        } else if (!isFinal && (type == int.class || type == long.class)) {
                            field.set(null, 0);
                        } else if (!isFinal && (type == boolean.class)) {
                            field.set(null, false);
                        }
                    } catch (Exception e) {
                        // Ignora
                    }
                }
            }
        } catch (Throwable t) {
            // Ignora
        }
    }

    // ================================================================================
    // 2. LOG DETALHADO (Agora busca o contexto do cache)
    // ================================================================================
    public String getDetailedSimulationLog() {
        Long processId = getActiveProcessId();
        SimulationRunContext runContext = (processId != null) ? cacheManager.getRunContext(processId) : null;

        if (runContext == null || runContext.getDaysPerReplication().isEmpty()) {
            return "Nenhuma simulação executada ou resultados não encontrados no cache.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------- DETAILED RESULTS BY REPLICATION -------------------------------------- \n");

        int totalReplications = runContext.getDaysPerReplication().size();

        for (int i = 1; i <= totalReplications; i++) {
            sb.append("\n------------------------------------------------------------------------\n");
            sb.append("Replication (execution) #").append(i).append("\n");
            sb.append("------------------------------------------------------------------------\n");

            double days = runContext.getDaysPerReplication().get(i - 1);
            sb.append("\tProject duration: ").append(String.format("%.2f", days)).append(" days\n");

            sb.append("\n\t[Observer Report - Raw Statistics]\n");
            String rawReport = runContext.getHistoryObserverReports().get(i);
            sb.append((rawReport != null) ? rawReport : "\t\tObserver report not available for this replication.\n");
            sb.append("\n");

            sb.append("\n\t[Dead states snapshot] - Printing the number of entities in each dead state at the end of the replication\n");
            HashMap queues = runContext.getResultadoGlobal().get("run #" + i);
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

            sb.append(formatActivityResults(runContext.getHistoryActivityResults().get(i)));
            sb.append(formatPhaseResults(runContext.getHistoryPhaseResults().get(i)));
            sb.append(formatMilestoneResults(runContext.getHistoryMilestoneResults().get(i)));
            sb.append(formatIterationResults(runContext.getHistoryIterationResults().get(i)));
        }
        return sb.toString();
    }

    // ... (Os métodos de formatação como formatActivityResults, etc., permanecem exatamente os mesmos) ...
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
