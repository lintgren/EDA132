package LAB3.model;

import LAB3.control.EstimatorInterface;

import java.util.Random;

/**
 * Created by Erik on 2017-02-22.
 */
public class Localizer implements EstimatorInterface {

    private int rows, cols, head, currX, currY, currHead;
    private static final int NORTH = 0,EAST=1, SOUTH = 2, WEST = 3;
    private double transitionProb[][];
    private double emissionProb[][];

    public Localizer(int rows, int cols, int head){
        this.rows = rows;
        this.cols = cols;
        this.head = head;
        transitionProb = new double[rows][cols];
        emissionProb = new double[rows][cols];
        setRandomPosition();
        initFilterProbs(rows,cols);
        update();

    }

    private void initFilterProbs(int rows, int cols) {
        double probability = 1.0 / (rows * cols);
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                emissionProb[x][y] = probability;
            }
        }
    }

    private void setRandomPosition(){
        Random ran = new Random();
        currX = ran.nextInt(cols*rows)%cols;
        currY = ran.nextInt(cols*rows)/rows;
        currHead = ran.nextInt(4);
        System.out.println(currX);
        System.out.println(currY);
    }

    private int[] findHighestProb(){
        double highestProb = Double.MIN_VALUE;
        int indexX=0, indexY=0;
        for (int y = 0; y<rows; y++) {
            for (int x = 0;x<cols;x++) {
                if(emissionProb[x][y]>highestProb){
                    highestProb = emissionProb[x][y];
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
                        currY -= 1;
                        double temp = transitionProb[currX][currY];
                        break;
                    case EAST:
                        currX += 1;
                        temp = transitionProb[currX][currY];
                        break;
                    case SOUTH:
                        currY += 1;
                        temp = transitionProb[currX][currY];
                        break;
                    case WEST:
                        currX -= 1;
                        temp = transitionProb[currX][currY];
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

    private void updateTransMatrix(){


        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if((currX - 2 + x)>=0 && (currX - 2 + x)<cols &&(currY - 2 + y)>=0&&(currY - 2 + y)<rows) {
                    transitionProb[currX - 2 + x][currY - 2 + y] = 0.025;
                }
            }
        }
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if((currX - 1 + x)>=0 && (currX - 1 + x)<cols &&(currY - 1 + y)>=0&&(currY - 1 + y)<rows) {
                        transitionProb[currX - 1 + x][currY - 1 + y] = 0.05;
                    }
                }
            }
        transitionProb[currX][currY] = 0.1;



    }
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
    updateTransMatrix();
    }

    @Override
    public int[] getCurrentTruePosition() {
        int[] currPos = {currX,currY};
        return currPos;
    }

    @Override
    public int[] getCurrentReading() {

    }

    @Override
    public double getCurrentProb(int x, int y) {
        System.out.println(emissionProb[x][y]);
        return emissionProb[x][y];
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
