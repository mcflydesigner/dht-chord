package ru.dht.dhtchord.spring.listener;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DhtEventListener {

    private final Integer nodeId;

    public DhtEventListener(@Value("${dht.node.id}") Integer nodeId) {
        this.nodeId = nodeId;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logNodeInfo() {
        log.info("Node with nodeId = {} started", nodeId);
    }

}
