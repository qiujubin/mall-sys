package com.gec.common;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {
    
    private Integer current = 1;
    private Integer size = 10;
    private String keyword;
    private Long deptId;
    private Long categoryId;
    private Long brandId;
    
    public PageQuery() {}
    
    public PageQuery(Integer current, Integer size) {
        this.current = current;
        this.size = size;
    }
}
