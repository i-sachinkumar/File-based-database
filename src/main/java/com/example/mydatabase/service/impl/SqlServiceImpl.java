package com.example.mydatabase.service.impl;

import com.example.mydatabase.service.SqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SqlServiceImpl implements SqlService {
    private static final Logger log = LoggerFactory.getLogger(SqlServiceImpl.class);

    @Override
    public void createTable(String sql) throws IOException {
        // E.g: CREATE TABLE  myTable (col1 INTEGER , col2 STRING, col3 STRING)

        // split with white space in 3 part
        String[] keywords = sql.split(" +", 3);
        System.out.println("keyword: " + Arrays.toString(keywords));

        //get table name
        String tableName = keywords[2].substring(0, keywords[2].indexOf('(')).trim();

        //check if table already exists
        File file = new File(tableName + ".txt");
        if (file.exists()) throw new IOException(tableName + " already exist");

        System.out.println("creating table: " + tableName);

        //column information inside parenthesis
        String columns = sql.substring(sql.indexOf('(') + 1, sql.lastIndexOf(')')).trim();

        //split with comma with or without white space
        List<String> columnList = List.of(columns.split("( )*,( )*"));

        // meta data to be stored
        StringBuilder metaData = new StringBuilder();
        for (String s : columnList) {
            String[] column = s.split(" +");
            log.debug("column name: {}", column[0]);
            log.debug("column type: {}", column[1]);
            if (!column[1].equalsIgnoreCase("INTEGER") &&
                    !column[1].equalsIgnoreCase("STRING")) {
                throw new IOException("data type for column " + column[0] +
                        " is not accepted. It should be INTEGER or STRING");
            }
            metaData.append(column[0]).append(" ").append(column[1]).append("\n");
        }

        // write meta data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableName + "_metadata.txt"))) {
            writer.write(metaData.toString());
        }

        // inserting empty string in table file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableName + ".txt"))) {
            writer.write("");  // Create an empty file for the table data
        }
    }

    @Override
    public void insert(String sql) throws IOException {
        String[] keywords = sql.split(" +", 3);
        log.debug("keywords: {}",  Arrays.toString(keywords));

        String tableName = keywords[2].substring(0, keywords[2].indexOf('(')).trim();
        log.debug("inserting into table: {}", tableName);


        File metaDataFile = new File(tableName + "_metadata.txt");
        if (!metaDataFile.exists())
            throw new IOException(tableName + " does not exist");

        File dataFile = new File(tableName + ".txt");
        if (!dataFile.exists()) {
            //create one
            dataFile.createNewFile();
        }

        String normalizedSql = sql.toUpperCase();
        Pattern validatingPatter = Pattern.compile("INSERT( )+INTO( )+(.)+( )*\\(.*\\)( )*VALUES( )*\\(.*\\)");
        if (!validatingPatter.matcher(normalizedSql).find()) {
            throw new IOException("not a valid command");
        }

        //finding column name and corresponding value, using pattern-matcher
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(sql);

        /**
         * size will be 2
         * 1st entry: column names -> eg: col1, col2, col3
         * 2ns entry: corresponding values -> eg: 10, hi, there
         */
        List<String> datas = new ArrayList<>();
        while (matcher.find()) {
            //every thing inside parenthesis
            datas.add(matcher.group(1));
        }
        if (datas.size() != 2) throw new IOException("not a valid command");

        //meta data map: columnName -> dataType
        Map<String, String> metaDataMap = new LinkedHashMap<>();
        Scanner sc = new Scanner(metaDataFile);
        while (sc.hasNext()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) break;
            String[] column = line.split(" ", 2);
            metaDataMap.put(column[0], column[1]);
            System.out.println(column[0] + ", " + column[1]);
        }
        sc.close();

        String[] columnNames = datas.get(0).trim().split("( )*,( )*");
        String[] values = datas.get(1).trim().split("( )*,( )*");

        if (columnNames.length != values.length)
            throw new IOException("number of values doesn't match with number of columns provided");

        //values map: columnName -> value
        Map<String, String> valuesMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            System.out.println(metaDataMap.containsKey("col1"));
            System.out.println(columnNames[i].equals("col1"));
            System.out.println(columnNames[i]);
            if (!metaDataMap.containsKey(columnNames[i].trim()))
                throw new IOException(columnNames[i] + " is not found in table: " + tableName);
            valuesMap.put(columnNames[i], values[i]);
        }

        // new entry to be written in table
        StringBuilder valuesToWrite = new StringBuilder();
        for (Map.Entry<String, String> metaData : metaDataMap.entrySet()) {
            if (!valuesMap.containsKey(metaData.getKey())) {
                // value not available for this column, add empty value, move to next
                valuesToWrite.append(",");
                continue;
            }

            // else value is available
            if (metaData.getValue().equalsIgnoreCase("INTEGER")) {
                //integer to be added
                int value;
                try {
                    value = Integer.parseInt(valuesMap.get(metaData.getKey()));
                } catch (Exception e) {
                    throw new IOException("INTEGER is required for column: " + metaData.getKey());
                }
                valuesToWrite.append(value).append(",");
            } else {
                // string to be added
                valuesToWrite.append(valuesMap.get(metaData.getKey())).append(",");
            }
        }

        // finally write the entry
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile.getAbsolutePath(), true))) {
            //removing extra comma at last
            valuesToWrite.deleteCharAt(valuesToWrite.length() - 1);
            writer.write(valuesToWrite.toString());
            writer.newLine();
        }
    }
}
