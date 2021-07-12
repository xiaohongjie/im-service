package com.im.imservice.controller;


import com.im.imservice.common.ResponseBean;
import com.im.imservice.reqDto.OperateGroupInfoDto;
import com.im.imservice.resDto.BaseResponse;
import com.im.imservice.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * callbackController
 *
 * @author xhj
 * @date 2018/8/29 15:45
 */
@Slf4j
@RestController
@RequestMapping("/callback")
@PropertySource("config.properties")
public class CallBackController {

    @Autowired
    private IApiService apiService;

//    /**
//     * 返回结果处理
//     */
//    private ResponseBean getCallbackBean(String result) {
//        JSONObject jsonResult = JSONObject.parseObject(result);
//        if (HttpStatus.OK.value() == Integer.parseInt(jsonResult.getString("code"))) {
//            log.info("接口返回" + result);
//            return new ResponseBean(HttpStatus.OK.value(), "成功( Success)", result);
//        } else {
//            return new ResponseBean(Integer.parseInt(jsonResult.getString("code")), "失败(error)", result);
//        }
//    }
    /**
     * 异步回调接口—加入群组
     */
    @PostMapping("/authService/auth/operateGroupInfo/{appKey}")
    public ResponseBean operateGroupInfo(@Valid @RequestBody OperateGroupInfoDto operateGroupInfoDto) {
        UUID logId = UUID.randomUUID();
        log.info("operateGroupInfoReq = [{}] {}", operateGroupInfoDto, logId);
        BaseResponse responseBean = apiService.operateGroupInfo(operateGroupInfoDto);
        return new ResponseBean(Integer.parseInt(responseBean.getCode()), responseBean.getMsg(),"");
    }

}