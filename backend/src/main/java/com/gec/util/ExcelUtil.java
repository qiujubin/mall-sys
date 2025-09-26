package com.gec.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 */
public class ExcelUtil {
    
    /**
     * 解析Excel文件
     */
    public static List<List<String>> parseExcel(MultipartFile file) throws IOException {
        List<List<String>> data = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = null;
            
            // 根据文件扩展名选择不同的Workbook实现
            String filename = file.getOriginalFilename();
            if (filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("不支持的文件格式");
            }
            
            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            
            // 遍历所有行
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                
                // 遍历所有列
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    rowData.add(cellValue);
                }
                
                // 如果行不为空，添加到结果中
                if (!rowData.isEmpty() && !rowData.stream().allMatch(String::isEmpty)) {
                    data.add(rowData);
                }
            }
            
            workbook.close();
        }
        
        return data;
    }
    
    /**
     * 解析CSV文件
     */
    public static List<List<String>> parseCsv(MultipartFile file) throws IOException {
        List<List<String>> data = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, "UTF-8"))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                List<String> rowData = parseCsvLine(line);
                data.add(rowData);
            }
        }
        
        return data;
    }
    
    /**
     * 解析CSV行
     */
    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        result.add(currentField.toString().trim());
        return result;
    }
    
    /**
     * 获取单元格值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
