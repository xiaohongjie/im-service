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
@Document(collection = "GroupInfo")
public class GroupInfo implements Serializable {
    private String id;//唯一标识
    private String gpId;//群组id
    private String gpName;//群组名称
    private String gpLeaderId;//群主 ID，自动添加到群成员中。
    private String gpType;//群组形态，1永久群（在线 离线）2临时会话群（有人数限制 一人同时多个 只推在线）3临时直播群（无人数限制 一人同时只能在一个 只能在线）
    private Long startTime; //群开始时间
    private Long endTime;//群结束时间
    private Integer autoOver;//群主离线后自动解散 只针对临时群 1采用此策略 0不采用才策略 默认不采用此策略
    private String gpNotification;//群公告，最长300字节，使用 UTF-8 编码，1个汉字占3个字节
    private Integer gpApplyOption;//申请加群处理方式。 1需要验证 2自由加入3禁止加群不填默认为 1（需要验证）仅当创建支持申请加群的 群组 时，该字段有效
}