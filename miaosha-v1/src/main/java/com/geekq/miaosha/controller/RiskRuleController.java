package com.geekq.miaosha.controller;

import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.service.ProductService;
import com.geekq.miaosha.service.RiskRuleService;
import com.geekq.miaosha.vo.MiaoshaRuleVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: LZR
 * @date: 2022年04月03日 20:52
 */
@Controller
public class RiskRuleController {
    @Autowired
    RiskRuleService riskRuleService;


    //增加秒杀准入规则
    @ResponseBody
    @GetMapping("/admin/createRule")
    public ResultGeekQ<Integer> createRule(@RequestBody MiaoshaRuleVo miaoshaRuleVo){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        miaoshaRuleVo.setCreateTime(new Date());
        riskRuleService.createRule(miaoshaRuleVo);

        result.success();

        return result;
    }
    //list所有秒杀准入规则
    @ResponseBody
    @GetMapping("/admin/listRule")
    public ResultGeekQ<List<MiaoshaRuleVo>> listRule(){
        ResultGeekQ<List<MiaoshaRuleVo>> result = ResultGeekQ.build();
        List<MiaoshaRuleVo> miaoshaRuleVos = riskRuleService.listRule();
        result.setData(miaoshaRuleVos);
        result.success();
        return result;
    }
    //根据id删除秒杀规则
    @ResponseBody
    @GetMapping("/admin/deleteMiaoshaRuleById")
    public ResultGeekQ<Integer> deleteMiaoshaRule(@RequestParam("id")Long id){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        boolean b = riskRuleService.deleteMiaoshaRule(id);
        if (b){
            result.success();
        }
        else {
            result.withError(ResultStatus.RULE_NOT_EXIST);
        }
        return result;
    }
    //根据id改变规则
    @ResponseBody
    @GetMapping("/admin/updateRule")
    public ResultGeekQ<Integer> updateRule(@RequestBody MiaoshaRuleVo miaoshaRuleVo){
        ResultGeekQ<Integer> result = ResultGeekQ.build();

        boolean b = riskRuleService.updateRule(miaoshaRuleVo);
        if (b){
            result.success();
        }
        else {
            result.withError(ResultStatus.RULE_NOT_EXIST);
        }

        return result;
    }
    //根据id为秒杀产品配置准入规则
    @ResponseBody
    @GetMapping("/admin/updateMiaoshaGoodsRule")
    public ResultGeekQ<Integer> updateMiaoshaGoodsRule(@RequestParam("productId")Long productId,@RequestParam("ruleId")Long ruleId){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        boolean b = riskRuleService.updateMiaoshaGoodsRule(productId,ruleId);
        if (b){
            result.success();
        }
        else {
            result.withError(ResultStatus.PRODUCT_OR_RULE_NOT_EXIST);
        }
        return result;
    }
}
