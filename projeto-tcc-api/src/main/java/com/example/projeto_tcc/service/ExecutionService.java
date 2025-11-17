package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.enums.VariableType;

import simula.manager.SimulationManager;
import simula.manager.QueueEntry;
import simula.manager.ResourceEntry;
import simula.Scheduler;
import simula.Activity;
import simula.manager.ActiveEntry;
import simulator.spem.xacdml.results.IterationResults;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


import org.codehaus.janino.SimpleCompiler;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Iterator;

@Service
@SessionScope
public class ExecutionService {
    private String generatedJavaCode;
    private Long activeProcessId;
    private Class<?> compiledSimClass;

    private int totalReplicationsRun = 0;

    // private double[] numberOfDaysPerReplication; (Portado para Lista)
    private List<Double> daysPerReplication = new ArrayList<>();

    // private Map<String, List<Integer>> mapWithNumberOfIterationsPerReplication;
    private Map<String, List<Integer>> mapWithNumberOfIterationsPerReplication = new HashMap<>();

    // private Map<String, Integer> mapWithAcumulatedIterationResults;
    private Map<String, Integer> mapWithAcumulatedIterationResults = new HashMap<>();

    // private Map<String, HashMap<String, QueueEntry>> resultadoGlobal = new TreeMap();
    // (Armazena o HashMap de filas de cada rodada)
    private Map<String, HashMap> resultadoGlobal = new TreeMap<>();


    public void compile(String javaCode, String fullClassName, Long processId) throws Exception {
        System.out.println("Iniciando compilação dinâmica com Janino...");

        this.generatedJavaCode = javaCode;
        this.activeProcessId = processId;

        // Limpa execuções antigas (como no Facade.houseCleaning() ou no construtor)
        this.totalReplicationsRun = 0;
        this.daysPerReplication.clear();
        this.mapWithNumberOfIterationsPerReplication.clear();
        this.mapWithAcumulatedIterationResults.clear();
        this.resultadoGlobal.clear();

        // (Lógica do Janino)
        Path outputDir = Paths.get("output");
        if (!Files.exists(outputDir)) Files.createDirectories(outputDir);
        ClassLoader parentClassLoader = ExecutionService.class.getClassLoader();
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(parentClassLoader);
        compiler.cook(javaCode);
        this.compiledSimClass = compiler.getClassLoader().loadClass(fullClassName);

        System.out.println("Compilação bem-sucedida. Classe " + fullClassName + " carregada.");
    }

    public Long getActiveProcessId() { return activeProcessId; }
    public String getGeneratedJavaCode() { return this.generatedJavaCode; }


    public void executeSimulation(float duration, Integer replications) throws Exception {
        if (this.compiledSimClass == null) {
            throw new IllegalStateException("Nenhuma simulação foi compilada.");
        }
        if (replications == null || replications < 1) replications = 1;

        // Limpa os dados (como no Facade.execute)
        this.totalReplicationsRun = replications;
        this.daysPerReplication.clear();
        this.mapWithNumberOfIterationsPerReplication.clear();
        this.mapWithAcumulatedIterationResults.clear();
        this.resultadoGlobal.clear();

        double acumulatedNumberOfDays = 0.0;

        System.out.println("\n------------------------- DETAILED RESULTS BY REPLICATION -------------------------------------- \n");

        for (int i = 1; i <= replications; i++) {
            Object simInstance = this.compiledSimClass.getDeclaredConstructor().newInstance();

            // 2. Pega os métodos da instância
            Method setDurationMethod = this.compiledSimClass.getMethod("setSimulationDuration", float.class);
            Method executeMethod = this.compiledSimClass.getMethod("execute", float.class);
            Method getManMethod = this.compiledSimClass.getMethod("getSimulationManager");

            setDurationMethod.invoke(simInstance, duration);
            executeMethod.invoke(simInstance, duration);

            SimulationManager man = (SimulationManager) getManMethod.invoke(simInstance);

            double clock = man.getScheduler().GetClock();
            double numberOfDays = clock / 480.0;
            this.daysPerReplication.add(numberOfDays);
            acumulatedNumberOfDays += numberOfDays;

            Map<String, List<IterationResults>> mapWithIterationResults = man.getScheduler().getMapWithIterationResults();
            Set<String> keys = mapWithIterationResults.keySet();

            for (String key : keys) {
                List<IterationResults> listWithIterationResults = mapWithIterationResults.get(key);

                if (mapWithNumberOfIterationsPerReplication.containsKey(key)) {
                    List<Integer> temp = mapWithNumberOfIterationsPerReplication.get(key);
                    temp.add(listWithIterationResults.size());
                } else {
                    List<Integer> newReplicationList = new ArrayList<>();
                    newReplicationList.add(listWithIterationResults.size());
                    mapWithNumberOfIterationsPerReplication.put(key, newReplicationList);
                }

                if (mapWithAcumulatedIterationResults.containsKey(key)) {
                    Integer a = mapWithAcumulatedIterationResults.get(key);
                    a += listWithIterationResults.size();
                    mapWithAcumulatedIterationResults.put(key, a);
                } else {
                    mapWithAcumulatedIterationResults.put(key, listWithIterationResults.size());
                }
            }

            HashMap queues = man.getQueues();
            this.resultadoGlobal.put("run #" + i, queues);

            man.getScheduler().Stop();
            man.getScheduler().Clear();
            Activity.isBeginOfSimulation = true;
            ActiveEntry.lastid = 0;

            System.out.println("Replicação #" + i + " concluída e acumulada.");
        }
    }


    public String getFilteredResults(List<WorkProductConfig> configList) {
        if (this.totalReplicationsRun == 0) {
            return "ERRO: Nenhuma replicação foi executada.";
        }
        if (configList == null || configList.isEmpty()) {
            return "ERRO: A lista de configuração (WorkProductConfig) está vazia.";
        }

        // --- INÍCIO DA PARTE 1: getResultadosCabecalho() ---
        String header = this.buildHeader();

        // --- INÍCIO DA PARTE 2: getResultadosGlobalString() ---
        String body = this.buildGlobalString(configList);

        return header + body;
    }


    private String buildHeader() {
        double sumDays = 0;
        for (double d : this.daysPerReplication) {
            sumDays += d;
        }
        double avgDays = sumDays / this.totalReplicationsRun;

        StandardDeviation sd = new StandardDeviation();
        double[] daysArray = this.daysPerReplication.stream().mapToDouble(d -> d).toArray();
        double stdDevDays = 0.0;
        if (daysArray.length > 1) {
            stdDevDays = sd.evaluate(daysArray);
        }

        String resultadoGlobalString = "\n............   NUMBER OF SIMULATION RUNS, MEAN(SD) OF PROJECT DURATION...   ............\n";
        resultadoGlobalString += "\nNumber of simulation runs......................:  "  + this.totalReplicationsRun + "\n";

        // (No Facade, ele lia campos, aqui nós usamos os valores que acabámos de calcular)
        resultadoGlobalString += "Number of days.....................................:  " + Math.round(avgDays * 100.0) / 100.0 +
                "(" + Math.round(stdDevDays * 100.0) / 100.0 + ")" + "\n";


        return resultadoGlobalString;
    }

    private String buildGlobalString(List<WorkProductConfig> configList) {

        // 1. Converte a configList (do DB) para o Map (que o código legado espera)
        Map<String, VariableType> mapQueueVariableType = new HashMap<>();
        for (WorkProductConfig row : configList) {
            if (row.getVariableType() == VariableType.DEPENDENT) {
                mapQueueVariableType.put(row.getQueue_name(), row.getVariableType());
            }
        }

        if (mapQueueVariableType.isEmpty()) {
            return "\n\n(Nenhuma variável dependente foi selecionada para o relatório de filas)";
        }

        Set<String> keys = resultadoGlobal.keySet();
        int numeroExperimentos = keys.size();

        if (numeroExperimentos == 0) return "\nERRO: 'resultadoGlobal' está vazio.";

        HashMap hm = (HashMap) resultadoGlobal.get(keys.iterator().next());
        Set<String> chaves = hm.keySet();
        int numeroFilas = chaves.size();

        int[][] matrizResultados = new int[numeroExperimentos][numeroFilas];
        int quantity = 0;
        int contadorLinhas = 0;
        int contadorColunas = 0;

        for (String experimento : keys) {
            HashMap secondHash = (HashMap) resultadoGlobal.get(experimento);
            Collection<String> chavesNomeFilasSegundoHash = secondHash.keySet();

            for (String queueName : chavesNomeFilasSegundoHash) {
                if (secondHash.get(queueName) instanceof QueueEntry) {
                    QueueEntry qe1 = (QueueEntry) secondHash.get(queueName);
                    quantity = qe1.deadState.getCount();

                    matrizResultados[contadorLinhas][contadorColunas] = quantity;
                }
                contadorColunas++;
            }
            contadorLinhas++;
            contadorColunas = 0;
        }

        String temp = printResultadosGlobalTextArea(matrizResultados, mapQueueVariableType);
        return temp;
    }


    private String printResultadosGlobalTextArea(int[][] matrizResultados, Map<String, VariableType> mapQueueVariableType) {
        StringBuilder resultadoGlobalString = new StringBuilder();

        String cabecalho = "\n............................   PRINTING THE MEAN AND STANDARD DEVIATION OF ENTITIES IN EACH QUEUE CONFIGURED AS 'DEPENDENT' FOR EACH REPLICATION   ............................ \n";

        Set<String> keys = resultadoGlobal.keySet();
        HashMap secondHash = (HashMap) resultadoGlobal.get(keys.iterator().next());
        Collection<String> chavesNomeFilasSegundoHash = secondHash.keySet();

        double soma = 0.0;
        StandardDeviation sd = new StandardDeviation();

        Iterator iterator = chavesNomeFilasSegundoHash.iterator();
        double[] numeroEntidadesPorFila = new double[matrizResultados.length];

        // Itera sobre as COLUNAS (Filas)
        for (int j = 0; j < matrizResultados[0].length; j++) {

            String fila = (String) iterator.next();

            if (mapQueueVariableType.containsKey(fila)) {

                resultadoGlobalString.append("\nQueue: " + fila);

                for (int i = 0; i < matrizResultados.length; i++) {

                    resultadoGlobalString.append("\n\tquantity in replication " + (i + 1) + "..: " + matrizResultados[i][j]);
                    numeroEntidadesPorFila[i] = matrizResultados[i][j];
                    soma = soma + matrizResultados[i][j];
                }

                double mean = (soma / matrizResultados.length);
                double standardDeviation = 0.0;
                if (numeroEntidadesPorFila.length > 1) { // SD só funciona com 2+ amostras
                    standardDeviation = sd.evaluate(numeroEntidadesPorFila);
                }

                mean = Math.round(mean * 100.0) / 100.0;
                standardDeviation = Math.round(standardDeviation * 100.0) / 100.0;

                resultadoGlobalString.append("\n\tmean of entities in queue " + fila + "..:" + mean);
                resultadoGlobalString.append("\tStandard deviation..: " + standardDeviation + "\n");
                soma = 0.0;
            }
        }
        return cabecalho + resultadoGlobalString.toString();
    }
}