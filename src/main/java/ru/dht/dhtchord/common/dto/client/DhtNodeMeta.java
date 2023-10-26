package ru.dht.dhtchord.common.dto.client;

import lombok.Data;
import ru.dht.dhtchord.core.hash.HashKey;

@Data
public class DhtNodeMeta {

    private final String nodeId;
    private final HashKey key;
    private final DhtNodeAddress address;

}
