package com.chess;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{
    public Queen(boolean isWhite) {
        super(isWhite, PieceType.QUEEN);
    }

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();

        // wszystkie strony swiata
        int[][] directions = {
                {1, 0},  // dol
                {-1, 0}, // gora
                {0, 1},  // prawo
                {0, -1}, // lewo
                {1, 1},  // prawy dolny
                {1, -1}, // lewy dolny
                {-1, 1}, // prawy gorny
                {-1, -1} // lewy gorny
        };

        for(int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x;
            int newY = y;

            // poruszanie sie w strone dopoki nie natrafi na koniec planszy/figury
            while(true) {
                newX += dx;
                newY += dy;

                // sprawdzanie czy pozycja jest na planszy
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                    break;
                }

                Piece pieceAtNewPos = board.getPiece(newX, newY);
                if(pieceAtNewPos == null) {
                    // puste pole, jesli po poruszaniu sie nie odkryje krola na szacha jest git
                    if(!board.isPinned(this,x,y) || board.wouldExposeKing(x, y, newX, newY))
                        legalMoves.add(new Move(x, y, newX, newY, false));
                } else {
                    // sprawdanie koloru figury
                    if(pieceAtNewPos.isWhite != this.isWhite) {
                        // da sie zbic figure jesli nie odkryje krola na szacha albo to jest figura ktora pinuje do krola
                        if(!board.isPinned(this,x,y) || board.wouldExposeKing(x, y, newX, newY))
                            legalMoves.add(new Move(x, y, newX, newY, false));
                    }
                    break;
                }
            }
        }
        return legalMoves;
    }
    @Override
    public String getSymbol() {
        return isWhite ? "Q" : "q";
    }
}
