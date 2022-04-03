package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userNickName} and goods_id=#{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userNickName") long userNickName, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    public long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    public OrderInfo getOrderById(@Param("orderId") long orderId);

    @Select("select * from order_info where status=#{status} and create_Date<=#{createDate}")
    public List<OrderInfo> selectOrderStatusByCreateTime(@Param("status") Integer status, @Param("createDate") String createDate);

    @Select("update order_info set status=0 where id=#{id}")
    public int closeOrderByOrderInfo();

    @Select("select * from order_info")
    public List<OrderInfo> listOrderInfo();

    @Select("select * from order_info where user_Id = #{id}")
    public List<OrderInfo> listOrderInfoByUser(@Param("id")Long id);

    @Update("update order_info set status = 1 ,pay_date = #{payDate} where id = #{id}")
    public int changeStatus(OrderInfo orderInfo);

    @Select("select goods_price from order_info where id = #{id}")
    public double getOrderPrice(Long id);

    @Select("select user_id from order_info where id = #{id}")
    public long getUserId(Long id);

    @Select("select id from order_info where user_id = #{userId} and goods_id=#{productId}")
    public long getOrderId(@Param("userId") Long userId,@Param("productId") Long productId);
}
