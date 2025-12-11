package com.example.projeto_tcc.util;

import com.example.projeto_tcc.service.SimulationCacheManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que garante a limpeza do ThreadLocal ao final de cada requisição HTTP.
 * Isso previne vazamento de memória e contaminação de dados entre requisições.
 */
@Component
public class ThreadLocalCleanupFilter extends OncePerRequestFilter {

    @Autowired
    private SimulationCacheManager cacheManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Processa a requisição normalmente
            filterChain.doFilter(request, response);
        } finally {
            // ✅ CORREÇÃO: ThreadLocal removido do CacheManager, a limpeza não é mais necessária.
            // O cache agora é gerenciado por processId explícito.
            // A chamada a cacheManager.removeActiveProcess() foi removida para evitar erro de compilação.
            System.out.println("🧹 [Filter] ThreadLocalCleanupFilter ignorado (ThreadLocal removido) (Thread: " + Thread.currentThread().getId() + ")");
        }
    }
}
