package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.DeliveryProcessRequestDTO;
import com.example.projeto_tcc.model.DeliveryProcess;
import com.example.projeto_tcc.model.ProcessElement;
import com.example.projeto_tcc.model.ProcessType;
import com.example.projeto_tcc.repository.DeliveryProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryProcessService {
    private final DeliveryProcessRepository repository;

    private final ProcessElementIndexService indexService;

    public DeliveryProcessService(DeliveryProcessRepository repository,
                                  ProcessElementIndexService indexService) {
        this.repository = repository;
        this.indexService = indexService;
    }

    public List<DeliveryProcess> getAll() {
        return repository.findAll();
    }

    public DeliveryProcess create(DeliveryProcessRequestDTO dto) {
        DeliveryProcess dp = new DeliveryProcess();
        int index = indexService.getNextIndex();
        dp.setName(dto.getName());
        dp.setIndex(index);
        dp.setModelInfo(dto.getModelInfo());
        dp.setType(ProcessType.DELIVERY_PROCESS);

        // Associa o pai, se houver
        if (dto.getParentId() != null) {
            ProcessElement parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Elemento pai não encontrado com ID: " + dto.getParentId()));
            dp.setParent(parent);
        }

        return repository.save(dp);

    }

    public DeliveryProcess getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public DeliveryProcess atualizar(Long id, DeliveryProcess updatedProcess) {
        DeliveryProcess existingProcess = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DeliveryProcess não encontrado"));

        // Atualiza os campos necessários
        existingProcess.setName(updatedProcess.getName());
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
