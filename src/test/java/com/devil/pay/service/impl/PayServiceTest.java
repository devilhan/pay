package com.devil.pay.service.impl;

import com.devil.pay.PayApplicationTests;
import com.devil.pay.enums.PayPlatformEnum;
import com.devil.pay.pojo.PayInfo;
import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PayServiceTest extends PayApplicationTests {

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void create() {
        payService.create("123456789456123",BigDecimal.valueOf(0.01),BestPayTypeEnum.WXPAY_NATIVE);
    }


    @Test
    public void sendMQMsg(){
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(1591253898449L);
        payInfo.setPayAmount(BigDecimal.valueOf(0.01));
        payInfo.setPayPlatform(PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
        amqpTemplate.convertAndSend("payNotify",new Gson().toJson(payInfo));
    }
}