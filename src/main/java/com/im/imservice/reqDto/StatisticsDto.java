package com.im.imservice.reqDto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-15 9:35
 */
@Data
public class StatisticsDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //签名
    private String sign;
    //时间戳
    @NotNull(message = "timestamp should not be null")
    private Long timestamp;
}
