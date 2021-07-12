package com.im.imservice.resDto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xhj
 * @project im
 * @create 2021-06-25 20:47
 */
@Data
public class StatisticsRes implements Serializable {

    private Number total;//累计用户数

}
