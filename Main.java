import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        if (args.length < 5) {
            System.out.println("Incorrect number of arguments");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];
        String zeroCrossFile = args[2];
        String binEdgeFile = args[3];
        String debugFile = args[4];

        int numRows = 0;
        int numCols = 0;
        int minVal = 0;
        int maxVal = 0;

        try {
            Scanner scanner = new Scanner(new File(inFile));
            numRows = scanner.nextInt();
            numCols = scanner.nextInt();
            minVal = scanner.nextInt();
            maxVal = scanner.nextInt();
            scanner.close();
        } catch (FileNotFoundException e){
            System.out.println("Error: Input file not found. " + e.getMessage());
        }

        Edge edge = new Edge(numRows, numCols, minVal, maxVal);
        edge.loadImage(inFile);
        edge.imgReformatPrettyPrint(edge.MFArray, outFile);
        edge.zeroCrossingEdgeDetector(zeroCrossFile, binEdgeFile, debugFile);

    }
}

class Edge {
    int numRows;
    int numCols;
    int minVal;
    int maxVal;
    int[][] MFArray;
    int[][] edgeArray;
    int[][] zeroCrossArray;
    int[][] mask;

    public Edge(int numRows, int numCols, int minVal, int maxVal){
        this.numRows = numRows;
        this.numCols = numCols;
        this.minVal = minVal;
        this.maxVal = maxVal;

        this.MFArray = new int[numRows + 2][numCols + 2];
        this.edgeArray = new int[numRows + 2][numCols + 2];
        this.zeroCrossArray = new int[numRows + 2][numCols + 2];
        this.mask = new int[][]{
            {0, -2, 0},
            {-2, 8, -2},
            {0, -2, 0},
        };
    }

    public void loadImage(String inFile){
        try {
            Scanner scanner = new Scanner(new File(inFile));
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            for (int i = 2; i < numRows + 2; i++) {
                for (int j = 2; j < numCols + 2; j++) {
                    if (scanner.hasNextInt()) {
                        MFArray[i][j] = scanner.nextInt();
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    public void zeroCrossingEdgeDetector(String zeroCrossFile, String binEdgeFile, String debugFile){
        computeEdge(debugFile);
        crossZero(debugFile);
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(zeroCrossFile, true));
            writer.println(numRows + " " + numCols + " " + "0" + " " + "1");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
        imgReformatPrettyPrint(zeroCrossArray, zeroCrossFile);
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(binEdgeFile, true));
            writer.println(numRows + " " + numCols + " " + "0" + " " + "1");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
        binaryEdgeDetector(zeroCrossArray, binEdgeFile);


    }

    public void computeEdge(String debugFile){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(debugFile, true));
            writer.println("Entering computeEdge() method");


            for (int i = 1; i < numRows; i++) {
                for (int j = 1; j < numCols; j++) {
                    edgeArray[i][j] = convolution3x3(i, j);
                }
            }

            writer.println("Leaving computeEdge() method");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public void crossZero(String debugFile){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(debugFile, true));
            writer.println("Entering crossZero() method");


            for (int i = 1; i < numRows; i++) {
                for (int j = 1; j < numCols; j++) {
                    if (edgeArray[i][j] <= 0) {
                        zeroCrossArray[i][j] = 0;
                    } else {
                        zeroCrossArray[i][j] = 1;
                    }
                }
            }
            writer.println("Leaving crossZero() method");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public void imgReformatPrettyPrint(int[][] inArray, String outFile){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(outFile, true));
            writer.println(numRows + " " + numCols + " " + minVal + " " + maxVal);

            String str = Integer.toString(maxVal);
            int width = str.length();

            for (int i = 2; i < numRows + 2; i++) {
                for (int j = 2; j < numCols + 2; j++) {
                    if(inArray[i][j] > 0){
                        writer.print(inArray[i][j]);
                    } else {
                        writer.print('.');
                    }
                    String valueStr = Integer.toString(inArray[i][j]);
                    int valueWidth = valueStr.length();
                    while(valueWidth <= width) {
                        writer.print(" ");
                        valueWidth++;
                    }
                }
                writer.println();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the output file: " + e.getMessage());
        }
    }

    public int convolution3x3(int i, int j){
        int sum = 0;
        for (int maskRow = 0; maskRow < 3; maskRow++) {
            for (int maskCol = 0; maskCol < 3; maskCol++) {
                int row = i + maskRow - 1;
                int col = j + maskCol - 1;
                sum += MFArray[row][col] * mask[maskRow][maskCol];
            }
        }
        return sum;

    }

    public void binaryEdgeDetector(int[][] zeroCrossArray, String binEdgeFile){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(binEdgeFile));

            writer.println(numRows + " " + numCols + " 0 1");

            for (int i = 1; i <= numRows; i++) {
                for (int j = 1; j <= numCols; j++) {
                    if (isBinaryEdge(zeroCrossArray, i, j)) {
                        writer.print("1 ");
                    } else {
                        writer.print("0 ");
                    }
                }
                writer.println();
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the binary edge file: " + e.getMessage());
        }

    }

    private boolean isBinaryEdge(int[][] array, int i, int j) {
        if (array[i][j] < 1) return false;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                int neighborRow = i + rowOffset;
                int neighborCol = j + colOffset;

                if (rowOffset == 0 && colOffset == 0) continue;

                if (array[neighborRow][neighborCol] == 0) return true;
            }
        }

        return false;
    }
}


