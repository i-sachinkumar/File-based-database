package com.example.mydatabase.service;

import org.springframework.stereotype.Service;

@Service
public interface RedisService {
    void incrementSuccessCounter();
    void incrementFailureCounter();
    Integer getSuccessCount();
    Integer getFailureCount();
}
