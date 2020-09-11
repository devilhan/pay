package com.devil.pay.controller;

import com.devil.pay.pojo.PayInfo;
import com.devil.pay.service.impl.PayServiceImpl;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hanyanjiao
 * @date 2020/5/13
 */

@Slf4j
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId, @RequestParam("amount") BigDecimal amount, @RequestParam("payType") BestPayTypeEnum type){
        Map<String,String> map = new HashMap<>();

        PayResponse response = payService.create(orderId,amount,type);
//        支付方式不同渲染不同
        switch (type){
            case WXPAY_NATIVE:
                map.put("codeUrl",response.getCodeUrl());
                map.put("orderId",orderId);
                map.put("returnUrl",wxPayConfig.getReturnUrl());
                return new ModelAndView("createForWxNative",map);
            case ALIPAY_PC:
                map.put("body",response.getBody());
                return new ModelAndView("createForAliPayPC",map);
            default:
                throw new RuntimeException("当前支付方式尚未开通，敬请期待...");
        }
    }


    @PostMapping("/notify")
    @ResponseBody
    public void asyncNotify(@RequestBody String notifyData){
        log.info("notifyData ->{}",notifyData);
        payService.asyncNotify(notifyData);
    }

    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam("orderId") String orderId){
        return payService.queryByOrderId(orderId);
    }
}
