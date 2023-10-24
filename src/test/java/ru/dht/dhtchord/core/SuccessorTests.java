package ru.dht.dhtchord.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

public class SuccessorTests {

    @Test
    public void test1Node() {
        TreeSet<Integer> treeSet = new TreeSet<>(List.of(0));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 0));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 1));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 2));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 3));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 4));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 5));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 6));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 7));
    }

    @Test
    public void test2Nodes() {
        TreeSet<Integer> treeSet = new TreeSet<>(List.of(3, 7));
        Assertions.assertEquals(3, Successor.findSuccessor(treeSet, 0));
        Assertions.assertEquals(3, Successor.findSuccessor(treeSet, 1));
        Assertions.assertEquals(3, Successor.findSuccessor(treeSet, 2));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 3));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 4));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 5));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 6));
        Assertions.assertEquals(3, Successor.findSuccessor(treeSet, 7));
    }

    @Test
    public void test2NodesFirstAndLast() {
        TreeSet<Integer> treeSet = new TreeSet<>(List.of(0, 7));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 0));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 1));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 2));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 3));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 4));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 5));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 6));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 7));
    }

    @Test
    public void test3NodesAtEnd() {
        TreeSet<Integer> treeSet = new TreeSet<>(List.of(5, 6, 7));
        Assertions.assertEquals(5, Successor.findSuccessor(treeSet, 0));
        Assertions.assertEquals(5, Successor.findSuccessor(treeSet, 1));
        Assertions.assertEquals(5, Successor.findSuccessor(treeSet, 2));
        Assertions.assertEquals(5, Successor.findSuccessor(treeSet, 3));
        Assertions.assertEquals(5, Successor.findSuccessor(treeSet, 4));
        Assertions.assertEquals(6, Successor.findSuccessor(treeSet, 5));
        Assertions.assertEquals(7, Successor.findSuccessor(treeSet, 6));
        Assertions.assertEquals(5, Successor.findSuccessor(treeSet, 7));
    }

    @Test
    public void test3NodesAtBeginning() {
        TreeSet<Integer> treeSet = new TreeSet<>(List.of(0, 1, 2));
        Assertions.assertEquals(1, Successor.findSuccessor(treeSet, 0));
        Assertions.assertEquals(2, Successor.findSuccessor(treeSet, 1));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 2));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 3));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 4));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 5));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 6));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 7));
    }

    @Test
    public void test4Nodes() {
        TreeSet<Integer> treeSet = new TreeSet<>(List.of(0, 1, 2, 6));
        Assertions.assertEquals(1, Successor.findSuccessor(treeSet, 0));
        Assertions.assertEquals(2, Successor.findSuccessor(treeSet, 1));
        Assertions.assertEquals(6, Successor.findSuccessor(treeSet, 2));
        Assertions.assertEquals(6, Successor.findSuccessor(treeSet, 3));
        Assertions.assertEquals(6, Successor.findSuccessor(treeSet, 4));
        Assertions.assertEquals(6, Successor.findSuccessor(treeSet, 5));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 6));
        Assertions.assertEquals(0, Successor.findSuccessor(treeSet, 7));
    }

}
