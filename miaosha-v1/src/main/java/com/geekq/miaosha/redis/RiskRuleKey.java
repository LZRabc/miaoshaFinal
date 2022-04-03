package com.geekq.miaosha.redis;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName RiskRuleKey.java
 * @Description TODO
 * @createTime 2022年04月02日 20:04:00
 */
public class RiskRuleKey extends BasePrefix{
    public static RiskRuleKey getRiskRuleKey = new RiskRuleKey("riskRule");

    public static RiskRuleKey RiskRuleUser = new RiskRuleKey("UserRiskRule");
    public static RiskRuleKey RiskRuleProduct = new RiskRuleKey("ProductRiskRule");

    public RiskRuleKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public RiskRuleKey(String prefix) {
        super(prefix);
    }
}
