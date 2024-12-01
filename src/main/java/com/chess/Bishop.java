package com.chess;

import java.util.List;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super(isWhite, PieceType.BISHOP);
    }

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();

        // goniec po skosie leci
        int[][] directions = {
                {1, 1},  // prawy dolnu
                {1, -1}, // lewy dolny
                {-1, 1}, // prawy gorny
                {-1, -1} // lewy gorny
        };

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x;
            int newY = y;

            // poruszanie sie w strone dopoki nie natrafi na koniec planszy/figury
            while (true) {
                newX += dx;
                newY += dy;

                // sprawdzanie czy pozycja jest na planszy
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                    break;
                }

                Piece pieceAtNewPos = board.getPiece(newX, newY);
                boolean isOpponentPiece = pieceAtNewPos != null && pieceAtNewPos.isWhite != this.isWhite;

                if (pieceAtNewPos == null) {
                    // puste pole, jesli po poruszaniu sie nie odkryje krola na szacha jest git
                    if (!board.isPinned(this, x, y) || board.wouldExposeKing(x, y, newX, newY)) {
                        legalMoves.add(new Move(x, y, newX, newY, false));
                    }
                } else {
                    // sprawdanie koloru figury
                    boolean capturesPinningPiece = isOpponentPiece && board.isPinned(this, x, y) && board.wouldExposeKing(x, y, newX, newY);

                    if (isOpponentPiece && (!board.isPinned(this, x, y) || capturesPinningPiece)) {
                        // da sie zbic figure jesli nie odkryje krola na szacha albo to jest figura ktora pinuje do krola
                        legalMoves.add(new Move(x, y, newX, newY, false));
                    }
                    break; // nie da sie przejsc przez figure
                }
            }
        }
        return legalMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "B" : "b";
    }
}
