package LAB1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macAndy on 2017-02-02.
 */
public class MiniMax {
    ArrayList<Integer> white;
    ArrayList<Integer> black;

    public MiniMax(int time){
        white = new ArrayList<>();
        black = new ArrayList<>();
    }

    public MiniMax(int time,Board board){
        white = new ArrayList<>();
        black = new ArrayList<>();
    }
    public int calculateBestMove(int[][] currentFieldState,int color,int depth){
        int[][] copyOfCurrent = new int[currentFieldState.length][];
        for(int i = 0; i < currentFieldState.length; i++)
            copyOfCurrent[i] = currentFieldState[i].clone();
        Node root = new Node();
        Node bestNode =null;
        Node badNode =null;
        for(Node move:legalMoves(color,copyOfCurrent)){
            place(move.x,move.y,color,copyOfCurrent);
            move.parentNode = root;
            root.addChild(move);
            int winningColor = simulatedPlays(copyOfCurrent,color*-1,move,depth);
            if(winningColor == color){
                bestNode = move;
                break;
            }else if(winningColor == 0){
                bestNode = move;
            }else if(badNode != null){
                badNode = move;
            }
        }
        if(bestNode != null) {
            return bestNode.y * 8 + bestNode.x;
        }else if(badNode != null){
            //System.out.println("WTF är fel");
            return badNode.y*8+badNode.x;
            //return 0;
        }else{
            return -1;
        }
    }

    public int calculateBestMove2(int[][] currentFieldState,int color,int depth){

        Node bestNode =null;
        int score = 0;
        int[][] copyOfCurrent = new int[currentFieldState.length][];
        for(int i = 0; i < currentFieldState.length; i++)
            copyOfCurrent[i] = currentFieldState[i].clone();
        for(Node move:legalMoves(color,copyOfCurrent)){
                Node root = new Node();
                Node badNode =null;
                place(move.x,move.y,color,copyOfCurrent);
                move.parentNode = root;
                root.addChild(move);
            int winningColor;
                winningColor = simulatedPlays2(copyOfCurrent, color * -1, move, depth, false);
                if(winningColor>score)
                    bestNode = move;
        }
        return bestNode.y*8+bestNode.x;
    }

    public int simulatedPlays2(int[][] currentFieldState,int value,Node currentMove,int depth,boolean maxOrMin){
        depth--;
        ArrayList<Node> moves = legalMoves(value,currentFieldState);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        if(!moves.isEmpty() && depth>0) {
            for(Node move:moves){
                int[][] copyOfCurrent = new int[currentFieldState.length][];
                for(int i = 0; i < currentFieldState.length; i++)
                    copyOfCurrent[i] = currentFieldState[i].clone();
                place(move.x, move.y, value, copyOfCurrent);
                move.parentNode = currentMove;
                currentMove.addChild(move);
                int score = simulatedPlays2(copyOfCurrent,value*-1,move,depth,!maxOrMin);
                if(maxOrMin){
                    if(value>0 && score>max) {
                        max = score;
                    }else  if(value<0 && min< score){
                        min = score;
                    }
                }else
                    if(value>0&& score<min)
                        min = score;
                    else if (value <0 && score>max)
                            max = score;
            }
            if((maxOrMin && value>0)||(value<0&&!maxOrMin)){
                return max;
            }
            return min;
        }
        return whoWon(currentFieldState);
    }

    private int simulatedPlays(int[][] currentFieldState, int value, Node currentMove,int depth){
        depth--;
        if(legalMoves(value,currentFieldState).size()>0 && depth>0) {
            boolean white = false;
            boolean black = false;
            int bestMiniValue = 1000;
            for (Node move : legalMoves(value, currentFieldState)) {
                int[][] copyOfCurrent = new int[currentFieldState.length][];
                for(int i = 0; i < currentFieldState.length; i++)
                    copyOfCurrent[i] = currentFieldState[i].clone();
                place(move.x, move.y, value, copyOfCurrent);
                move.parentNode = currentMove;
                currentMove.addChild(move);
                if (simulatedPlays(copyOfCurrent, value * -1, move,depth) != value){
                    white = true;
                }else{
                    black = true;
                }
            }
            if((white && black)||(!white && !black)){
                return 0;
            }else if (white && !black){
                return 1;
            } else {
                return -1;
            }
        }else{
            if(whoWon(currentFieldState)>0){
                return 1;
            }else if(whoWon(currentFieldState)<0){
                return -1;
            }
            else{
                return 0;
            }
        }
    }

    private int whoWon(int[][] field){
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

    private boolean place(int x,int y, int value,int[][] field){
        ArrayList<Integer> flips = viableMove(x,y,value,field);
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

    private ArrayList<Node> legalMoves(int value, int[][] field){
        ArrayList<Node> moves = new ArrayList<>();
        for(int x = 0;x<field.length;x++){
            for(int y= 0 ;y<field[x].length;y++){
                if(viableMove(x,y,value,field) != null && !viableMove(x,y,value,field).isEmpty()){
                    moves.add(new Node(x,y));
                }
            }
        }
        return moves;
    }

    private ArrayList<Integer> viableMove(int x, int y, int value,int field[][]){
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

    private class Node{
        private List<Node> children = new ArrayList<Node>();
        private Node parentNode = null;
        public int x;
        public int y;
        public Node(){

        }
        public Node(int x,int y){
            this.x = x;
            this.y = y;
        }
        public Node(int x, int y, Node parentNode){
            this.x = x;
            this.y = y;
            this.parentNode = parentNode;
        }
        public void addChild(Node child){
            children.add(child);
        }
        public boolean isRoot(){
            return (this.parentNode == null);
        }
        public boolean isLeaf(){
            if(children.size() == 0)
                return true;
            return false;
        }
    }
}
