package ru.dht.dhtchord.spring.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.DhtChordRing;
import ru.dht.dhtchord.core.exception.NodeJoinException;

@Component
@AllArgsConstructor
@Slf4j
public class DhtEventListener {

    private final DhtChordRing dhtChordRing;
    private final DhtNodeMeta dhtNodeMeta;

    @EventListener(ApplicationStartedEvent.class)
    @Order(0)
    public void registerCurrentNode() throws NodeJoinException {
        // Notify other nodes to update their predecessors and finger tables
        log.info("Registering current node");
        dhtChordRing.registerCurrentNode();
    }

    @EventListener(ApplicationStartedEvent.class)
    @Order(1)
    public void transferDataFromSuccessorNode() {
        log.info("Requesting successor node to transfer data");
        boolean result = dhtChordRing.initDataCurrentNode();

        if (!result) {
            log.error("Failed to transfer data from the successor node.");
            throw new IllegalStateException(
                    String.format("Failed to initialize data for the nodeId = %s", dhtNodeMeta.getNodeId())
            );
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logNodeStartedInfo() {
        log.info("Node with nodeId = {} started. Listening to {}", dhtNodeMeta.getNodeId(),
                dhtNodeMeta.getDhtNodeAddress().getAddress());
    }

}
