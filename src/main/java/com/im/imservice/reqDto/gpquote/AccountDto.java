package com.im.imservice.reqDto.gpquote;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@Data
public class AccountDto implements Serializable {
    //用户名
    @NotBlank(message = "account should not be blank")
    private String account;
    //昵称
    //@NotBlank(message = "nickName should not be blank")
    private String nickName;
    //用户头像url
    //@NotBlank(message = "faceUrl should not be blank")
    private String faceUrl;
}