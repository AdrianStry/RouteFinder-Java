package com.example.RouteFinder;

import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

public class RouteController {
    @FXML
    private ComboBox<String> start;
    @FXML
    private ComboBox<String> waypoint;
    @FXML
    private ComboBox<String> end;
    @FXML
    private ListView viewRoute;
    @FXML
    private ImageView mapView;
    private Image originalImage;
    private WritableImage writtenImage;
    List<Graph<Station>> stationList = new ArrayList<>();
    Map<Integer, List<Station>> stationsById = new HashMap<>();

    // Handles the event to show a single route between two stations, optionally via a waypoint
    public List<Station> ShowSingleRoute(ActionEvent event) {
        // Set the image to it's grayed out version and update the view
        writtenImage = setGrey(originalImage);
        mapView.setImage(writtenImage);

        // Clear the route view before drawing a new route
        clearRouteView();

        // Get the nodes representing the start, end, and optional waypoint stations
        Graph<Station> startNode = getStationNodeFromComboBox(start);
        Graph<Station> endNode = getStationNodeFromComboBox(end);
        Graph<Station> waypointNode = getStationNodeFromComboBox(waypoint);

        // Validate the start and end nodes
        if (!validateNodes(startNode, endNode)) {
            return null;
        }

        // Process the paths from start to end, optionally via waypoint
        List<Station> stationRoute = processPaths(startNode, endNode, waypointNode);

        // Clear the waypoint selection
        waypoint.getSelectionModel().clearSelection();

        return stationRoute;
    }

    // Clears the route view
    private void clearRouteView() {
        viewRoute.getItems().clear();
    }

    // Returns the station node corresponding to the selected station in the combo box
    private Graph<Station> getStationNodeFromComboBox(ComboBox<String> comboBox) {
        int stationIndex = comboBox.getSelectionModel().getSelectedIndex();
        if (stationIndex != -1) {
            return stationList.get(stationIndex);
        }
        return null;
    }

    // Validates the start and end nodes, checking if they are not null and not the same
    private boolean validateNodes(Graph<Station> startNode, Graph<Station> endNode) {
        if (startNode == null || endNode == null) {
            System.out.println("Start or end station not found");
            return false;
        }

        if (startNode.equals(endNode)) {
            System.out.println("Start and end stations are the same");
            return false;
        }
        return true;
    }

    // Processes the paths from start to end, optionally via waypoint
    private List<Station> processPaths(Graph<Station> startNode, Graph<Station> endNode, Graph<Station> waypointNode) {
        List<Station> stationRoute = new ArrayList<>();

        if (waypointNode == null) {
            // If there is no waypoint, find the path from start to end
            List<Graph<Station>> path = Graph.TraverseGraphBreadthFirst(startNode, endNode);
            displayRoute(startNode, endNode, path);
        } else {
            // If there is a waypoint, find the path from start to waypoint and from waypoint to end
            List<Graph<Station>> startToWaypoint = Graph.TraverseGraphBreadthFirst(startNode, waypointNode);
            List<Graph<Station>> waypointToEnd = Graph.TraverseGraphBreadthFirst(waypointNode, endNode);
            displayRouteWithWaypoint(startNode, waypointNode, endNode, startToWaypoint, waypointToEnd);
        }

        return stationRoute;
    }

    // Displays the route from start to end
    private void displayRoute(Graph<Station> startNode, Graph<Station> endNode, List<Graph<Station>> path) {
        if (path == null) {
            System.out.println("No route exists between " + startNode.station.getStationName() + " and " + endNode.station.getStationName());
            return;
        }

        // Convert the list of nodes to a list of stations
        List<Station> stationPath = getStationListFromNodeList(path);
        printAndDisplayRoute(startNode, endNode, stationPath);
    }

    // Displays the route from start to end with waypoint
    private void displayRouteWithWaypoint(Graph<Station> startNode, Graph<Station> waypointNode, Graph<Station> endNode, List<Graph<Station>> startToWaypoint, List<Graph<Station>> waypointToEnd) {
        if (startToWaypoint == null || waypointToEnd == null) {
            System.out.println("No route exists between " + startNode.station.getStationName() + " and " + endNode.station.getStationName());
            return;
        }

        // Convert the list of nodes to a list of stations for both paths
        List<Station> stationPath1 = getStationListFromNodeList(startToWaypoint);
        List<Station> stationPath2 = getStationListFromNodeList(waypointToEnd);

        // Print and display the route from start to waypoint and from waypoint to end
        printAndDisplayRoute(startNode, waypointNode, stationPath1);
        printAndDisplayRoute(waypointNode, endNode, stationPath2);
    }

    // Converts a list of graph nodes to a list of stations
    private List<Station> getStationListFromNodeList(List<Graph<Station>> nodeList) {
        List<Station> stationList = new ArrayList<>();
        for (Graph<Station> node : nodeList) {
            stationList.add(node.station);
        }
        return stationList;
    }

    // Prints the route to the console and adds the stations to the route view
    private void printAndDisplayRoute(Graph<Station> startNode, Graph<Station> endNode, List<Station> stationPath) {
        System.out.println("Route from " + startNode.station.getStationName() + " to " + endNode.station.getStationName() + ": ");
        for (Station station : stationPath) {
            System.out.println(station.getStationName());
            viewRoute.getItems().add(station.getStationName());
        }
    }

    // Method to show the fastest route between two stations
    public List<Station> ShowFastestRoute(ActionEvent event) {
        // Set the image for the map view to grey
        writtenImage = setGrey(originalImage);
        // Display the grey image on the map view
        mapView.setImage(writtenImage);

        // Clear the previously shown route
        clearRouteView();

        // Retrieve the start and end nodes from the selected items in the combo box
        Graph<Station> startNode = getStationNodeFromComboBox(start);
        Graph<Station> endNode = getStationNodeFromComboBox(end);

        // Validate if the nodes are valid, if not return null
        if (!validateNodes(startNode, endNode)) {
            return null;
        }

        // Calculate the fastest path between the start and end nodes
        List<Station> stationRoute = processFastestPaths(startNode, endNode);

        // Return the fastest route
        return stationRoute;
    }

    // Method to calculate the fastest paths between two nodes
    private List<Station> processFastestPaths(Graph<Station> startNode, Graph<Station> endNode) {
        List<Station> stationRoute = new ArrayList<>();

        // Use Dijkstra's algorithm to find the shortest path between the start and end nodes
        List<Graph<Station>> path = Graph.dijkstra(startNode, endNode);
        // Display the fastest route
        displayFastestRoute(startNode, endNode, path);

        // Return the fastest route
        return stationRoute;
    }

    // Method to display the fastest route between two nodes
    private void displayFastestRoute(Graph<Station> startNode, Graph<Station> endNode, List<Graph<Station>> path) {
        // If no path exists, print a message and return
        if (path == null) {
            System.out.println("No route exists between " + startNode.station.getStationName() + " and " + endNode.station.getStationName());
            return;
        }

        // Convert the path nodes to station list
        List<Station> stationPath = getStationListFromNodeList(path);
        // Print and display the fastest route
        printAndDisplayRoute(startNode, endNode, stationPath);
    }


    @FXML
    // Method to read data from a CSV file and populate the stationList, start, end, and waypoint ComboBoxes
    public void ReadDataFromCSV() throws Exception{
        // Get the original image from the mapView
        originalImage = mapView.getImage();
        // Path to the CSV file
        String filepath = "C:\\Users\\strym\\OneDrive\\Pulpit\\RouteFinder\\src\\main\\java\\com\\example\\RouteFinder\\London.csv";
        // The character that separates the data in the CSV file
        String splitCSVBy = ",";

        // Create a BufferedReader to read from the CSV file
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String line ="";
        // Loop over each line in the CSV file
        while ((line = bufferedReader.readLine()) != null) {
            // Split the line into an array of strings
            String [] splitArray = line.split(splitCSVBy);
            // If the array has more than 4 elements and the fifth element is either "1" or "1.5"
            if (splitArray.length > 4 && ("1".equals(splitArray[4]) || "1.5".equals(splitArray[4]))) {
                // Create a new Station object with the data from the CSV file
                Station station = new Station(Integer.parseInt(splitArray[0]), Float.parseFloat(splitArray[1]), Float.parseFloat(splitArray[2]), splitArray[3]);
                // Create a new Graph Node with the station
                Graph<Station> stationGN = new Graph<>(station);
                // Add the Graph Node to the stationList
                stationList.add(stationGN);
                // Add the station name to the start, end, and waypoint ComboBoxes
                start.getItems().add(splitArray[3]);
                end.getItems().add(splitArray[3]);
                waypoint.getItems().add(splitArray[3]);
            }
        }
        // Close the BufferedReader
        bufferedReader.close();
    }

    public void createAdjacentNodeEdges() throws Exception {
        // Define the filepath of the CSV file and the separator.
        String filepath = "C:\\Users\\strym\\OneDrive\\Pulpit\\RouteFinder\\src\\main\\java\\com\\example\\RouteFinder\\Lines.csv";
        String splitCSVBy = ",";

        // Read the CSV file and create edges between nodes.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String line = "";
        line = bufferedReader.readLine();
        while ((line = bufferedReader.readLine()) != null) {
            String[] values = line.split(splitCSVBy);
            int startID = Integer.parseInt(values[0]);
            int destinationID = Integer.parseInt(values[1]);
            int lineID = Integer.parseInt(values[2]);

            // Find the start node and destination node in the station list.
            Graph<Station> startNode = null;
            Graph<Station> destinationNode = null;
            for (Graph<Station> node : stationList) {
                if (node.station.getID() == startID) {
                    startNode = node;
                }
                if (node.station.getID() == destinationID) {
                    destinationNode = node;
                }
            }

            // Create an undirected edge between the start node and destination node.
            if (startNode != null && destinationNode != null) {
                startNode.connectToNodeUndirected(destinationNode, lineID);
            }

            // Store the stations by line number.
            int lineNumber = -1;
            for (Graph<Station> node : stationList) {
                if (node.station.getID() == startID) {
                    lineNumber = lineID;
                    break;
                }
            }
            if (lineNumber != -1) {
                List<Station> stationLineNumber = stationsById.get(lineNumber);
                if (stationLineNumber == null) {
                    stationLineNumber = new ArrayList<>();
                    stationsById.put(lineNumber, stationLineNumber);
                }
                Station startStation = startNode.station;
                Station endStation = destinationNode != null ? destinationNode.station : null;

                // Add the start station to the station list for the line number.
                if (!stationLineNumber.contains(startStation)) {
                    stationLineNumber.add(startStation);
                }

                // Add the end station to the station list for the line number.
                if (endStation != null && !stationLineNumber.contains(endStation)) {
                    stationLineNumber.add(endStation);
                }
            }
        }
    }
    @FXML
    private void LoadData(ActionEvent event) throws Exception {
        ReadDataFromCSV();
        createAdjacentNodeEdges();
    }

    @FXML
    void ResetMap(ActionEvent event) {
        mapView.setImage(originalImage);
    }

    // Method to convert a colored image into a grayscale image
    public static WritableImage setGrey(Image image) {
        // The original colored image
        Image originalImage = image;

        // Get the width and height of the original image
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Create a new writable image with the same width and height as the original image
        WritableImage grayImage = new WritableImage(width, height);
        // The PixelWriter will be used to change the color of each pixel in the new image
        PixelWriter pixelWriter = grayImage.getPixelWriter();
        // The PixelReader will be used to read the color of each pixel in the original image
        PixelReader pixelReader = originalImage.getPixelReader();

        // Loop over each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the color of the pixel at position (x, y) in the original image
                Color color = pixelReader.getColor(x, y);
                // Calculate the grayscale value of the color using the formula for luminance
                double gray = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
                // Set the color of the pixel at position (x, y) in the new image to the grayscale value, maintaining the original opacity
                pixelWriter.setColor(x, y, new Color(gray, gray, gray, color.getOpacity()));
            }
        }

        // Return the grayscale image
        return grayImage;
    }
}