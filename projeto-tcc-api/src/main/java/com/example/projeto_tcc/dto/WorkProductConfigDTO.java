package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.Queue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkProductConfigDTO {
    private Long id;
    private String workProductName;
    private String input_output;
    private String task_name;
    private String queue_name;
    private String queue_type;
    private Integer queue_size;
    private Integer initial_quantity;
    private Queue policy;
    private boolean generate_activity;
    private boolean destroy;
    private Long activityId; // apenas o ID da Activity
    private List<MethodElementObserverDTO> observers; // lista de DTOs do observer
}
