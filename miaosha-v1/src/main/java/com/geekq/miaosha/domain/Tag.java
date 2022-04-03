package com.geekq.miaosha.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName Tags.java
 * @Description TODO
 * @createTime 2022年03月18日 22:18:00
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    private Long id;

    private String name;

    private String description;

    private String tag;
}
