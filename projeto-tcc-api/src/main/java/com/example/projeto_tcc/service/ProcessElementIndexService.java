package com.example.projeto_tcc.service;

import org.springframework.stereotype.Service;

@Service
public class ProcessElementIndexService {

    private int currentIndex = 0;

    public synchronized int getNextIndex() {
        return currentIndex++;
    }
}
