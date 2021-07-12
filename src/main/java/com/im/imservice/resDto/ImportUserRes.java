package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-25 20:47
 */
@Data
public class ImportUserRes implements Serializable {

    private String nickName;//昵称

    private String faceUrl;//用户头像url

    private Long imId;//返回注册后的IM ID（与web注册生成算法一致）

    private String account;//用户名

}
