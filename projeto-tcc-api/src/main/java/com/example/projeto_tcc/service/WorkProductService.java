package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.WorkProduct;
import com.example.projeto_tcc.repository.WorkProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkProductService {
    private final WorkProductRepository repository;

    private final ProcessElementIndexService indexService;

    public WorkProductService(WorkProductRepository repository,ProcessElementIndexService indexService) {
        this.repository = repository;
        this.indexService = indexService;
    }

    public WorkProduct createWorkProduct(WorkProduct workProduct) {
        int index = indexService.getNextIndex();
        workProduct.setIndex(index);
        return repository.save(workProduct);
    }

    public List<WorkProduct> getAllWorkProducts() {
        return repository.findAll();
    }

    public WorkProduct getWorkProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkProduct não encontrado"));
    }
}
