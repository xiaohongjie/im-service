package com.im.imservice.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

//import org.springframework.data.mongodb.core.mapping.Document;?\


/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
@Document(collection = "ChatInfo")
public class ChatInfo implements Serializable {

    private String id;//唯一标识

    private String msgId;//消息ID（Amber端生成的消息ID）

    private String gpId;//向哪个群组发送信息

    private Long timestamp;//消息发送时间

    private String opId;//向哪个群组发送信息

    private String msg;//消息内容 如果是2、3、4 msg为url

    private String mct;//消息样式（透传）

    private String mcType;//1 文本（包括表情） 2 图片 3 视频 4 音频图片 视频 音频上传 nas

    private String toId;//向谁发送信息

    private String extra;//扩展字段

    private String chatType;//聊天对象类型1群聊2私聊

    private List<String> atList;//@人员ID列表

}