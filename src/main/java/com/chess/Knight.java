package com.chess;

import java.util.List;
import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(boolean isWhite) {
        super(isWhite, PieceType.KNIGHT);
    }

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();

        // mozliwe ruchy (niemcy)
        int[][] moves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : moves) {
            int newX = x + move[0];
            int newY = y + move[1];

            // sprawdzanie czy pozycja jest na planszy
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Piece pieceAtNewPos = board.getPiece(newX, newY);
                boolean isOpponentPiece = pieceAtNewPos != null && pieceAtNewPos.isWhite != this.isWhite;
                if (pieceAtNewPos == null || isOpponentPiece) {
                    boolean canMoveOrCapture = !board.isPinned(this, x, y) || board.wouldExposeKing(x, y, newX, newY);
                    if(canMoveOrCapture) legalMoves.add(new Move(x, y, newX, newY, false));
                }
            }
        }
        return legalMoves;
    }
    @Override
    public String getSymbol() {
        return isWhite ? "N" : "n";
    }
}
