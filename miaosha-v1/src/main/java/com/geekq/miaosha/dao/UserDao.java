package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.BankCard;
import com.geekq.miaosha.domain.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {

    @Select("select * from user_account where id = #{id}")
    public User getById(@Param("id") int id);

    @Insert("insert into user_account (id, name) values(#{id}, #{name})")
    public int insert(User user);

    @Update("update user_account set money = #{price} where id = #{id}")
    public int decrMoney(@Param("id") Long id,@Param("price") double price);

    @Select("select money from user_account where id = #{id}")
    public double getMoney(Long id);

    @Update("update user_account set  money = money+#{money} where id = #{id}")
    public int recharge(User bankCard);
}
