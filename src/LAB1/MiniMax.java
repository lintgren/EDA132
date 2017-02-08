package LAB1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macAndy on 2017-02-02.
 */
public class MiniMax {
    ArrayList<Integer> white;
    ArrayList<Integer> black;
    Board board ;

    //TODO: pruning, skicka med bästa värdet och jämföra med det??
    public MiniMax(int time){
        white = new ArrayList<>();
        black = new ArrayList<>();
    }
    public boolean miniMaxAIMove(int value){
        //int move = ai.calculateBestMove(field,value,6);
        int move = calculateBestMove2(value,6);
        if(move<0){
            return false;
        }
        int dx = move %8;
        int dy = move/8;
        board.place(dx,dy,value);
        return true;
    }
    public MiniMax(int time,Board board){
        white = new ArrayList<>();
        black = new ArrayList<>();
        this.board = board;
    }
    public int calculateBestMove2(int color,int depth){

        Node bestNode =null;
        int score = 0;
        for(Node move:board.legalMoves(color)){
            Board copyOfCurrent = board.copyOfBoard();
            Node root = new Node();
            //place(move.x,move.y,color,copyOfCurrent);
            copyOfCurrent.place(move.x,move.y,color);
            move.setParentNode(root);
            //move.parentNode = root;
            root.addChild(move);
            int winningColor;
            winningColor = simulatedPlays2(copyOfCurrent, color * -1, move, depth, false);
            if(winningColor>score)
                    bestNode = move;
        }
        return bestNode.y*8+bestNode.x;
    }

    public int simulatedPlays2(Board board,int value,Node currentMove,int depth,boolean maxOrMin){
        depth--;
        ArrayList<Node> moves = board.legalMoves(value);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        if(!moves.isEmpty() && depth>0) {
            for(Node move:moves){
                Board copyOfCurrent = board.copyOfBoard();
                copyOfCurrent.place(move.x,move.y,value);
                move.setParentNode(currentMove);
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
        return board.whoWon();
    }


}
