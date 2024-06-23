package com.example.mydatabase.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface SqlService {
    void createTable(String sql) throws IOException;
    void insert(String sql) throws IOException;
}