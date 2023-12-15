package ru.dht.dhtchord.spring.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "dht.node.security")
@Component
@Getter
@Setter
public class NodeCredentialsConfig {
    private String username;
    private String password;
}
