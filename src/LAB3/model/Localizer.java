package LAB3.model;

import LAB3.control.EstimatorInterface;

import java.util.Random;

/**
 * Created by Erik on 2017-02-22.
 */
public class Localizer implements EstimatorInterface {

    private int rows, cols, head, currX, currY;
    private double transitionProb[][];
    private double emissionProb[][];

    public Localizer(int rows, int cols, int head){
        this.rows = rows;
        this.cols = cols;
        this.head = head;
        transitionProb = new double[rows][cols];
        emissionProb = new double[rows][cols];
        setRandomPosition();
        update();

    }


    public void setRandomPosition(){
        Random ran = new Random(cols*rows);
        currX = ran.nextInt()%cols;
        currY = ran.nextInt()/rows;
    }

    private void updateTransMatrix(){


        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if((currX-x)>0 && (currX+x)<cols &&(currY-y)>0&&(currY-y)<rows) {
                    transitionProb[currX - 2 + x][currY - 2 + y] = 0.025;
                }
            }
        }
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if((currX-x)>0 && (currX+x)<cols &&(currY-y)>0&&(currY-y)<rows) {
                        transitionProb[currX - 1 + x][currY - 1 + y] = 0.05;
                    }
                }
            }
        transitionProb[currX][currY] = 0.1;


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

    }

    @Override
    public int[] getCurrentTruePosition() {
        int[] currPos = {currX,currY};
        return currPos;
    }

    @Override
    public int[] getCurrentReading() {
        return new int[0];
    }

    @Override
    public double getCurrentProb(int x, int y) {
        return 0;
    }

    @Override
    public double getOrXY(int rX, int rY, int x, int y) {
        return 0;
    }

    @Override
    public double getTProb(int x, int y, int h, int nX, int nY, int nH) {
        return 0;
    }
}
