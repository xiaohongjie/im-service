package com.im.imservice.reqDto;



import com.im.imservice.reqDto.gpquote.QuoteDto;
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
public class GroupChatDto implements Serializable {
    //app标识（Amber提供）
    //@NotBlank(message = "appKey should not be blank")
    //private String appKey;
    //    //签名：md5
//    @NotBlank(message = "sign should not be blank")
    private String sign;
    //向哪个群组发送信息
    @NotBlank(message = "gpId should not be blank")
    private String gpId;
    //消息发送时间
    @NotNull(message = "timestamp should not be null")
    private Long timestamp;
    //群聊发送人员ID
    @NotBlank(message = "opId should not be blank")
    private String opId;
    //选填	@人员ID列表
    private List<String> atList;
    //消息内容 如果是2、3、4 msg为url
    @NotBlank(message = "msg should not be blank")
    private String msg;
    //消息样式（透传）
    @NotBlank(message = "mct should not be blank")
    private  String mct;
    //1 文本（包括表情） 2 图片 3 视频 4 音频  图片 视频 音频上传 nas
    @NotBlank(message = "mcType should not be blank")
    private String mcType;
    //扩展字段
    private String extra;
    //引用的消息信息对象
    private QuoteDto quote;

}
