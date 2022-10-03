/* CISC/CMPE 422/835
 * Example-based testing using Jqwik for shortest path implementation
 */
import net.jqwik.api.*;
import java.util.Arrays;

public class GraphTestExamples {
    @Example
    void exTest1() {
        Integer[][] adjM = new Integer[][]{
                {0,2,1},
                {1,0,0},
                {2,-1,0}
        };
        Graph graph = new Graph(adjM);
        System.out.println("Input graph is: \n" + graph);
        graph.pathM = graph.computeShortestPaths();
        System.out.println("After computation of shortest paths: \n" + graph);
    }
    @Example
    void exTest2() {
        Integer[][] adjM = new Integer[][]{
                {0, 10, 15, -1, -1, -1},  // a
                {-1, 0,-1, 12, -1, 15},   // b
                {-1, -1, 0, -1, 10, -1},  // c
                {-1, -1, -1, 0, 2, 1},    // d
                {-1, -1, -1, -1, 0, -1},  // e
                {-1, -1, -1, -1, 5, 0}    // f
        };
        Graph graph = new Graph(adjM);
        System.out.println("Initial graph is: \n" + graph);
        graph.pathM = graph.computeShortestPaths();
        System.out.println("After computation of shortest paths: \n" + graph);
    }

    @Example
    void exTest3() {
        Integer[][] adjM = new Integer[][]{
                {0, 10, 15, -1, -1, -1},  // a
                {-1, 0,-1, 12, -1, 15},   // b
                {-1, -1, 0, -1, 10, -1},  // c
                {-1, -1, -1, 0, 2, 1},    // d
                {-1, -1, -1, -1, 0, -1},  // e
                {-1, -1, -1, -1, 5, 0}    // f
        };
        Graph graph = new Graph(adjM);
        System.out.println("Input graph is: \n" + graph);
        graph.pathM = graph.computeShortestPaths();
        System.out.println("After computation of shortest paths: " + graph);
        System.out.println("Nodes reachable from node 0: " + Arrays.toString(graph.reachableFrom(0)));
        System.out.println("Nodes reachable from node 1: " + Arrays.toString(graph.reachableFrom(1)));
        System.out.println("Shortest path from node 0 to node 4: " + graph.shortestPath(0, 4));
    }

    @Example
    void exTest4() {
        Integer[][] adjM = new Integer[][]{
                {0, 10, 15, -1, -1, -1},  // a
                {-1, 0,-1, 12, -1, 15},   // b
                {-1, -1, 0, -1, 10, -1},  // c
                {-1, -1, -1, 0, 2, 1},    // d
                {-1, -1, -1, -1, 0, -1},  // e
                {-1, -1, -1, -1, 5, 0}    // f
        };
        Graph graph = new Graph(adjM);
        System.out.println(graph);
        graph.pathM = graph.computeShortestPaths();
        System.out.println(graph);
        Path p0 = graph.shortestPath(0, 4);
        System.out.println("shortest path from 0 to 4 is: " + p0);
        graph.updateEdge(0, 1, graph.adjM[0][1]+1);
        graph.pathM = graph.computeShortestPaths();
        Path p = graph.shortestPath(0, 4);
        System.out.println("shortest path from 0 to 4 is: " + p);
    }

}
