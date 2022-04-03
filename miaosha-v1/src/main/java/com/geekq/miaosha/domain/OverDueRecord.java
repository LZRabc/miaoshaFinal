package com.geekq.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName RiskRule.java
 * @Description TODO
 * @createTime 2022年04月02日 20:46:00
 */
@Data
public class OverDueRecord {

    private  Integer id;

    private  Long userId;

    private Date loanTime;      //贷款时间

    private Date originalRepayTime;//规定还款时间

    private Date actualRepayTime;//实际还款时间

    private Double overdueAmount;//逾期金额



}
