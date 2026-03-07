# RoadNetworkApp

Java Swing application that visualizes a road network represented as a graph.
The program computes the shortest path using Dijkstra’s algorithm and can generate alternative routes when a road is marked as problematic.

## Features

- Graph visualization using Java Swing (Java2D)
- Shortest path calculation using Dijkstra’s algorithm
- Alternative route generation
- Add / remove nodes
- Add / remove roads
- Edit road weight
- Temporarily block or unblock roads
- Load graph from CSV file
- Save results to TXT file

### Graph Model

- The road network is represented as a graph.
- Vertex (node) – represents a location
- Edge (road) – represents a connection between two locations
- Edge weight – represents travel cost (e.g. time or distance)
- The graph is undirected, meaning every road exists in both directions.

### Encapsulation

- The internal structure of the graph is fully encapsulated.
- Vertex and Edge classes are private inside Graph
- They never leave the Graph class
- Other parts of the program work only with vertex keys and data
- This prevents external classes from modifying the internal graph structure.
  
### Generics
The graph is implemented using Java generics.
- Graph<KV, DV, DE>
# Where:
- KV – vertex key type
- DV – vertex data type
- DE – edge data type

# How It Works

- The application uses Dijkstra’s algorithm to compute routes.

# Steps:

- The shortest path between two nodes is computed.
- If a road is marked as problematic, it is temporarily blocked.
- The algorithm runs again to find an alternative route.
- Multiple alternatives can be generated.

## Run

1. Open the project in **IntelliJ IDEA**
2. Use **JDK 17+**
3. Run `Main` or `MainFrame`
