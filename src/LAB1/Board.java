package LAB1;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by macAndy on 2017-01-30.
 */
public class Board {
    private int[][] field;
    private ArrayList<Integer> black;
    private ArrayList<Integer> white;
    private MiniMax ai;

    public Board(){
        ai = new MiniMax(0);
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

    public Board(int[][] field){
        black = new ArrayList<>();
        white = new ArrayList<>();
        this.field = field;
    }

    public int[][] getBoard(){
        return field;
    }

    public void printLegalMoves(int value){
        System.out.print("Legal moves(x,y): ");
        for(int x = 0;x<field.length;x++){
            for(int y= 0 ;y<field[x].length;y++){
                if(viableMove(x,y,value) != null && !viableMove(x,y,value).isEmpty()){
                    System.out.print("("+x+","+y+")");
                }
            }
        }
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
                    System.out.print("O");
                }else{
                    System.out.print("#");
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }


    public int whoWon(){
        int winner = 0;
        for(int x= 0;x<field.length;x++){
            for(int y=0;y<field[x].length;y++){
                winner += field[x][y];
            }
        }
        return winner;
/*
        if(black.size()> white.size()){
            return -1;
        }
        else if(white.size()>black.size()){
            return 1;
        }
        else{
            return 0;
        }
        */
        //return winner;
    }

    public boolean place(int x,int y, int value){
        ArrayList<Integer> flips = viableMove(x,y,value);
        if(flips != null) {
            if(value<0){
                black.add(y*8+x);
            }else{
                white.add(y*8+x);
            }
            for (int i : flips) {
                int dx = i % 8;
                int dy = i / 8;
                field[x][y] = value;
                field[dx][dy] *= (-1);
                if(value <0){
                    black.add(i);
                }
                else{
                    white.add(i);
                }
            }
        }
        return true;
    }

    public ArrayList<Node> legalMoves(int value){
        ArrayList<Node> moves = new ArrayList<>();
        for(int x = 0;x<field.length;x++){
            for(int y= 0 ;y<field[x].length;y++){
                if(viableMove(x,y,value) != null && !viableMove(x,y,value).isEmpty()){
                    moves.add(new Node(x,y));
                }
            }
        }
        return moves;
    }
    public Board copyOfBoard(){

        int[][] copyOfCurrent = new int[field.length][];
        for(int i = 0; i < field.length; i++)
            copyOfCurrent[i] = field[i].clone();
        return new Board(copyOfCurrent);
    }

    private ArrayList<Integer> viableMove(int x, int y, int value){
        if(field[x][y] != 0) {
        }else {
            ArrayList<Integer> piecesToFlip = new ArrayList();

            try {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if(x+i>-1&&y+j>-1&&x+i<8&&y+j<8) {
                            ArrayList<Integer> tmpPiecesToFlip = new ArrayList();
                            if (field[x + i][y + j] == value * (-1)) {
                                /*
                                found a piece of different color.
                                 */
                                for (int delta = 0; delta < field.length; delta++) {
                                    if (x + i * delta > -1 && x + i * delta < 8 && y + j * delta > -1 && y + j * delta < 8) {
                                        if (field[x + i * delta][y + j * delta] == value * (-1)) {
                                            /*
                                            Found one more piece of diffrerent color.
                                             */
                                            tmpPiecesToFlip.add((y + j * delta) * 8 + (x + i * delta));
                                        } else if (field[x + i * delta][y + j * delta] == value) {
                                            /*
                                            Found a piece of same color.
                                             */
                                            piecesToFlip.addAll(tmpPiecesToFlip);
                                            tmpPiecesToFlip = new ArrayList<>();
                                        } else {
                                            /*
                                            Bad path.
                                             */
                                            tmpPiecesToFlip = new ArrayList<>();
                                        }
                                    } else {
                                        /*
                                        Out of bounds
                                         */
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

}
