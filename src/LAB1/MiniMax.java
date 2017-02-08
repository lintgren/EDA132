package LAB1;

import java.util.ArrayList;

/**
 * Created by macAndy on 2017-02-02.
 */
public class MiniMax {
    ArrayList<Integer> white;
    ArrayList<Integer> black;
    Board board ;
    private long time;
    private long startTime;

    //TODO: pruning, skicka med bästa värdet och jämföra med det??
    public MiniMax(long time){
        white = new ArrayList<>();
        black = new ArrayList<>();
        this.time = time;
    }
    public boolean miniMaxAIMove(int value){
        //int move = ai.calculateBestMove(field,value,6);
        int move = calculateBestMove2(value,2);
        if(move<0){
            return false;
        }
        int dx = move %8;
        int dy = move/8;
        board.place(dx,dy,value);

        return true;
    }
    public MiniMax(long time,Board board){
        white = new ArrayList<>();
        black = new ArrayList<>();
        this.board = board;
        this.time = time;
    }
    public int calculateBestMove2(int color,int depth){
        startTime = System.currentTimeMillis();
        Node bestNode =null;
        Node root = new Node();
        int score = Integer.MIN_VALUE;
        for(Node move:board.legalMoves(color)){
            Board copyOfCurrent = board.copyOfBoard();
            //place(move.x,move.y,color,copyOfCurrent);
            copyOfCurrent.place(move.x,move.y,color);
            move.setParentNode(root);
            //move.parentNode = root;
            root.addChild(move);
            int winningColor;
            winningColor = simulatedPlays2(copyOfCurrent, color * -1, move, depth, false,score);
            if(winningColor>score)
                    bestNode = move;
        }
        printFukkinbroaaah(root);
        return bestNode.y*8+bestNode.x;
    }

    public int simulatedPlays2(Board board,int value,Node currentMove,int depth,boolean maxOrMin,int currentBestScore){

        depth--;
        ArrayList<Node> moves = board.legalMoves(value);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        if(!moves.isEmpty() && depth>0) {
            for(Node move:moves){
                if((startTime+time-System.currentTimeMillis())>0){
                    if(maxOrMin)
                        return max;
                    else
                        return min;
                }
                Board copyOfCurrent = board.copyOfBoard();
                copyOfCurrent.place(move.x,move.y,value);
                move.setParentNode(currentMove);
                currentMove.addChild(move);
                int score = simulatedPlays2(copyOfCurrent,value*-1,move,depth,!maxOrMin,max);
                if(maxOrMin){
                    /*
                    Maximizzeee
                     */
                    if(value>0 && score>max) {
                        max = score;
                    }else  if(value<0 && min< score){
                        min = score;
                    }
                }else {
                    /*
                    Minimiiiizeeeeeaah
                     */
                    //int score = simulatedPlays2(copyOfCurrent, value * -1, move, depth, !maxOrMin, min);
                    if (value > 0 && score < min)
                        min = score;
                    else if (value < 0 && score > max)
                        max = score;
                }
              //  if(!maxOrMin && min > currentBestScore){
                    /*
                    vi ska minimera och därför om värdet blir större bör vi sluta räkna på nya moves
                     */
              //      return min;
              //  }
            }

            if((maxOrMin && value>0)||(value<0&&!maxOrMin)){
                return max;
            }
            return min;
        }
        return board.whoWon();
    }
    private String printFukkinbroaaah(Node currentNode){
        String number="";
        if(currentNode.getChildren().isEmpty()){
            number = currentNode.x+"."+currentNode.y+"\t";
            System.out.print(currentNode.x);
            System.out.print('.');
            System.out.print(currentNode.y);
            System.out.print('\t');
            return number;
        }
        for(Node kiddo:currentNode.getChildren()){
            number += printFukkinbroaaah(kiddo);
        }
        return  number+" \n "+currentNode.x+"."+currentNode.y;
    }

}
