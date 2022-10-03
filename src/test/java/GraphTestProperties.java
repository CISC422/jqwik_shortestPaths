/* CISC/CMPE 422/835
 * Property-based testing using Jqwik for shortest path implementation
 */
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.Tuple.Tuple2;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

import static java.util.Collections.reverse;

public class GraphTestProperties {

    Graph clone(Graph d1) {
        int dim = d1.dim;
        Integer[][] adjM2 = new Integer[dim][dim];
        Path[][] pathM2 = new Path[dim][dim];
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++) {
                adjM2[i][j] = d1.adjM[i][j];
                pathM2[i][j] = d1.pathM[i][j];
            }
        return new Graph(adjM2, pathM2, dim);
    }


// PROPERTIES ========================================================

    // a. if there is no path from k to l in the graph, then after increasing the weight of an existing edge (i,j)
    // in the graph there will still not be a path from k to l (P1 in notes)
    // b. if there is a path w/ length len from l to k in graph, then after increasing the weight of an existing edge (i,j)
    // in the graph by w, the length of the shortest path from l to k will be between l and l+w (P2)
    @Property
    @Report(Reporting.GENERATED)
    public void propUpdateEdgesAndLengthsOfShortestPaths1(@ForAll("adjMatrices") Integer[][] adjM,
                                                          @ForAll("nodePairs") Tuple2<Integer,Integer> t,
                                                          @ForAll @IntRange(min = 1, max = 2) Integer w) {
        int i = t.get1();
        int j = t.get2();
        Assume.that(adjM[i][j] != -1);   // edge (i,j) must exist!
        Graph graph = new Graph(adjM);
        graph.computeShortestPaths();
        Graph graph0 = clone(graph);
        graph.updateEdge(i, j, adjM[i][j] + w);
        graph.computeShortestPaths();
        for (int k = 0; k < adjM.length; k++)
            for (int l = 0; l < adjM.length; l++) {
                int len0 = graph0.pathM[k][l].length;
                if (len0 < Integer.MAX_VALUE)   // there is a path from k to l in graph0
                    Assertions.assertThat(graph.pathM[k][l].length).isBetween(len0, len0 + w);
                else
                    Assertions.assertThat(graph.pathM[k][l].length).isEqualTo(Integer.MAX_VALUE);
            }
    }

    // Adding an edge (i,j) does not increase the length of any shortest paths (P3)
    @Property
    @Report(Reporting.GENERATED)
    public void propertyUpdateEdgesAndLengthsOfShortestPaths2(@ForAll("adjMatrices") Integer[][] adjM,
                                                              @ForAll("nodePairs") Tuple2<Integer,Integer> t,
                                                              @ForAll @IntRange(min = 1, max = 2) Integer w) {
        int i = t.get1();
        int j = t.get2();
        Assume.that(adjM[i][j] == -1);  // graph does not contain edge (i,j) yet
        Graph graph = new Graph(adjM);
        graph.computeShortestPaths();
        Graph graph0 = clone(graph);
        graph.updateEdge(i, j, w);   // add edge
        int dim = graph.dim;
        for (int k = 0; k < dim; k++)
            for (int l = 0; l < dim; l++)
                Assertions.assertThat(graph.pathM[k][l].length).isLessThanOrEqualTo(graph0.pathM[k][l].length);
    }

    // reverseEdges is idempotent
    @Property
    @Report(Reporting.GENERATED)
    public void propReverseEdgesIsIdempotent(@ForAll("adjMatrices") Integer[][] adjM) {
        Graph graph = new Graph(adjM);
        System.out.println(graph);
        graph.computeShortestPaths();
        String graph0Str = graph.toString();
        System.out.println(graph0Str);
        graph.reverseEdges();
        graph.reverseEdges();
        String graphStr = graph.toString();
        System.out.println(graph0Str);
        Assertions.assertThat(graphStr).isEqualTo(graph0Str);
    }

    // If j is reachable from i in graph, then i is reachable from j in reverseEdges(graph)
    @Property
    @Report(Reporting.GENERATED)
    public void propertyReverseEdgesAndReachability(@ForAll("adjMatrices") Integer[][] adjM,
                                                    @ForAll("nodePairs") Tuple2<Integer,Integer> t) {
        int i = t.get1();
        int j = t.get2();
        Assume.that(i != j);
        Graph graph = new Graph(adjM);
        graph.computeShortestPaths();
        Assume.that(graph.pathM[i][j].path != null);
        graph.reverseEdges();
        graph.computeShortestPaths();
        Assertions.assertThat(graph.pathM[j][i].path).isNotNull();
    }


    // computeShortestPaths is idempotent
    @Property
    @Report(Reporting.GENERATED)
    public void propComputeShortestPathsIsIdempotent(@ForAll("adjMatrices") Integer[][] adjM) {
        Graph graph = new Graph(adjM);
        System.out.println(graph);
        graph.computeShortestPaths();
        String graph0Str = graph.toString();
        System.out.println(graph0Str);
        graph.computeShortestPaths();
        String graphStr = graph.toString();
        System.out.println(graph0Str);
        Assertions.assertThat(graphStr).isEqualTo(graph0Str);
    }


    // If j in graph.reachableFrom(i) and graph1.update(k,l,w) then j in graph1.reachable(i)
    @Property
    @Report(Reporting.GENERATED)
    public void propertyUpdateEdgesAndReachability(@ForAll("adjMatrices") Integer[][] adjM,
                                                   @ForAll("nodePairs") Tuple2<Integer,Integer> t,
                                                   @ForAll @IntRange(min = 1, max = 2) Integer w) {
        Graph graph = new Graph(adjM);
        graph.computeShortestPaths();
        int i = t.get1();
        int j = t.get2();
        Assume.that(graph.pathM[i][j].path != null);
        graph.updateEdge(i, j, w);
        graph.computeShortestPaths();
        Assertions.assertThat(graph.pathM[i][j].path != null).isTrue();
    }


    // If (p,l) is the shortest, non-null path from i to j in graph
    // then (reverse(p),l) is the shortest path from j to i in reverseEdges(graph)
    @Property
    @Report(Reporting.GENERATED)
    public void propertyReverseEdgesAndShortestPaths1(@ForAll("adjMatrices") Integer[][] adjM,
                                                      @ForAll("nodePairs") Tuple2<Integer,Integer> t) {
        Graph graph = new Graph(adjM);
        graph.computeShortestPaths();
        int i = t.get1();
        int j = t.get2();
            Path p0 = graph.shortestPath(i,j);
        if (p0.path != null) {
            p0.path.add(0, i);
            System.out.println("p0 = " + p0);
            graph.reverseEdges();
            graph.computeShortestPaths();
            Path p = graph.shortestPath(j, i);
            p.path.add(0, j);
            System.out.println("p = " + p);
            Assertions.assertThat(p.length).isEqualTo(p0.length);
            reverse(p.path);
            Assertions.assertThat(p.path).isEqualTo(p0.path);
            System.out.println(t);
        }
        else {
            graph.reverseEdges();
            graph.computeShortestPaths();
            Path p = graph.shortestPath(j, i);
            Assertions.assertThat(p.path == null).isTrue();
        }
    }

    // GENERATORS ========================================================
    @Provide
    public static Arbitrary<Tuple2<Integer,Integer>> nodePairs() {
        final int numNodes = 10;
        Arbitrary<Integer> nodes = Arbitraries.integers().between(0, numNodes-1);
        return Combinators.combine(nodes,nodes)
                .as((n1,n2) -> Tuple.of(n1, n2))
                .filter(t -> t.get1() != t.get2());
    }

    @Provide
    public static Arbitrary<Integer[][]> adjMatrices() {
        final int MinNumNodes = 10;
        final int MaxNumNodes = MinNumNodes;
        Arbitrary<Integer> weightArb = Arbitraries.of(-1, 1, 2, 3);  // all choices equally likely
        // Arbitrary<Integer> weightArb = Arbitraries.of(-1, 1, 1, 2, 2, 3, 3);  // -1 less likely than other weights
        // Arbitrary<Integer> weightArb = Arbitraries.of(-1, -1, -1, 1, 2, 3);  // -1 more likely than other weights
        Arbitrary<Integer[]> intArrayArb = weightArb.array(Integer[].class).ofMinSize(MinNumNodes).ofMaxSize(MaxNumNodes);
        Arbitrary<Integer[][]> intMatrixArb =
                intArrayArb.array(Integer[][].class)
                        .ofMinSize(MinNumNodes)
                        .ofMaxSize(MaxNumNodes)
                        .filter(m -> {  // filter out the matrices that are not square
                            int dim = m[0].length;  // use length of first row of the matrix as matrix dimension
                            return Arrays
                                    .stream(m)
                                    .allMatch(row -> m.length==dim && row.length==dim);
                        })
                        .map(m -> {    // break cycles of length 0 and 1; w/o this, get too many cyclic matrices
                            int len = m[0].length;
                            for (int i=0; i<len; i++)
                                m[i][i] = 0;
                            return m;
                        });
        return intMatrixArb;
    }
    // GENERATORS: Tests ========================================================
    @Property
    @Report(Reporting.GENERATED)
    public void propertyCheckGenerator(@ForAll("adjMatrices") Integer[][] adjM) {
        Graph graph = new Graph(adjM);
        graph.computeShortestPaths();
        String graph0Str = graph.toString();
        System.out.println(graph0Str);
        graph.reverseEdges();
        graph.reverseEdges();
        String graphStr = graph.toString();
        System.out.println(graph0Str);
        Assertions.assertThat(graphStr).isEqualTo(graph0Str);
    }

}

