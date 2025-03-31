package com.example.projeto_tcc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class WorkProduct extends ProcessElement {

    @ManyToOne
    @JoinColumn(name = "task_descriptor_id", nullable = false)
    private TaskDescriptor taskDescriptor;

    public WorkProduct() {}

    public WorkProduct(TaskDescriptor taskDescriptor, ModelInfo modelInfo) {
        super(taskDescriptor.getIndex(), modelInfo);
        if (taskDescriptor == null) {
            throw new IllegalArgumentException("WorkProduct must be created from a TaskDescriptor.");
        }
        this.taskDescriptor = taskDescriptor;
    }

    @Override
    public boolean optional() {
        return false;
    }

    public TaskDescriptor getTaskDescriptor() {
        return taskDescriptor;
    }

    public void setTaskDescriptor(TaskDescriptor taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
    }
}
