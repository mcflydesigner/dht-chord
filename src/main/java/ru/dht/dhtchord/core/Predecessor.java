package ru.dht.dhtchord.core;

import java.util.Iterator;
import java.util.TreeSet;

public class Predecessor {

    public static int findPredecessor(TreeSet<Integer> nodes, int node) {
        Utils.verifyNodesSetIsNotEmpty(nodes);

        if (nodes.first() >= node) {
            return nodes.last();
        }

        // TODO: optimization (reversedIterator as a possible approach)
        Iterator<Integer> iterator = nodes.iterator();
        int maxBeforeNode = iterator.next();
        while (iterator.hasNext()) {
            int currentNode = iterator.next();
            if (node <= currentNode) {
                break;
            }
            maxBeforeNode = currentNode;
        }
        return maxBeforeNode;
    }

}
