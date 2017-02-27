package LAB3.model;

import LAB3.control.EstimatorInterface;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Random;

/**
 * Created by Erik on 2017-02-22.
 */
public class Localizer implements EstimatorInterface {
    private int rows, cols, head, currX, currY, currHead;
    private static final int NORTH = 0,EAST=1, SOUTH = 2, WEST = 3;
    private double transitionProb[][];
    private double emissionProb[][];
    private double emissionStateMatrix[][];
    private double transitionStateMatrix[][];
    private double resultMatrix[][];

    public Localizer(int rows, int cols, int head){
        this.rows = rows;
        this.cols = cols;
        this.head = head;
        transitionProb = new double[rows][cols];
        emissionProb = new double[rows*cols*head][cols*rows*head];
        transitionStateMatrix = new double[rows*cols*head][rows*cols*head];
        emissionStateMatrix = new double[rows*cols*head][rows*cols*head];
        resultMatrix = new double[rows*cols*head][rows*cols*head];
        setRandomPosition();
        initFilterProbs(rows,cols);
        initEmissionMatrix();
        initTransitionMatrix();
        resultMatrix = transitionStateMatrix;

    }

    private void initTransitionMatrix() {
        for(int row = 0;row<cols*rows*head;row++ ){
            for(int col = 0; col<rows*cols*head;col++){
                   transitionStateMatrix[row][col]= getTProb(row/16,(row%16)/4,row%4,col/16,(col%16)/4,col%4);
            }
        }
    }

    private void initEmissionMatrix() {

                for (int row = 0; row < cols * rows * head; row++) {
                    for (int col = 0; col < rows * cols * head; col++) {
                        emissionStateMatrix[row][col] = getOrXY(currX, currY, row, col);
                    }
                }
            }



    private void printTransitionMat(){
        for (int x = 0; x < cols*rows*head; x++) {
            for (int y = 0; y < cols*rows*head; y++) {
                System.out.print(transitionStateMatrix[x][y]+" ");
            }
            System.out.println("");
        }
    }

    private void printEmissionMat() {
        for (int x = 0; x < cols*rows*head; x++) {
            for (int y = 0; y < cols*rows*head; y++) {
                System.out.print(emissionProb[x][y]+" ");
            }
            System.out.println("");
        }
    }

    private void initFilterProbs(int rows, int cols) {
        double probability = 1.0 / (rows * cols);
        for (int y = 0; y < rows*cols*head; y++) {
                emissionProb[y][y] = probability;
        }
    }

    private void filter(){
        RealMatrix transitionMat = MatrixUtils.createRealMatrix(transitionStateMatrix);
        RealMatrix transisionTranspose = transitionMat.transpose();
        resultMatrix = MatrixUtils.createRealMatrix(emissionProb).multiply(transisionTranspose.multiply(MatrixUtils.createRealMatrix(resultMatrix))).getData();
    }

    private void setRandomPosition(){
        Random ran = new Random();
        currX = ran.nextInt(cols*rows)%cols;
        currY = ran.nextInt(cols*rows)/rows;
        currHead = ran.nextInt(4);
        System.out.println(currX);
        System.out.println(currY);
    }

    private int[] findHighestProb(double result[][]){
        double highestProb = Double.MIN_VALUE;
        int indexX=0, indexY=0;
        for (int y = 0; y<rows; y++) {
            for (int x = 0;x<cols;x++) {
                if(result[x][y]>highestProb){
                    highestProb = result[x][y];
                    indexY = y;
                    indexX = x;
                }

            }
        }
        int[] index = {indexX,indexY};
        return index;
    }

    private void moveRobot(){
        while(true) {
            try {
                switch (currHead) {
                    case NORTH:
                        double temp = transitionProb[currX][currY-1];
                        currY -= 1;
                        break;
                    case EAST:
                        temp = transitionProb[currX+1][currY];
                        currX += 1;
                        break;
                    case SOUTH:
                        temp = transitionProb[currX][currY+1];
                        currY += 1;
                        break;
                    case WEST:
                        temp = transitionProb[currX-1][currY];
                        currX -= 1;
                        break;
                }
                break;
            }
            catch(ArrayIndexOutOfBoundsException e){
                Random ran = new Random();
                currHead = ran.nextInt(3);
            }
        }

    }

    private void updateEmissionMatrix() {
        for(int row = 0; row<rows*cols*head;row++){
            emissionProb[row][row] = getOrXY(currX,currY,row/16,(row%16)/4);
        }
    }


    public void printResultMatrix(){
        for (int y = 0; y<rows; y++) {
            System.out.println("");
            for (int x = 0;x<cols;x++) {
                System.out.print(String.format("%.3f", resultMatrix[x][y]) + "\t");
            }}}



    public void printTransMatrix(){
        for (int y = 0; y<rows; y++) {
            System.out.println("");
            for (int x = 0;x<cols;x++) {
                System.out.print(String.format("%.3f",transitionProb[x][y])+"\t");
            }
        }
    }

    @Override
    public int getNumRows() {
        return rows;
    }

    @Override
    public int getNumCols() {
        return cols;
    }

    @Override
    public int getNumHead() {
        return head;
    }

    @Override
    public void update() {
        moveRobot();
        updateEmissionMatrix();
        printResultMatrix();
        filter();
    }

    @Override
    public int[] getCurrentTruePosition() {
        int[] currPos = {currY,currX};
        return currPos;
    }

    @Override
    public int[] getCurrentReading() {
        int[] ret = {1,3};
        return ret;
    }

    @Override
    public double getCurrentProb(int x, int y) {
        return resultMatrix[x][y];
    }

    @Override
    public double getOrXY(int rX, int rY, int x, int y) {
        if(x==rX && y==rY){
            return 0.1;
        }
        else if(Math.abs(x-rX)<2 && Math.abs(y-rY)<2){
            return 0.05;
        }
        else if(Math.abs(x-rX)<3 && Math.abs(y-rY)<3){
            return 0.025;
        }
        return 0;
    }

    @Override
    public double getTProb(int x, int y, int h, int nX, int nY, int nH) {
        /*Om det inte är en vägg åt det hållet ge det en prob av 0.3
				om riktningen är samma + 0.4*/
        int dx = nY-y;
        int dy = nX-x;
        if(dx ==0 && dy==1){
			/*
			South
			 */
            if(h == SOUTH &&nH == SOUTH)
                return 0.7;
            else if (nH == SOUTH)
                return 0.3;
        }else if(dx == 0 && dy ==-1){
			/*
			North
			 */
            if(h == NORTH&&nH == NORTH)
                return 0.7;
            else if (nH == NORTH)
                return 0.3;
        }else if(dx==1 && dy==0){
			/*
			EAST
			 */
            if(h==EAST && nH == EAST)
                return 0.7;
            else if (nH == EAST)
                return 0.3;
        }else if(dx == -1 && dy == 0){
			/*
			WEST
			 */
            if(h==WEST && nH == WEST)
                return 0.7;
            else if (nH == WEST)
                return 0.3;
        }
        return 0.0;
    }
}
