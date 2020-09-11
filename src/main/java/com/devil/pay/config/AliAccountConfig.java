package com.devil.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Hanyanjiao
 * @date 2020/5/14
 */

@Component
@ConfigurationProperties(prefix = "ali")
@Data
public class AliAccountConfig {
    private String appId;

    private String privateKey;

    private String aliPayPublicKey;

    private String returnUrl;

    private String notifyUrl;

}
