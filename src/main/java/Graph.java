/* CISC/CMPE 422/835
 * All-pair shortest path implementation for directed, weighted graphs
 * Uses adjacency matrix and Floyd-Warshall algorithm
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Graph {
    public Integer[][] adjM;
    public int dim;
    public Path[][] pathM;

    public Graph(Integer[][] adjM) {
        this.adjM = adjM;
        this.dim = adjM.length;
        this.pathM = new Path[this.dim][this.dim];
    }

    public Graph(Integer[][] adjM, Path[][] pathM, int dim) {
        this.adjM = adjM;
        this.dim = dim;
        this.pathM = pathM;
    }

    public String toString() {
        String str = "adjM = \n";
        for (int i=0; i<this.dim; i++)
            str = str + Arrays.deepToString(adjM[i]) + "\n";
        str = str + ("\npathM = \n");
        for (int i=0; i<this.dim; i++)
            str = str + Arrays.deepToString(pathM[i]) + "\n";
        return str;
    }

    public Path shortestPath(Integer from, Integer to) {
        return pathM[from][to];
    }

    public Path[][] computeShortestPaths() {
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                if (adjM[i][j] > -1) {
                    this.pathM[i][j] = new Path(new ArrayList<>(Arrays.asList(j)), adjM[i][j]);
                } else
                    this.pathM[i][j] = new Path(null, Integer.MAX_VALUE);
            }
        }
        for (int k = 0; k < dim; k++)
            for (int i = 0; i < dim; i++)
                for (int j = 0; j < dim; j++)
                    if (pathM[i][k].length != Integer.MAX_VALUE && pathM[k][j].length != Integer.MAX_VALUE) {
                        if (pathM[i][j].length > pathM[i][k].length + pathM[k][j].length) {
                            List<Integer> newP = new ArrayList<>();
                            newP.addAll(pathM[i][k].path);
                            newP.addAll(pathM[k][j].path);
                            this.pathM[i][j].path = newP;
                            int newL = pathM[i][k].length + pathM[k][j].length;  // correct
//                            int newL = pathM[i][k].length + pathM[k][i].length;  // bug
                            pathM[i][j] = new Path(newP, newL);
                        }
                    }
        return pathM;
    }

    public void updateEdge(int i, int j, int w) {
        adjM[i][j] = w;
    }

    public void reverseEdges() {
        Integer[][] newAdjM = new Integer[dim][dim];
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++)
                newAdjM[i][j] = adjM[j][i];
        adjM = newAdjM;
    }

    public int[] reachableFrom(int i) {
        return (IntStream.range(0,dim).filter(j -> (pathM[i][j].path != null)).toArray());
    }
}

