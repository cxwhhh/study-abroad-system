package com.study.service.rebate;

import lombok.Data;

/**
 * 返点申请请求类
 */
@Data
public class RebateApplicationRequest {
    
    /**
     * 申请ID
     */
    private Long applicationId;
    
    /**
     * 学校名称
     */
    private String schoolName;
    
    /**
     * 专业名称
     */
    private String majorName;
    
    /**
     * 支付方式 (bank_transfer, alipay, wechat_pay)
     */
    private String paymentMethod;
    
    /**
     * 银行账号
     */
    private String accountNumber;
    
    /**
     * 数字账户 (支付宝/微信账号)
     */
    private String digitalAccount;
    
    /**
     * 证明文件路径
     */
    private String proofFilePath;
    
    /**
     * 是否同意协议
     */
    private Boolean agreementAccepted;
    
    /**
     * 备注
     */
    private String notes;
}