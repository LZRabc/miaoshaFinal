package com.geekq.miaosha.common.enums;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName RiskRuleStatus.java
 * @Description TODO
 * @createTime 2022年04月02日 21:25:00
 */
public class RiskRuleStatus {
    public enum WorkStatus{
        NO_WORK(0, "无业/失业"), WORKING(1, "正在就业");

        private Integer code;
        private String msg;

        WorkStatus(Integer code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum OverDueStatus{
        OVERDUEe(0, "逾期记录过多");

        private Integer code;
        private String msg;

        OverDueStatus(Integer code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum AgeStatus{
        YOUNG(0, "年龄过小");

        private Integer code;
        private String msg;

        AgeStatus(Integer code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum UnTrustStatus{
        UN_TRUST(0, "失信人员");

        private Integer code;
        private String msg;

        UnTrustStatus(Integer code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
