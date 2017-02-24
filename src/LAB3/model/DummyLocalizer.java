package LAB3.model;

import LAB3.control.EstimatorInterface;

import java.util.Random;

public class DummyLocalizer implements EstimatorInterface {
		
	private int rows, cols, head, currX, currY;
	private final static int NORTH = 0, EAST =1, SOUTH =2, WEST =3;
	private double transitionProb[][];
	private double emissionProb[][];

	public DummyLocalizer( int rows, int cols, int head) {
		this.rows = rows;
		this.cols = cols;
		this.head = head;
		this.transitionProb = new double[rows][cols];
		this.emissionProb = new double[rows][cols];
		this.emissionProb = new double[rows][cols];



		
	}

	public int getNumRows() {
		return rows;
	}
	
	public int getNumCols() {
		return cols;
	}
	
	public int getNumHead() {
		return head;
	}
	
	public double getTProb( int x, int y, int h, int nX, int nY, int nH) {
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

	public double getOrXY( int rX, int rY, int x, int y) {
		return 0.1;
	}


	public int[] getCurrentTruePosition() {
		
		int[] ret = new int[2];
		ret[0] = 0;
		ret[1] = 0;
		return ret;
	}

	public int[] getCurrentReading() {
		int[] ret = {1,3};
		return ret;
	}


	public double getCurrentProb( int x, int y) {
		double ret = 0.0;
		return ret;
	}
	
	public void update() {
		System.out.println("Nothing is happening, no model to go for...");
	}
	
	
}