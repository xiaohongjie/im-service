package com.im.imservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuanning
 * @project mglive-api
 * @create 2020-01-16 10:10
 */
@Component
@Data
@ConfigurationProperties(prefix = "commonurl")
public class CommonProperties {
/*    //咪咕账号
    private String miguAccount;
    //用户密码
    private String miguPassword;
    //第三方视频云鉴权接口地址
    private String miguAuthUrl;
    //注册回调事件
    private String miguRegisterNotifyUrl;
    //流上下线
    private String publishCallbackUrl;
    //录制结束
    private String recCallbackUrl;
    //合片完成
    private String combineCallbackUrl;
    //创建频道
    private String miguCreateChannelUrl;
    //更新频道
    private String miguUpdateChannelUrl;
    //删除频道
    private String miguDeleteChannelUrl;
    //禁播
    private String miguForbidChannelUrl;
    //恢复接口
    private String miguResumeChannelUrl;
    //获取推流地址
    private String miguGetPushAddrUrl;
    //获取拉流地址
    private String miguGetPullAddrUrl;
    //敏感词过滤
    private String miguSensitiveWordUrl;
    //开始拉流直播
    private String miguBeginPullLiveUrl;
    //关闭拉流直播
    private String miguStopPullLiveUrl;
    //视频列表查询
    private String miguVideoQueryUrl;
    //视频合片
    private String miguCombieUrl;
    //文档转码回调地址
    private String callBackTransferUrl;
    //文档上传地址
    private String miguDocumentUploadUrl;
    //H5封面默认地址
    private String MiguDefaultH5;
    //视频剪裁和片接口地址
    private String videoSegMentUrl;
    //视频剪裁和片回调地址
    private String videoSegMentCallBackUrl;
    //查询视频信息接口地址
    private String queryVideoInfoUrl;
    //视频合片地址
    private String videoSyngamyUrl;
    //视频合片回调地址
    private String videoSygamyCallBackUrl;
    //开始录制
    private String startRecordUrl;
    //结束录制
    private String stopRecordUrl;*/
    //IMservice地址
    private String imServiceUrl;
    //IMappkey
    private  String imAppKey;
    //第三方系统地址以及端口
    private String thirdPartyUrl;
}
