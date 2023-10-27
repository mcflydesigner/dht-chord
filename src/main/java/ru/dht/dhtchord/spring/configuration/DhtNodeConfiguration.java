package ru.dht.dhtchord.spring.configuration;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.dht.dhtchord.common.dto.client.DhtNodeAddress;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtNode;
import ru.dht.dhtchord.core.DhtNodeImpl;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.core.hash.SHA1HashSpace;
import ru.dht.dhtchord.core.storage.InMemoryStorage;
import ru.dht.dhtchord.core.storage.KeyValueStorage;
import ru.dht.dhtchord.spring.client.DhtClient;

@Configuration("dhtNodeConfiguration")
public class DhtNodeConfiguration {

    private final String nodeId;
    private final String address;
    private final String joinAddress;
    private final DhtClient dhtClient;

    public DhtNodeConfiguration(@Value("${dht.node.id}") String nodeId,
                                @Value("${dht.node.address}") String address,
                                @Value("${dht.node.joinAddress}") String joinAddress,
                                @Lazy DhtClient dhtClient) {
        if (Strings.isBlank(address)) {
            throw new IllegalArgumentException("Address must be present");
        }
        this.address = address;
        this.nodeId = nodeId != null ? nodeId : address;
        this.joinAddress = joinAddress;
        this.dhtClient = dhtClient;
    }

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
                : DhtNodeImpl.join(hashSpace, selfMeta, new DhtNodeAddress(joinAddress), dhtNodeClient, keyValueStorage);
    }


    @Bean
    DhtNodeMeta selfMeta(HashSpace hashSpace) {
        return new DhtNodeMeta(nodeId, hashSpace.hash(nodeId), new DhtNodeAddress(address));
    }

    @Bean
    DhtNodeClient dhtNodeClient() {
        return new DhtNodeClient(dhtClient);
    }

}
