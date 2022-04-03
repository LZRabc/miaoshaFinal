package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.MiaoshaGoods;
import com.geekq.miaosha.domain.Product;
import com.geekq.miaosha.domain.ProductMiaoSha;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import com.geekq.miaosha.vo.ProductRecommendVo;
import org.apache.ibatis.annotations.*;

import java.lang.annotation.Inherited;
import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductDao.java
 * @Description TODO
 * @createTime 2022年03月18日 15:52:00
 */
@Mapper
public interface ProductDao {

    @Select("select * from product")
    public List<Product> listProducts();

    @Update("update product set price = #{price} ,description = #{description},total_num =#{totalNum} ,name =#{name},riskLevel=#{riskLevel},rateOfReturnName = #{rateOfReturnName} ,cycle =#{cycle}, rateOfReturnValue=#{rateOfReturnValue},tag=#{tag},tag2=#{tag2} where id = #{id}")
    public int updateProducts(Product product);

//    @Select("select p.*,mg.id as miaoshaId,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from product as p , miaosha_goods as mg  where mg.goods_id = p.id")
//    public List<ProductMiaoShaVo> listProductMiaoShas();

    @Select("select p.*,mg.id as miaoshaId,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date ,rr.* from product as p left JOIN miaosha_goods as mg ON p.id=mg.goods_id LEFT JOIN risk_rule as rr ON  mg.rule_id = rr.id ")
    public List<ProductMiaoShaVo> listProductMiaoShas();

    @Select("select p.* from product as p , product_recommend as pr where pr.product_id = p.id")
    public List<ProductRecommendVo> listProductRecommend();

    @Insert("insert into product(id,name,price,total_num,description,cycle,rateOfReturnName,rateOfReturnValue,riskLevel,tag,tag2,img_url)values(#{id},#{name},#{price},#{totalNum},#{description},#{cycle},#{rateOfReturnName},#{rateOfReturnValue},#{riskLevel},#{tag},#{tag2},#{imgUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertProduct(Product product);

    @Select("select * from product where id = #{productId}" )
    public Product getProductById(Long id);

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join product g on mg.goods_id = g.id where g.id = #{id}")
    public ProductMiaoShaVo getProductMiaoShaVoByid(@Param("id") Long id);

//    @Select("select * from product where tag = #{tag}" )
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join product g on mg.goods_id = g.id where g.tag = #{tag}")
    public List<ProductMiaoShaVo> getProductByTag(String tag);


//    @Select("select * from product where tag = #{tag} and tag2=#{tag2}" )
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join product g on mg.goods_id = g.id where g.tag = #{tag} and tag2=#{tag2}")
    public List<ProductMiaoShaVo> getProductByTag2(@Param("tag")String tag, @Param("tag2")String tag2);


    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    //这里有个多表查询我不会还请大神帮忙，我就当已经开发好了
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);


    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(MiaoshaGoods g);

    //添加秒杀产品
    @Insert("insert into miaosha_goods (goods_id,miaosha_price,stock_count,start_date,end_date)values(#{id},#{miaoshaPrice},#{stockCount},#{startDate},#{endDate})")
    public int addMiaoshaProduct(ProductMiaoShaVo productMiaoShaVo);

    //修改秒杀产品
    @Update("update miaosha_goods set miaosha_price = #{miaoshaPrice},stock_count = #{stockCount},start_date=#{startDate},end_date=#{endDate} where id = #{miaoshaId}")
    public int updateMiaoshaProduct(ProductMiaoShaVo productMiaoShaVo);

    //取消秒杀产品
    @Delete("delete from miaosha_goods where id = #{id} ")
    public int deleteMiaoshaProduct(Long id);




}
