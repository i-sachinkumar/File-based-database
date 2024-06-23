package com.example.mydatabase.service.impl;

import com.example.mydatabase.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private static final String SUCCESS_KEY = "SUCCESS";
    private static final String FAILURE_KEY = "FAILURE";
    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void incrementSuccessCounter() {
        redisTemplate.opsForValue().increment(SUCCESS_KEY, 1);
    }

    @Override
    public void incrementFailureCounter() {
        redisTemplate.opsForValue().increment(FAILURE_KEY, 1);
    }

    @Override
    public Integer getSuccessCount() {
        return redisTemplate.opsForValue().get(SUCCESS_KEY);
    }

    @Override
    public Integer getFailureCount() {
        return redisTemplate.opsForValue().get(FAILURE_KEY);
    }
}
