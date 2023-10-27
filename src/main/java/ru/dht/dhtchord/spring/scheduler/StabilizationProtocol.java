package ru.dht.dhtchord.spring.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.dht.dhtchord.core.DhtNode;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Component
@Slf4j
public class StabilizationProtocol {

    private final DhtNode dhtNode;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void stabilize() {
        dhtNode.stabilize();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void fixFingers() {
        dhtNode.fixFinger();
    }

}
