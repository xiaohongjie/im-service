package com.im.imservice.reqDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
public class CreateGroupDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
//    //签名：md5
//    @NotBlank(message = "sign should not be blank")
    private String sign;
    //群组名称，最长30字节，使用 UTF-8 编码，1个汉字占3个字节
    @NotBlank(message = "gpName should not be blank")
    @Size(max = 30, message = "gpName is too long")
    private String gpName;
    //群主 ID，自动添加到群成员中。
    @NotBlank(message = "gpLeaderId should not be blank")
    private String gpLeaderId;
    //群组形态，1永久群（在线 离线）2临时会话群（有人数限制 一人同时多个 只推在线）3临时直播群（无人数限制 一人同时只能在一个 只能在线）
    @NotBlank(message = "gpType should not be blank")
    private String gpType;
    //群开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    //群结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    //群主离线后自动解散 只针对临时群,群主离线后自动解散 只针对临时群
    private Integer autoOver;
    //群公告，最长300字节，使用 UTF-8 编码，1个汉字占3个字节
    @Size(max = 300, message = "gpName is too long")
    private String gpNotification;
    //申请加群处理方式。1需要验证,2自由加入,3禁止加群
    private Integer gpApplyOption;
    //初始群成员ID列表，最多1000个
    private List<String> uidList;
    //固定值
    private String zimSecret;
}
