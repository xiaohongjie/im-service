package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2019-12-12 20:47
 */
@Data
public class GetUserInfoRes implements Serializable {

    private String account;//用户账号

    private String nickname;//用户昵称

    private String picurl;//头像url

    private String area;//区域

    private Integer sex;//性别0：未知1：男2：女

}
