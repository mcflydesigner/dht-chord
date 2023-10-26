package ru.dht.dhtchord.spring.configuration;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtChordRing;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.DhtNodeImpl;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.hash.SHA1HashSpace;
import ru.dht.dhtchord.core.storage.InMemoryStorage;
import ru.dht.dhtchord.core.storage.KeyValueStorage;
import ru.dht.dhtchord.spring.client.DhtClient;

import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

@Configuration("dhtNodeConfiguration")
public class DhtNodeConfiguration {

    private final String nodeId;
    private final String address;
    private final String joinAddress;
    private final DhtClient dhtClient;

    public DhtNodeConfiguration(@Value("${dht.node.id}") String nodeId,
                                @Value("${dht.node.address}") String address,
                                @Value("${dht.node.joinAddress}") String joinAddress,
                                DhtClient dhtClient) {
        if (Strings.isBlank(address)) {
            throw new IllegalArgumentException("Address must be present");
        }
        this.address = address;
        this.nodeId = nodeId != null ? nodeId : address;
        this.joinAddress = joinAddress;
        this.dhtClient = dhtClient;
    }

//    @Bean
//    @ConfigurationProperties(prefix = "dht.known-nodes")
//    Map<String, String> knownNodesConfig() {
//        return new HashMap<>();
//    }

    @Bean
    HashSpace hashSpace() {
        return new SHA1HashSpace();
    }

    @Bean
    KeyValueStorage keyValueStorage() {
        return new InMemoryStorage();
    }

    @Bean
    DhtNode dhtNode(
            DhtNodeMeta selfMeta,
            HashSpace hashSpace,
            DhtNodeClient dhtNodeClient,
            KeyValueStorage keyValueStorage
    ) {
        return Strings.isBlank(joinAddress) ?
                DhtNodeImpl.buildSingleNode(hashSpace, selfMeta, dhtNodeClient, keyValueStorage)
                : DhtNodeImpl.join(hashSpace, new DhtNodeAddress(joinAddress), dhtNodeClient, keyValueStorage);
    }

//    @Bean
//    DhtChordRing dhtChordRing(
//            DhtNodeMeta selfMeta,
//            DhtNode dhtNode,
//            TreeSet<HashKey> knownNodeKeys,
//            AtomicReference<Map<HashKey, DhtNodeAddress>> nodeAddressesMapRef
//    ) {
//        return new DhtChordRing(selfMeta, dhtNode, knownNodeKeys, dhtNodeClient(), nodeAddressesMapRef);
//    }

    @Bean
    DhtNodeMeta selfMeta(HashSpace hashSpace) {
        return new DhtNodeMeta(nodeId, hashSpace.hash(nodeId), new DhtNodeAddress(address));
    }

//    @Bean
//    TreeSet<HashKey> knownNodeKeys(
//            AtomicReference<Map<HashKey, DhtNodeAddress>> nodeAddressesMapRef
//    ) {
//        return new TreeSet<>(nodeAddressesMapRef.get().keySet());
//    }

    @Bean
    DhtNodeClient dhtNodeClient() {
        return new DhtNodeClient(dhtClient, null);
    }

//    @Bean
//    AtomicReference<Map<HashKey, DhtNodeAddress>> nodeAddressesMapRef(
//            HashSpace hashSpace,
//            DhtNodeMeta selfMeta
//    ) {
//        Map<HashKey, DhtNodeAddress> map = knownNodesConfig().entrySet().stream().collect(Collectors.toMap(
//                e -> hashSpace.hash(e.getKey()),
//                e -> new DhtNodeAddress(e.getValue())
//        ));
//        // Adding current node to the map
//        map.put(selfMeta.getKey(), selfMeta.getAddress());
//
//        return new AtomicReference<>(map);
//    }

}
