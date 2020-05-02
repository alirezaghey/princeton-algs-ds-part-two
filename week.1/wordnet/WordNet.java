/* *****************************************************************************
 *  Name: Alireza Ghey
 *  Date: 2-05-2020
 *  Description: A wordnet with an underlying directed graph that helps you find
 *  distance between different words and their best related words.
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class WordNet {
    private Digraph graph;
    private Map<String, Set<Integer>> words;
    private ArrayList<String> synsets;
    private SAP sap;


    // TODO: Throw IllegalArgumentException if input does not correspond to a rooted DAG.
    // constructor takes the name of the two input files
    public WordNet(String synsetsFilePath, String hypernymsFilePath) {
        if (synsetsFilePath == null || hypernymsFilePath == null)
            throw new IllegalArgumentException();

        readSynsetsWithIn(synsetsFilePath);
        graph = new Digraph(synsets.size());

        addEdgesWithIn(hypernymsFilePath);

        // Commented out since the checking algorithm doesn't work properly
        // if (!isRootedDAG()) throw new IllegalArgumentException();

        sap = new SAP(graph);
    }

    // TODO: Currently not working properly.
    // Specification are not clear to me.
    private boolean isRootedDAG() {
        int root = -1;
        int[] inDegrees = new int[graph.V()];
        for (int i = 0; i < graph.V(); i++) {
            inDegrees[i] = graph.indegree(i);
            if (inDegrees[i] == 0) {
                if (root == -1) root = i;
                else return false;
            }
        }
        if (root == -1) return false;

        ArrayList<Integer> topSorted = new ArrayList<>();
        Stack<Integer> zeroDegrees = new Stack<>();
        zeroDegrees.push(root);

        while (!zeroDegrees.isEmpty()) {
            int node = zeroDegrees.pop();
            topSorted.add(node);
            for (int neighbor : graph.adj(node)) {
                inDegrees[neighbor]--;
                if (inDegrees[neighbor] == 0) zeroDegrees.push(neighbor);
                if (inDegrees[neighbor] < 0) return false;
            }
        }

        return topSorted.size() == graph.V();

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return words.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();

        return words.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        return sap.length(words.get(nounA), words.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        int id = sap.ancestor(words.get(nounA), words.get(nounB));
        if (id >= 0 && id < synsets.size()) return synsets.get(id);
        else return null;
    }

    private void readSynsetsWithIn(String filePath) {
        if (filePath == null || filePath.length() == 0) throw new IllegalArgumentException();

        words = new HashMap<>();
        synsets = new ArrayList<>();
        In in = new In(filePath);

        while (in.hasNextLine()) {
            String str = in.readLine().split(",")[1];
            synsets.add(str);
            for (String subStr : str.split(" ")) {
                if (words.containsKey(subStr)) {
                    words.get(subStr).add(synsets.size() - 1);
                }
                else {
                    Set<Integer> set = new HashSet<>();
                    set.add(synsets.size() - 1);
                    words.put(subStr, set);
                }
            }
        }
    }

    private void readSynsets(String filePath) {
        if (filePath == null || filePath.length() == 0) throw new IllegalArgumentException();

        words = new HashMap<>();
        synsets = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filePath), "UTF-8")) {
            while (sc.hasNextLine()) {
                String str = sc.nextLine().split(",")[1];
                synsets.add(str);
                for (String subStr : str.split(" ")) {
                    if (words.containsKey(subStr)) {
                        words.get(subStr).add(synsets.size() - 1);
                    }
                    else {
                        Set<Integer> set = new HashSet<>();
                        set.add(synsets.size() - 1);
                        words.put(subStr, set);
                    }
                }
            }
            if (sc.ioException() != null) throw sc.ioException();
        }
        catch (java.io.IOException e) {
            System.out.print(e.getMessage());
        }

    }

    private void addEdgesWithIn(String filePath) {
        if (filePath == null || filePath.length() == 0) throw new IllegalArgumentException();
        In in = new In(filePath);
        while (in.hasNextLine()) {
            String[] vertices = in.readLine().split(",");
            int source = Integer.parseInt(vertices[0]);
            for (int i = 1; i < vertices.length; i++) {
                graph.addEdge(source, Integer.parseInt(vertices[i]));
            }
        }
    }

    private void addEdges(String filePath) {
        if (filePath == null || filePath.length() == 0) throw new IllegalArgumentException();
        try (Scanner sc = new Scanner(new File(filePath), "UTF-8")) {
            while (sc.hasNextLine()) {
                String[] vertices = sc.nextLine().split(",");
                int source = Integer.parseInt(vertices[0]);
                for (int i = 1; i < vertices.length; i++) {
                    graph.addEdge(source, Integer.parseInt(vertices[i]));
                }
            }
            if (sc.ioException() != null) throw sc.ioException();
        }
        catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        for (String noun : wordnet.nouns()) {
            System.out.println(noun);
        }
        System.out.println("Hello World!");
    }
}
