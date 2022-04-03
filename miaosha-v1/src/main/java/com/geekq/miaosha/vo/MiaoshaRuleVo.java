package com.geekq.miaosha.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: LZR
 * @date: 2022年04月03日 16:14
 */
@Data
public class MiaoshaRuleVo {
    //可以不用传入id
    int id;
    int overduePeriod;
    int frequency;
    //最低逾期金额
    double miniAmount;
    //还款宽限期
    int gracePeriod;
    //此条规则是否生效
    int status;
    Date createTime;
    int workStatus;
    int untrustPerson;
    //最低能参加的年龄
    int age;
}
