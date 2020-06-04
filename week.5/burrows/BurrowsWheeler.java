/* *****************************************************************************
 *  Author: Alireza Ghey
 *  Name: BurrowsWheeler
 *  Date: 28-05-2020
 *  Description: Implements the Burrows Wheeler transform.
 *  Known issues: Small optimization possile in the inverse transform implementation
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csArray = new CircularSuffixArray(s);
        char[] chars = new char[csArray.length()];
        int start = 0;
        for (int i = 0; i < csArray.length(); i++) {
            chars[i] = s.charAt((csArray.index(i) + csArray.length() - 1) % csArray.length());
            if (csArray.index(i) == 0) start = i;
        }
        BinaryStdOut.write(start);
        for (int i = 0; i < chars.length; i++) BinaryStdOut.write(chars[i]);
        BinaryStdOut.flush();

    }

    // NAIVE IMPLEMENTATION
    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    // public static void inverseTransform() {
    //     int first = BinaryStdIn.readInt();
    //     char[] chars = BinaryStdIn.readString().toCharArray();
    //     char[][] matrix = new char[chars.length][chars.length];
    //
    //     for (int i = chars.length - 1; i >= 0; i--) {
    //         for (int j = 0; j < chars.length; j++) {
    //             matrix[j][i] = chars[j];
    //         }
    //         Arrays.sort(matrix, (char[] a, char[] b) -> {
    //             for (int k = 0; k < a.length; k++) {
    //                 if (a[k] < b[k]) return -1;
    //                 if (a[k] > b[k]) return 1;
    //             }
    //             return 0;
    //         });
    //     }
    //     for (char c : matrix[first])
    //         BinaryStdOut.write(c);
    //     BinaryStdOut.flush();
    // }

    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] chars = BinaryStdIn.readString().toCharArray();
        Set<Character> setChars = new HashSet<>();
        for (char c : chars)
            setChars.add(c);
        Character[] uniqueSortedChars = setChars.toArray(new Character[setChars.size()]);
        Arrays.sort(uniqueSortedChars);

        Map<Character, Integer> charToPosition = new HashMap<>();
        for (int i = 0; i < uniqueSortedChars.length; i++)
            charToPosition.put(uniqueSortedChars[i], i);

        ArrayList<LinkedList<Integer>> mapping = new ArrayList<>(uniqueSortedChars.length);
        for (int i = 0; i < uniqueSortedChars.length; i++)
            mapping.add(new LinkedList<>());

        for (int i = 0; i < chars.length; i++) {
            mapping.get(charToPosition.get(chars[i])).add(i);
        }

        int[] next = new int[chars.length];
        for (int i = 0, k = 0; i < mapping.size(); i++) {
            LinkedList<Integer> ll = mapping.get(i);
            while (!ll.isEmpty())
                next[k++] = ll.remove();
        }

        for (int i = 0; i < chars.length; i++) {
            first = next[first];
            BinaryStdOut.write(chars[first]);
        }
        BinaryStdOut.flush();
    }


    // public static void inverseTransform() {
    //     int first = BinaryStdIn.readInt();
    //     ArrayList<Character> chars = new ArrayList<>();
    //     ArrayList<Integer>[] mapping = (ArrayList<Integer>[]) new ArrayList[256];
    //     int counter = 0;
    //     while (!BinaryStdIn.isEmpty()) {
    //         char c = BinaryStdIn.readChar();
    //         chars.add(c);
    //         if (mapping[c] == null)
    //             mapping[c] = new ArrayList<Integer>();
    //         mapping[c].add(counter++);
    //     }
    //
    //     int[] next = new int[counter];
    //     for (int i = 0, k = 0; i < mapping.length; i++) {
    //         if (mapping[i] == null) continue;
    //         ArrayList<Integer> list = mapping[i];
    //         for (int idx : list) {
    //             next[k++] = idx;
    //         }
    //     }
    //
    //     for (int i = 0; i < counter; i++) {
    //         first = next[first];
    //         BinaryStdOut.write(chars.get(first));
    //     }
    //     BinaryStdOut.flush();
    // }


    // public static void inverseTransform() {
    //     int first = BinaryStdIn.readInt();
    //     ArrayList<Character> chars = new ArrayList<>();
    //     int[] counts = new int[256];
    //     while (!BinaryStdIn.isEmpty()) {
    //         char c = BinaryStdIn.readChar();
    //         chars.add(c);
    //         counts[c]++;
    //
    //     }
    //     int curr = counts[0];
    //     for (int i = 1; i < counts.length; i++) {
    //         int temp = counts[i];
    //         counts[i] = curr;
    //         curr += temp;
    //     }
    //
    //     int[] next = new int[chars.size()];
    //     for (int i = 0; i < chars.size(); i++) {
    //         char c = chars.get(i);
    //         next[counts[c]] = i;
    //         counts[c]++;
    //     }
    //
    //
    //     for (int i = 0; i < chars.size(); i++) {
    //         first = next[first];
    //         BinaryStdOut.write(chars.get(first));
    //     }
    //     BinaryStdOut.flush();
    // }


    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        char mode = args[0].charAt(0);
        if (mode == '-') transform();
        else if (mode == '+') inverseTransform();
    }

}
