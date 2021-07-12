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
public class MsgExportDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //群组ID
    private String gpId;
    //开始时间（毫秒级）
    @NotNull(message = "startTime should not be null")
    private long startTime;
    //结束时间（毫秒级）
    @NotNull(message = "endTime should not be blank")
    private long endTime;
    //拉取消息操作人ID（必须是超管）
    @NotBlank(message = "opId should not be blank")
    private String opId;
    //任务ID
    @NotBlank(message = "taskId should not be blank")
    private String taskId;
    //1 群组历史消息文件 2 群名称与群ID映射关系文件3人员账号与IMId映射关系文件
    private Integer type;
    //回调地址 例如：http://xxx:xxx/api/im/msgResultAsync
    @NotBlank(message = "callback should not be blank")
    private String callback;
    //签名：md5
    private String sign;
}