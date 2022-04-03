package com.geekq.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: LZR
 * @date: 2022年04月03日 15:59
 */
@Data
public class MiaoshaRule {
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
