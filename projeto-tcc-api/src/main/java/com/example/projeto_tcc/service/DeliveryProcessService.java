package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.DeliveryProcess;
import com.example.projeto_tcc.repository.DeliveryProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryProcessService {
    private final DeliveryProcessRepository repository;

    public DeliveryProcessService(DeliveryProcessRepository repository) {
        this.repository = repository;
    }

    public List<DeliveryProcess> getAll() {
        return repository.findAll();
    }

    public DeliveryProcess create(DeliveryProcess deliveryProcess) {
        return repository.save(deliveryProcess);
    }

    public DeliveryProcess getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public DeliveryProcess atualizar(Long id, DeliveryProcess updatedProcess) {
        DeliveryProcess existingProcess = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DeliveryProcess não encontrado"));

        // Atualiza os campos necessários
        existingProcess.setIndex(updatedProcess.getIndex());
        existingProcess.setModelInfo(updatedProcess.getModelInfo());
        existingProcess.setType(updatedProcess.getType());

        return repository.save(existingProcess); // Salva as alterações
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("DeliveryProcess não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

}
