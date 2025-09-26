package com.gec.dto;

import lombok.Data;

import java.util.List;

/**
 * 导入结果DTO
 */
@Data
public class ImportResult {
    
    private int successCount;
    private int failCount;
    private List<ImportError> errors;
    
    public ImportResult() {
        this.successCount = 0;
        this.failCount = 0;
    }
    
    public ImportResult(int successCount, int failCount, List<ImportError> errors) {
        this.successCount = successCount;
        this.failCount = failCount;
        this.errors = errors;
    }
    
    @Data
    public static class ImportError {
        private int row;
        private String message;
        
        public ImportError(int row, String message) {
            this.row = row;
            this.message = message;
        }
    }
}
