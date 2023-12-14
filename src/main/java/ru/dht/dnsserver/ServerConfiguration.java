package ru.dht.dnsserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import ru.dht.dhtchord.spring.client.DhtClient;
import ru.dht.dhtchord.spring.configuration.DhtNodeConfiguration;
import ru.dht.dnsserver.resolver.DhtResolver;
import ru.dht.dnsserver.resolver.DNSResolver;

import java.net.SocketException;

@Configuration
@Import(DhtNodeConfiguration.class)
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
    public DNSServer dnsServer() throws SocketException {
        return new DNSServer(5354);
    }

//    @Bean
//    public DNSResolver chordResolver(DhtClient dhtClient) {
//        return new DhtResolver(dhtClient);
//    }
}
