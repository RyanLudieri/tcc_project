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
    private final ExecutionService executionService;

    public Path generateSimulation(Long processId, String acdId) {
        try {
            String xacdmlContent = xacdmlService.generateXACDMLContent(processId, acdId);

            String xsltPath = "/xslt/transform.xsl";
            String javaOutputPath = "target/generated-sources/DynamicExperimentationProgramProxy.java";
            String generatedJavaCode = xsltTransformationService.transform(xacdmlContent, xsltPath, javaOutputPath);

            // --- CORRETO ---
            // Esta chamada agora está correta. Ela chama o 'compile' (Janino)
            // com os 3 argumentos que o nosso ExecutionService espera.
            executionService.compile(
                    generatedJavaCode,
                    "DynamicExperimentationProgramProxy", // O nome da classe para o Janino
                    processId
            );
            // ---------------

            return Paths.get(javaOutputPath);

        } catch (Exception e) {
            System.err.println("Ocorreu um erro crítico durante a geração ou execução da simulação.");
            e.printStackTrace();
            throw new RuntimeException("Falha ao gerar ou executar o código de simulação.", e);
        }
    }
}