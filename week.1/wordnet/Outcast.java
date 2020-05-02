/* *****************************************************************************
 *  Name: Alireza Ghey
 *  Date: 2-05-2020
 *  Description: Using a WordNet, Outcast shows you the least related noun amoung
 *  a group of nouns.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[] dists = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                int dist = wordnet.distance(nouns[i], nouns[j]);
                dists[i] += dist;
                dists[j] += dist;
            }
        }
        int cand = 0;
        for (int i = 1; i < dists.length; i++) {
            if (dists[i] > dists[cand]) cand = i;
        }
        return nouns[cand];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

