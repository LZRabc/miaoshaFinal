package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.UserDao;
import com.geekq.miaosha.domain.BankCard;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrderService orderService;


    public User getById(int id) {
        return userDao.getById(id);
    }

    public double getMoney(Long id){
        return userDao.getMoney(id);
    }
    public int decrMoney(Long id,double money){
        long userId = orderService.getUserId(id);
        double totalMoney = this.getMoney(userId);
        if (totalMoney > money){
            double price = totalMoney-money;
            return userDao.decrMoney(userId,price);
        }
        return -1;
    }

    public int addUser(User user ){
        return  userDao.insert(user);
    }

}
