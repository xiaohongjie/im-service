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
public class MsgResultAsyncDto implements Serializable {
    //文件名称
    //@NotBlank(message = "fileName should not be blank")
    //private String fileName;
    //任务ID
    @NotBlank(message = "taskId should not be blank")
    private String taskId;
    //1 群组历史消息文件2 群名称与群ID映射关系文件3人员账号与IMId映射关系文件
    @NotNull(message = "type should not be null")
    private Integer type;
}