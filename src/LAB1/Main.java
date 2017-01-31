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
            System.out.println("type in tour move(x and y):");
            x = scan.nextInt();
            y = scan.nextInt();
            board.place(x,y,-1);
            System.out.println();
            board.print();
            System.out.println("type in tour move(x and y):");
            x = scan.nextInt();
            y = scan.nextInt();
            board.place(x,y,1);
            System.out.println();
        }


    }
}
