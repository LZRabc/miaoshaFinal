package com.geekq.miaosha.config;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName RiskRuleConfig.java
 * @Description TODO
 * @createTime 2022年04月02日 20:09:00
 */
@Data
public class RiskRuleConfig {

    private   Integer overduePeriod;//近几年

    private Integer frequency ;//次数

    private Double miniAmount; //最少逾期金额

    private Integer gracePeriod; //还款宽限期

    private Integer workStatus;//就业情况0-失业 1-工作

    private Integer untrustPerson;//是否失信人员 0-失信 1-可信

    private Integer age;//最小年纪


    public RiskRuleConfig() {
        this.age = 18;
        this.gracePeriod = 3;
        this.miniAmount = 1000.00;
        this.overduePeriod = 3;
        this.workStatus = 1;
        this.frequency = 2;
    }
}
