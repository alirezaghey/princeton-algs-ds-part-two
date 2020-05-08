/* *****************************************************************************
 *  Name: Alireza Ghey
 *  Date: 08-05-2020
 *  Description: Enables content-aware resizing of an image but finding
 *  horizontal and vertical seams of least interesting pixels and removing
 *  them.
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {
    private static final int MAX_ENERGY = 1000;
    private Picture pic;
    private double[][] energyMatrix;
    private int[] hSeam;
    private int[] vSeam;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        pic = new Picture(picture);
        updateEnergyMatrix();
        updateHSeam();
        updateVSeam();
    }

    private void updateEnergyMatrix() {
        energyMatrix = new double[height()][width()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                energyMatrix[j][i] = energy(i, j);
            }
        }
    }

    // Find the best left-right seam
    private void updateHSeam() {
        double[][] dp = new double[height()][width()];

        // Fill first column with max energy
        for (int i = 0; i < height(); i++)
            dp[i][0] = MAX_ENERGY;

        // Fill the first and last rows with max energy
        // for (int i = 0; i < width(); i++)
        //     dp[0][i] = dp[height() - 1][i] = MAX_ENERGY;

        // Fill the dp with the cheapest path
        for (int i = 1; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (height() == 1)
                    dp[j][i] = dp[j][i - 1] + energyMatrix[j][i];
                else if (j == 0)
                    dp[j][i] = Math.min(dp[j][i - 1], dp[j + 1][i - 1]) + energyMatrix[j][i];
                else if (j == height() - 1)
                    dp[j][i] = Math.min(dp[j - 1][i - 1], dp[j][i - 1]) + energyMatrix[j][i];
                else
                    dp[j][i] = Math.min(Math.min(dp[j - 1][i - 1], dp[j][i - 1]), dp[j + 1][i - 1])
                            + energyMatrix[j][i];
            }
        }
        // printDP(dp);

        // Find the smallest cell at the right most column
        // This is the min cost to move horizontally from left to right
        int minCand = 0;
        for (int i = 1; i < height(); i++) {
            if (dp[i][width() - 1] < dp[minCand][width() - 1])
                minCand = i;
        }

        // Reconstruct the cheapest path from right to left
        int[] newHSeam = new int[width()];
        newHSeam[width() - 1] = minCand;
        for (int i = width() - 2; i >= 0; i--) {
            double best = dp[newHSeam[i + 1]][i];
            newHSeam[i] = newHSeam[i + 1];
            if (newHSeam[i + 1] > 0 && best > dp[newHSeam[i + 1] - 1][i]) {
                best = dp[newHSeam[i + 1] - 1][i];
                newHSeam[i] = newHSeam[i + 1] - 1;
            }
            if (newHSeam[i + 1] < height() - 1 && best > dp[newHSeam[i + 1] + 1][i]) {
                newHSeam[i] = newHSeam[i + 1] + 1;
            }
        }

        hSeam = newHSeam;


    }

    // Find the best top-down seam
    private void updateVSeam() {
        double[][] dp = new double[height()][width()];

        // Fill first row with max energy
        for (int i = 0; i < width(); i++)
            dp[0][i] = MAX_ENERGY;

        // Fill the left- and right most columns with max energy
        // for (int i = 0; i < height(); i++)
        //     dp[i][0] = dp[i][width() - 1] = MAX_ENERGY;

        // Fill the dp with the cheapest path
        for (int i = 1; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                if (width() == 1) {
                    dp[i][j] = dp[i - 1][j] + energyMatrix[i][j];
                }
                else if (j == 0)
                    dp[i][j] = Math.min(dp[i - 1][j], dp[i - 1][j + 1])
                            + energyMatrix[i][j];
                else if (j == width() - 1)
                    dp[i][j] = Math.min(dp[i - 1][j - 1], dp[i - 1][j])
                            + energyMatrix[i][j];
                else
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1], dp[i - 1][j]), dp[i - 1][j + 1])
                            + energyMatrix[i][j];
            }
        }

        // printDP(dp);
        // Find the smallest cell at the last row of dp
        // This is the min cost to move vertically from top to bottom
        int minCand = 0;
        for (int i = 1; i < width(); i++) {
            if (dp[height() - 1][i] < dp[height() - 1][minCand])
                minCand = i;
        }

        // Reconstruct the cheapest path from bottom up
        int[] newVSeam = new int[height()];
        newVSeam[height() - 1] = minCand;
        for (int i = height() - 2; i >= 0; i--) {
            double best = dp[i][newVSeam[i + 1]];
            newVSeam[i] = newVSeam[i + 1];
            if (newVSeam[i + 1] > 0 && best > dp[i][newVSeam[i + 1] - 1]) {
                best = dp[i][newVSeam[i + 1] - 1];
                newVSeam[i] = newVSeam[i + 1] - 1;
            }
            if (newVSeam[i + 1] < width() - 1 && best > dp[i][newVSeam[i + 1] + 1]) {
                newVSeam[i] = newVSeam[i + 1] + 1;
            }
        }

        vSeam = newVSeam;

    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) throw new IllegalArgumentException();
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) return MAX_ENERGY;

        int leftPix = pic.getRGB(x - 1, y);
        int rightPix = pic.getRGB(x + 1, y);
        double rX = Math.pow(((leftPix >> 16) & 0xFF) - ((rightPix >> 16) & 0xFF), 2);
        double gX = Math.pow(((leftPix >> 8) & 0xFF) - ((rightPix >> 8) & 0xFF), 2);
        double bX = Math.pow((leftPix & 0xFF) - (rightPix & 0xFF), 2);

        int topPix = pic.getRGB(x, y - 1);
        int bottomPix = pic.getRGB(x, y + 1);
        double rY = Math.pow(((topPix >> 16) & 0xFF) - ((bottomPix >> 16) & 0xFF), 2);
        double gY = Math.pow(((topPix >> 8) & 0xFF) - ((bottomPix >> 8) & 0xFF), 2);
        double bY = Math.pow((topPix & 0xFF) - (bottomPix & 0xFF), 2);

        return Math.sqrt(rX + gX + bX + rY + gY + bY);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // return Arrays.copyOf(hSeam, hSeam.length);
        return Arrays.copyOf(hSeam, hSeam.length);
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // return Arrays.copyOf(vSeam, vSeam.length);
        return Arrays.copyOf(vSeam, vSeam.length);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != width()) throw new IllegalArgumentException();
        if (height() < 2) throw new IllegalArgumentException();

        for (int i = 0; i < width(); i++) {
            if (seam[i] < 0 || seam[i] >= height()) throw new IllegalArgumentException();
            if (i > 0 && Math.abs(seam[i - 1] - seam[i]) > 1) throw new IllegalArgumentException();
        }

        Picture newPic = new Picture(width(), height() - 1);
        for (int i = 0; i < width(); i++) {
            boolean found = false;
            for (int j = 0; j < height(); j++) {
                if (seam[i] == j) {
                    found = true;
                    continue;
                }
                newPic.setRGB(i, j - (found ? 1 : 0), pic.getRGB(i, j));
            }
        }
        pic = newPic;
        updateEnergyMatrix();
        updateVSeam();
        updateHSeam();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();
        if (width() < 2) throw new IllegalArgumentException();

        for (int i = 0; i < height(); i++) {
            if (seam[i] < 0 || seam[i] >= width()) throw new IllegalArgumentException();
            if (i > 0 && Math.abs(seam[i - 1] - seam[i]) > 1) throw new IllegalArgumentException();
        }

        Picture newPic = new Picture(width() - 1, height());
        for (int i = 0; i < height(); i++) {
            boolean found = false;
            for (int j = 0; j < width(); j++) {
                if (j == seam[i]) {
                    found = true;
                    continue;
                }
                newPic.setRGB(j - (found ? 1 : 0), i, pic.getRGB(j, i));
            }
        }
        pic = newPic;
        updateEnergyMatrix();
        updateVSeam();
        updateHSeam();
    }

    private void printDP(double[][] dp) {
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                System.out.printf("%7.2f ", dp[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

}
