package ru.dht.dhtchord.spring.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;

@Component
@AllArgsConstructor
@Slf4j
public class DhtEventListener {

    private final DhtNodeMeta dhtNodeMeta;

    @EventListener(ApplicationReadyEvent.class)
    public void logNodeStartedInfo() {
        log.info("Node with nodeId = {} started. Node key: {}. Listening to {}",
                dhtNodeMeta.getNodeId(), dhtNodeMeta.getKey(), dhtNodeMeta.getAddress().getAddress());
    }

}
