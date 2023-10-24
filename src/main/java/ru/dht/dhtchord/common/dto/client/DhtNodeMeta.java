package ru.dht.dhtchord.common.dto.client;

import lombok.Data;

@Data
public class DhtNodeMeta {

    private final int nodeId;
    private final DhtNodeAddress dhtNodeAddress;

}
