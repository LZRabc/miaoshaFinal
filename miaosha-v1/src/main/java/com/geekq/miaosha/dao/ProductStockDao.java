package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.Product;
import com.geekq.miaosha.domain.ProductStock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductStockDao.java
 * @Description TODO
 * @createTime 2022年03月18日 20:25:00
 */
@Mapper
public interface ProductStockDao {
    @Select("select stock from product_stock where product_id = #{id}")
    public int getStockById(long id);

    @Insert("insert into product_stock  (stock,product_id) values(#{stock},#{productId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insertStock(ProductStock productStock);
}
