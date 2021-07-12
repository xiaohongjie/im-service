package com.im.imservice.reqDto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
public class KickOutGroupDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //签名：md5
    //@NotBlank(message = "sign should not be blank")
    private String sign;
    //操作的群 ID
    @NotBlank(message = "gpId should not be blank")
    private String gpId;
    //需要禁言的用户ID列表，最多支持1000个帐号
    @NotNull(message = "uidList should not be Null")
    private List<String> uidList;
    //需禁言时间，单位为秒，为0时表示取消禁言，为-1永久禁言,默认为0
    private Integer shutUpTime;
    //操作者ID
    @NotBlank(message = "opId should not be blank")
    private  String opId;

}