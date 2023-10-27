package ru.dht.dhtchord.spring.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "dht.node.security")
@Getter
@Setter
public class NodeCredentialsConfig {
    private String username;
    private String password;
}
