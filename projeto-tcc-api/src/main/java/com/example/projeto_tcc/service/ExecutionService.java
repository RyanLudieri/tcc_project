package com.example.projeto_tcc.service;

import org.codehaus.janino.SimpleCompiler;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.io.StringReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ExecutionService {

    public void compileAndExecute(String javaCode, String fullClassName) throws Exception {
        System.out.println("Iniciando compilação dinâmica com Janino...");

        Path outputDir = Paths.get("output");

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
            System.out.println("Diretório 'output' criado com sucesso.");
        }

        ClassLoader parentClassLoader = ExecutionService.class.getClassLoader();

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(parentClassLoader);

        compiler.cook(javaCode);

        Class<?> compiledClass = compiler.getClassLoader().loadClass(fullClassName);
        Object instance = compiledClass.getDeclaredConstructor().newInstance();
        Method executeMethod = compiledClass.getMethod("execute", float.class);
        float simulationDuration = 4800.0f;

        System.out.println("Compilação bem-sucedida. Executando o método 'execute'...");
        executeMethod.invoke(instance, simulationDuration);

        System.out.println("Execução da simulação dinâmica concluída com sucesso.");
    }
}