package com.example.RouteFinder;

import java.util.*;

public class Graph<T> {
    // Constructor for the Graph class
    public Graph(T station) {
        this.station = station;
    }
    // This list will hold the neighbouring nodes of the current node
    public List<Graph<T>> neighbours = new ArrayList<>();
    // This field holds the station data associated with the node
    public T station;



    // This method performs a Breadth-First Search on the graph from the startNode to the endNode
    public static <T> List<Graph<T>> TraverseGraphBreadthFirst(Graph<T> startNode, Graph<T> endNode) {
        // This queue holds the nodes that need to be visited
        Queue<Graph<T>> agenda = new LinkedList<>();
        // This set holds the nodes that have been visited
        Set<Graph<T>> encountered = new HashSet<>();
        // This map will hold each node and its parent in the path
        Map<Graph<T>, Graph<T>> parentMap = new HashMap<>();

        // Add the startNode to the queue and the set
        agenda.add(startNode);
        encountered.add(startNode);

        while (!agenda.isEmpty()) {
            // Poll the queue to get the current node and check if it is the endNode
            Graph<T> currentNode = agenda.poll();

            if (currentNode.equals(endNode)) {
                // If the currentNode is the endNode, construct the path from the startNode to the endNode and return it
                return constructPath(startNode, endNode, parentMap);
            }

            // If the currentNode is not the endNode, add all its unvisited neighbours to the queue and the set
            for (Graph<T> neighbor : currentNode.neighbours) {
                if (!encountered.contains(neighbor)) {
                    agenda.add(neighbor);
                    encountered.add(neighbor);
                    parentMap.put(neighbor, currentNode);
                }
            }
        }

        // If the endNode was not found, return null
        return null;
    }

    // This method connects the current node to the destinationNode
    public void connectToNodeUndirected(Graph<T> destinationNode, int line) {
        if (!(destinationNode instanceof Graph)) {
            // If the destinationNode is not a Graph, throw an exception
            throw new IllegalArgumentException("destinationNode must be of type GraphNodes");
        } else {
            // If the destinationNode is a Graph, add it to the neighbours of the current node and vice versa
            neighbours.add(destinationNode);
            destinationNode.neighbours.add(this);
        }
    }


    public static <T> List<Graph<T>> dijkstra(Graph<T> startNode, Graph<T> endNode) {
        // Initialize data structures
        Map<Graph<T>, Integer> shortestDistances = new HashMap<>(); // Shortest distance from startNode to each node
        Map<Graph<T>, Graph<T>> shortestPathPredecessors = new HashMap<>(); // Predecessor of each node in shortest path
        Set<Graph<T>> unvisitedNodes = new HashSet<>(); // Nodes yet to be visited
        Set<Graph<T>> visitedNodes = new HashSet<>(); // Nodes already visited

        // The shortest distance from startNode to itself is 0
        shortestDistances.put(startNode, 0);
        unvisitedNodes.add(startNode);

        while (!unvisitedNodes.isEmpty()) {
            // Select the unvisited node with the smallest known distance from the start node
            Graph<T> currentNode = getClosestNode(unvisitedNodes, shortestDistances);

            // Mark the selected node as visited
            unvisitedNodes.remove(currentNode);
            visitedNodes.add(currentNode);

            // Update the shortest distances to the current node's neighbours
            for (Graph<T> neighbour : currentNode.neighbours) {
                if (visitedNodes.contains(neighbour)) {
                    continue;
                }
                int distanceThroughCurrentNode = shortestDistances.getOrDefault(currentNode, Integer.MAX_VALUE) + 1; // +1 assuming unweighted graph
                if (distanceThroughCurrentNode < shortestDistances.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    shortestDistances.put(neighbour, distanceThroughCurrentNode);
                    shortestPathPredecessors.put(neighbour, currentNode);
                    unvisitedNodes.add(neighbour);
                }
            }
        }

        // Construct the shortest path by backtracking from endNode to startNode
        return constructShortestPath(shortestPathPredecessors, endNode);
    }

    // This method returns the node among the unvisited nodes that has the shortest distance from the start node
    private static <T> Graph<T> getClosestNode(Set<Graph<T>> unvisitedNodes, Map<Graph<T>, Integer> shortestDistances) {
        Graph<T> closestNode = null; // To store the node with the shortest distance
        int shortestDistance = Integer.MAX_VALUE; // Initialize shortest distance as maximum integer value
        for (Graph<T> node : unvisitedNodes) { // Iterate over each unvisited node
            int nodeDistance = shortestDistances.getOrDefault(node, Integer.MAX_VALUE); // Get the distance of the current node from start node
            if (nodeDistance < shortestDistance) { // If this distance is less than the current shortest distance
                closestNode = node; // Update the closest node
                shortestDistance = nodeDistance; // Update the shortest distance
            }
        }
        return closestNode; // Return the node with the shortest distance
    }

    // This method constructs the shortest path from start node to end node using the map of predecessors
    private static <T> List<Graph<T>> constructShortestPath(Map<Graph<T>, Graph<T>> shortestPathPredecessors, Graph<T> endNode) {
        List<Graph<T>> shortestPath = new ArrayList<>(); // To store the shortest path
        for (Graph<T> node = endNode; node != null; node = shortestPathPredecessors.get(node)) { // Start from the end node and go backwards
            shortestPath.add(node); // Add each node to the path
        }
        Collections.reverse(shortestPath); // Reverse the path to get it from start node to end node
        return shortestPath; // Return the shortest path
    }

    // This method constructs the path from start node to end node using the map of predecessors
    private static <T> List<Graph<T>> constructPath(Graph<T> startNode, Graph<T> endNode, Map<Graph<T>, Graph<T>> parentMap) {
        List<Graph<T>> path = new LinkedList<>(); // To store the path
        Graph<T> currentNode = endNode; // Start from the end node
        while (currentNode != null) { // While we haven't reached the start node
            path.add(0, currentNode); // Add each node to the beginning of the path
            currentNode = parentMap.get(currentNode); // Move to the predecessor of the current node
        }
        return path; // Return the path
    }

    // This method overrides the default toString() method and returns the station associated with the node
    @Override
    public String toString() {
        return "Graphnodes :" + station;
    }
}
