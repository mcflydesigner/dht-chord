package ru.dht.dhtchord.spring.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Configuration
public class RestConfiguration {

    private final NodeCredentialsConfig nodeCredentialsConfig;

    @Bean
    RestTemplate rest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(nodeCredentialsConfig.getUsername(), nodeCredentialsConfig.getPassword())
        );
        return restTemplate;
    }

}
