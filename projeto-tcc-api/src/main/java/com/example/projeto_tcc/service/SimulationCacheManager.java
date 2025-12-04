package com.example.projeto_tcc.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia o estado compartilhado da simulação (cache) entre diferentes requisições 
 * e instâncias do ExecutionService. Esta classe é um Singleton (padrão @Service).
 */
@Service
public class SimulationCacheManager {

    // Armazena a classe compilada (Class<?>) por processId (para Execução)
    private final Map<Long, Class<?>> compiledClassesCache = new ConcurrentHashMap<>();

    // Armazena o código fonte gerado (String) por processId (para Preview)
    private final Map<Long, String> generatedCodeCache = new ConcurrentHashMap<>();

    // Armazena o ID do processo ativo globalmente. Usamos a chave 1L para 
    // representar o estado da aplicação.
    private final Map<Long, Long> activeProcessMap = new ConcurrentHashMap<>();

    // =========================================================================
    // 1. Métodos para Classe Compilada (Class<?>)
    // =========================================================================

    public void putCompiledClass(Long processId, Class<?> compiledClass) {
        compiledClassesCache.put(processId, compiledClass);
    }

    public Class<?> getCompiledClass(Long processId) {
        return compiledClassesCache.get(processId);
    }

    public void removeCompiledClass(Long processId) {
        compiledClassesCache.remove(processId);
    }

    // =========================================================================
    // 2. Métodos para Código Fonte (String)
    // =========================================================================

    public void putGeneratedJavaCode(Long processId, String javaCode) {
        generatedCodeCache.put(processId, javaCode);
    }

    public String getGeneratedJavaCode(Long processId) {
        return generatedCodeCache.get(processId);
    }

    public void removeGeneratedJavaCode(Long processId) {
        generatedCodeCache.remove(processId);
    }

    // =========================================================================
    // 3. Métodos para Processo Ativo (ActiveProcessId)
    // =========================================================================

    public void setActiveProcess(Long processId) {
        // Define o processo atual como ativo (usando 1L como chave única)
        activeProcessMap.put(1L, processId);
    }

    public Long getActiveProcessId() {
        // Recupera o ID do processo ativo
        return activeProcessMap.get(1L);
    }

    public void removeActiveProcess() {
        activeProcessMap.remove(1L);
    }
}