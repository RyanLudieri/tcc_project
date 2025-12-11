package com.example.projeto_tcc.util;

import simulator.spem.xacdml.results.ActivityResults;
import simulator.spem.xacdml.results.IterationResults;
import simulator.spem.xacdml.results.MilestoneResults;
import simulator.spem.xacdml.results.PhaseResults;

import java.util.*;

/**
 * Esta classe é um objeto simples (POJO) que armazena todos os dados e resultados
 * de UMA ÚNICA execução de simulação. Ela não é um bean do Spring.
 * Uma nova instância desta classe é criada para cada simulação, garantindo o isolamento do estado.
 */
public class SimulationRunContext {

    private final List<Double> daysPerReplication = new ArrayList<>();
    private final Map<Integer, String> historyObserverReports = new HashMap<>();
    private final Map<Integer, Map<String, List<ActivityResults>>> historyActivityResults = new HashMap<>();
    private final Map<Integer, Map<String, List<PhaseResults>>> historyPhaseResults = new HashMap<>();
    private final Map<Integer, Map<String, List<MilestoneResults>>> historyMilestoneResults = new HashMap<>();
    private final Map<Integer, Map<String, List<IterationResults>>> historyIterationResults = new HashMap<>();
    private final Map<String, HashMap> resultadoGlobal = new TreeMap<>();

    // Getters para que o ExecutionService e outros possam ler os dados.

    public List<Double> getDaysPerReplication() {
        return daysPerReplication;
    }

    public Map<Integer, String> getHistoryObserverReports() {
        return historyObserverReports;
    }

    public Map<Integer, Map<String, List<ActivityResults>>> getHistoryActivityResults() {
        return historyActivityResults;
    }

    public Map<Integer, Map<String, List<PhaseResults>>> getHistoryPhaseResults() {
        return historyPhaseResults;
    }

    public Map<Integer, Map<String, List<MilestoneResults>>> getHistoryMilestoneResults() {
        return historyMilestoneResults;
    }

    public Map<Integer, Map<String, List<IterationResults>>> getHistoryIterationResults() {
        return historyIterationResults;
    }

    public Map<String, HashMap> getResultadoGlobal() {
        return resultadoGlobal;
    }
}
