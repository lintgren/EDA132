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
            board.showLegalMoves(-1);
            System.out.println();
            System.out.println("X : type in your move(x and y):");
            x = scan.nextInt();
            y = scan.nextInt();
            board.place(x,y,-1);
            System.out.println();
            board.print();
            board.showLegalMoves(1);
            System.out.println();
            System.out.println("O : type in your move(x and y):");
            x = scan.nextInt();
            y = scan.nextInt();
            board.place(x,y,1);
            System.out.println();
        }


    }
}
