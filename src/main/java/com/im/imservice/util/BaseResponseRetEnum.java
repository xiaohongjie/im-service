package com.im.imservice.util;

/**
 * @ClassName BaseResponseRetEnum
 * @Description TODO
 * @Author AN
 * @Date 2020/4/2 3:33 下午
 * @Version 1.0
 **/
public enum BaseResponseRetEnum {

    SUCCESS("0", "success"),
    FAILED("1", "failed");
    /**
     * 响应状态
     */
    private String code;
    /**
     * 返回消息
     */
    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    BaseResponseRetEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
