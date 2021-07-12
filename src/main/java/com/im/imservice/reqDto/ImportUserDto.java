package com.im.imservice.reqDto;


import com.im.imservice.reqDto.gpquote.AccountDto;
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
public class ImportUserDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //导入用户操作者ID（必须是超管）
    @NotBlank(message = "opId should not be blank")
    private String opId;
    //签名
    private String sign;
    //三方账号
    @NotNull(message = "accountList should not be null")
    private List<AccountDto> accountList;
}