package org.example.tic.tac.toe;

public class Board {
    private int m, n;
    private PlayingPiece[][] board;
    private int winStreak;

    public Board(int m, int n) {
        this.m = m;
        this.n = n;
        board = new PlayingPiece[m][n];
        winStreak = Math.min(m, n);
    }

    public void printBoard() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == null) System.out.print("\t\t");
                else System.out.print("\t" + board[i][j].piece.name()+"\t");
                if (j < n - 1) System.out.print("|");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean nextMove(int i, int j, PlayingPiece playingPiece) {
        if (board[i][j] != null) return false;
        board[i][j] = playingPiece;
        return true;
    }

    public boolean checkWin(PlayingPiece playingPiece) {
        int i, j;
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                if (board[i][j] != playingPiece) break;
            }
            if (j == n) return true;
        }

        for (j = 0; j < n; j++) {
            for (i = 0; i < m; i++) {
                if (board[i][j] != playingPiece) break;
            }
            if (i == m) return true;
        }

        for (i = 0; i < m; i++) {
            if (board[i][i] != playingPiece) break;
        }
        if (i == m) return true;

        for (i = 0; i < m; i++) {
            if (board[i][n - i - 1] != playingPiece) break;
        }
        if (i == m) return true;

        return false;
    }

    public boolean checkDraw() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == null) return false;
            }
        }
        return true;
    }
}
