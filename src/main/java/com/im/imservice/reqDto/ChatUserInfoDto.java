package com.im.imservice.reqDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
public class ChatUserInfoDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //签名
    private String sign;
    //时间戳
    @NotNull(message = "timestamp should not be null")
    private Long timestamp;
    //聊天室ID
    @NotBlank(message = "chatRoomId should not be blank")
    private String chatRoomId;
    //用户ID
    @NotBlank(message = "uid should not be blank")
    private String uid;
}
