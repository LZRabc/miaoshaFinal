package com.geekq.miaosha.controller;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.pojo.RegisterPojo;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.MiaoShaUserService;
import com.geekq.miaosha.service.MiaoshaService;
import com.geekq.miaosha.utils.SMSSending;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Random;

import static com.geekq.miaosha.common.enums.ResultStatus.CODE_FAIL;
import static com.geekq.miaosha.common.enums.ResultStatus.RESIGETER_FAIL;

@Controller
//@RequestMapping("/user")
public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private MiaoShaUserService miaoShaUserService;

    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @RequestMapping("/do_register")
    public String registerIndex() {
        return "register";
    }

    /**
     * @title register
     * @description 注册网站接收json参数
     * @author zzh
     * @param: register
     * @param: response
     * @updateTime 2022/3/18 12:36
     * @return: com.geekq.miaosha.common.resultbean.ResultGeekQ<java.lang.String>
     * @throws
     */
    //把salt改了
    @PostMapping("/register")
    @ResponseBody
    public ResultGeekQ<String> register(@RequestBody RegisterPojo register,
                                        HttpServletResponse response) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        //前端这里得传个盐过来
        /**
         * 校验验证码
         */
        String salt="1a2b3c4d";
//        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(register.getTel());

        String inSessionOtpCode = redisService.get(register.getUsername());
        if(!com.alibaba.druid.util.StringUtils.equals(register.getVerificationCode(),inSessionOtpCode)){
            result.withError(CODE_FAIL.getCode(), CODE_FAIL.getMessage());
            return result;
        }
//        boolean check = miaoshaService.checkVerifyCodeRegister(Integer.valueOf(verifyCode));
//        if (!check) {
//            result.withError(CODE_FAIL.getCode(), CODE_FAIL.getMessage());
//            return result;
//        }
        boolean registerInfo = miaoShaUserService.register(response, register.getUsername(), register.getPassword(),salt,register.getTel(),register.getRealName(),register.getIdNum());
        if (!registerInfo) {
            result.withError(RESIGETER_FAIL.getCode(), RESIGETER_FAIL.getMessage());
            return result;
        }
        return result;
    }
    //用户获取otp短信接口
    @GetMapping(value = "/getVerificationCode")
    @ResponseBody
    public ResultGeekQ<String> getOtp(@RequestParam(name="tel")String telphone) throws Exception {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();

        int randomInt =  random.nextInt(8999);
        randomInt += 1000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证码同对应用户的手机号关联，使用httpsession的方式绑定他的手机号与OTPCODE
//        httpServletRequest.getSession().setAttribute(telphone,otpCode);
        redisService.setnx(telphone,otpCode);
        redisService.expire(telphone,60*5);
        //将OTP验证码通过短信通道发送给用户,省略
//        System.out.println("telphone = " + telphone + " & otpCode = "+otpCode);
        com.aliyun.dysmsapi20170525.Client client = SMSSending.createClient();
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers(telphone)
                .setTemplateParam("{\"code\":\""+otpCode+"\"}");
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse response =  client.sendSms(sendSmsRequest);
//        System.out.println(response.body.getCode());
//        System.out.println(response.body.getMessage());
        
        ResultGeekQ<String> result = ResultGeekQ.build();
        result.setData(response.getBody().getMessage());
        return result;
    }
}
