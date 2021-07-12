package com.im.imservice.reqDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;


/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
public class GetUserInfoDto implements Serializable {
    //用户登录后传递给sdk的token
    @NotBlank(message = "token should not be blank")
    private String token;
}