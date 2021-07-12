package com.im.imservice.resDto;



import lombok.Data;

/**
 * @ClassName BaseResponse
 * @Description 响应基类
 * @Author AN
 * @Date 2020/4/2 3:28 下午
 * @Version 1.0
 **/
@Data
public class BaseResponse {

    private String code;

    private String msg;

    private Object data;
}
