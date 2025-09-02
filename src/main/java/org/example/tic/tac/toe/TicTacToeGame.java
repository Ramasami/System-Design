package org.example.tic.tac.toe;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class TicTacToeGame {
    private Deque<Player> players;
    private Board board;

    public TicTacToeGame(String player1, String player2) {
        this.players = new ArrayDeque<>();
        players.add(new Player(player1, new PlayingPieceX()));
        players.add(new Player(player2, new PlayingPieceO()));
        board = new Board(3, 3);
        board.printBoard();
    }

    public GameStatus nextMove(int i, int j) {
        if (!board.nextMove(i, j, players.peek().getPlayingPiece())) {
            throw  new RuntimeException("Invalid move from " + players.peek().getPlayingPiece());
        }
        board.printBoard();
        boolean win = board.checkWin(players.peek().getPlayingPiece());
        boolean draw = board.checkDraw();
        players.add(players.poll());
        if (win) return GameStatus.Won;
        else if (draw) return GameStatus.Draw;
        else return  GameStatus.Ongoing;
    }

    public void play() {
        GameStatus gameStatus = GameStatus.Ongoing;
        Player player = null;
        do {
            try {
                player = getCurrentPlayer();
                System.out.print("Enter row,column: ");
                Scanner scanner = new Scanner(System.in);
                String[] input = scanner.nextLine().split(",");
                gameStatus = nextMove(Integer.parseInt(input[0]), Integer.parseInt(input[1]));
            } catch (RuntimeException e) {
                System.out.println("Enter valid inputs");
            }
        } while (gameStatus == GameStatus.Ongoing);
        if (gameStatus == GameStatus.Draw) {
            System.out.println("Game Drawed");
        } else if (gameStatus == GameStatus.Won) {
            System.out.println(player.getPlayerName() + " Wins!!");
        }
    }


    private Player getCurrentPlayer() {
        return players.peek();
    }
}
