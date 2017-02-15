package LAB1;
import java.util.Scanner;
import java.util.stream.Collector;

public class Main {


    public static void main(String[] args) {
        Board board = new Board();
        Scanner scan = new Scanner(System.in);
        System.out.println("type in how long the ai should wait(in ms):");
        long sec = scan.nextLong();
        MiniMax ai = new MiniMax(sec,board);
        int x;
        int y;
        boolean game = true;
        boolean successfulMove;
        while (game) {
            board.print();
            board.legalMoves(-1);
            System.out.println();
            board.printLegalMoves(-1);
            System.out.println("X : type in your move(x and y):");
            x = scan.nextInt();
            y = scan.next().charAt(0)-97;
            int b = (y);
            successfulMove= board.place(x-1,y,-1);
            if (successfulMove) {
                if(board.isGameFinished(-1)){
                    System.out.println("Winner!! X is negative 0 is positive Sum of all pieces:");
                    System.out.println(board.whoWon());
                }
                ai.miniMaxAIMove(1);
                if(board.isGameFinished(1)){
                    System.out.println("Winner!! X is negative O is positive Sum of all pieces:");
                    System.out.println(board.whoWon());
                }
                System.out.println();
            }

        }
    }
}