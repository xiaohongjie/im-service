package com.im.imservice.service;





import com.im.imservice.reqDto.*;
import com.im.imservice.resDto.BaseResponse;

import java.util.TreeMap;

/**
 *
 * @author xhj
 * @date 2018/8/9 15:44
 */
public interface IApiService{

    String sign(TreeMap<String, String> params);

    String applyAdmin(ApplyAdminDto applyAdminDto);

    String createGroup(CreateGroupDto createGroupDto);

    String disbandGroup(DisbandGroupDto disbandGroupDto);

    String groupForbiddenWords(GroupStatusDto groupStatusDto);

    String kickOutGroup(KickOutGroupDto kickOutGroupDto);

    String groupChat(GroupChatDto groupChatDto);

    String importUser(ImportUserDto importUserDto);

    String deleteUser(DelectUserDto delectUserDto);

    String singleChat(SingleChatDto singleChatDto);

    String joinGroup(JoinGroupDto joinGroupDto);

    BaseResponse operateGroupInfo(OperateGroupInfoDto operateGroupInfoDto);

//    String getUserInfo(GetUserInfoDto getUserInfoDto);

    String dailyStatistics(DailyStatisticsDto dailyStatisticsDto);

    String statistics(StatisticsDto statisticsDto);

    String info(ChatRoomInfoDto chatRoomInfoDto);

    String user(ChatUserInfoDto chatUserInfoDto);

    String msgExport(MsgExportDto msgExportDto);

    BaseResponse msgResultAsync(MsgResultAsyncDto msgResultAsyncDto);

//    String test1();
}
