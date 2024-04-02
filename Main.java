import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


//    Step 1: loadImage (inFile, MFAry)
//    Step 2: imgReformatPrettyPrint (MFAry, outFile1)
//    Step 3: zeroCrossingEdgeDetector (MFAry, edgeAry, mask, zeroCrossAry, zeroCrossFile, binEdgeFile, deBugFile)
//    Step 4: close all files

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
        edge.zeroReformatPrettyPrint(outFile);
        edge.zeroCrossingEdgeDetector();

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
        }
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
                        this.MFArray[i][j] = scanner.nextInt();
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    public void zeroCrossingEdgeDetector(){}
}


