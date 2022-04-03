package com.geekq.miaosha.controller;

import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.domain.Product;
import com.geekq.miaosha.service.ProductService;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.MiaoshaRuleVo;
import com.geekq.miaosha.vo.ProductMiaoShaVo;
import com.geekq.miaosha.vo.ProductRecommendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName ProductController.java
 * @Description TODO
 * @createTime 2022年03月18日 17:07:00
 */
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @ResponseBody
    @PostMapping("/add")
    public ResultGeekQ<Product> addProduct(@RequestBody Product product){
        ResultGeekQ<Product> result = ResultGeekQ.build();
        long id = System.currentTimeMillis();
        product.setId(id);
        productService.createProduct(product);
        result.success();
        return result;
    }
    @ResponseBody
    @PostMapping("/updateProduct")
    public ResultGeekQ<Integer> updateProduct(@RequestBody Product product){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        int res = productService.updateProduct(product);
        result.setData(res);
        result.success();
        return result;
    }
    @ResponseBody
    @GetMapping("/list")
    public ResultGeekQ<List<Product>> listProducts(){
        ResultGeekQ<List<Product>> result = ResultGeekQ.build();
        List<Product> products = productService.listProducts();
        result.success();
        result.setData(products);
        return result;
    }
    @ResponseBody
    @GetMapping("/listMiaoSha")
    public ResultGeekQ<List<ProductMiaoShaVo>> listProductMiaoshas(HttpServletRequest request){
        ResultGeekQ<List<ProductMiaoShaVo>> result = ResultGeekQ.build();
        List<ProductMiaoShaVo> products = productService.listProductMiaoshas(request);
        result.success();
        result.setData(products);
        return result;
    }
    @ResponseBody
    @GetMapping("/getHomeDeposit")
    public ResultGeekQ<List<ProductRecommendVo>> getHomeDeposit(){
        ResultGeekQ<List<ProductRecommendVo>> result = ResultGeekQ.build();
        List<ProductRecommendVo> products = productService.listProductRecommend();
        result.success();
        result.setData(products);
        return result;
    }
    @ResponseBody
    @GetMapping("/getDeposit")
    public ResultGeekQ<ProductMiaoShaVo> getProduct(@RequestParam("id")long id){
        ResultGeekQ<ProductMiaoShaVo> result = ResultGeekQ.build();
        ProductMiaoShaVo productMiaoShaVo = productService.getGoodsVoByGoodsId(id);
        if(productMiaoShaVo==null) {
            result.withError(ResultStatus.GOODS_NOT_EXIST.getCode(),ResultStatus.GOODS_NOT_EXIST.getMessage());
            return result;
        }
        result.success();
        result.setData(productMiaoShaVo);
        return result;
    }
    @ResponseBody
    @GetMapping("/getDepositByTag")
    public ResultGeekQ<List<ProductMiaoShaVo>> getDepositByTag(HttpServletRequest request,@RequestParam("tag")String tag){
        ResultGeekQ<List<ProductMiaoShaVo>> result = ResultGeekQ.build();
        List<ProductMiaoShaVo> products = productService.getDepositByTag( request,tag);
        result.setData(products);
        result.success();
        return result;
    }
    @ResponseBody
    @GetMapping("/getDepositByTag2")
    public ResultGeekQ<List<ProductMiaoShaVo>> getDepositByTag2(HttpServletRequest request,
                                                        @RequestParam("tag")String tag,
                                                       @RequestParam("tag2")String tag2){
        ResultGeekQ<List<ProductMiaoShaVo>> result = ResultGeekQ.build();
        List<ProductMiaoShaVo> products = productService.getDepositByTag2(request,tag,tag2);
        result.setData(products);
        result.success();
        return result;
    }
    /*
    @ResponseBody
    @PostMapping("/admin/addProductMiaosha")
    public ResultGeekQ<Integer> addProductMiaosha(@RequestBody ProductMiaoShaVo productMiaoShaVo){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        productService.addProductMiaoSha(productMiaoShaVo);
        result.success();
        return result;
    }*/
    @ResponseBody
    @PostMapping("/admin/updateProductMiaosha")
    public ResultGeekQ<Integer> updateProductMiaosha(@RequestBody ProductMiaoShaVo productMiaoShaVo){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        productService.updateProductMiaosha(productMiaoShaVo);
        result.success();
        return result;
    }

    @ResponseBody
    @GetMapping("/admin/deleteProductMiaosha")
    public ResultGeekQ<Integer> deleteProductMiaosha(@RequestParam("id")Long id){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        productService.deleteProductMiaosha(id);
        result.success();
        return result;
    }
    @ResponseBody
    @GetMapping("/admin/createProduct")
    public ResultGeekQ<Integer> createProduct(@RequestBody Product product){
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        productService.createProduct(product);
        result.success();
        return result;
    }





}
