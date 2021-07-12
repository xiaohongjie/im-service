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
public class SingleChatDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //签名：md5
//    @NotBlank(message = "sign should not be blank")
    private String sign;
    //向谁发送信息
    @NotBlank(message = "toId should not be blank")
    private String toId;
    //	1 文本（包括表情） 2 图片 3 视频 4 音频  图片 视频 音频上传 nas
    @NotBlank(message = "mcType should not be blank")
    private String mcType;
    //消息内容 如果是2、3、4 msg为url
    @NotBlank(message = "msg should not be blank")
    private String msg;
    //消息样式（透传）
    @NotBlank(message = "mct should not be blank")
    private String mct;
    //消息发送时间
    @NotNull(message = "timestamp should not be null")
    private Long  timestamp;
    //扩展字段
    private String extra;
    //私聊发送人员ID
    @NotBlank(message = "opId should not be blank")
    private String  opId;
}