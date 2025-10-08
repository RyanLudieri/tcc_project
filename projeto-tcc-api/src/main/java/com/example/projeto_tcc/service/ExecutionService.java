package com.example.projeto_tcc.service;

import org.codehaus.janino.SimpleCompiler;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;

@Service
public class ExecutionService {

    public void compileAndExecute(String javaCode, String fullClassName) throws Exception {
        System.out.println("Iniciando compilação dinâmica com Janino...");

        // 1. Obtém o ClassLoader da thread atual. Este ClassLoader "conhece"
        //    todas as classes da sua aplicação Spring Boot.
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();

        // 2. Cria o compilador Janino, passando o nosso ClassLoader como "pai".
        //    Agora, o compilador herdará todo o conhecimento do nosso app.
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(parentClassLoader);

        // O resto do código permanece o mesmo.
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