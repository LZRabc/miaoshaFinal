package com.geekq.miaosha.controller;

import com.geekq.miaosha.access.AccessLimit;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.config.RiskRuleConfig;
import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.rabbitmq.MQSender;
import com.geekq.miaosha.rabbitmq.MiaoshaMessage;
import com.geekq.miaosha.redis.GoodsKey;
import com.geekq.miaosha.redis.MiaoShaUserKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.redis.RiskRuleKey;
import com.geekq.miaosha.service.*;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import static com.geekq.miaosha.common.enums.ResultStatus.*;

@Controller
//@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    private MiaoShaUserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RiskRuleService riskRuleService;

    @Autowired
    MQSender mqSender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * QPS:1306
     * 5000 * 10
     * get　post get 幂等　从服务端获取数据　不会产生影响　　post 对服务端产生变化
     */
    /*


    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public ResultGeekQ<Integer> miaosha(Model model, MiaoshaUser user, @PathVariable("path") String path,
                                        @RequestParam("goodsId") long goodsId) {
        ResultGeekQ<Integer> result = ResultGeekQ.build();

        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        //验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check) {
            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
            return result;
        }
//		//使用RateLimiter 限流
//		RateLimiter rateLimiter = RateLimiter.create(10);
//		//判断能否在1秒内得到令牌，如果不能则立即返回false，不会阻塞程序
//		if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
//			System.out.println("短期无法获取令牌，真不幸，排队也瞎排");
//			return ResultGeekQ.error(CodeMsg.MIAOSHA_FAIL);
//
//		}

        //是否已经秒杀到
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(Long.valueOf(user.getNickname()), goodsId);
        if (order != null) {
            result.withError(REPEATE_MIAOSHA.getCode(), REPEATE_MIAOSHA.getMessage());
            return result;
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        //预见库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setUser(user);
        mqSender.sendMiaoshaMessage(mm);
        return result;
    }
 */

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @PostMapping(value = "/{path}/miaosha")
    @ResponseBody
    public ResultGeekQ<MiaoshaOrder> miaosha(HttpServletRequest request,@RequestParam("id") Long id,
                                             @PathVariable("path")String path) {
        String token = request.getHeader("token");
        ResultGeekQ<MiaoshaOrder> result = ResultGeekQ.build();


        MiaoshaUser user = redisService.get(MiaoShaUserKey.token,token,MiaoshaUser.class);

        // 验证path
        boolean check = miaoshaService.checkPath(user, id, path);
        if (!check) {
            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
            return result;
        }
        boolean secondFilter = riskRuleService.firstFilter(user, id);
        if (!secondFilter) {
            result.withError(NOT_QUALIFIED.getCode(), NOT_QUALIFIED.getMessage());
            return result;
        }
        //判断库存
//        ProductMiaoShaVo goods = productService.getGoodsVoByGoodsId(productId);
//        Integer productStock = goods.getStockCount();
//        if (productStock<=0){
//           result.withError(MIAOSHA_FAIL.getCode(),MIAOSHA_FAIL.getMessage());
//            return  result;
//        }

		//使用RateLimiter 限流
//		RateLimiter rateLimiter = RateLimiter.create(10);
//		//判断能否在1秒内得到令牌，如果不能则立即返回false，不会阻塞程序
//		if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
//			System.out.println("短期无法获取令牌，真不幸，排队也瞎排");
//			return ResultGeekQ.error(ResultStatus.MIAOSHA_FAIL);
//		}
        //是否已经秒杀到
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(Long.valueOf(user.getNickname()), id);
        if (order != null) {
            result.withError(REPEATE_MIAOSHA.getCode(), REPEATE_MIAOSHA.getMessage());
            return result;
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(id);
        if (over) {
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        //预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + id);
        if (stock < 0) {
            localOverMap.put(id, true);
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(id);
        mm.setUser(user);
        mqSender.sendMiaoshaMessage(mm);
        return result;
    }



    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<Long> miaoshaResult(HttpServletRequest request, @RequestParam("id") long goodsId) {
        ResultGeekQ<Long> result = ResultGeekQ.build();
        String token = request.getHeader("token");
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
//        model.addAttribute("user", user);

        Long miaoshaResult = miaoshaService.getMiaoshaResult(Long.valueOf(user.getId()), goodsId);
        result.setData(miaoshaResult);
        return result;
    }

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/getDepostPayUrl", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<String> getMiaoshaPath(HttpServletRequest request,
                                              @RequestParam("productId") long productId
                                              ) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        String token = request.getHeader("token");
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
//        boolean check = miaoshaService.checkVerifyCode(user, productId, verifyCode);
//        if (!check) {
//            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
//            return result;
//        }
        String path = miaoshaService.createMiaoshaPath(user, productId);
        result.setData(path);
        return result;
    }

    @RequestMapping(value = "/verifyCodeRegister", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<String> getMiaoshaVerifyCod(HttpServletResponse response
    ) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        try {
            BufferedImage image = miaoshaService.createVerifyCodeRegister();
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            logger.error("生成验证码错误-----注册:{}", e);
            result.withError(MIAOSHA_FAIL.getCode(), MIAOSHA_FAIL.getMessage());
            return result;
        }
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                                   @RequestParam("goodsId") long goodsId) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            logger.error("生成验证码错误-----goodsId:{}", goodsId, e);
            result.withError(MIAOSHA_FAIL.getCode(), MIAOSHA_FAIL.getMessage());
            return result;
        }
    }
    @ResponseBody
    @PostMapping("/admin/addProductMiaosha")
    public ResultGeekQ<Integer> addProductMiaosha(@RequestBody ProductMiaoShaVo productMiaoShaVo){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        productService.addProductMiaoSha(productMiaoShaVo);
        redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + productMiaoShaVo.getId(), productMiaoShaVo.getStockCount());
        localOverMap.put(productMiaoShaVo.getId(), false);
        result.success();
        return result;
    }
    /**
     * 系统初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
       /* List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
        */
        List<ProductMiaoShaVo> goodsList = productService.listProductMiaoshas();
        if (goodsList == null) {
            return;
        }
        for (ProductMiaoShaVo goods: goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            RiskRuleConfig riskRuleConfig = new RiskRuleConfig();
            riskRuleConfig.setAge(goods.getAge());
            riskRuleConfig.setFrequency(goods.getFrequency());
            riskRuleConfig.setGracePeriod(goods.getGracePeriod());
            riskRuleConfig.setMiniAmount(goods.getMiniAmount());
            riskRuleConfig.setWorkStatus(goods.getWorkStatus());
            riskRuleConfig.setOverduePeriod(goods.getOverduePeriod());
            redisService.set(RiskRuleKey.RiskRuleProduct,goods.getId()+"",riskRuleConfig);
            localOverMap.put(goods.getId(), false);
        }
    }
}
