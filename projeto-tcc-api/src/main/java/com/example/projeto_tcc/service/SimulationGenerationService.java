package com.example.projeto_tcc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SimulationGenerationService {

    private final XACDMLService xacdmlService;
    private final XsltTransformationService xsltTransformationService;
    private final ExecutionService executionService; // <-- INJETA O NOVO SERVIÇO

    public Path generateSimulation(Long processId, String acdId) {
        try {
            // Passo 1: Gera o conteúdo XACDML (nenhuma mudança aqui)
            String xacdmlContent = xacdmlService.generateXACDMLContent(processId, acdId);

            // Passo 2: Transforma o XACDML em código Java (nenhuma mudança aqui)
            String xsltPath = "/xslt/transform.xsl";
            String javaOutputPath = "target/generated-sources/DynamicExperimentationProgramProxy.java";
            String generatedJavaCode = xsltTransformationService.transform(xacdmlContent, xsltPath, javaOutputPath);

            // --- PASSO 3: EXECUÇÃO ---
            // Chama o novo serviço para compilar e executar o código que acabamos de gerar.
            executionService.compileAndExecute(generatedJavaCode, "adaptme.DynamicExperimentationProgramProxy");

            return Paths.get(javaOutputPath);

        } catch (Exception e) {
            System.err.println("Ocorreu um erro crítico durante a geração ou execução da simulação.");
            e.printStackTrace();
            throw new RuntimeException("Falha ao gerar ou executar o código de simulação.", e);
        }
    }
}