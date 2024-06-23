package com.example.mydatabase.controller;


import com.example.mydatabase.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @GetMapping("/redis/SUCCESS")
    public Object getSuccessCount() {
        return redisService.getSuccessCount();
    }

    @GetMapping("/redis/FAILURE")
    public Object getFailureCount() {
        return redisService.getFailureCount();
    }
}
