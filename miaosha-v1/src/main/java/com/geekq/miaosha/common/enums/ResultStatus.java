package com.geekq.miaosha.common.enums;

/**
 * @title
 * @description
 *  * 普通返回类
 *  * 1打头 系统系列错误
 *  * 2 注册登录系列错误
 *  * 3 check 系列错误
 *  * 4 秒杀错误
 *  * 5 商品错误
 *  * 6 订单错误
 *  *
 * @author zzh
 * @updateTime 2022/3/17 22:29
 * @throws
 */
public enum ResultStatus {
    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
//    FAILD(-1, "失败"),
    EXCEPTION(-1, "系统异常"),
    PARAM_ERROR(10000, "参数错误"),
    SYSTEM_ERROR(10001, "系统错误"),
    FILE_NOT_EXIST(10002, "文件不存在"),
    FILE_NOT_DOWNLOAD(10003, "文件没有下载"),
    FILE_NOT_GENERATE(10004, "文件没有生成"),
    FILE_NOT_STORAGE(10005, "文件没有入库"),
    SYSTEM_DB_ERROR(10006, "数据库系统错误"),
    FILE_ALREADY_DOWNLOAD(10007, "文件已经下载"),
    DATA_ALREADY_PEXISTS(10008, "数据已经存在"),


    /**
     * 注册登录
     */
    RESIGETR_SUCCESS(0, "注册成功!"),
    RESULT_EXIST(103,"用户名已注册"),
    IDCARD_MATCH_FAIL(105,"身份证不匹配"),
    RESIGETER_FAIL(200001, "注册失败!"),
    CODE_FAIL(104, "验证码错误!"),



    /**
     * check
     */
    BIND_ERROR(30001, "参数校验异常：%s"),
    ACCESS_LIMIT_REACHED(30002, "请求非法!"),
    REQUEST_ILLEGAL(30004, "访问太频繁!"),
    SESSION_ERROR(30005, "Session不存在或者已经失效!"),
    PASSWORD_EMPTY(30006, "登录密码不能为空!"),
    MOBILE_EMPTY(30007, "手机号不能为空!"),
    MOBILE_ERROR(30008, "手机号格式错误!"),
    MOBILE_NOT_EXIST(30009, "手机号不存在!"),
    PASSWORD_ERROR(102, "密码错误!"),
    USER_NOT_EXIST(101, "用户不存在！"),

    /**
     * 存款模块
     */
    CATEGORY_ONE_NOT_EXIST(106,"一级分类不存在"),
    CATEGORY_TWO_NOT_EXIST(107,"二级分类不存在"),
    GOODS_NOT_EXIST(108,"不存在该商品"),
    BALANCE_NOT_ENOUGH(109,"余额不足"),
    /**
     * 订单模块
     */
    ORDER_NOT_EXIST(60001, "订单不存在"),
    ORDER_PAYED(60002, "订单已支付"),
    /**
     * 秒杀模块
     */
    MIAO_SHA_OVER(40001, "商品已经秒杀完毕"),
    REPEATE_MIAOSHA(40002, "不能重复秒杀"),
    MIAOSHA_FAIL(40003, "秒杀失败"),
    NOT_QUALIFIED(40004, "没有秒杀资格"),

    /**
     * 商品模块
     */
    PRODUCT_NOT_EMPTY(1001,"产品不能为空"),
    PRODUCT_NOT_EXIST(1002,"产品不存在"),

    DATA_ILLEGAL_EXCEPTION(7000,"数据格式有误"),
    DATA_NOT_NULL_EXCEPTION(7001,"数据不能为空"),
    /*
     * 分类
     */
    TYPE_NOT_EMPTY(1003,"分类不能为空"),
    /**
     * 准入初筛规则模块
     */
    RULE_NOT_EMPTY(2001,"规则信息不能为空"),
    RULE_NOT_EXIST(2002,"规则不存在"),
    PRODUCT_OR_RULE_NOT_EXIST(2003,"秒杀产品或准入初筛规则不存在");
    private int code;
    private String message;

    private ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private ResultStatus(Object... args) {
        this.message = String.format(this.message, args);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return this.name();
    }

    public String getOutputName() {
        return this.name();
    }

    public String toString() {
        return this.getName();
    }
}
