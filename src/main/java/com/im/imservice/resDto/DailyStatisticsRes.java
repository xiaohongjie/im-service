package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author xhj
 * @project im
 * @create 2019-12-12 20:47
 */
@Data
public class DailyStatisticsRes implements Serializable {

    private Number dailyNew;//日新增用户数

    private Number dailyActive;//日活跃用户数

}
