package com.im.imservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.im.imservice.config.CommonProperties;
import com.im.imservice.entity.ChatInfo;
import com.im.imservice.entity.GroupInfo;
import com.im.imservice.entity.LogMessageInfo;
import com.im.imservice.entity.UserInfo;
import com.im.imservice.reqDto.*;
import com.im.imservice.resDto.BaseResponse;
import com.im.imservice.service.IApiService;
import com.im.imservice.util.BaseResponseRetEnum;
import com.im.imservice.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


/**
 * @author dolyw.com
 * @date 2018/8/9 15:45
 */
@Service
@Slf4j
public class ApiServiceImpl implements IApiService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommonProperties commonProperties;

    public final static String IMSRVICEURL = "imsrviceurl";// im地址标识

    public final static String THIRDPARTYURL = "thirdpartyurl";// 第三方地址标识

    /**
     * 生成签名
     */
    @Override
    public String sign(TreeMap<String, String> params) {
        StringBuilder paramValues = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramValues.append(entry.getValue());
        }
        log.info("摘要:" + DigestUtils.md5Hex(paramValues.toString()));
        return DigestUtils.md5Hex(paramValues.toString());
    }

    /**
     * 请求插入日志
     */
    public void requestSave(LogMessageInfo logMessageInfo, JSONObject paramJson, String methodName, String url) {
        logMessageInfo.setLogId(UUID.randomUUID().toString());
        logMessageInfo.setCreatDate(new Date());
        if (url.equals(IMSRVICEURL)) {
            logMessageInfo.setCustomerIp(commonProperties.getImServiceUrl());
        } else {
            logMessageInfo.setCustomerIp(commonProperties.getThirdPartyUrl());
        }
        logMessageInfo.setInterfaceType("request");
        logMessageInfo.setStatus("0");
        logMessageInfo.setInterfaceName(methodName);
        logMessageInfo.setRequestString(paramJson.toString());
        mongoTemplate.save(logMessageInfo);
    }

    /**
     * 返回数据插入日志
     */
    public void responseSave(LogMessageInfo logMessageInfo, String result, String url) {
        JSONObject jsonResult = JSONObject.parseObject(result);
        logMessageInfo.setResponseCode(jsonResult.getString("code"));
        logMessageInfo.setResponseMsg(jsonResult.getString("msg"));
        logMessageInfo.setResponseString(jsonResult.getString("data"));
        if (url.equals(IMSRVICEURL)) {
            logMessageInfo.setCustomerIp(commonProperties.getImServiceUrl());
        } else {
            logMessageInfo.setCustomerIp(commonProperties.getThirdPartyUrl());
        }
        logMessageInfo.setInterfaceType("response");
        logMessageInfo.setCreatDate(new Date());
        logMessageInfo.setStatus("1");
        mongoTemplate.save(logMessageInfo);
    }

    @Override
    public String applyAdmin(ApplyAdminDto applyAdminDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        // 固定值 见文档
        params.put("zimSecret", "amberIMUser");
        String sign = sign(params);
        // 组装 json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("imId", applyAdminDto.getImId());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/applyAdmin", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String createGroup(CreateGroupDto createGroupDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("gpName", createGroupDto.getGpName());
        params.put("gpType", createGroupDto.getGpType());
        // 固定值 见文档
        params.put("zimSecret", "amberIMGroup");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("gpName", createGroupDto.getGpName());
        paramJson.put("gpLeaderId", createGroupDto.getGpLeaderId());
        paramJson.put("gpType", createGroupDto.getGpType());
        paramJson.put("startTime", createGroupDto.getStartTime());
        paramJson.put("endTime", createGroupDto.getEndTime());
        paramJson.put("autoOver", createGroupDto.getAutoOver());
        paramJson.put("gpNotification", createGroupDto.getGpNotification());
        paramJson.put("gpApplyOption", createGroupDto.getGpApplyOption());
        paramJson.put("uidList", createGroupDto.getUidList());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/createGroup", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
            JSONObject jsonResult = JSONObject.parseObject(result);
            //返回结果后添加数据到表存放记录后续补上
            if ("200".equals(jsonResult.getString("code"))) {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setId(UUID.randomUUID().toString());
                BeanUtils.copyProperties(createGroupDto, groupInfo);
                JSONObject json_test = jsonResult.getJSONObject("data");
                String gpid = json_test.getString("gpId");
                groupInfo.setGpId(gpid);
                mongoTemplate.save(groupInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String disbandGroup(DisbandGroupDto disbandGroupDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("gpId", disbandGroupDto.getGpId());
        params.put("opId", disbandGroupDto.getOpId());
        // 固定值 见文档
        params.put("zimSecret", "amberIMGroup");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("gpId", disbandGroupDto.getGpId());
        paramJson.put("opId", disbandGroupDto.getOpId());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), IMSRVICEURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/disbandGroup", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, THIRDPARTYURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String groupForbiddenWords(GroupStatusDto groupStatusDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("gpId", groupStatusDto.getGpId());
        params.put("opId", groupStatusDto.getOpId());
        params.put("shutUpTime", groupStatusDto.getShutUpTime().toString());
        // 固定值 见文档
        params.put("zimSecret", "amberIMGroup");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("gpId", groupStatusDto.getGpId());
        paramJson.put("opId", groupStatusDto.getOpId());
        paramJson.put("shutUpTime", groupStatusDto.getShutUpTime().toString());
        paramJson.put("uidList", groupStatusDto.getUidList());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/groupForbiddenWords", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String kickOutGroup(KickOutGroupDto kickOutGroupDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("gpId", kickOutGroupDto.getGpId());
        params.put("opId", kickOutGroupDto.getOpId());
        // 固定值 见文档
        params.put("zimSecret", "amberIMGroup");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("gpId", kickOutGroupDto.getGpId());
        paramJson.put("opId", kickOutGroupDto.getOpId());
        paramJson.put("uidList", kickOutGroupDto.getUidList());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {

            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/kickOutGroup", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String groupChat(GroupChatDto groupChatDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("gpId", groupChatDto.getGpId());
        params.put("mct", groupChatDto.getMct());
        params.put("mcType", groupChatDto.getMcType());
        params.put("msg", groupChatDto.getMsg());
        params.put("opId", groupChatDto.getOpId());
        params.put("timestamp", groupChatDto.getTimestamp().toString());
        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("gpId", groupChatDto.getGpId());
        paramJson.put("mct", groupChatDto.getMct());
        paramJson.put("mcType", groupChatDto.getMcType());
        paramJson.put("msg", groupChatDto.getMsg());
        paramJson.put("opId", groupChatDto.getOpId());
        paramJson.put("timestamp", groupChatDto.getTimestamp().toString());
        paramJson.put("atList", groupChatDto.getAtList());
        paramJson.put("extra", groupChatDto.getExtra());
        paramJson.put("quote", groupChatDto.getQuote());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/groupChat", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
            JSONObject jsonResult = JSONObject.parseObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setId(UUID.randomUUID().toString());
                BeanUtils.copyProperties(logMessageInfo, chatInfo);
                JSONObject json_test = jsonResult.getJSONObject("date");
                String msgId = json_test.getString("msgId");
                chatInfo.setMsgId(msgId);
                chatInfo.setChatType("1");//群聊
                mongoTemplate.save(chatInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String importUser(ImportUserDto importUserDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        // 固定值 见文档
        params.put("zimSecret", "amberIMUser");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("opId", importUserDto.getOpId());
        paramJson.put("accountList", importUserDto.getAccountList());
        // paramJson.put("zimSecret", "amberIMGroup");
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/importUser", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
            JSONObject jsonResult = JSONObject.parseObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                JSONArray resultarray = jsonResult.getJSONArray("data");
                for (int i = 0; i < resultarray.size(); i++) {
                    String json_test = resultarray.get(i).toString();
                    JSONObject resultuser = JSONObject.parseObject(json_test);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setId(UUID.randomUUID().toString());
                    BeanUtils.copyProperties(logMessageInfo, userInfo);
                    userInfo.setAccount(resultuser.getString("account"));//用户名
                    userInfo.setFaceUrl(resultuser.getString("faceUrl"));//头像地址
                    userInfo.setImId(Long.valueOf(resultuser.getString("imId")));//头像id
                    userInfo.setOpId(importUserDto.getOpId());
                    mongoTemplate.save(userInfo);
                }
            }
        } catch (Exception e) {
            log.error("导入用户数据失败");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String deleteUser(DelectUserDto delectUserDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        // 固定值 见文档
        params.put("zimSecret", "amberIMUser");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("opId", delectUserDto.getOpId());
        paramJson.put("imIdList", delectUserDto.getImIdList());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/deleteUser", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String singleChat(SingleChatDto singleChatDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("mct", singleChatDto.getMct());
        params.put("mcType", singleChatDto.getMcType());
        params.put("msg", singleChatDto.getMsg());
        params.put("opId", singleChatDto.getOpId());
        params.put("timestamp", singleChatDto.getTimestamp().toString());
        params.put("toId", singleChatDto.getToId());
        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("toId", singleChatDto.getToId());
        paramJson.put("mcType", singleChatDto.getMcType());
        paramJson.put("msg", singleChatDto.getMsg());
        paramJson.put("mct", singleChatDto.getMct());
        paramJson.put("timestamp", singleChatDto.getTimestamp());
        paramJson.put("extra", singleChatDto.getExtra());
        paramJson.put("opId", singleChatDto.getOpId());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/singleChat", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
            //解析返回数据
            JSONObject jsonResult = JSONObject.parseObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setId(UUID.randomUUID().toString());
                BeanUtils.copyProperties(logMessageInfo, chatInfo);
                JSONObject json_test = jsonResult.getJSONObject("date");
                String msgId = json_test.getString("msgId");
                chatInfo.setMsgId(msgId);
                chatInfo.setChatType("2");//私聊
                mongoTemplate.save(chatInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String joinGroup(JoinGroupDto joinGroupDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("gpId", joinGroupDto.getGpId());
        params.put("opId", joinGroupDto.getOpId());
        // 固定值 见文档
        params.put("zimSecret", "amberIMGroup");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("sign", sign);
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("gpId", joinGroupDto.getGpId());
        paramJson.put("opId", joinGroupDto.getOpId());
        paramJson.put("msg", joinGroupDto.getMsg());
        paramJson.put("taskId", joinGroupDto.getTaskId());
        paramJson.put("callback", joinGroupDto.getCallback());//回调地址重新设计
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/joinGroup", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public BaseResponse operateGroupInfo(OperateGroupInfoDto operateGroupInfoDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(BaseResponseRetEnum.SUCCESS.getCode());
        baseResponse.setMsg(BaseResponseRetEnum.SUCCESS.getMsg());
        /*// 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", joinGroupDto.getAppKey());
        params.put("gpId", joinGroupDto.getGpId());
        params.put("opId", joinGroupDto.getOpId());

        // 固定值 见文档
        params.put("zimSecret", "amberIMGroup");
        String sign = sign(params);*/
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("code", operateGroupInfoDto.getCode());
        paramJson.put("msg", operateGroupInfoDto.getMsg());
        paramJson.put("type", operateGroupInfoDto.getType());
        paramJson.put("gpId", operateGroupInfoDto.getGpId());
        paramJson.put("taskId", operateGroupInfoDto.getTaskId());
        paramJson.put("callback", operateGroupInfoDto.getCallback());//回调地址重新设计

        // paramJson.put("zimSecret", "amberIMGroup");
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), IMSRVICEURL);
            // result = HttpClientUtil.doPost("http://39.156.1.74:8088/callback/im/operateGroupInfo/"+"{"+commonProperties.getImAppKey()+"}", paramJson.toString());
            // JSONObject jsonResult = JSONObject.parseObject(result);
            // logMessageInfo.setResponseCode(jsonResult.getString("code"));
            // logMessageInfo.setResponseString(jsonResult.getString("msg"));
            //返回数据入库
            logMessageInfo.setResponseCode(BaseResponseRetEnum.SUCCESS.getCode());
            logMessageInfo.setResponseString(BaseResponseRetEnum.SUCCESS.getMsg());
            logMessageInfo.setCustomerIp(commonProperties.getThirdPartyUrl());
            logMessageInfo.setInterfaceType("response");
            logMessageInfo.setCreatDate(new Date());
            logMessageInfo.setStatus("1");
            mongoTemplate.save(logMessageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseResponse;
    }

//    @Override
//    public String getUserInfo(GetUserInfoDto getUserInfoDto) {
//        LogMessageInfo logMessageInfo = new LogMessageInfo();
//        /*// 算签名
//        TreeMap<String, String> params = new TreeMap<String, String>();
//        params.put("appKey", joinGroupDto.getAppKey());
//        params.put("gpId", joinGroupDto.getGpId());
//        params.put("opId", joinGroupDto.getOpId());
//
//        // 固定值 见文档
//        params.put("zimSecret", "amberIMGroup");
//        String sign = sign(params);*/
//        // 组装 参数json
//        JSONObject paramJson = new JSONObject();
//        paramJson.put("token", getUserInfoDto.getToken());
//        // paramJson.put("zimSecret", "amberIMGroup");
//        log.info("paramJson" + paramJson.toJSONString());
//        // 发请求
//        String result = null;
//        try {
//            //发送请求插入日志
//            logMessageInfo.setLogId(UUID.randomUUID().toString());
//            logMessageInfo.setCreatDate(new Date());
//            logMessageInfo.setCustomerIp("39.156.1.74");//可能需要调整
//            logMessageInfo.setInterfaceType("request");
//            logMessageInfo.setStatus("0");
//            logMessageInfo.setInterfaceName("getUserInfo");
//            logMessageInfo.setRequestString(paramJson.toString());
//            mongoTemplate.save(logMessageInfo);
//            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl()+"/authService/auth/getUserInfo", paramJson.toString());
//            JSONObject jsonResult = JSONObject.parseObject(result);
//            logMessageInfo.setResponseCode(jsonResult.getString("code"));
//            logMessageInfo.setResponseString(jsonResult.getString("msg"));
//            logMessageInfo.setInterfaceType("response");
//            logMessageInfo.setCreatDate(new Date());
//            logMessageInfo.setStatus("1");//已经返回状态
//            mongoTemplate.save(logMessageInfo);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    @Override
    public String dailyStatistics(DailyStatisticsDto dailyStatisticsDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("date", dailyStatisticsDto.getDate());
        params.put("timestamp", dailyStatisticsDto.getTimestamp().toString());
        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("sign", sign);
        paramJson.put("date", dailyStatisticsDto.getDate());
        paramJson.put("timestamp", dailyStatisticsDto.getTimestamp());
        // paramJson.put("zimSecret", "amberIMGroup");
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/user/dailyStatistics", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String statistics(StatisticsDto statisticsDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("timestamp", statisticsDto.getTimestamp().toString());
        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("sign", sign);
        paramJson.put("timestamp", statisticsDto.getTimestamp());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/user/statistics", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String info(ChatRoomInfoDto chatRoomInfoDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("chatRoomId", chatRoomInfoDto.getChatRoomId());
        params.put("timestamp", chatRoomInfoDto.getTimestamp().toString());
        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("sign", sign);
        paramJson.put("chatRoomId", chatRoomInfoDto.getChatRoomId());
        paramJson.put("timestamp", chatRoomInfoDto.getTimestamp());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/chatroom/info", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String user(ChatUserInfoDto chatUserInfoDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("chatRoomId", chatUserInfoDto.getChatRoomId());
        params.put("timestamp", chatUserInfoDto.getTimestamp().toString());
        params.put("uid", chatUserInfoDto.getUid());
        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("sign", sign);
        paramJson.put("chatRoomId", chatUserInfoDto.getChatRoomId());
        paramJson.put("timestamp", chatUserInfoDto.getTimestamp());
        paramJson.put("uid", chatUserInfoDto.getUid());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/chatroom/user", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String msgExport(MsgExportDto msgExportDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        // 算签名
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appKey", commonProperties.getImAppKey());
        params.put("endTime", String.valueOf(msgExportDto.getEndTime()));
        params.put("gpId", msgExportDto.getGpId());
        params.put("startTime", String.valueOf(msgExportDto.getStartTime()));

        // 固定值 见文档
        params.put("zimSecret", "amberIMMsg");
        String sign = sign(params);
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("appKey", commonProperties.getImAppKey());
        paramJson.put("sign", sign);
        paramJson.put("startTime", msgExportDto.getStartTime());
        paramJson.put("endTime", msgExportDto.getEndTime());
        paramJson.put("opId", msgExportDto.getOpId());
        paramJson.put("taskId", msgExportDto.getTaskId());
        paramJson.put("type", msgExportDto.getType());
        paramJson.put("callback", msgExportDto.getCallback());//路径
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        String result = null;
        try {
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), THIRDPARTYURL);
            //执行http请求
            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl() + "/api/im/msgExport", paramJson.toString());
            //返回数据入库
            responseSave(logMessageInfo, result, IMSRVICEURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public BaseResponse msgResultAsync(MsgResultAsyncDto msgResultAsyncDto) {
        LogMessageInfo logMessageInfo = new LogMessageInfo();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(BaseResponseRetEnum.SUCCESS.getCode());
        baseResponse.setMsg(BaseResponseRetEnum.SUCCESS.getMsg());
        // 组装 参数json
        JSONObject paramJson = new JSONObject();
        paramJson.put("fileName", commonProperties.getImAppKey());
        paramJson.put("taskId", msgResultAsyncDto.getTaskId());
        paramJson.put("type", msgResultAsyncDto.getType());
        log.info("paramJson" + paramJson.toJSONString());
        // 发请求
        //String result = null;
        try {
//            //发送请求插入日志
//            logMessageInfo.setLogId(UUID.randomUUID().toString());
//            logMessageInfo.setCreatDate(new Date());
//            logMessageInfo.setCustomerIp(commonProperties.getImServiceUrl());
//            logMessageInfo.setInterfaceType("request");
//            logMessageInfo.setStatus("0");
//            logMessageInfo.setInterfaceName("msgResultAsync");
//            logMessageInfo.setRequestString(paramJson.toString());
//            mongoTemplate.save(logMessageInfo);
            //发送请求插入日志
            requestSave(logMessageInfo, paramJson, Thread.currentThread().getStackTrace()[1].getMethodName(), IMSRVICEURL);
//            result = HttpClientUtil.doPost(commonProperties.getImServiceUrl()+"/im/msgResultAsync", paramJson.toString());
//            JSONObject jsonResult = JSONObject.parseObject(result);
//            logMessageInfo.setResponseCode(jsonResult.getString("code"));
//            logMessageInfo.setResponseString(jsonResult.getString("msg"));
            logMessageInfo.setResponseCode(BaseResponseRetEnum.SUCCESS.getCode());
            logMessageInfo.setResponseString(BaseResponseRetEnum.SUCCESS.getMsg());
            logMessageInfo.setCustomerIp(commonProperties.getThirdPartyUrl());
            logMessageInfo.setInterfaceType("response");
            logMessageInfo.setCreatDate(new Date());
            logMessageInfo.setStatus("1");
            mongoTemplate.save(logMessageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseResponse;
    }

//    @Override
//    public String test1() {
////        String msgCode = StringUtilz.getSixRandoString();
////        String con = MSG_CONTENT.replace("***", title);
////        String sharingAddress = MSG_URL.replace("###",url);
////        String content = con + "" + sharingAddress;
////        //logger.info("短信加密密钥：" + ConstantConfig.MiGu_MSGKEY);
////        String sign = DigestUtils.md5Hex("TrendSendSmsCode" + ConstantConfig.MiGu_MSGKEY);
////        logger.info("加密后的签名：" + sign);
//        // 算签名
//        TreeMap<String, String> params = new TreeMap<String, String>();
//        params.put("appKey", "e0b8141eb04f4e46abf66fbd3e0a8ae3");
//        // 固定值 见文档
//        params.put("zimSecret", "amberIMUser");
//        String sign = sign(params);
//        JSONObject paramMap = new JSONObject();
////        paramMap.put("mod", "Trend"); // 模块名
////        paramMap.put("act", "SendSmsCode"); // 方法名
////        paramMap.put("sign", sign);
////        paramMap.put("mer_code", ConstantConfig.MIGU_MerCode);
////        paramMap.put("phone", phone);
////        paramMap.put("content", content);
//
//        paramMap.put("appKey", "e0b8141eb04f4e46abf66fbd3e0a8ae3");
//        paramMap.put("imId", "123456");
////        paramMap.put("gpType", "1");
////        paramMap.put("zimSecret", "amberIMGroup");
//        paramMap.put("sign", sign);
//
//        String result = null;
//        try {
//            result = HttpClientUtil.doPost("http://39.156.1.74:8088/api/im/applyAdmin", paramMap.toString());
//            //logger.info("咪咕返回结果：" + result);
//            System.out.println("im返回结果：" + result);
//            JSONObject jsonResult = JSONObject.parseObject(result);
//            if (!"200".equals(jsonResult.getString("status"))) {
//                return jsonResult.toJSONString();
//            }
//            return jsonResult.toJSONString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
