package com.devil.pay.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Hanyanjiao
 * @date 2020/5/13
 */

@Component
public class BestPayConfig {

    @Autowired
    private WxAccountConfig wxAccountConfig;

    @Autowired
    private AliAccountConfig aliAccountConfig;

//    Spring 容器管理功能IOC，在程序启动的时候就执行
    @Bean
    public BestPayService bestPayService(WxPayConfig wxPayConfig){

        //支付宝配置
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(aliAccountConfig.getAppId());
        aliPayConfig.setPrivateKey(aliAccountConfig.getPrivateKey());
        aliPayConfig.setAliPayPublicKey(aliAccountConfig.getAliPayPublicKey());
        aliPayConfig.setReturnUrl(aliAccountConfig.getReturnUrl());
        aliPayConfig.setNotifyUrl(aliAccountConfig.getNotifyUrl());

        //支付类, 所有方法都在这个类里
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }

    @Bean
    public WxPayConfig wxPayConfig(){
        //微信支付配置
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wxAccountConfig.getAppId());          //公众号Id
        wxPayConfig.setMiniAppId(wxAccountConfig.getMiniAppId());      //小程序Id
        wxPayConfig.setAppAppId(wxAccountConfig.getAppAppId());       //移动AppId

        //支付商户资料
        wxPayConfig.setMchId(wxAccountConfig.getMchId());  //商户Id
        wxPayConfig.setMchKey(wxAccountConfig.getMchKey());   //商户密钥
        wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl());      //接受支付平台异步通知的地址
        wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());  //支付平台返回地址

        return wxPayConfig;
    }
}