/* *****************************************************************************
 *  Author: Alireza Ghey
 *  Name: CircularSuffixArray
 *  Date: 28-05-2020
 *  Description: Creates a circularly shifted suffix array that helps in creating
 *  the Burrows-Wheeler transform
 *  Known Issues: The current sorting mechnism uses java's built-in Arrays.sort
 *  which is an implementation of the Tim-Sort. It's time complexity of O(n * log n)
 *  is inferior compared to what can be achieve with radix sort.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.Arrays;

public class CircularSuffixArray {
    private int[] indices;
    private int len;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        len = s.length();
        indices = new int[len];
        char[] chars = s.toCharArray();
        Suffix[] suffixes = new Suffix[len];
        for (int i = 0; i < len; i++)
            suffixes[i] = new Suffix(chars, i);

        Arrays.sort(suffixes);

        for (int i = 0; i < len; i++)
            indices[i] = suffixes[i].index;

    }

    // length of s
    public int length() {
        return len;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= this.length()) throw new IllegalArgumentException();
        return indices[i];
    }

    private class Suffix implements Comparable<Suffix> {
        private int index;
        private char[] chars;

        public Suffix(char[] chars, int index) {
            this.index = index;
            this.chars = chars;
        }

        @Override
        public int compareTo(Suffix that) {
            int sz = this.chars.length;
            for (int i = 0; i < sz; i++) {
                if (this.chars[(i + this.index) % sz] < that.chars[(i + that.index) % sz])
                    return -1;
                if (this.chars[(i + this.index) % sz] > that.chars[(i + that.index) % sz])
                    return 1;
            }
            if (this.chars.length < that.chars.length) return -1;
            if (this.chars.length > that.chars.length) return 1;
            return 0;
        }

        @Override
        public String toString() {
            return String.format("%s %s", Arrays.toString(this.chars), this.index);
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        String s = in.readAll();
        System.out.println(s);
        CircularSuffixArray csArray = new CircularSuffixArray(s);
        System.out.println(Arrays.toString(csArray.indices));
    }

}
