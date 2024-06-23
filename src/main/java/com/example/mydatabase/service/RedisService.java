package com.example.mydatabase.service;

import org.springframework.stereotype.Service;

@Service
public interface RedisService {
    public void incrementSuccessCounter();
    public void incrementFailureCounter();
    public Integer getSuccessCount();
    public Integer getFailureCount();
}
