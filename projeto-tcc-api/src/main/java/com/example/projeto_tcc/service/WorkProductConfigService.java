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
        // Preserva ordem de aparição dos work products (determinístico)
        Set<String> uniqueWPNames = methodElements.stream()
                .filter(me -> me.getModelInfo() != null &&
                        (me.getModelInfo().equals("MANDATORY_INPUT") || me.getModelInfo().equals("OUTPUT")))
                .map(MethodElement::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Reset de estado
        queueIndex = 0;
        queueMap.clear();

        // 1. Executa a travessia normal, onde todos os eventos de ligação são "INPUT"
        for (Activity root : roots) {
            traverseAndCreateConfigs(root, uniqueWPNames);
        }

        // 2. Adiciona o evento OUTPUT extra apenas para a última atividade de todo o processo
        if (roots != null && !roots.isEmpty()) {
            Activity lastRoot = roots.get(roots.size() - 1);

            String finalTaskName;
            ProcessType type = lastRoot.getType();

            // Determina o nome correto para a "tarefa" de output final
            if (type == ProcessType.TASK_DESCRIPTOR || type == ProcessType.MILESTONE) {
                finalTaskName = lastRoot.getName();
            } else {
                // Para nós internos, o evento de finalização correspondente é "END_<nome>"
                finalTaskName = "END_" + lastRoot.getName();
            }

            // Cria a configuração final e especial de OUTPUT
            createConfigsIfAbsent(lastRoot, uniqueWPNames, finalTaskName, "OUTPUT");
        }
    }

    /**
     * Implementa a travessia recursiva para gerar a cadeia de eventos de INPUT.
     * - TASK (folha)         -> criar INPUT (apenas)
     * - MILESTONE (folha)    -> criar INPUT (antes) e INPUT (depois)
     * - Nó Interno           -> criar BEGIN_<nome> (INPUT) ; recursão nos filhos ; criar END_<nome> (INPUT)
     */
    private void traverseAndCreateConfigs(Activity activity, Set<String> uniqueWPNames) {
        boolean isTask = activity.getType() == ProcessType.TASK_DESCRIPTOR;
        boolean isMilestone = activity.getType() == ProcessType.MILESTONE;

        if (isTask) {
            // Task: sempre gera apenas um INPUT
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT");
            if (activity.getChildren() != null) {
                for (Activity child : activity.getChildren()) {
                    traverseAndCreateConfigs(child, uniqueWPNames);
                }
            }
            return;
        }

        if (isMilestone) {
            // Milestone intermediário: gera INPUT antes e INPUT depois para ligar com o próximo passo
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT");
            if (activity.getChildren() != null) {
                for (Activity child : activity.getChildren()) {
                    traverseAndCreateConfigs(child, uniqueWPNames);
                }
            }
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT"); // Padronizado para INPUT
            return;
        }

        // Nó Interno: BEGIN (INPUT) -> filhos -> END (INPUT)
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
                // Já existe essa fila para essa combinação — pula
                continue;
            }

            WorkProductConfig config = new WorkProductConfig();
            config.setActivity(activity);
            config.setWorkProductName(wpName);
            config.setQueue_name("q" + queueIndex++); // Só incrementa quando realmente cria a fila
            config.setQueue_type("QUEUE");
            config.setQueue_size(50);
            config.setInitial_quantity(0);
            config.setPolicy(Queue.FIFO);
            config.setGenerate_activity(false);
            config.setInput_output(inputOutput);
            config.setTask_name(taskName);

            configRepository.save(config);
            queueMap.put(key, config);
        }
    }
}