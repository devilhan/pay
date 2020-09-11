package com.devil.pay.service.impl;

import com.devil.pay.dao.PayInfoMapper;
import com.devil.pay.enums.PayPlatformEnum;
import com.devil.pay.pojo.PayInfo;
import com.devil.pay.service.IPayService;
import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

/**
 * @author Hanyanjiao
 * @date 2020/5/13
 */

@Slf4j
@Service
public class PayServiceImpl implements IPayService {

    public static final String QUEUE_PAY_NOTIFY = "payNotify";

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 创建/发起支付
     * @param orderId
     * @param amount
     * @param payTypeEnum
     * @return
     */
    @Override
    public PayResponse create(String orderId, BigDecimal amount,BestPayTypeEnum payTypeEnum) {

        PayInfo info = payInfoMapper.selectByOrderNo(Long.valueOf(orderId));

        if(info == null) {
            //写入数据库
            PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                    PayPlatformEnum.getByBestPayTypeEnum(payTypeEnum).getCode(),
                    OrderStatusEnum.NOTPAY.name(), amount);

            payInfoMapper.insertSelective(payInfo);
        }

        /*if(payTypeEnum != BestPayTypeEnum.WXPAY_NATIVE &&
                payTypeEnum != BestPayTypeEnum.ALIPAY_PC){
            throw new RuntimeException("暂不支持的支付类型");
        }*/

        //发起支付
        PayRequest payRequest = new PayRequest();
        payRequest.setPayTypeEnum(payTypeEnum);
        payRequest.setOrderId(orderId);
        payRequest.setOrderName("1198811-韩艳姣支付练习");
        payRequest.setOrderAmount(amount.doubleValue());
//        payRequest.setOpenid("openid_xxxxxx");
        PayResponse response = bestPayService.pay(payRequest);
        log.info("发起支付：response={}",response);
        return  response;
    }

    @Override
    public String asyncNotify(@RequestBody String notifyData) {
        //签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("支付通知：payResponse={}",payResponse);

        //金额校验（从数据库查订单）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));

        //比较严重(正常情况下不会发生的) 发出告警 钉钉，短信
        if (payInfo == null){
            //发短信通知
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }

        //如果订单状态不是支付成功
        if(!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS)){
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0){
                throw new RuntimeException("异步通知中的金额与数据库中的不一致,orderNo is "+payResponse.getOrderId());
            }

            //校验通过后修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }
        //pay发送MQ消息，mall接收MQ消息
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY,new Gson().toJson(payInfo));

        if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            //告诉微信不要再通知了
            return "<xml> \n" +
                    "\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml> \n";
        }else if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            return "success";
        }

        throw  new RuntimeException("异步通知中错误的支付平台");
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
