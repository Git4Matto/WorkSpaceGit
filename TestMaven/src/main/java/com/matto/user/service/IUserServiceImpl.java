package com.matto.user.service;

import com.matto.user.dao.IUserDao;
import com.matto.user.pojo.TUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by matto on 16-6-11.
 */
@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao ;

    @Override
    public TUser getUserById(int id) {
        return userDao.getUserById(id);
    }

    //创建订单
    @Override
    public int  createOrder(List<OrderEntity> orderList) {
        List<String> shopIdList = new ArrayList<String>();
        List<CommodityEntity> orderItemList =  orderServiceDao.getOrderItems(orderList);

        for(int i=0 ; i<orderItemList.size() ; i++){
            shopIdList.add(orderItemList.get(i).getShop_id()) ;
        }
        //shopId去重
        shopIdList = new ArrayList<>(new HashSet<>(shopIdList));
        Map<String, String> orderMap = new HashMap<>();
        for (int i = 0; i < shopIdList.size(); i++) {
            String orderId = commonServiceDao.getSysGuid();
            orderMap.put(shopIdList.get(i), orderId);
        }
        Map<String, String> comIdShopIdMap = new HashMap<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            comIdShopIdMap.put(orderItemList.get(i).getId(), orderItemList.get(i).getShop_id());
        }
        for (int i = 0; i < orderList.size(); i++) {
            OrderEntity oe = orderList.get(i);
            oe.setOrderId(orderMap.get(comIdShopIdMap.get(orderList.get(i).getCommodity_id())));
        }

        int insertOrder = orderServiceDao.createOrder(orderList);
        return 0;
    }


    //获取订单状态-数量
    public Map<String,Integer> getStateAmount(String userId){
        List<OrderEntity> orderList = orderServiceDao.getOrderList(userId);
        Map<String, Integer> stateMap = new HashMap<>();
        int wait_pay = 0 ;
        int wait_send = 0 ;
        int wait_receive = 0 ;
        int wait_appraise = 0 ;
        int wait_refunds = 0 ;
        for(int i=0 ; i<orderList.size() ; i++){
            if(orderList.get(i).getState().equals("1")){
                wait_pay += 1 ;
            }else if (orderList.get(i).getState().equals("3")||orderList.get(i).getState().equals("2")) {
                wait_send += 1 ;
            }else if (orderList.get(i).getState().equals("4")) {
                wait_receive += 1 ;
            }else if (orderList.get(i).getState().equals("5")) {
                wait_appraise += 1 ;
            }else if (orderList.get(i).getState().equals("11")) {
                wait_refunds += 1 ;
            }
        }
        stateMap.put("wait_pay", wait_pay);
        stateMap.put("wait_send", wait_send);
        stateMap.put("wait_receive", wait_receive);
        stateMap.put("wait_appraise", wait_appraise);
        stateMap.put("wait_refunds", wait_refunds);

        return stateMap;
    }

    //插入订单web
    @Override
    public String getOrderMessage(String userId, String ids, String postId,String remark) {
        System.out.println(remark);
        String[] cartId = ids.split(",");
        List<String> strings = new ArrayList<String>();
        Collections.addAll(strings, cartId);
        System.out.println(strings.size());
        List<OrderEntity> orderList = new ArrayList<OrderEntity>();
        List<CommodityApplyEntity> items = userServiceDao.getCartByCartID(strings);//获得商品
        for(int i = 0 ; i < items.size() ; i ++){
            OrderEntity order = new OrderEntity();
            order.setCommodity_id(items.get(i).getId());
            order.setMount(items.get(i).getMount());
            order.setPost_info_id(postId);
            order.setUserId(userId);
            order.setRemark(remark);
            order.setAmount(String.valueOf(Double.parseDouble(items.get(i).getPrice())*Integer.parseInt(items.get(i).getMount())));//小计
            order.setState("1");
            orderList.add(order);
        }
        ////////////////////
        List<String> shopIdList = new ArrayList<String>();
        for(int i=0 ; i<items.size() ; i++){
            shopIdList.add(items.get(i).getShop_id()) ;
        }
        String orderIdsString="";//返回多个orderid
        //shopId去重
        shopIdList = new ArrayList<>(new HashSet<>(shopIdList));
        Map<String, String> orderMap = new HashMap<>();
        for (int i = 0; i < shopIdList.size(); i++) {
            String orderId = commonServiceDao.getSysGuid();
            orderIdsString += orderId+",";
            orderMap.put(shopIdList.get(i), orderId);
        }
        Map<String, String> comIdShopIdMap = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            comIdShopIdMap.put(items.get(i).getId(), items.get(i).getShop_id());
        }
        for (int i = 0; i < orderList.size(); i++) {
            OrderEntity oe = orderList.get(i);
            oe.setOrderId(orderMap.get(comIdShopIdMap.get(orderList.get(i).getCommodity_id())));
        }

        int insertOrder = orderServiceDao.createOrder(orderList);
        System.out.println("insertOrder"+insertOrder);
        if(insertOrder>0){
            //插入订单成功后删除购物车 by Matto 04-15
            int delCart = userServiceDao.delOrderCart(strings);

            return orderIdsString;
        }else {
            return "";
        }

        //根据userid和orderid查看订单详情
        @Override
        public  List<OrderMsgBean> selectOrderDeatil(String userId, String orderId) {

            List<OrderMessageEntity> messageEntityList = orderServiceDao.selectOrderDeatil(userId,orderId);
            List<OrderMsgBean> orderMsgBeans = new ArrayList<OrderMsgBean>();
            String orderID = "";
            int orderIndex = -1 ;

            for(int i = 0 ; i < messageEntityList.size(); i ++){
                OrderMessageEntity order = messageEntityList.get(i);
                OrderMsgBean orderMsg = new OrderMsgBean();
                orderMsg.setFinish_datetime(order.getFinish_datetime());
                orderMsg.setOrderId(order.getOrderId());
                orderMsg.setCreate_datetime(order.getCreate_DateTime());
                orderMsg.setShop_name(order.getShop_name());
                orderMsg.setShop_id(order.getShop_id());
                orderMsg.setDeliver_datetime(order.getDeliver_DateTime());
                orderMsg.setPay_datetime(order.getPay_DateTime());
                orderMsg.setOrderState(order.getState());    //添加字段 by Matto
                //orderMsg.setOrderAmount(order.getAmount());    //添加字段 by Matto
                OrderInfoBean orderInfo = new OrderInfoBean();
                orderInfo.setAmount(order.getAmount());
                //orderInfo.setCommodity_id(order.getCommodity_id());
                orderInfo.setCommodity_id(order.getId());
                orderInfo.setCommodityImage(order.getCommodityImage());
                orderInfo.setGoodsName(order.getName());
                orderInfo.setMount(order.getMount());
                orderInfo.setState(order.getState());
                orderInfo.setUnit(order.getUnit());
                orderInfo.setPrice(order.getPrice());
                orderInfo.setUserId(order.getUserId());
                if(orderID.equals(order.getOrderId())){//如果一样的话添加到list
                    orderMsgBeans.get(orderIndex).getOrderInfoBeansList().add(orderInfo);
                }else {

                    orderMsgBeans.add(orderMsg);          //如果不一样新建order
                    orderIndex++;
                    orderMsgBeans.get(orderIndex).getOrderInfoBeansList().add(orderInfo);
                    orderID = order.getOrderId();
                }
            }
            return orderMsgBeans;
        }
        //根据ordeId查询 卖家信息
        @Override
        public ShopEntity getShopInfo(String orderID) {
            return orderServiceDao.getShopInfo(orderID);
        }
        //根据orderid查询卖家信息
        @Override
        public UserEntity getShopOwnerInfo(String orderID) {
            return orderServiceDao.getShopOwnerInfo(orderID);
        }
        //根据orderid
        @Override
        public PostInfo getPostInfo(String orderID) {
            return orderServiceDao.getPostInfo(orderID);
        }
        //根据时间查询订单
        @Override
        public List<OrderMsgBean> getOrderByDate(String userId, String fDate,
                String sDate) {

            List<OrderMessageEntity> messageEntityList =  orderServiceDao.getOrderByDate(userId,fDate,sDate);
            //List<OrderMessageEntity> messageEntityList = userService.getOrders(userId,state);//查到所有的订单
            List<OrderMsgBean> orderMsgBeans = new ArrayList<OrderMsgBean>();
            String orderID = "";
            int orderIndex = -1 ;

            for(int i = 0 ; i < messageEntityList.size(); i ++){
                OrderMessageEntity order = messageEntityList.get(i);
                OrderMsgBean orderMsg = new OrderMsgBean();
                orderMsg.setOrderId(order.getOrderId());
                orderMsg.setCreate_datetime(order.getCreate_DateTime());
                orderMsg.setShop_name(order.getShop_name());
                orderMsg.setShop_id(order.getShop_id());
                orderMsg.setDeliver_datetime(order.getDeliver_DateTime());
                orderMsg.setPay_datetime(order.getPay_DateTime());
                orderMsg.setOrderState(order.getState());    //添加字段 by Matto
                //orderMsg.setOrderAmount(order.getAmount());    //添加字段 by Matto
                OrderInfoBean orderInfo = new OrderInfoBean();
                orderInfo.setAmount(order.getAmount());
                //orderInfo.setCommodity_id(order.getCommodity_id());
                orderInfo.setCommodity_id(order.getId());
                orderInfo.setCommodityImage(order.getCommodityImage());
                orderInfo.setGoodsName(order.getName());
                orderInfo.setMount(order.getMount());
                orderInfo.setState(order.getState());
                orderInfo.setUnit(order.getUnit());
                orderInfo.setPrice(order.getPrice());
                orderInfo.setUserId(order.getUserId());
                if(orderID.equals(order.getOrderId())){//如果一样的话添加到list
                    orderMsgBeans.get(orderIndex).getOrderInfoBeansList().add(orderInfo);
                }else {

                    orderMsgBeans.add(orderMsg);          //如果不一样新建order
                    orderIndex++;
                    orderMsgBeans.get(orderIndex).getOrderInfoBeansList().add(orderInfo);
                    orderID = order.getOrderId();
                }
            }
            return orderMsgBeans;
        }
    }

}
