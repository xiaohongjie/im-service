package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-25 20:47
 */
@Data
public class GroupChatRes implements Serializable {

    private String msgId;//消息ID（Amber端生成的消息ID）

}
