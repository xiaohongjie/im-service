package com.im.imservice.reqDto.gpquote;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@Data
public class QuoteDto implements Serializable {
    //引用消息ID
    @NotBlank(message = "msgId should not be blank")
    private String msgId;
    //引用消息发送者ID
    @NotBlank(message = "uid should not be blank")
    private String uid;
    //引用消息内容 如果是2、3、4 msg为url
    @NotBlank(message = "msg should not be blank")
    private String msg;
    //引用消息样式（透传）
    @NotBlank(message = "mct should not be blank")
    private String mct;
    //1 文本（包括表情） 2 图片 3 视频 4 音频 图片 视频 音频上传 nas
    @NotBlank(message = "mcType should not be blank")
    private String mcType;
    //10708186
}