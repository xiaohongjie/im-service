package com.im.imservice.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;


/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
@Document(collection = "LogMessageInfo")
public class LogMessageInfo implements Serializable {

    private String logId;//日志唯一标识

    private String interfaceName;//请求名称

    private String requestString;//请求参数

    private String interfaceType;//请求类型

    private Date creatDate;//请求时间

    private String customerIp; //服务器ip

    private String responseString;//返回参数

    private String responseCode;//接口返回状态

    private String responseMsg;//接口返回描述

    private String status;//报文状态 0:未读取，1:已读取
}