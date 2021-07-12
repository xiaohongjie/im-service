package com.im.imservice.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
@Document(collection = "UserInfo")
public class UserInfo implements Serializable {
    private String id;//唯一标识

    private Long imId;//返回注册后的IM ID（与web注册生成算法一致）

    private String opId;// 导入用户操作者ID（必须是超管）

    private String account;//用户名

    private String nickName;//昵称

    private String faceUrl;//用户头像url
}