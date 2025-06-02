package com.example.projeto_tcc.dto;

public class ObserverDTO {
    private Long id;
    private String name;
    private String type;
    private ActivitySummaryDTO activity;

    public ObserverDTO() {}

    public ObserverDTO(Long id, String name, String type, ActivitySummaryDTO activity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.activity = activity;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public ActivitySummaryDTO getActivity() { return activity; }
    public void setActivity(ActivitySummaryDTO activity) { this.activity = activity; }
}

