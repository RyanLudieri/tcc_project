// Crie este novo arquivo: GeneratorObserverRepository.java
package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.GeneratorObserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratorObserverRepository extends JpaRepository<GeneratorObserver, Long> {
}
