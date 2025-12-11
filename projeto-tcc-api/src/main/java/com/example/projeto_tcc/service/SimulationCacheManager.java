package com.example.projeto_tcc.service;

import com.example.projeto_tcc.util.SimulationRunContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia o estado compartilhado da simulação (cache) entre diferentes requisições.
 * Esta classe é um Singleton (padrão @Service do Spring).
 */
@Service
public class SimulationCacheManager {

    // Armazena a classe compilada (Class<?>) por processId (para Execução)
    private final Map<Long, Class<?>> compiledClassesCache = new ConcurrentHashMap<>();
    private final Map<Long, String> generatedCodeCache = new ConcurrentHashMap<>();
    private final Map<Long, SimulationRunContext> runContextCache = new ConcurrentHashMap<>();

    // ✅ CORREÇÃO: Removido ThreadLocal e substituído por Map thread-safe
    // Agora cada thread/requisição pode ter seu próprio processo ativo
    private final ThreadLocal<Long> activeProcessIdLocal = ThreadLocal.withInitial(() -> null);

    public void setActiveProcess(Long processId) {
        System.out.println("🔵 [CacheManager] setActiveProcess chamado com processId: " + processId + " (Thread: " + Thread.currentThread().getId() + ")");
        activeProcessIdLocal.set(processId);
    }

    public Long getActiveProcessId() {
        Long processId = activeProcessIdLocal.get();
        System.out.println("🔵 [CacheManager] getActiveProcessId retornando: " + processId + " (Thread: " + Thread.currentThread().getId() + ")");
        return processId;
    }

    public void removeActiveProcess() {
        System.out.println("🔵 [CacheManager] removeActiveProcess chamado (Thread: " + Thread.currentThread().getId() + ")");
        // É crucial remover o valor ao final da requisição para evitar vazamento de memória e contaminação
        activeProcessIdLocal.remove();
    }


    // =========================================================================
    // 1. Métodos para Classe Compilada (Class<?>)
    // =========================================================================

    public void putCompiledClass(Long processId, Class<?> compiledClass) {
        System.out.println("🟢 [CacheManager] putCompiledClass: processId=" + processId + ", ClassLoader hash=" + compiledClass.getClassLoader().hashCode());
        compiledClassesCache.put(processId, compiledClass);
    }

    public Class<?> getCompiledClass(Long processId) {
        Class<?> clazz = compiledClassesCache.get(processId);
        if (clazz != null) {
            System.out.println("🟢 [CacheManager] getCompiledClass: processId=" + processId + ", ClassLoader hash=" + clazz.getClassLoader().hashCode());
        } else {
            System.out.println("🔴 [CacheManager] getCompiledClass: processId=" + processId + " NÃO ENCONTRADO!");
        }
        return clazz;
    }

    public void removeCompiledClass(Long processId) {
        System.out.println("🟡 [CacheManager] removeCompiledClass: processId=" + processId);
        compiledClassesCache.remove(processId);
    }

    // =========================================================================
    // 2. Métodos para Código Fonte (String)
    // =========================================================================

    public void putGeneratedJavaCode(Long processId, String javaCode) {
        System.out.println("🟢 [CacheManager] putGeneratedJavaCode: processId=" + processId + ", tamanho=" + javaCode.length());
        generatedCodeCache.put(processId, javaCode);
    }

    public String getGeneratedJavaCode(Long processId) {
        String code = generatedCodeCache.get(processId);
        System.out.println("🟢 [CacheManager] getGeneratedJavaCode: processId=" + processId + ", encontrado=" + (code != null));
        return code;
    }

    public void removeGeneratedJavaCode(Long processId) {
        System.out.println("🟡 [CacheManager] removeGeneratedJavaCode: processId=" + processId);
        generatedCodeCache.remove(processId);
    }

    // =========================================================================
    // 4. ✅ MÉTODOS NOVOS: Para o Contexto da Execução (SimulationRunContext)
    // =========================================================================

    public void putRunContext(Long processId, SimulationRunContext context) {
        System.out.println("🟢 [CacheManager] putRunContext: processId=" + processId);
        runContextCache.put(processId, context);
    }

    public SimulationRunContext getRunContext(Long processId) {
        SimulationRunContext context = runContextCache.get(processId);
        System.out.println("🟢 [CacheManager] getRunContext: processId=" + processId + ", encontrado=" + (context != null));
        return context;
    }

    public void removeRunContext(Long processId) {
        System.out.println("🟡 [CacheManager] removeRunContext: processId=" + processId);
        runContextCache.remove(processId);
    }

    /**
     * Remove completamente todos os dados em cache associados a um processo.
     * Deve ser chamado antes de uma nova compilação para garantir que as referências
     * antigas (especialmente da classe compilada) sejam liberadas.
     */
    public void clearCacheForProcess(Long processId) {
        if (processId == null) return;

        System.out.println("🔴 [CacheManager] ========================================");
        System.out.println("🔴 [CacheManager] LIMPANDO CACHE COMPLETO PARA processId: " + processId);
        System.out.println("🔴 [CacheManager] ========================================");

        compiledClassesCache.remove(processId);
        generatedCodeCache.remove(processId);
        runContextCache.remove(processId);

        // ✅ IMPORTANTE: Força garbage collection do ClassLoader antigo
        // O GC é crucial para liberar a memória do ClassLoader anterior,
        // permitindo que o novo código seja carregado corretamente.
        System.gc();

        System.out.println("🔴 [CacheManager] Cache limpo com sucesso para processId: " + processId);
    }
}
