package ru.dht.dhtchord.spring.security;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@AllArgsConstructor
@Configuration
public class RestConfiguration {

    private final NodeCredentialsConfig nodeCredentialsConfig;

    private static final Duration TIMEOUT = Duration.ofMillis(5000);

    @Bean
    RestTemplate rest() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setReadTimeout(TIMEOUT)
                .setConnectTimeout(TIMEOUT)
                .build();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(nodeCredentialsConfig.getUsername(), nodeCredentialsConfig.getPassword())
        );
        return restTemplate;
    }

}
