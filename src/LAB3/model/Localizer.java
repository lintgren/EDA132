package LAB3.model;

import LAB3.control.EstimatorInterface;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javax.rmi.CORBA.Util;
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
//        initFilterProbs(rows,cols);
        //initEmissionMatrix();
        initTransitionMatrix();
        initResultMatrix(rows,cols);
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
        for (int row = 0; row < cols*rows*head; row++) {
            for (int col = 0; col < cols*rows*head; col++) {
                System.out.print(transitionStateMatrix[row][col]+" ");
            }
            System.out.println("");
        }
    }

    private void printResultMat(){
        for (int row = 0; row < cols*rows*head; row++) {
            for (int col = 0; col < cols*rows*head; col++) {
                System.out.print(resultMatrix[row][col]+" ");
            }
            System.out.println("");
        }
    }

    private void printEmissionMat() {
        for (int row = 0; row < cols*rows*head; row++) {
            for (int col = 0; col < cols*rows*head; col++) {
                System.out.print(emissionProb[row][col]+" ");
            }
            System.out.println("");
        }
    }

    private void initResultMatrix(int rows, int cols) {
        double probability = 1.0 / (rows * cols * head);
        for (int y = 0; y < rows*cols*head; y++) {
            resultMatrix[y][y] = probability;
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
        RealMatrix obsTrans = MatrixUtils.createRealMatrix(emissionProb).multiply(transisionTranspose);
        resultMatrix = obsTrans.multiply(MatrixUtils.createRealMatrix(resultMatrix)).getData();
        resultMatrix = MatrixUtils.createRealMatrix(resultMatrix).scalarMultiply(norm()).getData();
        //resultMatrix = (MatrixUtils.createRealMatrix(emissionProb).multiply(transisionTranspose.multiply(MatrixUtils.createRealMatrix(resultMatrix)))).getData();
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
        for (int row = 0; row<rows; row++) {
            for (int col = 0;col<cols;col++) {
                if(result[row][col]>highestProb){
                    highestProb = result[row][col];
                    indexY = col;
                    indexX = row;
                }

            }
        }
        int[] index = {indexX,indexY};
        return index;
    }

    private void moveRobot(){
        Random ran = new Random();
        double ranValue = ran.nextDouble();
        if(ranValue<=0.7) {
            while (true) {
                try {
                    switch (currHead) {
                        case NORTH:
                            double temp = transitionProb[currX][currY - 1];
                            currY -= 1;
                            break;
                        case EAST:
                            temp = transitionProb[currX + 1][currY];
                            currX += 1;
                            break;
                        case SOUTH:
                            temp = transitionProb[currX][currY + 1];
                            currY += 1;
                            break;
                        case WEST:
                            temp = transitionProb[currX - 1][currY];
                            currX -= 1;
                            break;
                    }
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    currHead = ran.nextInt(4);
                }
            }
        }
        else{
            while (true) {
                try {
                    int oldHead = currHead;
                    ranValue = ran.nextDouble();
                    currHead = ran.nextInt(4);
                    while(currHead==oldHead){
                        currHead=ran.nextInt(4);
                    }
                    switch (currHead) {
                        case NORTH:
                            double temp = transitionProb[currX][currY - 1];
                            currY -= 1;
                            break;
                        case EAST:
                            temp = transitionProb[currX + 1][currY];
                            currX += 1;
                            break;
                        case SOUTH:
                            temp = transitionProb[currX][currY + 1];
                            currY += 1;
                            break;
                        case WEST:
                            temp = transitionProb[currX - 1][currY];
                            currX -= 1;
                            break;
                    }
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    currHead = ran.nextInt(4);
                }
            }
        }

    }

    private void updateEmissionMatrix() {
        for(int row = 0; row<rows*cols*head;row++) {
            emissionProb[row][row] = getOrXY(currX, currY, row / 16, (row % 16) / 4);
        }
        /*
        for (int row = 0; row < cols * rows * head; row++) {
            for (int col = 0; col < rows * cols * head; col++) {
                emissionStateMatrix[row][col] = getOrXY(currX, currY, row, col);
            }
        }
        */
    }

    public void printResultMatrix(){
        for (int row = 0; row<rows*cols*head; row++) {
            System.out.println("");
            for (int col = 0;col<cols*rows*head;col++) {
                System.out.print(String.format("%.3f", resultMatrix[row][col]) + "\t");
            }}}

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

    private double norm(){
        double sum = 0.0;
        for(int x = 0;x<rows;x++){
            for(int y = 0; y<cols;y++){
                sum += getCurrentProb(x,y);
            }
        }
        return 1/sum;
    }

    @Override
    public void update() {
        moveRobot();
        updateEmissionMatrix();
        filter();
    }

    @Override
    public int[] getCurrentTruePosition() {
        int[] currPos = {currY,currX};
        return currPos;
    }

    @Override
    public int[] getCurrentReading() {
        int[] ret = getCurrentTruePosition();
        return ret;
    }

    @Override
    public double getCurrentProb(int x, int y) {
        int rowIndex = x*16+y*4;
        double sum = 0.0;
        for (int row = rowIndex; row<rowIndex+4; row++){
            for(int col = 0; col<rows*cols*head; col++){
                sum += resultMatrix[row][col];
            }
        }
        return sum;
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
        int dx = nY-y;
        int dy = nX-x;
        double ways = 4;
        double prob =1.0;
        if(x==0 || x==rows-1)
            ways -=1;
        if(y==0||y == cols-1)
            ways -=1;
        if(!((h==NORTH && x-1<0)||(h==EAST && y==rows-1)||(h==SOUTH&& x==cols-1)||(h==WEST && y-1<0))){
            prob-=0.7;
            ways-=1;
        }
        if(dx ==0 && dy==1){
			/*
			South
			 */
            if(h == SOUTH &&nH == SOUTH)
                return 0.7;
            else if (nH == SOUTH) {
                return prob/ways;
            }
        }else if(dx == 0 && dy ==-1){
			/*
			North
			 */
            if(h == NORTH&&nH == NORTH)
                return 0.7;
            else if (nH == NORTH) {
                return prob/ways;
            }
        }else if(dx==1 && dy==0){
			/*
			EAST
			 */
            if(h==EAST && nH == EAST)
                return 0.7;
            else if (nH == EAST){
                return prob/ways;
            }
        }else if(dx == -1 && dy == 0){
			/*
			WEST
			 */
            if(h==WEST && nH == WEST)
                return 0.7;
            else if (nH == WEST){
                return prob/ways;
            }
        }
        return 0.0;
    }
}
