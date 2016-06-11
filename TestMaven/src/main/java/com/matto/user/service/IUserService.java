package com.matto.user.service;

import com.matto.user.pojo.TUser;

import java.util.Map;

/**
 * Created by matto on 16-6-11.
 */
public interface IUserService {

    TUser getUserById(int id);

    //获取订单状态-数量
    Map<String, Integer> getStateAmount(String userId);

    //根据时间查询订单
    List<OrderMsgBean> getOrderByDate(String userId, String fDate, String sDate);

}
