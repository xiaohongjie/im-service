package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author xhj
 * @project im
 * @create 2021-06-25 20:47
 */
@Data
public class InfoRes implements Serializable {

    private Number userTimes;//进入人次

    private Number onlineUser;//实时在线人数

    private Number onlinePeak;//在线峰值人数

    private Number totalMsg;//消息数

    private Number validMsg;//有效消息数（审核）

}
