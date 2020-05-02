/* *****************************************************************************
 *  Name: Alireza Ghey
 *  Date: 2-05-2020
 *  Description: An immutable type with an underlying DiGraph that calculates the
 *  nearest common ancestor of two nodes or two set of nodes and their distance.
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SAP {
    private Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        graph = new Digraph(G);
    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v >= graph.V() || w < 0 || w >= graph.V())
            throw new IllegalArgumentException();

        Integer[] vArr = { v };
        Integer[] wArr = { w };
        Ancestor ancestor = getAncestor((Iterable<Integer>) Arrays.asList(vArr),
                                        (Iterable<Integer>) Arrays.asList(wArr));

        return ancestor != null ? ancestor.distance() : -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v >= graph.V() || w < 0 || w >= graph.V())
            throw new IllegalArgumentException();

        Integer[] vArr = { v };
        Integer[] wArr = { w };
        Ancestor ancestor = getAncestor((Iterable<Integer>) Arrays.asList(vArr),
                                        (Iterable<Integer>) Arrays.asList(wArr));

        return ancestor != null ? ancestor.ancestor() : -1;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        Ancestor ancestor = getAncestor(v, w);
        return ancestor != null ? ancestor.distance() : -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        Ancestor ancestor = getAncestor(v, w);
        return ancestor != null ? ancestor.ancestor() : -1;

    }

    private int hasSameNode(Iterable<Integer> v, Iterable<Integer> w) {
        for (Integer vv : v) {
            if (vv == null || vv < 0 || vv >= graph.V()) throw new IllegalArgumentException();
            for (Integer ww : w) {
                if (ww == null || ww < 0 || ww >= graph.V()) throw new IllegalArgumentException();
                if (vv.equals(ww)) return vv;
            }
        }
        return -1;
    }

    private Ancestor getAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        int sameNode = hasSameNode(v, w);
        if (sameNode != -1) {
            return new Ancestor(sameNode, 0);
        }
        // int distance = 0;
        Map<Integer, Integer> wSeen = new HashMap<>();
        Map<Integer, Integer> vSeen = new HashMap<>();
        Queue<Ancestor> vQueue = new LinkedList<>();
        Queue<Ancestor> wQueue = new LinkedList<>();

        for (int vv : v) {
            vQueue.add(new Ancestor(vv, 0));
            vSeen.put(vv, 0);
        }
        for (int ww : w) {
            wQueue.add(new Ancestor(ww, 0));
            wSeen.put(ww, 0);
        }

        // ArrayList<Ancestor> resultCands = new ArrayList<>();
        Ancestor resultCand = null;
        while (!vQueue.isEmpty() || !wQueue.isEmpty()) {
            // Map<Integer, Integer> vNew = new HashMap<>();
            // Map<Integer, Integer> wNew = new HashMap<>();
            if (!vQueue.isEmpty()) {
                int length = vQueue.size();
                for (int i = 0; i < length; i++) {
                    Ancestor vCurrent = vQueue.remove();
                    if (wSeen.containsKey(vCurrent.ance)) {
                        int distance = wSeen.get(vCurrent.ance) + vCurrent.dist;
                        if (resultCand == null || resultCand.dist > distance)
                            resultCand = new Ancestor(vCurrent.ance, distance);
                    }
                    for (int neighbor : graph.adj(vCurrent.ance)) {
                        if (!vSeen.containsKey(neighbor)) {
                            vQueue.add(new Ancestor(neighbor, vCurrent.dist + 1));
                            // vNew.put(neighbor, vCurrent.dist + 1);
                            vSeen.put(neighbor, vCurrent.dist + 1);
                        }
                    }
                }
            }

            if (!wQueue.isEmpty()) {
                int length = wQueue.size();
                for (int i = 0; i < length; i++) {
                    Ancestor wCurrent = wQueue.remove();
                    if (vSeen.containsKey(wCurrent.ance)) {
                        int distance = vSeen.get(wCurrent.ance) + wCurrent.dist;
                        if (resultCand == null || resultCand.dist > distance)
                            resultCand = new Ancestor(wCurrent.ance, distance);
                    }
                    for (int neighbor : graph.adj(wCurrent.ance)) {
                        if (!wSeen.containsKey(neighbor)) {
                            wQueue.add(new Ancestor(neighbor, wCurrent.dist + 1));
                            // wNew.put(neighbor, wCurrent.dist + 1);
                            wSeen.put(neighbor, wCurrent.dist + 1);
                        }
                    }
                }
            }
            // vSeen.putAll(vNew);
            // wSeen.putAll(wNew);
            if (resultCand != null) {
                if (!vQueue.isEmpty() && resultCand.dist <= vQueue.peek().dist) break;
                if (!wQueue.isEmpty() && resultCand.dist <= wQueue.peek().dist) break;
            }
        }
        return resultCand;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }


    private class Ancestor {
        private int ance;
        private int dist;

        public Ancestor(int ance, int dist) {
            this.ance = ance;
            this.dist = dist;
        }

        public int ancestor() {
            return ance;
        }

        public int distance() {
            return dist;
        }

        public int hashCode() {
            return Integer.hashCode(this.ance);
        }

        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (obj.getClass() != this.getClass()) return false;
            Ancestor that = (Ancestor) obj;
            return this.ance == that.ance;
        }
    }
}
