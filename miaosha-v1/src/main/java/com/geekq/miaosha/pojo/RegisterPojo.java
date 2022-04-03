package com.geekq.miaosha.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName registerPojo.java
 * @Description TODO
 * @createTime 2022年03月18日 12:25:00
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPojo {

    private String username;

    private String password;

    private String verificationCode;

    private String tel;

    private String realName;

    private String idNum;
}
