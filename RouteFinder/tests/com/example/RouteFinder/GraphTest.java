package com.example.RouteFinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph<String> graphA;
    private Graph<String> graphB;
    private Graph<String> graphC;

    @BeforeEach
    public void setUp() {
        graphA = new Graph<>("A");
        graphB = new Graph<>("B");
        graphC = new Graph<>("C");
    }

    @Test
    public void testConnectToNodeUndirected() {
        graphA.connectToNodeUndirected(graphB, 1);
        assertTrue(graphA.neighbours.contains(graphB));
        assertTrue(graphB.neighbours.contains(graphA));
    }

    @Test
    public void testTraverseGraphBreadthFirst() {
        graphA.connectToNodeUndirected(graphB, 1);
        graphB.connectToNodeUndirected(graphC, 1);
        List<Graph<String>> path = Graph.TraverseGraphBreadthFirst(graphA, graphC);
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals(graphA, path.get(0));
        assertEquals(graphB, path.get(1));
        assertEquals(graphC, path.get(2));
    }

    @Test
    public void testDijkstra() {
        graphA.connectToNodeUndirected(graphB, 1);
        graphB.connectToNodeUndirected(graphC, 1);
        List<Graph<String>> path = Graph.dijkstra(graphA, graphC);
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals(graphA, path.get(0));
        assertEquals(graphB, path.get(1));
        assertEquals(graphC, path.get(2));
    }

}