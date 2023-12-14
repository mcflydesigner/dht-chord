package ru.dht.dnsserver;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Master;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;

import java.io.InputStream;

@SpringBootApplication
public class DNSProxyApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(DNSProxyApplication.class, args);

//        ClassPathResource zoneFile = new ClassPathResource("st7.sne23.ru.zone");
//        try (Master master = new Master(zoneFile.getInputStream())) {
//            while (true) {
//                Record rec = master.nextRecord();
//                if (rec == null) break;
////                rec = Record.fromString(Name.fromString("a."), 0, 0, 0, rec.toString(), null);
//                System.out.println(rec.toString());
//            }
//        }
    }
}