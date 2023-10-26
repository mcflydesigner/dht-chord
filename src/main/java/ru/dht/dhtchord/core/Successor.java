package ru.dht.dhtchord.core;

import java.util.Iterator;
import java.util.TreeSet;

public class Successor {

    public static int findSuccessor(TreeSet<Integer> nodes, int node) {
        Utils.verifyNodesSetIsNotEmpty(nodes);

        if (nodes.contains(node)) {
            return node;
        }

        // TODO: optimization
        Iterator<Integer> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            int currentNode = iterator.next();
            if (currentNode > node) {
                return currentNode;
            }
        }
        return nodes.first();
    }
}
