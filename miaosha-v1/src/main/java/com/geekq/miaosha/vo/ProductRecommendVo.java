package com.geekq.miaosha.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductRecommendVo.java
 * @Description TODO
 * @createTime 2022年03月20日 13:37:00
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRecommendVo {
    //产品价格
    private BigDecimal price;

    //产品描述
    private String description;

    //存款产品名字
    private String name;

    //风险等级（0为低，1为中，2为高）
    private Integer riskLevel;

    //产品期限
    private String cycle;

    //收益率参数名
    private String rateOfReturnName;
    //收益率参数值
    private BigDecimal rateOfReturnValue;

    private Long id;

}
