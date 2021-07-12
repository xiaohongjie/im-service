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
public class DisbandGroupDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
//    签名：md5
//    @NotBlank(message = "sign should not be blank")
    private String sign;
//    操作的群 ID
    @NotBlank(message = "gpId should not be blank")
    private String gpId;
    //解散群操作者ID
    @NotBlank(message = "opId should not be blank")
    private String opId;
}