package ru.dht.dhtchord.spring.configuration;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtChordRing;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.DhtNodeImpl;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.hash.SHA1HashSpace;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Configuration("dhtNodeConfiguration")
public class DhtNodeConfiguration {

    private final String nodeId;
    private final String address;
    private final DhtClient dhtClient;

    public DhtNodeConfiguration(@Value("${dht.node.id}") String nodeId,
                                @Value("${dht.node.address}") String address,
                                DhtClient dhtClient) {
        if (Strings.isBlank(address)) {
            throw new IllegalArgumentException("Address must be present");
        }
        this.address = address;
        this.nodeId = nodeId != null ? nodeId : address;
        this.dhtClient = dhtClient;
    }

    @Bean
    @Primary
    HashSpace hashSpace() {
        return new SHA1HashSpace();
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
