package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.MiaoShaMessageInfo;
import com.geekq.miaosha.domain.MiaoShaMessageUser;
import com.geekq.miaosha.domain.MiaoshaRule;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.MiaoshaRuleVo;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Description:
 * @author: LZR
 * @date: 2022年04月03日 16:18
 */
@Mapper
public interface MiaoshaRuleDao {
    @Insert("insert into risk_rule (id , overdue_period ,frequency , mini_amount ,grace_period,status,create_time,work_status,untrust_person,age)" +
            "value (#{id},#{overduePeriod},#{frequency},#{miniAmount},#{gracePeriod},#{status},#{createTime},#{workStatus},#{untrustPerson},#{age}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insertMiaoshaRule(MiaoshaRuleVo miaoshaRuleVo);


    @Select("SELECT * FROM risk_rule")
    public List<MiaoshaRuleVo> listMiaoshaRuleVo();


    @Delete("delete from risk_rule where id = #{id} ")
    public int deleteMiaoshaProduct(Long id);


    @Update("update risk_rule set overdue_period = #{overduePeriod},frequency = #{frequency},mini_amount=#{miniAmount},grace_period=#{gracePeriod},status=#{status},create_time=#{createTime},work_status=#{workStatus}," +
            "untrust_person=#{untrustPerson},age=#{age} where id = #{id}")
    public int updateMiaoshaProduct(MiaoshaRuleVo miaoshaRuleVo);

    @Update("update miaosha_goods set rule_id = #{ruleId} where id = #{productId}")
    public int updateMiaoshaGoodsRule(@Param("productId")Long productId,@Param("ruleId")Long ruleId);

    @Select("SELECT * FROM risk_rule WHERE id=#{id} ")
    public MiaoshaRuleVo listMiaoshaRuleVoById(Long id);
}
