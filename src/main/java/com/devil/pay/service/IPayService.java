package com.devil.pay.service;

import com.devil.pay.pojo.PayInfo;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;

/**
 * @author Hanyanjiao
 * @date 2020/5/13
 */

public interface IPayService {
    /**
     * 创建/发起支付
     */

    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum payTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     */
    String asyncNotify(String notifyData);

    /**
     * 查询支付记录（通过订单号）
     * @param orderId
     * @return
     */
    PayInfo queryByOrderId(String orderId);
}
