package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.OrderDao;
import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.OrderInfo;
import com.geekq.miaosha.redis.OrderKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.utils.DateTimeUtils;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.geekq.miaosha.common.Constanst.orderStaus.ORDER_NOT_PAY;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, ProductMiaoShaVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(Long.valueOf(user.getNickname()));
        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(Long.valueOf(user.getNickname()));
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getNickname() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }


    public void closeOrder(int hour) {
        Date closeDateTime = DateUtils.addHours(new Date(), -hour);
        List<OrderInfo> orderInfoList = orderDao.selectOrderStatusByCreateTime(Integer.valueOf(ORDER_NOT_PAY.ordinal()), DateTimeUtils.dateToStr(closeDateTime));
        for (OrderInfo orderInfo : orderInfoList) {
            System.out.println("orderinfo  infomation " + orderInfo.getGoodsName());
        }
    }
    /**
     * @title 获取所有订单
     * @description 
     * @author zzh 
     * @updateTime 2022/3/23 14:29 
     * @throws 
     */
    public List<OrderInfo> listOrderInfo(){
        return orderDao.listOrderInfo();
    }
    /**
     * @title 用户获取订单所有
     * @description 
     * @author zzh 
     * @updateTime 2022/4/1 1:15 
     * @throws 
     */
    public List<OrderInfo> listOrderInfoByUser(Long userId){
        return orderDao.listOrderInfoByUser(userId);
    }



    /**
     * @title 改变订单支付状态
     * @description
     * @author zzh
     * @updateTime 2022/3/26 19:30
     * @throws
     */
    public int changePayStatus(Long id){

        OrderInfo orderInfo = orderDao.getOrderById(id);
        int status = 0;
        if(orderInfo==null) {
            return 0;
        }else if(orderInfo.getStatus()==1){
            return -1;
        }else{
            orderInfo.setStatus(1);
            orderInfo.setPayDate( new Date());
            status = orderDao.changeStatus(orderInfo);
        }
        return status;
    }

    /**
     * @title 获取订单金额
     * @description
     * @author zzh
     * @updateTime 2022/3/26 20:19
     * @throws
     */
    public Double getOrderPrice(Long id){
        return orderDao.getOrderPrice(id);
    }
    /**
     * @title 根据订单获取用户id
     * @description
     * @author zzh
     * @updateTime 2022/3/26 20:19
     * @throws
     */
    public long getUserId(Long id){
        return orderDao.getUserId(id);
    }

    /**
     * @title 根据订单获取订单id
     * @description
     * @author zzh
     * @updateTime 2022/3/26 20:19
     * @throws
     */
    public long getOrderId(Long userid,Long productId){
        return orderDao.getOrderId(userid,productId);
    }
}
