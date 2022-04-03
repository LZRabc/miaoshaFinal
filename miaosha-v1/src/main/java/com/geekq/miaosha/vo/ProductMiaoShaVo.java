package com.geekq.miaosha.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geekq.miaosha.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductMiaoShaVo.java
 * @Description TODO
 * @createTime 2022年03月19日 20:53:00
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductMiaoShaVo {

    private long id;

    private long miaoshaId;
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

    private Integer stockCount;

    private Boolean permission;

    private Double miaoshaPrice;
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @JsonIgnore
    private   Integer overduePeriod;//近几年
    @JsonIgnore
    private Integer frequency ;//次数
    @JsonIgnore
    private Double miniAmount; //最少逾期金额
    @JsonIgnore
    private Integer gracePeriod; //还款宽限期
    @JsonIgnore
    private Integer workStatus;//就业情况0-失业 1-工作
    @JsonIgnore
    private Integer untrustPerson;//是否失信人员 0-失信 1-可信
    @JsonIgnore
    private Integer age;//最小年纪
}
