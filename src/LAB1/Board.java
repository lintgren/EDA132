package LAB1;
import java.util.ArrayList;

/**
 * Created by macAndy on 2017-01-30.
 */
public class Board {
    private int[][] field;
    private ArrayList<Integer> black;
    private ArrayList<Integer> white;

    public Board(){
        for(int i =0;i<8;i++){
            for(int j=0;j<8;j++){
                System.out.print((i*8+j));
                System.out.print(" ");
            }
            System.out.println();
        }
        field = new int[8][8];
        black = new ArrayList<>();
        white = new ArrayList<>();
        field[3][3] = 1;
        white.add(3*8+3);
        field[3][4] = -1;
        black.add(3*8+4);
        field[4][3] = -1;
        black.add(4*8+3);
        field[4][4] = 1;
        white.add(4*8+4);
    }

    public boolean place(int x,int y, int value){
        ArrayList<Integer> flips = viableMove(x,y,value);
        if(flips != null) {
            for (int i : flips) {
                int dx = i % 8;
                int dy = i / 8;
                System.out.println("flip x:" + dx);
                System.out.println("flip y:" + dy);
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                field[x][y] = value;
                field[dx][dy] *= (-1);
            }
        }
        return true;
    }
    public ArrayList<Integer> viableMove(int x, int y, int value){
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        if(field[x][y] != 0) {
            System.out.println("There is a piece there");
        }else {
            ArrayList<Integer> piecesToFlip = new ArrayList();

            try {
                ArrayList<Integer> tmpPiecesToFlip = new ArrayList();
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if(x+i>0&&y+j>0&&x+i<8&&y+j<8) {
                            if (field[x + i][y + j] == value * (-1)) {
                                for (int delta = 0; delta < field.length; delta++) {
                                    if (x + i * delta > 0 && x + i * delta < 8 && y + j * delta > 0 && y + j * delta < 8) {
                                        if (field[x + i * delta][y + j * delta] == value * (-1)) {
                                            tmpPiecesToFlip.add((y + j * delta) * 8 + (x + i * delta));
                                        } else if (field[x + i * delta][y + j * delta] == value) {
                                            piecesToFlip.addAll(tmpPiecesToFlip);
                                        } else {
                                            tmpPiecesToFlip = new ArrayList<>();
                                        }
                                    } else {
                                        tmpPiecesToFlip = new ArrayList<>();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("out of bounds");
            }
            return piecesToFlip;
        }
        return null;
    }

    public void print(){
        System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7");
        for(int x= 0; x < field.length;x++){
            System.out.print(x+"\t");
            for(int y = 0 ; y<field[x].length;y++){
                if(field[y][x]<0) {
                    System.out.print("X");
                }
                else if(field[y][x]>0){
                    System.out.print("o");
                }else{
                    System.out.print("0");
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }


}
