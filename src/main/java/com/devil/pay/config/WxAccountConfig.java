package com.devil.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Hanyanjiao
 * @date 2020/5/14
 */

@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {

    private String appId;

    private String miniAppId;

    private String appAppId;

    private String mchId;

    private String mchKey;

    private String notifyUrl;

    private String returnUrl;
}
