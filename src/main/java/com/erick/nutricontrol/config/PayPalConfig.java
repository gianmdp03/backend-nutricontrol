package com.erick.nutricontrol.config;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.Environment;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public PaypalServerSdkClient paypalClient() {
        Environment env = mode.equalsIgnoreCase("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION;

        return new PaypalServerSdkClient.Builder()
                .environment(env)
                .clientCredentialsAuth(
                        new ClientCredentialsAuthModel.Builder(clientId, clientSecret).build()
                )
                .build();
    }
}