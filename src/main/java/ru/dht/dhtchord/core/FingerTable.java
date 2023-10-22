package ru.dht.dhtchord.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FingerTable {

    @Getter
    private final Map<Integer, Integer> fingerTable;
    private final TreeSet<Integer> nodes;
    private final int nodeId;
    private final int m;

    public static FingerTable buildTable(int m, int nodeId, TreeSet<Integer> nodes) {
        Utils.verifyNodesSetIsNotEmpty(nodes);

        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < m; i++) {
            int k = (nodeId + (1 << i)) % (1 << m);
            map.put(k, Successor.findSuccessor(nodes, k));
        }
        return new FingerTable(map, nodes, nodeId, m);
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
