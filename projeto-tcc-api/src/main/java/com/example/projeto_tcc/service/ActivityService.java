package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Activity;
import com.example.projeto_tcc.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository repository;

    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public Activity createActivity(Activity activity) {
        return repository.save(activity);
    }

    public List<Activity> getAllActivities() {
        return repository.findAll();
    }

    public Activity getActivityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
    }
}
