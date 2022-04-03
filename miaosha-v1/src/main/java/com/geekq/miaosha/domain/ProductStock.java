package com.geekq.miaosha.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductStock.java
 * @Description TODO
 * @createTime 2022年03月18日 15:57:00
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStock {
    private Long id;


    //余量
    private Integer stock;

    //产品id
    private  Long productId;
}
