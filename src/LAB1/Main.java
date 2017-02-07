package LAB1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Board board = new Board();
        Scanner scan = new Scanner(System.in);
        int x;
        int y;
        boolean game = true;
        while(game){
            board.print();
            //board.miniMaxAIMove(-1);
            board.legalMoves(-1);
            System.out.println();
            System.out.println("X : type in your move(x and y):");
            x = scan.nextInt();
            y = scan.nextInt();
            if(!board.place(x,y,-1)){
                System.out.println("we have a winner!");
                break;
            }
            System.out.println();

            board.print();
            try {
                Thread.sleep((long) 0.4);
            }catch (InterruptedException e){

            }
            //System.out.println();
            //System.out.println("O : type in your move(x and y):");
            //x = scan.nextInt();
            //y = scan.nextInt();
            //board.place(x,y,1);
            if(!board.miniMaxAIMove(1)){
                System.out.println("we have a winner!");
                break;
            }
            System.out.println();
        }


    }
}
