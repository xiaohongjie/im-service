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
public class OperateGroupInfoDto implements Serializable {
    //返回码（见返回状态码）
    @NotNull(message = "code should not be null")
    private Integer code;
    //返回信息
    @NotBlank(message = "msg should not be blank")
    private String msg;
    //2 加入群
    private Integer type;
    //群组ID
    @NotBlank(message = "gpId should not be blank")
    private String gpId;
    //任务ID
    @NotBlank(message = "taskId should not be blank")
    private String taskId;
    //回调地址
    @NotBlank(message = "callback should not be blank")
    private String callback;
}