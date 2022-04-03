package com.geekq.miaosha.service;

import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.dao.MiaoshaRuleDao;
import com.geekq.miaosha.dao.ProductDao;
import com.geekq.miaosha.dao.ProductStockDao;
import com.geekq.miaosha.domain.*;
import com.geekq.miaosha.exception.GlobleException;
import com.geekq.miaosha.redis.MiaoShaUserKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.MiaoshaRuleVo;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import com.geekq.miaosha.vo.ProductRecommendVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductService.java
 * @Description 这里本应该创建接口但是、、、、整体如此 那就如此吧
 * @createTime 2022年03月18日 16:02:00
 */
@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductStockDao productStockDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RiskRuleService riskRuleService;
    @Autowired
    private MiaoshaRuleDao miaoshaRuleDao;

   /**
    * @title 创建产品
    * @description
    * @author zzh
    * @updateTime 2022/3/18 16:05
    * @throws
    */
   @Transactional
    public boolean createProduct(Product product){

        if (product == null){
            throw new GlobleException(ResultStatus.PRODUCT_NOT_EMPTY);
        }
        long id = System.currentTimeMillis();
        product.setId(id);
        productDao.insertProduct(product);
        ProductStock productStock = new ProductStock();
        productStock.setProductId(product.getId());
        productStock.setStock(product.getTotalNum());
        productStockDao.insertStock(productStock);
        return true;
    }
    /**
     * @title 更新产品
     * @description
     * @author zzh
     * @updateTime 2022/3/24 11:17
     * @throws
     */
   public  int updateProduct(Product product){
       return productDao.updateProducts(product);
   }
    /**
     * @title 产品列表
     * @description
     * @author zzh
     * @updateTime 2022/3/18 16:06
     * @throws
     */
    public List<Product> listProducts(){
        List<Product> products = productDao.listProducts();
        return products;
    }

    /**
     * @title 获取推荐商品
     * @description
     * @author zzh
     * @updateTime 2022/3/20 13:48
     * @throws
     */
    public List<ProductRecommendVo> listProductRecommend(){
        List<ProductRecommendVo> products = productDao.listProductRecommend();
        return products;
    }

    /**
     * @title 所有参与秒杀的产品
     * @description
     * @author zzh
     * @updateTime 2022/3/20 11:25
     * @throws
     */
    public List<ProductMiaoShaVo> listProductMiaoshas(HttpServletRequest request){
        String token = request.getHeader("token");
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        List<ProductMiaoShaVo> products = productDao.listProductMiaoShas();
        for (int i = 0; i < products.size(); i++) {
            products.get(i).setPermission(riskRuleService.firstFilter(user,products.get(i).getId()));
        }
        return products;
    }
    public List<ProductMiaoShaVo> listProductMiaoshas(){

        List<ProductMiaoShaVo> products = productDao.listProductMiaoShas();
        return products;
    }
    /**
     * @title 产品详情浏览
     * @description
     * @author zzh
     * @updateTime 2022/3/18 16:07
     * @throws
     */
    public Product getProductById(Long id){
        Product product = productDao.getProductById(id);
        if (product==null){
            throw new GlobleException(ResultStatus.GOODS_NOT_EXIST);
        }
        return product;
    }

    /**
     * @title 添加库存
     * @description
     * @author zzh
     * @updateTime 2022/3/18 16:59
     * @throws
     */
    public int updateStock(ProductStock productStock){
        return 0;
    }
    
    
    /**
     * @title 获取指定一级分类存款产品
     * @description 
     * @author zzh 
     * @updateTime 2022/3/18 21:41
     * @throws 
     */
    public List<ProductMiaoShaVo> getDepositByTag(HttpServletRequest request,String tag){
        List<ProductMiaoShaVo> products = productDao.getProductByTag(tag);
        String token = request.getHeader("token");
        if (token == null){
            for (int i = 0; i < products.size(); i++) {
                products.get(i).setPermission(false);
            }
            return products;
        }
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        for (int i = 0; i < products.size(); i++) {
            products.get(i).setPermission(riskRuleService.firstFilter(user,products.get(i).getId()));
        }
        return products;
    }

    /**
     * @title 获取指定二级分类存款产品
     * @description
     * @author zzh
     * @updateTime 2022/3/18 21:41
     * @throws
     */
    public List<ProductMiaoShaVo> getDepositByTag2(HttpServletRequest request,String tag,String tag2){
        List<ProductMiaoShaVo> products = productDao.getProductByTag2(tag,tag2);
//        List<ProductMiaoShaVo> products = productDao.getProductByTag(tag);
        String token = request.getHeader("token");
        if (token == null){
            for (int i = 0; i < products.size(); i++) {
                products.get(i).setPermission(false);
            }
            return products;
        }
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        for (int i = 0; i < products.size(); i++) {
            products.get(i).setPermission(riskRuleService.firstFilter(user,products.get(i).getId()));
        }
        return products;
    }
    
    /**
     * @title 库存扣减
     * @description 
     * @author zzh 
     * @updateTime 2022/3/20 16:34 
     * @throws 
     */
    public boolean reduceStock(ProductMiaoShaVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        int ret = productDao.reduceStock(g);
        return ret > 0;
    }
    /**
     * @title 获取秒杀产品
     * @description
     * @author zzh
     * @updateTime 2022/3/21 14:26
     * @throws
     */
    public ProductMiaoShaVo getGoodsVoByGoodsId(long id) {
        return productDao.getProductMiaoShaVoByid(id);
    }
    /**
     * @title 商品销量增加
     * @description 
     * @author zzh 
     * @updateTime 2022/3/20 20:26
     * @throws 
     */
    
    /**
     * @title 添加秒杀产品
     * @description 
     * @author zzh 
     * @updateTime 2022/3/22 21:54
     * @throws 
     */

    public int addProductMiaoSha(ProductMiaoShaVo productMiaoShaVo) {
        return productDao.addMiaoshaProduct(productMiaoShaVo);
    }

    /**
     * @title 更新秒杀产品
     * @description
     * @author zzh
     * @updateTime 2022/3/22 21:54
     * @throws
     */

    public int updateProductMiaosha(ProductMiaoShaVo productMiaoShaVo) {
        return productDao.updateMiaoshaProduct(productMiaoShaVo);
    }

    /**
     * @title 删除秒杀产品
     * @description
     * @author zzh
     * @updateTime 2022/3/22 21:54
     * @throws
     */

    public int deleteProductMiaosha(Long id) {
        return productDao.deleteMiaoshaProduct(id);
    }

}
