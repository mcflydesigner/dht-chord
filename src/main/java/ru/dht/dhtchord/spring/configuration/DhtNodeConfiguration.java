package ru.dht.dhtchord.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtChordRing;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.DhtNodeImpl;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
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
    private final String address;
    private final DhtClient dhtClient;

    public DhtNodeConfiguration(@Value("${dht.node.id}") Integer nodeId,
                                @Value("${dht.m}") Integer m,
                                @Value("${dht.node.address}") String address,
                                DhtClient dhtClient) {
        this.nodeId = nodeId;
        this.m = m;
        this.address = address;
        this.dhtClient = dhtClient;
    }

    @Bean
    @ConfigurationProperties(prefix = "dht.nodes")
    Map<String, String> dhtNodesConfig() {
        return new HashMap<>();
    }

    @Bean
    DhtNode dhtNode() {
        return DhtNodeImpl.build(m, nodeId, nodeIds(), dhtNodeClient());
    }

    @Bean
    DhtChordRing dhtChordRing() {
        return new DhtChordRing(m, dhtNodeMeta(), dhtNode(), nodeIds(), dhtNodeClient(), nodeAddressesMapRef());
    }

    @Bean
    DhtNodeMeta dhtNodeMeta() {
        return new DhtNodeMeta(nodeId, new DhtNodeAddress(address));
    }

    @Bean
    TreeSet<Integer> nodeIds() {
        return new TreeSet<>(nodeAddressesMapRef().get().keySet());
    }

    @Bean
    DhtNodeClient dhtNodeClient() {
        return new DhtNodeClient(dhtClient, nodeAddressesMapRef());
    }

    @Bean
    AtomicReference<Map<Integer, DhtNodeAddress>> nodeAddressesMapRef() {
        Map<Integer, DhtNodeAddress> map = dhtNodesConfig().entrySet().stream().collect(Collectors.toMap(
                e -> Integer.valueOf(e.getKey()),
                e -> new DhtNodeAddress(e.getValue())
        ));
        // Adding current node to the map
        DhtNodeMeta dhtNodeMeta = dhtNodeMeta();
        map.put(dhtNodeMeta.getNodeId(), dhtNodeMeta.getDhtNodeAddress());

        return new AtomicReference<>(map);
    }

}
