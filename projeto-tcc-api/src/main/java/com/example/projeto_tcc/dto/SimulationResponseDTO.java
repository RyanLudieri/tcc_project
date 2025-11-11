package com.example.projeto_tcc.dto;

import java.util.List;

public class SimulationResponseDTO {
    private Long id;
    private String objective;
    private List<ProcessSummaryDTO> processes;
    private int processCount;
    private String status;
    private String lastModified;

    public SimulationResponseDTO(Long id, String objective, List<ProcessSummaryDTO> processes, int processCount,
                                 String status, String lastModified) {
        this.id = id;
        this.objective = objective;
        this.processes = processes;
        this.processCount = processCount;
        this.status = status;
        this.lastModified = lastModified;
    }

    // Getters e setters
    public Long getId() { return id; }
    public String getObjective() { return objective; }
    public List<ProcessSummaryDTO> getProcesses() { return processes; }
    public int getProcessCount() { return processCount; }
    public String getStatus() { return status; }
    public String getLastModified() { return lastModified; }

    public void setId(Long id) { this.id = id; }
    public void setObjective(String objective) { this.objective = objective; }
    public void setProcesses(List<ProcessSummaryDTO> process) { this.processes = process; }
    public void setProcessCount(int processCount) { this.processCount = processCount; }
    public void setStatus(String status) { this.status = status; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
}
