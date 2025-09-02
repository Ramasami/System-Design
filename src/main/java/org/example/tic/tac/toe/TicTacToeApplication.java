package org.example.tic.tac.toe;

public class TicTacToeApplication {

    public static void main(String[] args) {
        TicTacToeGame ticTacToeGame = new TicTacToeGame("player1", "player2");
        ticTacToeGame.play();
    }
}
