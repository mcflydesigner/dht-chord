package ru.dht.dhtchord.spring.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtChordRing;

@Component
@AllArgsConstructor
@Slf4j
public class DhtEventListener {

    private final DhtChordRing dhtChordRing;
    private final DhtNodeMeta dhtNodeMeta;

    @EventListener(ApplicationStartedEvent.class)
    public void registerCurrentNode() {
        // Notify other nodes to update their predecessors and finger tables
        log.info("Registering current node");
        dhtChordRing.registerCurrentNode();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void transferDataFromSuccessorNode() {
        log.info("Requesting successor node to transfer data");
        dhtChordRing.requestToTransferDataToNode()
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logNodeStartedInfo() {
        log.info("Node with nodeId = {} started. Listening to {}", dhtNodeMeta.getNodeId(),
                dhtNodeMeta.getDhtNodeAddress().getAddress());
    }

}
