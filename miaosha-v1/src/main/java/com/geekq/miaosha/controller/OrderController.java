package com.geekq.miaosha.controller;

import com.alibaba.fastjson.JSONObject;
import com.geekq.miaosha.access.AccessLimit;
import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.OrderInfo;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.GoodsService;
import com.geekq.miaosha.service.MiaoShaUserService;
import com.geekq.miaosha.service.OrderService;
import com.geekq.miaosha.service.UserService;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.OrderDetailVo;
import com.geekq.miaosha.vo.PayVo;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.geekq.miaosha.common.enums.ResultStatus.ORDER_NOT_EXIST;
import static com.geekq.miaosha.common.enums.ResultStatus.SESSION_ERROR;

@Controller
public class OrderController {

    @Autowired
    MiaoShaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    UserService uService;

    @RequestMapping("/order/detail")
    @ResponseBody
    public ResultGeekQ<OrderDetailVo> info(Model model, MiaoshaUser user,
                                           @RequestParam("orderId") long orderId) {
        ResultGeekQ<OrderDetailVo> result = ResultGeekQ.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            result.withError(ORDER_NOT_EXIST.getCode(), ORDER_NOT_EXIST.getMessage());
            return result;
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        result.setData(vo);
        return result;
    }
    @RequestMapping("/order/list")
    @ResponseBody
    public ResultGeekQ<List<OrderInfo>> list() {
        ResultGeekQ<List<OrderInfo>> result = ResultGeekQ.build();

        List<OrderInfo> order= orderService.listOrderInfo();
        if (order == null) {
            result.withError(ORDER_NOT_EXIST.getCode(), ORDER_NOT_EXIST.getMessage());
            return result;
        }
        result.success();
        result.setData(order);
            return result;
    }
    @PostMapping("/pay")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @Transactional
    public ResultGeekQ<String> pay(HttpServletRequest request,@RequestBody JSONObject id){
        ResultGeekQ<String> result = ResultGeekQ.build();
        //先判断订单是否存在
//        id.
//        Long orderId = id;
        Long orderId = id.getLong("id");
        int status = orderService.changePayStatus(orderId);
        if (status==0){
            result.withError(ORDER_NOT_EXIST);
            return  result;
        }else if (status == -1){
            result.withError(ResultStatus.ORDER_PAYED);
            return result;
        }
        //获取订单总金额
        Double money = orderService.getOrderPrice(orderId);
        //判断用户钱是否够
        int decrMoney = uService.decrMoney(orderId, money);
        if (decrMoney == -1 ){
             result.withError(ResultStatus.BALANCE_NOT_ENOUGH);
             return result;
        }
        result.success();
        return result;
    }
    @GetMapping("/order/listByUser")
    @ResponseBody
    public ResultGeekQ<List<OrderInfo>> listByUser(HttpServletRequest request) {
        ResultGeekQ<List<OrderInfo>> result = ResultGeekQ.build();
        String token = request.getHeader("token");
        MiaoshaUser user = userService.getByToken(token);
        List<OrderInfo> order= orderService.listOrderInfoByUser(user.getId());
        result.success();
        result.setData(order);
        return result;
    }

}
