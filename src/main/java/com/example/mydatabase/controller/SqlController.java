package com.example.mydatabase.controller;

import com.example.mydatabase.service.RedisService;
import com.example.mydatabase.service.SqlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class SqlController {

    private final SqlService sqlService;
    private final RedisService redisService;
    // Ex 1: CREATE TABLE tableName ( col1 INTEGER, col2 STRING,....).
    // Ex 2: INSERT into tableName (col1, col2,..) VALUES(,)...
    @PostMapping("/execute")
    public String executeSql(@RequestBody String sql) {
        try {
            System.out.println("sql: " + sql);
            String normalizedSql = sql.toUpperCase();

            //pattern: CREATE [WHITE SPACES] TABLE
            Pattern createPattern = Pattern.compile("CREATE( )+TABLE");

            //pattern: INSERT [WHITE SPACES] INTO
            Pattern insertPattern = Pattern.compile("INSERT( )+INTO");

            if (createPattern.matcher(normalizedSql).find()) {
                //create table
                sqlService.createTable(sql);
            } else if (insertPattern.matcher(normalizedSql).find()) {
                // insert into table
                sqlService.insert(sql);
            } else {
                throw new IOException("Unsupported SQL command");
            }
            redisService.incrementSuccessCounter();
            return "Command executed successfully";
        }  catch (Exception e) {
            redisService.incrementFailureCounter();
            return "Error executing command: " + e.getMessage();
        }
    }
}
