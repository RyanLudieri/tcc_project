package com.example.projeto_tcc.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkProductConfigSummaryDTO {
    private Long id;
    private String workProductName;
    private String queue_name;
    private boolean generate_activity;
}
