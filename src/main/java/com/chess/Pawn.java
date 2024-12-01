package com.chess;

import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece{
    public Pawn(boolean isWhite) {
        super(isWhite,false, PieceType.PAWN);
    }

    public boolean hasMovedTwoSquares = false;
    public boolean enPassant;
    public boolean isPromo;

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();
        int direction = isWhite ? 1 : -1;
        enPassant = false;

        // ruch do przodu
        if(x + direction >= 0 && x + direction < 8) {
            if(board.getPiece(x + direction, y) == null) {
                if(!board.isPinned(this,x,y) || !board.wouldExposeKing(x, y,x+direction, y))
                    legalMoves.add(new Move(x, y, x + direction, y, false));
            }
        }

        // pozycja startowa (ruch podwojny)
        if((isWhite && x == 1) || (!isWhite && x == 6)) {
            if(board.getPiece(x + direction, y) == null && board.getPiece(x + 2*direction, y) == null) {
                if(!board.isPinned(this,x,y) || !board.wouldExposeKing(x,y,x+ 2 * direction,y)) {
                    legalMoves.add(new Move(x, y, x + 2 * direction, y, false));
                    //hasMovedTwoSquares = true;
                }
            }
        }
        // zbicie
        for(int dx = -1; dx <= 1; dx += 2) { // dx = -1 (lewo) and 1 (prawo)
            int newX = x + direction;
            int newY = y + dx;
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Piece pieceAtNewPos = board.getPiece(newX, newY);
                if (pieceAtNewPos != null && pieceAtNewPos.isWhite != this.isWhite) {
                    if(!board.isPinned(this,x,y) || !board.wouldExposeKing(x,y,newX,newY))
                        legalMoves.add(new Move(x, y, newX, newY, false));
                }
            }
        }

        // enpassant
        if(y - 1 >= 0 && board.getPiece(x, y - 1) != null && board.getPiece(x, y - 1).pieceType == PieceType.PAWN &&
                board.getPiece(x, y - 1).isWhite != this.isWhite &&
                ((Pawn) board.getPiece(x, y - 1)).hasMovedTwoSquares) {
            if(!board.isPinned(this,x,y) || !board.wouldExposeKing(x,y,x+direction,y-1)) {
                legalMoves.add(new Move(x, y, x + direction, y - 1, false)); // en passant
                enPassant = true;
            }
        }

        if(y + 1 < 8 && board.getPiece(x, y + 1) != null && board.getPiece(x, y + 1).pieceType == PieceType.PAWN &&
                board.getPiece(x, y + 1).isWhite != this.isWhite &&
                ((Pawn) board.getPiece(x, y + 1)).hasMovedTwoSquares) {
            if(!board.isPinned(this,x,y) || !board.wouldExposeKing(x,y,x+direction,y+1)) {
                legalMoves.add(new Move(x, y, x + direction, y + 1, false)); // en passant
                enPassant = true;
            }
        }

        // Promotion check
        if(isWhite && x == 6) { // bialy pion promocja
            if(!board.isPinned(this,7,y) && !board.wouldExposeKing(x,y,7,y) && board.getPiece(x + direction, y) == null) {
                Move move = new Move(x, y, 7, y, true);
                legalMoves.add(move);
                isPromo = move.isPromotion();
            }
        } else if(!isWhite && x == 1) { // czarny pion promocja
            if(!board.isPinned(this,0,y) && !board.wouldExposeKing(x,y,0,y) && board.getPiece(x + direction, y) == null) {
                Move move = new Move(x, y, 0, y, true);
                legalMoves.add(move);
                isPromo = move.isPromotion();
            }
        }

        return legalMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "P" : "p";
    }
}
