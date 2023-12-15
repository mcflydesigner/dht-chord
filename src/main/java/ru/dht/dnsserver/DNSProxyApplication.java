package ru.dht.dnsserver;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Master;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;

import java.io.InputStream;

@SpringBootApplication
@ComponentScan({"ru.dht.dnsserver", "ru.dht.dhtchord"})
public class DNSProxyApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(DNSProxyApplication.class, args);
    }
}