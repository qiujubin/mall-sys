package com.gec.domain.dto;

import com.gec.domain.entity.GoodsAttr;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规格参数DTO，包含分组名称
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsAttrDTO extends GoodsAttr {
    
    private String groupName; // 分组名称
}
