package ru.dht.dhtchord.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dht.dhtchord.core.*;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.storage.KeyValueInMemoryStorage;
import ru.dht.dhtchord.core.storage.KeyValueStorage;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Configuration("dhtNodeConfiguration")
public class DhtNodeConfiguration {

    private final Integer nodeId;
    private final Integer m;
    private final DhtClient dhtClient;

    public DhtNodeConfiguration(@Value("${dht.node.id}") Integer nodeId,
                                @Value("${dht.m}") Integer m,
                                DhtClient dhtClient) {
        this.nodeId = nodeId;
        this.m = m;
        this.dhtClient = dhtClient;
    }

    @Bean
    @ConfigurationProperties(prefix = "dht.nodes")
    Map<String, String> dhtNodesConfig() {
        return new HashMap<>();
    }

    @Bean
    KeyValueStorage dhtStorage() {
        return new KeyValueInMemoryStorage();
    }

    @Bean
    DhtNode dhtNode() {
        return DhtNodeImpl.build(m, nodeId, nodeIds(), fingerTableRef(), dhtNodeClient());
    }

    @Bean
    AtomicReference<FingerTable> fingerTableRef() {
        return new AtomicReference<>(FingerTable.buildTable(m, nodeId, nodeIds()));
    }

    @Bean
    TreeSet<Integer> nodeIds() {
        return dhtNodesConfig().keySet().stream().map(Integer::valueOf).collect(
                Collectors.toCollection(TreeSet::new)
        );
    }

    @Bean
    DhtNodeClient dhtNodeClient() {
        return new DhtNodeClient(dhtClient, nodeAddressesMapRef());
    }

    @Bean
    AtomicReference<Map<Integer, DhtNodeAddress>> nodeAddressesMapRef() {
        return new AtomicReference<>(
                dhtNodesConfig().entrySet().stream().collect(Collectors.toMap(
                        e -> Integer.valueOf(e.getKey()),
                        e -> new DhtNodeAddress(e.getValue())
                ))
        );
    }


}
