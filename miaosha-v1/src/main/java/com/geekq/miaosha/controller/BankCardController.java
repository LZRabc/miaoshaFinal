package com.geekq.miaosha.controller;

import com.geekq.miaosha.access.AccessLimit;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.domain.BankCard;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.User;
import com.geekq.miaosha.service.BankCardService;
import com.geekq.miaosha.service.MiaoShaUserService;
import com.geekq.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName BankCardController.java
 * @Description TODO
 * @createTime 2022年04月01日 02:28:00
 */
@Controller
public class BankCardController {

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private UserService userService;

    @Autowired
    private MiaoShaUserService miaoShaUserService;

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @ResponseBody
    @PostMapping("/addBankCard")
    public ResultGeekQ<Integer> addBankCard(HttpServletRequest request, @RequestParam("id")String id){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        bankCardService.addBankCard(request,id);
        result.success();
        return result;
    }
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @ResponseBody
    @GetMapping("/listBankCard")
    public ResultGeekQ<List<BankCard>> listBankCard(HttpServletRequest request){
        ResultGeekQ<List<BankCard>> result = ResultGeekQ.build();
        List<BankCard> bankCards = bankCardService.listBankCard( request);
        result.setData(bankCards);
        result.success();
        return result;
    }

    @ResponseBody
    @GetMapping("/deleteBankCard")
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    public ResultGeekQ<Integer> deleteBankCard(@RequestParam("id")Long id){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        int bankCards = bankCardService.deleteBankCard(id);
        result.setData(bankCards);
        result.success();
        return result;
    }
    @ResponseBody
    @PostMapping("/recharge")
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    public ResultGeekQ<Integer> recharge(HttpServletRequest request,@RequestBody User user){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        int recharge = bankCardService.recharge(request, user);
        result.setData(recharge);
        result.success();
        return result;
    }

}
