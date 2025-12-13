package com.example.projeto_tcc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimulationGenerationService {

    private final XACDMLService xacdmlService;
    private final XsltTransformationService xsltTransformationService;
    private final ExecutionService executionService;

    private static final String CLASS_NAME = "DynamicExperimentationProgramProxy";

    public Path generateSimulation(Long processId, String acdId) {
        try {
            // 1. Gera o conteúdo XACDML (XML)
            String xacdmlContent = xacdmlService.generateXACDMLContent(processId, acdId);

            // 2. Define o caminho onde o código Java será salvo (Source Code)
            String javaOutputPath = "projeto-tcc-api/target/generated-sources/" + CLASS_NAME + ".java";

            // 3. Define o caminho e nome do arquivo de SAÍDA DE RESULTADOS (.out)
            String outputDir = Paths.get(System.getProperty("user.dir"), "output").toAbsolutePath().toString();
            String outputFilename = "SimulationRun_" + acdId + ".out";

            // Cria o caminho absoluto completo
            String absoluteOutputFilePath = outputDir + File.separator + outputFilename;

            // 4. Configura os parâmetros para o XSLT
            Map<String, Object> transformationParams = new HashMap<>();
            // A chave 'outputFilePath' deve corresponder ao <xsl:param> no XSLT
            transformationParams.put("outputFilePath", absoluteOutputFilePath);

            String xsltPath = "/xslt/transform.xsl";

            // 5. Chama o método 'transform' sobrecarregado, passando o mapa de parâmetros.
            String generatedJavaCode = xsltTransformationService.transform(
                    xacdmlContent,
                    xsltPath,
                    javaOutputPath,
                    transformationParams
            );

            // 6. Compilação
            executionService.compile(
                    generatedJavaCode,
                    CLASS_NAME,
                    processId
            );

            // Retorna o caminho do arquivo Java gerado
            return Paths.get(javaOutputPath);

        } catch (Exception e) {
            System.err.println("A critical error occurred during simulation generation or execution.");
            e.printStackTrace();
            throw new RuntimeException("Failed to generate or execute the simulation code.", e);
        }
    }
}