package ru.dht.dhtchord.core;

import lombok.experimental.UtilityClass;

import java.util.TreeSet;

@UtilityClass
public class Utils {

    public static void verifyNodesSetIsNotEmpty(TreeSet<Integer> nodes) {
        if (nodes.size() == 0) {
            throw new IllegalStateException("The list of nodes must contain at least 1 node, but provided empty list");
        }
    }

}
