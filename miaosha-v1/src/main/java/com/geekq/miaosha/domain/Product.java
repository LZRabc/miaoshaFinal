package com.geekq.miaosha.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName Product.java
 * @Description TODO
 * @createTime 2022年03月18日 15:08:00
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private long id;


    //产品价格
    private BigDecimal price;


    //产品描述
    private String description;

    //产品总份数
    private Integer totalNum;

    //存款产品名字
    private String name;

    //产品图片地址
    private String imgUrl;

    //风险等级（0为低，1为中，2为高）
    private Integer riskLevel;

    //产品期限
    private String cycle;

    //收益率参数名
    private String rateOfReturnName;

    //收益率参数值
    private BigDecimal rateOfReturnValue;

    //一级分类
    private String tag;

    //二级分类
    private String tag2;
}
