//package ru.dht.dhtchord.core;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.Map;
//import java.util.TreeSet;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class FingerTableTests {
//
//    @Test
//    @DisplayName("Testing finger table for m = 1, nodeId = 0, nodes = [0]")
//    public void testCase1() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//
//        FingerTable fingerTable = FingerTable.buildTable(1, 0, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(1, fingerTableMap.size());
//        assertEquals(0, fingerTableMap.get(1));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 1, nodeId = 0, nodes = [0, 1]")
//    public void testCase2() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//        nodes.add(1);
//
//        FingerTable fingerTable = FingerTable.buildTable(1, 0, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(1, fingerTableMap.size());
//        assertEquals(1, fingerTableMap.get(1));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 1, nodeId = 1, nodes = [0, 1]")
//    public void testCase3() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//        nodes.add(1);
//
//        FingerTable fingerTable = FingerTable.buildTable(1, 1, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(1, fingerTableMap.size());
//        assertEquals(0, fingerTableMap.get(0));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 3, nodeId = 0, nodes = [0]")
//    public void testCase4() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//
//        FingerTable fingerTable = FingerTable.buildTable(3, 0, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(3, fingerTableMap.size());
//        assertEquals(0, fingerTableMap.get(1));
//        assertEquals(0, fingerTableMap.get(2));
//        assertEquals(0, fingerTableMap.get(4));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 3, nodeId = 0, nodes = [0, 7]")
//    public void testCase5() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//        nodes.add(7);
//
//        FingerTable fingerTable = FingerTable.buildTable(3, 0, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(3, fingerTableMap.size());
//        assertEquals(7, fingerTableMap.get(1));
//        assertEquals(7, fingerTableMap.get(2));
//        assertEquals(7, fingerTableMap.get(4));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 3, nodeId = 7, nodes = [0, 7]")
//    public void testCase6() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//        nodes.add(7);
//
//        FingerTable fingerTable = FingerTable.buildTable(3, 7, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(3, fingerTableMap.size());
//        assertEquals(0, fingerTableMap.get(0));
//        assertEquals(7, fingerTableMap.get(1));
//        assertEquals(7, fingerTableMap.get(3));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 3, nodeId = 7, nodes = [0, 7]")
//    public void testCase7() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//        nodes.add(7);
//
//        FingerTable fingerTable = FingerTable.buildTable(3, 7, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(3, fingerTableMap.size());
//        assertEquals(0, fingerTableMap.get(0));
//        assertEquals(7, fingerTableMap.get(1));
//        assertEquals(7, fingerTableMap.get(3));
//    }
//
//    @Test
//    @DisplayName("Testing finger table for m = 3, nodeId = 4, nodes = [0, 4, 7]")
//    public void testCase8() {
//        TreeSet<Integer> nodes = new TreeSet<>();
//        nodes.add(0);
//        nodes.add(4);
//        nodes.add(7);
//
//        FingerTable fingerTable = FingerTable.buildTable(3, 4, nodes);
//        Map<Integer, Integer> fingerTableMap = fingerTable.getFingerTable();
//        assertEquals(3, fingerTableMap.size());
//        assertEquals(7, fingerTableMap.get(5));
//        assertEquals(7, fingerTableMap.get(6));
//        assertEquals(0, fingerTableMap.get(0));
//    }
//
//}
