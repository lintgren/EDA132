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
    public boolean miniMaxAIMove(int value){
        //int move = ai.calculateBestMove(field,value,6);
        int move = calculateBestMove(value,7);
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
        this.time = time;
        this.board = board;
    }
    public int calculateBestMove(int color, int depth){
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
            winningColor = simulatedPlays(copyOfCurrent, color * -1, move, depth, false,score);
            if(winningColor>score) {
                bestNode = move;
                score = winningColor;
                root.score = score;
            }
        }
        return bestNode.y*8+bestNode.x;

    }

    public int simulatedPlays(Board board, int value, Node currentMove, int depth, boolean maxOrMin, int currentBestScore){

        depth--;
        ArrayList<Node> moves = board.legalMoves(value);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        if(!moves.isEmpty() && depth>0) {
            for(Node move:moves){
                if((startTime+time-System.currentTimeMillis())<=0){
                    if(value>0)
                        return max;
                    else
                        return min;
                }
                Board copyOfCurrent = board.copyOfBoard();
                copyOfCurrent.place(move.x,move.y,value);
                move.setParentNode(currentMove);
                currentMove.addChild(move);
                if(value==1){
                    int score = simulatedPlays(copyOfCurrent,value*-1,move,depth,!maxOrMin,max);
                    if(score>max){
                        max= score;
                    }
                }else{
                    int score = simulatedPlays(copyOfCurrent,value*-1,move,depth,!maxOrMin,min);
                    if(score<min){
                        min = score;
                    }
                }
                if(value<0 && min<currentBestScore){
                    /*
                    Pruning.
                     */
                    currentMove.score = min;
                    return min;
                }



            }
            if(value>0){
                currentMove.score = max;
                return max;
            }
            currentMove.score = min;
            return min;
        }
        currentMove.score = board.whoWon();
        return board.whoWon();
    }

}
