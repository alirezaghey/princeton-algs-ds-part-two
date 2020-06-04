/* *****************************************************************************
 *  Author: Alireza Ghey
 *  Name: MoveToFront
 *  Date: 28-05-2020
 *  Description: Implementation of the MTF Transform used as a pre-routine for
 *  data compression
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] EXTENDED = new char[256];
        for (int i = 0; i < 256; i++)
            EXTENDED[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            for (int i = 0; i < 256; i++) {
                if (c == EXTENDED[i]) {
                    BinaryStdOut.write((char) i);
                    char temp = EXTENDED[i];
                    System.arraycopy(EXTENDED, 0, EXTENDED, 1, i);
                    System.arraycopy(EXTENDED, i + 1, EXTENDED, i + 1, 256 - i - 1);
                    EXTENDED[0] = temp;
                    break;
                }
            }
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] EXTENDED = new char[256];
        for (int i = 0; i < 256; i++)
            EXTENDED[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(EXTENDED[c]);
            char temp = EXTENDED[c];
            System.arraycopy(EXTENDED, 0, EXTENDED, 1, c);
            System.arraycopy(EXTENDED, c + 1, EXTENDED, c + 1, 256 - c - 1);
            EXTENDED[0] = temp;
        }
        BinaryStdOut.flush();
    }


    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        char mode = args[0].charAt(0);
        if (mode == '-') encode();
        if (mode == '+') decode();
    }

}
