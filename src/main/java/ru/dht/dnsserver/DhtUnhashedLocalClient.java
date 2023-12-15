package ru.dht.dnsserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.connection.DhtNodeClient;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;
import ru.dht.dhtchord.spring.client.DhtClient;

@RequiredArgsConstructor
@Component
public class DhtUnhashedLocalClient {
    private final DhtNodeClient dhtClient;
    private final DhtNodeMeta selfMeta;
    private final HashSpace hashSpace;

    public void storeData(String key, String value) {
        HashKey hashedKey = hashSpace.hash(key);
        dhtClient.storeDataToNode(selfMeta, hashedKey, value);
    }

    public String getData(String key) {
        HashKey hashedKey = hashSpace.hash(key);
        return dhtClient.getDataFromNode(selfMeta, hashedKey);
    }
}
