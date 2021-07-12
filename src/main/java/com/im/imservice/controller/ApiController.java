package com.im.imservice.controller;

import com.alibaba.fastjson.JSONObject;

import com.im.imservice.common.ResponseBean;
import com.im.imservice.reqDto.*;
import com.im.imservice.resDto.BaseResponse;
import com.im.imservice.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;


/**
 * UserController
 *
 * @author xhj
 * @date 2018/8/29 15:45
 */
@Slf4j
@RestController
@RequestMapping("/api")
@PropertySource("config.properties")
public class ApiController {

    @Autowired
    private IApiService apiService;

    /**
     * 返回结果处理
     */
    private ResponseBean getResponseBean(String result) {
        JSONObject jsonResult = JSONObject.parseObject(result);
        if (HttpStatus.OK.value() == Integer.parseInt(jsonResult.getString("code"))) {
            log.info("接口返回" + result);
            return new ResponseBean(HttpStatus.OK.value(), "成功( Success)", result);
        } else {
            return new ResponseBean(Integer.parseInt(jsonResult.getString("code")), "失败(error)", result);
        }
    }

    /**
     * 申请超管用户
     */
    @PostMapping("/im/applyAdmin")
    public ResponseBean applyAdmin(@Valid @RequestBody ApplyAdminDto applyAdminDto) {
        UUID logId = UUID.randomUUID();
        log.info("applyAdminReq = [{}] {}", applyAdminDto, logId);
        String result = apiService.applyAdmin(applyAdminDto);
        return getResponseBean(result);
    }


    /**
     * 创建群组
     */
    @PostMapping("/im/createGroup")
    public ResponseBean createGroup(@Valid @RequestBody CreateGroupDto createGroupDto) {
        UUID logId = UUID.randomUUID();
        log.info("createGroupReq = [{}] {}", createGroupDto, logId);
        String result = apiService.createGroup(createGroupDto);
        return getResponseBean(result);
    }

    /**
     * 解散群组
     */
    @PostMapping("/im/disbandGroup")
    public ResponseBean disbandGroup(@Valid @RequestBody DisbandGroupDto disbandGroupDto) {
        UUID logId = UUID.randomUUID();
        log.info("disbandGroupReq = [{}] {}", disbandGroupDto, logId);
        String result = apiService.disbandGroup(disbandGroupDto);
        return getResponseBean(result);
    }

    /**
     * 群禁言以及群解禁—批量
     */
    @PostMapping("/im/groupForbiddenWords")
    public ResponseBean groupForbiddenWords(@Valid @RequestBody GroupStatusDto groupStatusDto) {
        UUID logId = UUID.randomUUID();
        log.info("groupForbiddenWordsReq = [{}] {}", groupStatusDto, logId);
        String result = apiService.groupForbiddenWords(groupStatusDto);
        return getResponseBean(result);
    }

    /**
     * 踢出群组
     */
    @PostMapping("/im/kickOutGroup")
    public ResponseBean kickOutGroup(@Valid @RequestBody KickOutGroupDto kickOutGroupDto) {
        UUID logId = UUID.randomUUID();
        log.info("kickOutGroupReq = [{}] {}", kickOutGroupDto, logId);
        String result = apiService.kickOutGroup(kickOutGroupDto);
        return getResponseBean(result);
    }

    /**
     * 群聊
     */
    @PostMapping("/im/groupChat")
    public ResponseBean groupChat(@Valid @RequestBody GroupChatDto groupChatDto) {
        UUID logId = UUID.randomUUID();
        log.info("groupChatReq = [{}] {}", groupChatDto, logId);
        String result = apiService.groupChat(groupChatDto);
        return getResponseBean(result);
    }

    /**
     * IM导入用户
     */
    @PostMapping("/im/importUser")
    public ResponseBean importUser(@Valid @RequestBody ImportUserDto importUserDto) {
        UUID logId = UUID.randomUUID();
        log.info("importUserReq = [{}] {}", importUserDto, logId);
        String result = apiService.importUser(importUserDto);
        return getResponseBean(result);
    }

    /**
     * IM删除用户
     */
    @PostMapping("/im/deleteUser")
    public ResponseBean deleteUser(@Valid @RequestBody DelectUserDto delectUserDto) {
        UUID logId = UUID.randomUUID();
        log.info("deleteUserReq = [{}] {}", delectUserDto, logId);
        String result = apiService.deleteUser(delectUserDto);
        return getResponseBean(result);
    }

    /**
     * 私聊
     */
    @PostMapping("/im/singleChat")
    public ResponseBean singleChat(@Valid @RequestBody SingleChatDto singleChatDto) {
        UUID logId = UUID.randomUUID();
        log.info("singleChatReq = [{}] {}", singleChatDto, logId);
        String result = apiService.singleChat(singleChatDto);
        return getResponseBean(result);
    }

    /**
     * 加入群组
     */
    @PostMapping("/im/joinGroup")
    public ResponseBean joinGroup(@Valid @RequestBody JoinGroupDto joinGroupDto) {
        UUID logId = UUID.randomUUID();
        log.info("joinGroupReq = [{}] {}", joinGroupDto, logId);
        String result = apiService.joinGroup(joinGroupDto);
        return getResponseBean(result);
    }


    /**
     * 异步回调接口—加入群组
     */
/*    @PostMapping("/authService/auth/operateGroupInfo")
    public ResponseBean operateGroupInfo(@Valid @RequestBody OperateGroupInfoDto operateGroupInfoDto) {
        UUID logId = UUID.randomUUID();
        log.info("operateGroupInfoReq = [{}] {}", operateGroupInfoDto, logId);

        String result = apiService.operateGroupInfo(operateGroupInfoDto);
        return getResponseBean(result);
    }*/

    /**
     * 三方授权用户信息
     */
//    @PostMapping("im/getUserInfo")
//    public ResponseBean getUserInfo(@Valid @RequestBody GetUserInfoDto getUserInfoDto) {
//        UUID logId = UUID.randomUUID();
//        log.info("getUserInfoReq = [{}] {}", getUserInfoDto, logId);
//
//        String result = apiService.getUserInfo(getUserInfoDto);
//        return getResponseBean(result);
//    }

    /**
     * 单日用户统计信息
     */
    @PostMapping("/im/user/dailyStatistics")
    public ResponseBean dailyStatistics(@Valid @RequestBody DailyStatisticsDto dailyStatisticsDto) {
        UUID logId = UUID.randomUUID();
        log.info("dailyStatisticsReq = [{}] {}", dailyStatisticsDto, logId);
        String result = apiService.dailyStatistics(dailyStatisticsDto);
        return getResponseBean(result);
    }

    /**
     * 用户统计信息
     */
    @PostMapping("/im/user/statistics")
    public ResponseBean statistics(@Valid @RequestBody StatisticsDto statisticsDto) {
        UUID logId = UUID.randomUUID();
        log.info("statisticsReq = [{}] {}", statisticsDto, logId);
        String result = apiService.statistics(statisticsDto);
        return getResponseBean(result);
    }

    /**
     * 聊天室信息查询
     */
    @PostMapping("/im/chatroom/info")
    public ResponseBean info(@Valid @RequestBody ChatRoomInfoDto chatRoomInfoDto) {
        UUID logId = UUID.randomUUID();
        log.info("infoReq = [{}] {}", chatRoomInfoDto, logId);
        String result = apiService.info(chatRoomInfoDto);
        return getResponseBean(result);
    }

    /**
     * 聊天室内用户信息查询
     */
    @PostMapping("/im/chatroom/user")
    public ResponseBean user(@Valid @RequestBody ChatUserInfoDto chatUserInfoDto) {
        UUID logId = UUID.randomUUID();
        log.info("userReq = [{}] {}", chatUserInfoDto, logId);
        String result = apiService.user(chatUserInfoDto);
        return getResponseBean(result);
    }


    /**
     * 群组历史聊天消息、群名称与群id、人员账号与IMID映射关系导出
     */
    @PostMapping("/im/msgExport")
    public ResponseBean msgExport(@Valid @RequestBody MsgExportDto msgExportDto) {
        UUID logId = UUID.randomUUID();
        log.info("msgExportReq = [{}] {}", msgExportDto, logId);
        String result = apiService.msgExport(msgExportDto);
        return getResponseBean(result);
    }


    /**
     * 文件名称异步回调接口（1.17的回调结果）
     */
    @PostMapping("/im/msgResultAsync")
    public ResponseBean msgResultAsync(@Valid @RequestBody MsgResultAsyncDto msgResultAsyncDto) {
        UUID logId = UUID.randomUUID();
        log.info("msgResultAsyncReq = [{}] {}", msgResultAsyncDto, logId);//测试提交
        BaseResponse responseBean = apiService.msgResultAsync(msgResultAsyncDto);
        return new ResponseBean(Integer.parseInt(responseBean.getCode()), responseBean.getMsg(),"");
    }


//    @PostMapping("/test1")
//    public ResponseBean test1() {
//        String appKey = "c12abbf4f3bf3ece9c5c2b27bce343fa";
//        String gpId = "1";
//        String gpName = "testGroup";
//        String gpType = "1";
//        String opId = "1";
//        // 算签名
//        TreeMap<String, String> params = new TreeMap<String, String>();
//        params.put("appKey", appKey);
//        params.put("gpName", gpName);
//        params.put("gpType", gpType);
//        // 固定值 见文档
//        params.put("zimSecret", "amberIMGroup");
//        String sign = apiService.sign(params);
//        // 组装 json
//        JSONObject paramJson = new JSONObject();
//        paramJson.put("appKey", appKey);
//        paramJson.put("gpName", "testGroup");
//        paramJson.put("gpType", "1");
//        paramJson.put("zimSecret", "amberIMGroup");
//        paramJson.put("sign", sign);
        // 发请求
//        String result = null;
//        HttpClient client = HttpClients.createDefault();
//        URIBuilder builder = new URIBuilder();
//        URI uri = null;
//        try {
//            uri = builder.setScheme("http").setHost("39.156.1.74:8088").setPath("/api/im/createGroup").build();
//            HttpPost post = new HttpPost(uri);
//            // 设置请求头
//            post.setHeader("Content-Type", "application/json"); // 设置请求体
//            post.setEntity(new StringEntity(paramJson.toString(), Charset.forName("UTF-8")));
//            // 获取返回信息
//            HttpResponse response = client.execute(post);
//            result = response.toString();
//        } catch (Exception e) {
//           // logger.info("接口请求失败" + e.getStackTrace());
//        }
//        //logger.info("接口返回" + result);
//        String result = null;
//        result = apiService.test1();
//        return new ResponseBean(HttpStatus.OK.value(), "成功( Success)", result);
//    }


}