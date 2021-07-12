package com.im.imservice.reqDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
public class JoinGroupDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
     //签名：md5
//    @NotBlank(message = "sign should not be blank")
    private String sign;
    //群组ID
    @NotBlank(message = "gpId should not be blank")
    private String gpId;
    //加入群组申请人员
    @NotBlank(message = "opId should not be blank")
    private String opId;
    //申请加群描述
    @NotBlank(message = "msg should not be blank")
    private String msg;
    //任务ID（用于回调透传）
    @NotBlank(message = "taskId should not be blank")
    private String taskId;
    //回调地址 例如：http://XXXX:8080//callback/im/operateGroupInfo/{appKey}
    @NotBlank(message = "callback should not be blank")
    private String callback;
}