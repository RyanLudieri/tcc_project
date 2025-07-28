package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.MethodElement;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.repository.WorkProductConfigRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkProductConfigService {

    private final WorkProductConfigRepository configRepository;

    public WorkProductConfigService(WorkProductConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Transactional
    public void generateConfigurations(List<MethodElement> methodElements, List<Activity> roots) {
        Set<String> uniqueWPNames = methodElements.stream()
                .filter(me -> me.getModelInfo() != null &&
                        (me.getModelInfo().equals("MANDATORY_INPUT") || me.getModelInfo().equals("OUTPUT")))
                .map(MethodElement::getName)
                .collect(Collectors.toSet());

        queueIndex = 0;

        for (Activity root : roots) {
            traverseAndCreateConfigs(root, uniqueWPNames);
        }
    }



    // Variável de instância para manter índice global
    private int queueIndex = 0;

    private void traverseAndCreateConfigs(Activity activity, Set<String> uniqueWPNames) {
        boolean isTask = activity.getType() == ProcessType.TASK_DESCRIPTOR;
        boolean isMilestone = activity.getType() == ProcessType.MILESTONE;

        // BEGIN (para todos que não são task ou milestone)
        if (!isTask && !isMilestone) {
            createConfigs(activity, uniqueWPNames, "BEGIN_" + activity.getName(), "INPUT");
        }

        // TASK_DESCRIPTOR → apenas INPUT com o nome da task
        if (isTask) {
            createConfigs(activity, uniqueWPNames, activity.getName(), "INPUT");
        }

        // MILESTONE → INPUT com o nome do milestone (antes dos filhos)
        if (isMilestone) {
            createConfigs(activity, uniqueWPNames, activity.getName(), "INPUT");
        }

        // Recursivamente processa os filhos
        if (activity.getChildren() != null) {
            for (Activity child : activity.getChildren()) {
                traverseAndCreateConfigs(child, uniqueWPNames);
            }
        }

        // END (para todos que não são task ou milestone)
        if (!isTask && !isMilestone) {
            createConfigs(activity, uniqueWPNames, "END_" + activity.getName(), "INPUT");
        }

        // MILESTONE → OUTPUT com o nome do milestone (depois dos filhos)
        if (isMilestone) {
            createConfigs(activity, uniqueWPNames, activity.getName(), "OUTPUT");
        }
    }

    private void createConfigs(Activity activity, Set<String> wpNames, String taskName, String inputOutput) {
        for (String wpName : wpNames) {
            WorkProductConfig config = new WorkProductConfig();
            config.setActivity(activity);
            config.setWorkProductName(wpName);
            config.setQueue_name("q" + queueIndex++);
            config.setQueue_type("QUEUE");
            config.setQueue_size(50);
            config.setInitial_quantity(0);
            config.setPolicy(Queue.FIFO);
            config.setGenerate_activity(false);
            config.setInput_output(inputOutput);
            config.setTask_name(taskName);
            configRepository.save(config);
        }
    }




    public List<Activity> collectAllActivities(List<Activity> roots) {
        List<Activity> all = new ArrayList<>();
        for (Activity activity : roots) {
            collectRecursive(activity, all);
        }
        return all;
    }

    private void collectRecursive(Activity current, List<Activity> collector) {
        collector.add(current);
        if (current.getChildren() != null) {
            for (Activity child : current.getChildren()) {
                collectRecursive(child, collector);
            }
        }
    }

}

