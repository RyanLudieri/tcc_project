package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.enums.VariableType;
import lombok.Getter;
import org.codehaus.janino.SimpleCompiler;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope; // <<< IMPORTANTE
import simula.Scheduler;
import simula.manager.ObserverEntry;
import simula.manager.QueueEntry;
import simula.manager.ResourceEntry;
import simula.manager.SimulationManager;

import java.lang.reflect.Method;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SessionScope
public class ExecutionService {

    private Object compiledInstance;
    private Method compiledExecuteMethod;

    private String generatedJavaCode;

    private SimulationManager simulationManager;
    @Getter
    private Long activeProcessId;


    public void compile(String javaCode, String fullClassName, Long processId) throws Exception {
        System.out.println("Iniciando compilação dinâmica com Janino (para sessão " + this + ")...");

        this.generatedJavaCode = javaCode;
        this.activeProcessId = processId;

        Path outputDir = Paths.get("output");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        ClassLoader parentClassLoader = ExecutionService.class.getClassLoader();
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(parentClassLoader);

        compiler.cook(javaCode);

        Class<?> compiledClass = compiler.getClassLoader().loadClass(fullClassName);

        this.compiledInstance = compiledClass.getDeclaredConstructor().newInstance();
        this.compiledExecuteMethod = compiledClass.getMethod("execute", float.class);

        Method getManMethod = compiledClass.getMethod("getSimulationManager");
        this.simulationManager = (SimulationManager) getManMethod.invoke(this.compiledInstance);

        System.out.println("Compilação bem-sucedida. Simulação pronta na memória.");
    }

    public void execute(float simulationDuration) throws Exception {

        if (this.compiledInstance == null || this.compiledExecuteMethod == null) {
            System.err.println("Tentativa de execução sem compilação prévia!");
            throw new IllegalStateException("Nenhuma simulação foi compilada ainda. Por favor, compile primeiro.");
        }

        System.out.println("Executando simulação dinâmica com duração: " + simulationDuration + "...");

        this.compiledExecuteMethod.invoke(this.compiledInstance, simulationDuration);

        System.out.println("Execução da simulação dinâmica concluída com sucesso.");
    }


    public String getFilteredResults(List<WorkProductConfig> configList) {
        if (this.simulationManager == null) {
            return "ERRO: O SimulationManager não foi inicializado.";
        }

        // A verificação da lista agora é feita aqui
        if (configList == null || configList.isEmpty()) {
            return "ERRO: A lista de configuração de variáveis (WorkProductConfig) está vazia ou nula.";
        }

        // --- 1. Pega o tempo de simulação real ---
        Scheduler scheduler = this.simulationManager.getScheduler();
        if (scheduler == null) {
            return "ERRO: O Scheduler interno não foi inicializado.";
        }
        float obstime = (float) scheduler.GetClock();
        float resettime = this.simulationManager.getResettime();

        float duration = obstime - resettime;

        if (duration <= 0) {
            return "ERRO: A simulação ainda não rodou ou o tempo de execução foi zero.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Relatório de Variáveis Dependentes (Tempo Total: " + duration + ") ---\n\n");

        // --- 2. Pega os mapas de observadores e filas ---
        HashMap observersMap = this.simulationManager.getObservers();
        HashMap queuesMap = this.simulationManager.getQueues();

        if (observersMap == null || queuesMap == null) {
            return "ERRO: Não foi possível buscar os observadores ou filas.";
        }

        // --- 3. Itera pela LISTA DE CONFIGURAÇÃO (vinda como parâmetro) ---
        for (WorkProductConfig row : configList) {

            // Filtra apenas pelas variáveis que o usuário marcou como "DEPENDENT"
            if (VariableType.DEPENDENT.equals(row.getVariableType())) {

                String queueName = row.getQueue_name(); // Ex: "q0" ou "Developer queue"
                String taskName = row.getTask_name();   // Ex: "BEGIN_Software production"

                QueueEntry qe = null;
                ResourceEntry re = null;

                // --- 4. Tenta encontrar o item (Fila ou Recurso) ---
                qe = (QueueEntry) queuesMap.get(queueName);

                if (qe == null) {
                    re = this.simulationManager.GetResource(queueName);
                }

                String observerName = null;
                ObserverEntry observer = null;
                String itemType = "Item";

                if (qe != null) {
                    itemType = "Fila";
                    observerName = qe.getObsid();
                    observer = (ObserverEntry) observersMap.get(observerName); // Cast
                } else if (re != null) {
                    itemType = "Recurso";
                    observerName = re.getObsid();
                    observer = (ObserverEntry) observersMap.get(observerName); // Cast
                } else {
                    sb.append(String.format("Variável: %s (Item: %s)\n", taskName, queueName));
                    sb.append("  (AVISO: Fila ou Recurso não encontrado na simulação)\n");
                    sb.append("---------------------------------\n");
                    continue;
                }

                // --- 5. Formata a Saída ---
                sb.append(String.format("Variável: %s (%s: %s)\n", taskName, itemType, queueName));

                if (observer != null) {
                    double numObs = observer.getNumberOfObservations(duration);
                    sb.append(String.format("  Observador: %s\n", observerName));
                    sb.append(String.format("  Observações: %.0f\n", numObs));

                    if (numObs > 0) {
                        if (observer.getType() == ObserverEntry.QUEUE || observer.getType() == ObserverEntry.RESOURCE) {
                            sb.append(String.format("  Média (Ponderada): %.4f\n", observer.getAvearageWeighted(duration)));
                            sb.append(String.format("  Desvio Padrão (Ponderado): %.4f\n", observer.getStandardDeviationWeighted(duration)));
                        } else {
                            sb.append(String.format("  Média (API): %.4f\n", observer.getAvearageWeighted(duration)));
                        }
                        sb.append(String.format("  Mínimo: %.4f\n", observer.getMin(duration)));
                        sb.append(String.format("  Máximo: %.4f\n", observer.getMax(duration)));
                    } else {
                        sb.append("  (Sem observações registradas)\n");
                    }
                } else {
                    sb.append(String.format("  (ERRO: Observador '%s' não encontrado)\n", observerName));
                }
                sb.append("---------------------------------\n");
            }
        }
        return sb.toString();
    }

    public String getGeneratedJavaCode() {
        if (this.generatedJavaCode == null) {
            return "// Nenhum código foi gerado para esta sessão ainda.\n" +
                    "// Por favor, acione o endpoint /generate-and-compile primeiro.";
        }
        return this.generatedJavaCode;
    }


}