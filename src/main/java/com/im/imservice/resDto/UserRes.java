package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author xhj
 * @project im
 * @create 2021-06-25 20:47
 */
@Data
public class UserRes implements Serializable {

    private String deviceSystem;//设备系统类型

    private String deviceModel;//设备机型

    private String browserKernelVersion;//web浏览器内核版本

    private Number survivalTime;//存活时间

}
