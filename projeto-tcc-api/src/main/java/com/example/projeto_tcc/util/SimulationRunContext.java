package com.example.projeto_tcc.util;

import simulator.spem.xacdml.results.ActivityResults;
import simulator.spem.xacdml.results.IterationResults;
import simulator.spem.xacdml.results.MilestoneResults;
import simulator.spem.xacdml.results.PhaseResults;

import java.util.*;

/**
 * Armazena todo o estado de UMA execução completa de simulação.
 * Cada SimulationRunContext é isolado e usado apenas para sua execução.
 */
public class SimulationRunContext {

    private final List<Double> daysPerReplication = new ArrayList<>();
    private final Map<Integer, String> historyObserverReports = new HashMap<>();
    private final Map<Integer, Map<String, List<ActivityResults>>> historyActivityResults = new HashMap<>();
    private final Map<Integer, Map<String, List<PhaseResults>>> historyPhaseResults = new HashMap<>();
    private final Map<Integer, Map<String, List<MilestoneResults>>> historyMilestoneResults = new HashMap<>();
    private final Map<Integer, Map<String, List<IterationResults>>> historyIterationResults = new HashMap<>();
    private final Map<String, HashMap> resultadoGlobal = new TreeMap<>();

    // 🔥 NOVO: armazenamento de uso de recursos por nome do recurso
    private final Map<String, List<Double>> resourceUsageMap = new HashMap<>();

    // ===========================
    // GETTERS
    // ===========================

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

    public Map<String, List<Double>> getResourceUsageMap() {
        return resourceUsageMap;
    }

    // ===========================
    // MÉTODO PARA ADICIONAR USO DE RECURSO
    // ===========================
    public void addResourceUsage(String resourceName, double duration) {
        resourceUsageMap
                .computeIfAbsent(resourceName, k -> new ArrayList<>())
                .add(duration);
    }
}
