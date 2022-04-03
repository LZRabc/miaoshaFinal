package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.MiaoShaUserDao;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.User;
import com.geekq.miaosha.exception.GlobleException;
import com.geekq.miaosha.rabbitmq.MQSender;
import com.geekq.miaosha.redis.MiaoShaUserKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.utils.MD5Utils;
import com.geekq.miaosha.utils.TokenUtils;
import com.geekq.miaosha.utils.UUIDUtil;
import com.geekq.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import static com.geekq.miaosha.common.enums.ResultStatus.*;
import static com.geekq.miaosha.utils.SM2Utils.*;

import com.geekq.miaosha.utils.SM2Utils.*;

@Service
public class MiaoShaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";
    private static Logger logger = LoggerFactory.getLogger(MiaoShaUserService.class);

    @Autowired
    private MiaoShaUserDao miaoShaUserDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender sender;

    @Autowired
    private  UserService userService;

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;

    }
    public MiaoshaUser getByToken( String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser s = redisService.get(MiaoShaUserKey.token,token,MiaoshaUser.class);

        return s;

    }
    //其实这里是getbyid我不想改了等下好多地方要改
    public MiaoshaUser getByNickName(String nickName) {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoShaUserKey.getByNickName, "" + nickName, MiaoshaUser.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = miaoShaUserDao.getById(Long.valueOf(nickName));
        if (user != null) {
            redisService.set(MiaoShaUserKey.getByNickName, "" + nickName, user);
        }
        return user;
    }


    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, String nickName, String formPass) {
        //取user
        MiaoshaUser user = getByNickName(nickName);
        if (user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setNickname(nickName);
        toBeUpdate.setPassword(MD5Utils.formPassToDBPass(formPass, user.getSalt()));
        miaoShaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoShaUserKey.getByNickName, "" + nickName);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoShaUserKey.token, token, user);
        return true;
    }


    public boolean register(HttpServletResponse response, String userName, String passWord, String salt, String tel, String realName, String idNum) {
        MiaoshaUser miaoShaUser = new MiaoshaUser();
        miaoShaUser.setNickname(userName);
        //前端改了之后这里还得改
        String DBPassWord = MD5Utils.inputPassToDBPass(passWord, salt);
        String publicKeyHex = null;
        String privateKeyHex = null;
        KeyPair keyPair = createECKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        if (publicKey instanceof BCECPublicKey) {
            //获取65字节非压缩缩的十六进制公钥串(0x04)
            publicKeyHex = Hex.toHexString(((BCECPublicKey) publicKey).getQ().getEncoded(false));
        }
        PrivateKey privateKey = keyPair.getPrivate();
        if (privateKey instanceof BCECPrivateKey) {
            //获取32字节十六进制私钥串
            privateKeyHex = ((BCECPrivateKey) privateKey).getD().toString(16);
        }

        //将十六进制公钥串转换为 BCECPublicKey 公钥对象
        String encryptData = encrypt(publicKeyHex, DBPassWord);

        miaoShaUser.setPrivateKey(privateKeyHex);
        miaoShaUser.setPassword(encryptData);
        miaoShaUser.setSalt(salt);
        miaoShaUser.setId(Long.valueOf(tel));
        miaoShaUser.setRealName(realName);
        miaoShaUser.setIdNum(idNum);
        miaoShaUser.setRegisterDate(new Date());
        miaoShaUser.setNickname(userName);
        User userDetail = new User();
        userDetail.setName(realName);
        userDetail.setId(Long.valueOf(tel));
        try {
            miaoShaUserDao.insertMiaoShaUser(miaoShaUser);
            MiaoshaUser user = miaoShaUserDao.getByNickname(miaoShaUser.getNickname());
            if (user == null) {
                return false;
            }
            userService.addUser(userDetail);
            //生成cookie 将session返回游览器 分布式session
            String token = UUIDUtil.uuid();
            addCookie(response, token, user);
        } catch (Exception e) {
            logger.error("注册失败", e);
            return false;
        }
        return true;
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobleException(SYSTEM_ERROR);
        }
        String mobile = loginVo.getUsername();
        String password = loginVo.getPassword();
        MiaoshaUser user = getByNickName(mobile);

        if (user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass = MD5Utils.inputPassToDBPass(password, saltDb);
        String privateKeyHex=user.getPrivateKey();
        /**
         * 私钥解密
         */
        //将十六进制私钥串转换为 BCECPrivateKey 私钥对象
        String temp = decrypt(privateKeyHex, dbPass);
        //我觉得这里逻辑错误了好像是equals loginvo password//
//        if (!calcPass.equals(dbPass)) {
//            throw new GlobleException(PASSWORD_ERROR);
//        }

        if (!calcPass.equals(temp)){
            throw new GlobleException(PASSWORD_ERROR);
        }
        //生成cookie 将session返回游览器 分布式session
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }


    public String createToken(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobleException(SYSTEM_ERROR);
        }
        String mobile = loginVo.getUsername();
        String password = loginVo.getPassword();
        MiaoshaUser user = getByNickName(mobile);
        if (user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass = MD5Utils.inputPassToDBPass(password, saltDb);
        String privateKeyHex=user.getPrivateKey();
        String temp = decrypt(privateKeyHex, dbPass);
        if (!calcPass.equals(temp)) {
            throw new GlobleException(PASSWORD_ERROR);
        }
        //生成cookie 将session返回游览器 分布式session
        String token = TokenUtils.createToken(user,60*24);
        String retoken = token.replace("+", "");
        redisService.set(MiaoShaUserKey.token,retoken,user);
        addCookie(response, retoken, user);
        return retoken;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoShaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
