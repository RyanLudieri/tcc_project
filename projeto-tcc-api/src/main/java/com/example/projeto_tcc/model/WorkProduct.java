package com.example.projeto_tcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class WorkProduct extends ProcessElement {

    @Column(name = "name", nullable = false)
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
