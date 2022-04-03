package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.BankCardMsgDao;
import com.geekq.miaosha.dao.UserDao;
import com.geekq.miaosha.domain.BankCard;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.User;
import com.geekq.miaosha.redis.MiaoShaUserKey;
import com.geekq.miaosha.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName BankService.java
 * @Description TODO
 * @createTime 2022年04月01日 02:24:00
 */
@Service
public class BankCardService {

    @Autowired
    private BankCardMsgDao bankCardMsgDao;

    @Autowired
    private RedisService  redisService;

    @Autowired
    private  MiaoShaUserService miaoShaUserService;

    @Autowired
    private UserDao userDao;
    /**
     * @title 添加银行卡
     * @description
     * @author zzh
     * @updateTime 2022/4/1 2:25
     * @throws
     */
    @Transactional
    public int addBankCard(HttpServletRequest request, String id){
        String token = request.getHeader("token");
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        BankCard bankCard = new BankCard();
        bankCard.setCreateTime(new Date());
        bankCard.setBankCardId(id);
        bankCard.setUserId(user.getId());
        int res = bankCardMsgDao.addBankCard(bankCard);
        return res;
    }

    /**
     * @title 列出某格用户得所有银行卡
     * @description
     * @author zzh
     * @updateTime 2022/4/1 2:48
     * @throws
     */
    public List<BankCard> listBankCard(HttpServletRequest request){
        String token = request.getHeader("token");
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        List<BankCard> res = bankCardMsgDao.listBankCard(user.getId());
        return res;
    }
    /**
     * @title 删除某个银行卡
     * @description
     * @author zzh
     * @updateTime 2022/4/1 2:54
     * @throws
     */
    public int deleteBankCard(Long id){
        int  res = bankCardMsgDao.deleteBankCard(id);
        return res;
    }

    /**
     * @title 充值
     * @description
     * @author zzh
     * @updateTime 2022/4/1 22:13
     * @throws
     */
    @Transactional
    public int recharge(HttpServletRequest request, User bankCard){
        String token = request.getHeader("token");
        MiaoshaUser user = miaoShaUserService.getByToken(token);
        bankCard.setId(user.getId());
        int  res = userDao.recharge(bankCard);
        return res;
    }
}
