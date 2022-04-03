package com.geekq.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName UserBankCardMsg.java
 * @Description TODO
 * @createTime 2022年04月01日 02:13:00
 */
@Data
public class BankCard {
    private Long userId;

    private String bankCardId;

    private Double balance;

    private Date createTime;

    private String password;
}
