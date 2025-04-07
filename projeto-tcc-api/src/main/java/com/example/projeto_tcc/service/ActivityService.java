package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Activity;
import com.example.projeto_tcc.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository repository;

    private final ProcessElementIndexService indexService;

    public ActivityService(ActivityRepository repository, ProcessElementIndexService indexService) {
        this.repository = repository;
        this.indexService = indexService;
    }

    public Activity createActivity(Activity activity) {
        int index = indexService.getNextIndex();
        activity.setIndex(index);
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
