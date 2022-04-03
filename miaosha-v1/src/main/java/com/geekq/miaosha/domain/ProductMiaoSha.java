package com.geekq.miaosha.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductMiaoSha.java
 * @Description TODO
 * @createTime 2022年03月20日 10:57:00
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductMiaoSha {

    private Long id;
    private Long productId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
