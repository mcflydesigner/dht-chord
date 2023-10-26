package ru.dht.dhtchord.core;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.dht.dhtchord.common.dto.client.DhtNodeMeta;
import ru.dht.dhtchord.core.hash.HashKey;
import ru.dht.dhtchord.core.hash.HashSpace;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class FingerTable {

    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    private final HashSpace hashSpace;
    private final DhtNodeMeta selfNode;
    @Getter
    private DhtNodeMeta predecessorNode;
    @Getter
    private final LinkedList<FingerEntry> fingerTable;
    @Getter
    private final int version;

    public DhtNodeMeta getImmediateSuccessor() {
        return fingerTable.isEmpty() ? selfNode : fingerTable.get(0).getNode();
    }

//    public static FingerTable buildTable(HashSpace hashSpace, HashKey selfKey,
//                                         TreeMap<HashKey, DhtNodeMeta> knownNodes) {
//        Utils.verifyNodesSetIsNotEmpty(knownNodes);
//
//        LinkedList<DhtNodeMeta> fingers = new LinkedList<>();
//        for (int i = 0; i < hashSpace.getBitLength(); i++) {
//
//        }
//        return new FingerTable(m, nodeId, nodes, fingers, atomicCounter.incrementAndGet());
//    }

    public static FingerTable buildForSingleNode(HashSpace hashSpace, DhtNodeMeta selfNode) {
        return new FingerTable(
                hashSpace,
                selfNode,
                selfNode,
                new LinkedList<>(),
                atomicCounter.incrementAndGet()
        );
    }

    public boolean isSuccessor(HashKey key) {
        return intervalContains(predecessorNode.getKey(), selfNode.getKey(), key);
    }

    public DhtNodeMeta findClosestPredecessor(HashKey key) {
        for (Iterator<FingerEntry> it = fingerTable.descendingIterator(); it.hasNext(); ) {
            FingerEntry f = it.next();
            if (intervalContains(f.intervalStart, f.intervalEnd, key)) {
                return f.node;
            }
        }
        return selfNode;
    }

    private static boolean intervalContains(HashKey start, HashKey end, HashKey key) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(key) <= 0 && key.compareTo(end) < 0;
        } else {
            return start.compareTo(key) <= 0 || key.compareTo(end) < 0;
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class FingerEntry {
        HashKey intervalStart;
        HashKey intervalEnd;
        DhtNodeMeta node;
    }
}
