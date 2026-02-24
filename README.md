# RoadNetworkApp

Java Swing application that visualizes a road network (graph), computes the shortest path using Dijkstra’s algorithm, and generates alternative routes when a road is marked as problematic.

## Features

- Graph visualization (Java2D)
- Shortest path calculation (Dijkstra)
- Alternative route generation
- Add / remove nodes and roads
- Edit road weight
- Mark / unmark problematic roads

## Architecture

### Encapsulation
- `Graph` keeps nodes and edges private
- Internal lists are not exposed
- Access is provided only through public methods
- Read-only views are returned when needed

### Generics
The graph is implemented using generics:

# How It Works

- **Nodes** represent locations (name + coordinates).
- **Edges** represent roads (weight + problematic flag).
- The graph is **undirected** (each road exists in both directions).
- **Dijkstra’s algorithm** computes:
  - The main shortest route
  - Alternative routes when a problematic edge is temporarily blocked

## Run

1. Open the project in **IntelliJ IDEA**
2. Use **JDK 17+**
3. Run `Main` or `MainFrame`
