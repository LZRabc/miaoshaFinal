package com.geekq.miaosha.service;

import com.alibaba.fastjson.JSON;
import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.common.enums.RiskRuleStatus;
import com.geekq.miaosha.config.RiskRuleConfig;
import com.geekq.miaosha.dao.MiaoshaRuleDao;
import com.geekq.miaosha.dao.OverRecordDao;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.OverDueRecord;
import com.geekq.miaosha.exception.GlobleException;
import com.geekq.miaosha.redis.MiaoShaUserKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.redis.RiskRuleKey;
import com.geekq.miaosha.vo.MiaoshaRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName RiskRule.java
 * @Description TODO
 * @createTime 2022年04月02日 19:52:00
 */
@Service
public class RiskRuleService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private OverRecordDao overRecordDao;
    @Autowired
    private MiaoshaRuleDao miaoshaRuleDao;

    /**
     * @title 逾期过滤
     * @description
     * @author zzh
     * @updateTime 2022/4/2 19:59
     * @throws
     */
    public boolean overdueFilter(MiaoshaUser user,Long id) {

        // 从 redis 中拿到逾期配置类转为逾期配置类
        RiskRuleConfig riskRuleConfig = redisService.get(RiskRuleKey.RiskRuleProduct,id+"",RiskRuleConfig.class);

        // 判断是否为空
        if (riskRuleConfig == null){
            /*
            // 如果为空 则调用无参构造创建默认值
            riskRuleConfig = new RiskRuleConfig();
            // 放入 redis
            redisService.set(RiskRuleKey.getRiskRuleKey,id+"",riskRuleConfig);
             */
            return true;
        }

        // 拿到用户逾期记录
        List<OverDueRecord> overDueRecords = overRecordDao.listOverDueRecords(user.getId());
        // 如果逾期记录为 0 直接通过
        if (overDueRecords.isEmpty()) {
            return true;
        }

        // 拿到当前时间
        Calendar instance = Calendar.getInstance();
        Date currentTime = instance.getTime();

        // 拿出配置条件
        RiskRuleConfig finalRiskRuleConfig = riskRuleConfig;
        // 最小逾期次数
        Integer overdueTimes = finalRiskRuleConfig.getFrequency();
        // 最小逾期金额
        Double overdueMinMoney = finalRiskRuleConfig.getMiniAmount();
        // 最小逾期年限
        Integer overdueYear = finalRiskRuleConfig.getOverduePeriod();
        // 最小宽容期
        Integer overdueDay = finalRiskRuleConfig.getGracePeriod();


        // 过滤流式编程
        List<OverDueRecord> collect = overDueRecords.stream().filter(item -> {
            // 判断逾期金额
            Double overMoney = item.getOverdueAmount();
            boolean moneyJudge = overMoney.compareTo(overdueMinMoney) > 0;

            // 判断是否过了几年年
            Date beginTime = item.getOriginalRepayTime();
            // 设置日历为 beginTime
            instance.setTime(beginTime);
            instance.add(Calendar.YEAR, overdueYear);
            Date afterAddYearTime = instance.getTime();
            boolean yearJudge = afterAddYearTime.after(currentTime);

            // 判断是否在逾期三天之内还完
            // 继续设置为应该支付最后时间
            Date originalRepayTime = item.getOriginalRepayTime();
            instance.setTime(originalRepayTime);
            instance.add(Calendar.DAY_OF_WEEK, overdueDay);
            Date afterAddDayTime = instance.getTime();
            Date endTime = item.getActualRepayTime();
            boolean dayJudge = afterAddDayTime.before(endTime);

            // 全部满足则通过
            return moneyJudge && yearJudge && dayJudge;
        }).collect(Collectors.toList());

        // 判断逾期次数 是否小于 最小逾期次数
        return collect.size() <= overdueTimes;
    }
    /**
     * @title 工作状态过滤
     * @description
     * @author zzh
     * @updateTime 2022/4/2 21:20
     * @throws
     */
    public boolean workStatusFilter(MiaoshaUser user,Long id) {

//        String token = request.getHeader("token");
        // 从 redis 中拿到工作状态
        RiskRuleConfig riskRuleConfig = redisService.get(RiskRuleKey.getRiskRuleKey, "" + id, RiskRuleConfig.class);
        if (riskRuleConfig == null)
            return true;
        Integer workStatus = riskRuleConfig.getWorkStatus();

        //从redis中拿到用户
//        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        workStatus = workStatus == null ? RiskRuleStatus.WorkStatus.NO_WORK.getCode() : workStatus;

        return !user.getWorkStatus().equals(workStatus);
    }
    /**
     * @title 失信人过滤
     * @description
     * @author zzh
     * @updateTime 2022/4/2 21:35
     * @throws
     */
    public boolean unTrustFilter(MiaoshaUser user) {

        return user.getUntrustPerson() == 0;
    }


    /**
     * @title 过滤年龄
     * @description
     * @author zzh
     * @updateTime 2022/4/2 21:45
     * @throws
     */
    public boolean ageFilter(MiaoshaUser user,Long id) {


        Integer age =user.getAge();
        // 从 redis 中拿到年龄限制
        RiskRuleConfig riskRuleConfig = redisService.get(RiskRuleKey.getRiskRuleKey, "" + id, RiskRuleConfig.class);
        if (riskRuleConfig == null)
            return true;
        Integer ageRule =riskRuleConfig.getAge();
        ageRule = ageRule == null ? 18 : ageRule;

        return age >= ageRule;
    }

    /**
     * @title 整体初筛记录到redis
     * @description
     * @author zzh
     * @updateTime 2022/4/2 22:37
     * @throws
     */
    public  boolean firstFilter(MiaoshaUser user,Long id){
        Boolean res = false;
        boolean overdueFilter=overdueFilter(user,id);
        boolean workStatusFilter=workStatusFilter(user,id);
        boolean unTrustFilter=unTrustFilter(user);
        boolean ageFilter=ageFilter(user,id);

        res = overdueFilter&&ageFilter(user,id)&&workStatusFilter&&unTrustFilter&&ageFilter;
        redisService.setnx(RiskRuleKey.RiskRuleUser.getPrefix()+user.getId(),res.toString());
        return res;
    }


    public boolean createRule(MiaoshaRuleVo miaoshaRuleVo) {
        if(miaoshaRuleVo==null){
            throw new GlobleException(ResultStatus.RULE_NOT_EMPTY);
        }
        miaoshaRuleDao.insertMiaoshaRule(miaoshaRuleVo);
        return true;
    }

    public List<MiaoshaRuleVo> listRule() {
        List<MiaoshaRuleVo> miaoshaRuleVos = miaoshaRuleDao.listMiaoshaRuleVo();
        return miaoshaRuleVos;
    }

    public boolean deleteMiaoshaRule(Long id) {
        int i = miaoshaRuleDao.deleteMiaoshaProduct(id);
        if (i==0){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean updateRule(MiaoshaRuleVo miaoshaRuleVo) {
        int i = miaoshaRuleDao.updateMiaoshaProduct(miaoshaRuleVo);
        if (i==0){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean updateMiaoshaGoodsRule(Long productId, Long ruleId) {
        int i = miaoshaRuleDao.updateMiaoshaGoodsRule(productId,ruleId);
        MiaoshaRuleVo miaoshaRuleVo = miaoshaRuleDao.listMiaoshaRuleVoById(ruleId);
        //秒杀商品和准入规则都可能不存在所以都做一下判断
        if(i!=0&&miaoshaRuleVo!=null){
            return true;
        }
        else {
            return false;
        }
    }
}
