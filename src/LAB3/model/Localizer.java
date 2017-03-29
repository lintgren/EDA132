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
    private Integer[] currReading = {-1,-1};
    private double transitionProb[][];
    private double emissionProb[][];
    private double emissionStateMatrix[][];
    private double transitionStateMatrix[][];
    private double resultMatrix[][];
    private double compareMatrix[][];
    private double totalSum;
    private double iterationCounter;
    private DistributedRandomNumberGenerator drng;


    public Localizer(int rows, int cols, int head){
        drng = new DistributedRandomNumberGenerator();
        this.rows = rows;
        this.cols = cols;
        this.head = head;
        transitionProb = new double[rows][cols];
        emissionProb = new double[rows*cols*head][cols*rows*head];
        transitionStateMatrix = new double[rows*cols*head][rows*cols*head];
        emissionStateMatrix = new double[rows*cols*head][rows*cols*head];
        resultMatrix = new double[rows*cols*head][rows*cols*head];
        compareMatrix = new double[rows][cols];
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
        if(norm()!=0)
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
        int[] index = {indexY,indexX};
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
        /*
            Prob: 0.025, 0.05, 0.1, Nothing
         */
        drng.clear();
        emissionProb = new double[rows*cols*head][rows*cols*head];
        /*System.out.println("currX: ");
        System.out.print(currX);
        System.out.println("currY: ");
        System.out.print(currY);
        */

        emissionProb= new double[rows*cols*head][rows*cols*head];
        for(int row = 0;row<rows;row++) {
            for (int column = 0; column < cols; column++) {
                double prob = getOrXY(currX,currY,row,column);
                if(prob==0.025||prob==0.05||prob==0.1){
                    drng.addNumber(column*rows+row,prob);
                }else {
                    //System.out.println(getOrXY(-1,-1,currX,currY));
                    drng.addNumber(-1,getOrXY(-1,-1,currX,currY));
                }
            }
        }
        int coord = drng.getDistributedRandomNumber();
        /*
        Coord is current sensorReading.
         */
        if(coord != -1){
            for(int row = 0; row<rows*cols*head;row++) {
                double prob = getOrXY(coord/rows,coord%cols, row / 16, (row % 16) / 4);
                emissionProb[row][row] = prob;//getOrXY(currX, currY, row / 16, (row % 16) / 4);
                // else
                //   emissionProb[row][row] = getOrXY(-1, -1,currX,currY);
            }
        }
        else{
            /*
            Lägger in sannolikheten för nothing.s
             */
            for(int row = 0; row<rows*cols*head;row++) {
                double prob = getOrXY(-1,-1,row / 16, (row % 16) / 4);//coord/rows,coord%cols);
                emissionProb[row][row] = prob;//getOrXY(currX, currY, row / 16, (row % 16) / 4);
                // else
                //   emissionProb[row][row] = getOrXY(-1, -1,currX,currY);
            }
            int k= 0;

        }
        currReading[0] = coord/rows;
        currReading[1] = coord%cols;
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
        if(sum>0)
        return 1/sum;
        else
            return 0;
    }

    @Override
    public void update() {
        moveRobot();
        updateEmissionMatrix();
        filter();
        for (int row = 0; row<rows;row++){
            for (int col = 0; col<cols;col++){
                compareMatrix[row][col] = getCurrentProb(row,col);
            }
        }
        int[] pos = findHighestProb(compareMatrix);
        int dx = Math.abs(pos[0]-currX);
        int dy = Math.abs(pos[1]-currY);
        totalSum += (double)(dx+dy);
        iterationCounter++;


        System.out.println("Current manhattan distance between robot and estimation is:" + dx +" " + dy);
        System.out.println("Estimated (x,y) coordinates: " +pos[0] + " " + pos[1]);
        System.out.println("True coordinates (x,y): " +currX + " " + currY);
        System.out.println("avg manhattan distane: " + (totalSum/iterationCounter));
        System.out.println("Current iteration: " + iterationCounter);
    }

    @Override
    public int[] getCurrentTruePosition() {
        int[] currPos = {currY,currX};
        return currPos;
    }

    @Override
    public int[] getCurrentReading() {
        int[] i =new int[2];
        if(i[0]==-1||i[1]==-1){
            return null;
        }
        i[0] = currReading[0];
        i[1] = currReading[1];
        return i;
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
        else if(Math.abs(x-rX)<2 && Math.abs(y-rY)<2 && rY!=-1 && rX!=-1){
            return 0.05;
        }
        else if(Math.abs(x-rX)<3 && Math.abs(y-rY)<3 && rY!=-1 && rX!=-1){
            return 0.025;
        }else if(rX == -1 || rY ==-1){
            if((x==0&&y==0) || (x==0&&y==3) || (x==3&&y==0) || (x==3&&y==3))
                return 0.107759;
            else if ((x<3&&x>0) && (y<3&&y>0))
                return 0.0560394;
            else{
                return 0.0431034;
            }
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
