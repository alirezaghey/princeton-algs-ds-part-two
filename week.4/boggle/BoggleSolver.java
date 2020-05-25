/* *****************************************************************************
 *  Author: Alireza Ghey
 *  Name: BoggleSolver
 *  Date: 25-05-2020
 *  Description: An implementation for the famous Boggle game.
 *  Create a trie out of a dictionary of words.
 *  Receives a board of letters and solves the board by finding all the words
 *  that appear in the dictionary and are possible to create out of adjacent
 *  letters in the board.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BoggleSolver {
    private Set<String> wordSet;
    private Trie wordTrie;
    private int[] lengthToScore = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        wordSet = new HashSet<>();
        wordTrie = new Trie();
        for (int i = 0; i < dictionary.length; i++) {
            wordSet.add(dictionary[i]);
            wordTrie.addWord(dictionary[i]);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<Point> visited = new HashSet<>();
        Set<String> res = new HashSet<>();
        StringBuilder partialRes = new StringBuilder();
        Map<Character, Trie.TrieNode> curr = this.wordTrie.root;
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                getWords(board, visited, res, partialRes, curr, row, col);
            }
        }

        return res;

    }

    private void getWords(BoggleBoard board, Set<Point> visited, Set<String> res,
                          StringBuilder partialRes, Map<Character, Trie.TrieNode> curr,
                          int row, int col) {
        if (row < 0 || row >= board.rows() || col < 0 || col >= board.cols()) return;
        Point p = new Point(row, col);
        if (visited.contains(p)) return;
        char character = board.getLetter(row, col);
        if (!curr.containsKey(character)) return;
        // if (!isU) {
        boolean isQu = false;
        if (character == 'Q') {
            curr = curr.get(character).children;
            character = 'U';
            if (!curr.containsKey(character)) return;
            partialRes.append("QU");
            isQu = true;
        }
        else {
            partialRes.append(character);
        }

        Trie.TrieNode node = curr.get(character);
        if (node.ends && partialRes.length() > 2) res.add(partialRes.toString());
        visited.add(p);
        getWords(board, visited, res, partialRes, node.children,
                 row - 1, col); // Top
        getWords(board, visited, res, partialRes, node.children,
                 row - 1, col + 1); // Top-Right
        getWords(board, visited, res, partialRes, node.children,
                 row, col + 1); // Right
        getWords(board, visited, res, partialRes, node.children,
                 row + 1, col + 1); // Bottom-Right
        getWords(board, visited, res, partialRes, node.children,
                 row + 1, col); // Bottom
        getWords(board, visited, res, partialRes, node.children,
                 row + 1, col - 1); // Bottom-Left
        getWords(board, visited, res, partialRes, node.children,
                 row, col - 1); // Left
        getWords(board, visited, res, partialRes, node.children,
                 row - 1, col - 1); // Top-Left
        visited.remove(p);
        if (isQu) {
            partialRes.deleteCharAt(partialRes.length() - 1);
            partialRes.deleteCharAt(partialRes.length() - 1);
        }
        else {
            partialRes.deleteCharAt(partialRes.length() - 1);
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (wordSet.contains(word)) {
            if (word.length() > 8) return 11;
            else return this.lengthToScore[word.length()];
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private class Point {
        private final int row;
        private final int col;

        public Point(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Point point = (Point) object;
            return row == point.row &&
                    col == point.col;
        }

        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    private class Trie {
        private class TrieNode {
            private char character;
            private boolean ends;
            private final Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();

            public TrieNode(char character) {
                this.character = character;
                this.ends = false;
            }

            public boolean equals(Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                TrieNode trieNode = (TrieNode) object;
                return character == trieNode.character;
            }

            public int hashCode() {
                return Objects.hash(character);
            }
        }

        private final Map<Character, TrieNode> root = new HashMap<>();

        public void addWord(String word) {
            Map<Character, TrieNode> curr = root;
            for (int i = 0; i < word.length(); i++) {
                if (!curr.containsKey(word.charAt(i))) {
                    TrieNode node = new TrieNode(word.charAt(i));
                    if (i == word.length() - 1) node.ends = true;
                    curr.put(word.charAt(i), node);
                }
                if (i == word.length() - 1) curr.get(word.charAt(i)).ends = true;
                curr = curr.get(word.charAt(i)).children;
            }
        }

        public Map<Character, TrieNode> getRootOfPrefix(String prefix) {
            Map<Character, TrieNode> curr = this.root;
            for (int i = 0; i < prefix.length(); i++) {
                if (!curr.containsKey(prefix.charAt(i))) return null;
                curr = curr.get(prefix.charAt(i)).children;
            }
            return curr;
        }

        public Iterable<String> collect(List<String> result,
                                        StringBuilder partialResult,
                                        Map<Character, TrieNode> curr) {
            for (Character character : curr.keySet()) {
                partialResult.append(character);
                if (curr.get(character).ends) result.add(partialResult.toString());
                collect(result, partialResult, curr.get(character).children);
                partialResult.deleteCharAt(partialResult.length() - 1);
            }
            return result;
        }


    }
}
