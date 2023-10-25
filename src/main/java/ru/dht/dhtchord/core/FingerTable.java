package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class FingerTable {

    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    private final int m;
    private final int nodeId;
    private final TreeSet<Integer> nodes;
    @Getter
    private final Map<Integer, Integer> fingerTable;
    @Getter
    private final int version;

    public static FingerTable buildTable(int m, int nodeId, TreeSet<Integer> nodes) {
        Utils.verifyNodesSetIsNotEmpty(nodes);

        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < m; i++) {
            int k = (nodeId + (1 << i)) % (1 << m);
            map.put(k, Successor.findSuccessor(nodes, k));
        }
        return new FingerTable(m, nodeId, nodes, map, atomicCounter.incrementAndGet());
    }

    public int lookupSuccessorForKey(String key) {
       int nodeIdForKey = HashUtils.getNodeIdForKey(key, m);
       int maxLess = nodes.first();
       // TODO: reversed iterator faster?
       for (var fingerTableEntry : fingerTable.entrySet()) {
           int k = fingerTableEntry.getKey();
           int successor = fingerTableEntry.getValue();

           if (k == nodeIdForKey) {
               return successor;
           } else if (nodeIdForKey > k) {
               maxLess = k;
           }
       }
       return maxLess;
    }

}
