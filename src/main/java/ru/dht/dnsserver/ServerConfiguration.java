package ru.dht.dnsserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.client.RestTemplate;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.spring.client.DhtClient;
import ru.dht.dhtchord.spring.client.RestTemplateDhtClient;
import ru.dht.dhtchord.spring.configuration.DhtNodeConfiguration;
import ru.dht.dhtchord.spring.controller.DhtStorageController;
import ru.dht.dhtchord.spring.security.NodeCredentialsConfig;
import ru.dht.dhtchord.spring.security.RestConfiguration;
import ru.dht.dhtchord.spring.security.SpringSecurityConfig;
import ru.dht.dnsserver.resolver.DhtResolver;
import ru.dht.dnsserver.resolver.DNSResolver;

import java.net.SocketException;

@Configuration
@Import({
//        DhtNodeConfiguration.class,
//        RestConfiguration.class,
//        NodeCredentialsConfig.class,
//        RestTemplateDhtClient.class,
//        SpringSecurityConfig.class,
//        DhtStorageController.class
})
public class ServerConfiguration {
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor(); // Or use another one of your liking
    }

    @Bean
    public CommandLineRunner serverRunner(DNSServer server, TaskExecutor taskExecutor) {
        return args -> taskExecutor.execute(server);
    }

    @Bean
    public CommandLineRunner enricherRunner(ZoneFileEnricher enricher, TaskExecutor taskExecutor) {
        return args -> taskExecutor.execute(enricher);
    }

    @Bean
    public ZoneFileEnricher zoneFileEnricher(
            @Value("${dns.enricher.enabled:false}") boolean enabled,
            @Value("${dns.enricher.zonefile:}") String zoneFile,
            DhtUnhashedLocalClient client
    ) {
        return new ZoneFileEnricher(enabled, zoneFile, client);
    }

    @Bean
    public DNSServer dnsServer(@Value("${dns.server.port:53}") int port,
                               DNSResolver resolver) throws SocketException {
        return new DNSServer(port, resolver);
    }

    @Bean
    public DNSResolver dhtResolver(DhtUnhashedLocalClient dhtClient) {
        return new DhtResolver(dhtClient);
    }

}
