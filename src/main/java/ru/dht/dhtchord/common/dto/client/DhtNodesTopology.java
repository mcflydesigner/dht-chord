package ru.dht.dhtchord.common.dto.client;

import lombok.Data;

import java.util.Map;

@Data
public class DhtNodesTopology {

    private final Map<Integer, DhtNodeAddress> nodeAddresses;

}
