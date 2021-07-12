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
public class ApplyAdminDto implements Serializable {
    //app标识（Amber提供）
//    @Value("${common:imAppKey}")
//    @NotBlank(message = "appKey should not be blank")
//    private String appKey;
    //imId（超管用户最大50个）
    @NotBlank(message = "imId should not be blank")
    private String imId;
    //签名：md5
    private String sign;
}