package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.BankCard;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName BankCardMsgDao.java
 * @Description TODO
 * @createTime 2022年04月01日 02:19:00
 */
@Mapper
public interface BankCardMsgDao {

    @Insert("insert into user_bank_card(user_id,bank_card_id,create_time)value(#{userId},#{bankCardId},#{createTime})")
    public int addBankCard(BankCard bankCard);

    @Select("select * from user_bank_card where user_id = #{id}")
    public List<BankCard> listBankCard(Long id);

    @Delete("delete  from user_bank_card where bank_card_id = #{id}")
    public int deleteBankCard(Long id);


}
