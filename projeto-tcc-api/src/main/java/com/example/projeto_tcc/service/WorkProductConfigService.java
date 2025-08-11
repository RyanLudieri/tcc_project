package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.MethodElement;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.repository.WorkProductConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkProductConfigService {

    private final WorkProductConfigRepository configRepository;

    // índice global das filas (q0, q1, ...)
    private int queueIndex = 0;

    // mapa para evitar duplicatas: key = wpName + "|" + taskName + "|" + inputOutput
    private final Map<String, WorkProductConfig> queueMap = new LinkedHashMap<>();

    @Transactional
    public void generateConfigurations(List<MethodElement> methodElements, List<Activity> roots) {
        // preserva ordem de aparição dos work products (determinístico)
        Set<String> uniqueWPNames = methodElements.stream()
                .filter(me -> me.getModelInfo() != null &&
                        (me.getModelInfo().equals("MANDATORY_INPUT") || me.getModelInfo().equals("OUTPUT")))
                .map(MethodElement::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // reset estado
        queueIndex = 0;
        queueMap.clear();

        // IMPORTANTE: passe aqui os *WBEs raiz* (wbs.processElements), não a DeliveryProcess
        for (Activity root : roots) {
            traverseAndCreateConfigs(root, uniqueWPNames);
        }
    }

    /**
     * Implementa o Algorithm#5 (recursivo):
     * - TASK (leaf)         -> criar INPUT (apenas)
     * - MILESTONE (leaf)    -> criar INPUT (antes) e OUTPUT (depois)
     * - INTERNAL node       -> criar BEGIN_<name> (antes) ; recurse nos filhos ; criar END_<name> (depois)
     */
    private void traverseAndCreateConfigs(Activity activity, Set<String> uniqueWPNames) {
        boolean isTask = activity.getType() == ProcessType.TASK_DESCRIPTOR;
        boolean isMilestone = activity.getType() == ProcessType.MILESTONE;

        if (isTask) {
            // Task: apenas INPUT (prev dead state)
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT");
            // tasks são folhas: ainda assim chamamos filhos caso existam (defensivo)
            if (activity.getChildren() != null) {
                for (Activity child : activity.getChildren()) {
                    traverseAndCreateConfigs(child, uniqueWPNames);
                }
            }
            // NB: NÃO criar OUTPUT para task — o next normalmente será END_ do pai ou outro dead state.
            return;
        }

        if (isMilestone) {
            // Milestone: INPUT antes, OUTPUT depois (leaf)
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT");
            if (activity.getChildren() != null) { // normalmente milestones são folhas, mas por segurança:
                for (Activity child : activity.getChildren()) {
                    traverseAndCreateConfigs(child, uniqueWPNames);
                }
            }
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "OUTPUT");
            return;
        }

        // Internal node: BEGIN -> filhos -> END
        createConfigsIfAbsent(activity, uniqueWPNames, "BEGIN_" + activity.getName(), "INPUT");

        if (activity.getChildren() != null) {
            for (Activity child : activity.getChildren()) {
                traverseAndCreateConfigs(child, uniqueWPNames);
            }
        }

        createConfigsIfAbsent(activity, uniqueWPNames, "END_" + activity.getName(), "INPUT");
    }

    private void createConfigsIfAbsent(Activity activity, Set<String> wpNames, String taskName, String inputOutput) {
        for (String wpName : wpNames) {
            String key = wpName + "|" + taskName + "|" + inputOutput;
            if (queueMap.containsKey(key)) {
                // já existe essa fila para essa combinação — pula
                continue;
            }

            WorkProductConfig config = new WorkProductConfig();
            config.setActivity(activity);
            config.setWorkProductName(wpName);
            config.setQueue_name("q" + queueIndex++); // só incrementa quando realmente cria a fila
            config.setQueue_type("QUEUE");
            config.setQueue_size(50);
            config.setInitial_quantity(0); // ajuste conforme Algorithm#2 se for firstWBE
            config.setPolicy(Queue.FIFO);
            config.setGenerate_activity(false);
            config.setInput_output(inputOutput);
            config.setTask_name(taskName);

            configRepository.save(config);
            queueMap.put(key, config);
        }
    }
}
