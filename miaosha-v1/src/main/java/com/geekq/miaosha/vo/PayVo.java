package com.geekq.miaosha.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName PayVo.java
 * @Description TODO
 * @createTime 2022年03月26日 20:03:00
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PayVo {
    private Long id;

    private String token;
}
