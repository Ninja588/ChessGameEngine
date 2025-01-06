package com.chess;

import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece{
    public Pawn(boolean isWhite) {
        super(isWhite, PieceType.PAWN);
    }

    protected boolean hasMovedTwoSquares = false;
    protected boolean enPassant;
    protected boolean forward;
    protected boolean forcedEnPssant;

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();
        int direction = isWhite ? 1 : -1;
        enPassant = false;
        forward = true;

        if((x + direction >= 0 && x + direction < 8 && y - 1 >= 0 && !board.isEnPassantForced(x,y,x+direction,y-1,this.isWhite)
                || (x + direction >= 0 && x + direction < 8 && y + 1 < 8 && !board.isEnPassantForced(x,y,x+direction,y+1,this.isWhite))))
            forcedEnPssant = false;
        else forcedEnPssant = true;

        // ruch do przodu
        if (x + direction >= 0 && x + direction < 8 && board.getPiece(x + direction, y) == null &&
                (!board.isPinned(this, x, y) || board.wouldExposeKing(x, y, x + direction, y)) && forward)
            legalMoves.add(new Move(x, y, x + direction, y, false));

        // pozycja startowa (ruch podwojny)
        if (((isWhite && x == 1) || (!isWhite && x == 6)) && board.getPiece(x + direction, y) == null &&
                board.getPiece(x + 2 * direction, y) == null && (!board.isPinned(this, x, y) || board.wouldExposeKing(x, y, x + 2 * direction, y))) {
            legalMoves.add(new Move(x, y, x + 2 * direction, y, false));
        }

        // zbicie
        for(int dx = -1; dx <= 1; dx += 2) { // dx = -1 (lewo) and 1 (prawo)
            int newX = x + direction;
            int newY = y + dx;

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Piece pieceAtNewPos = board.getPiece(newX, newY);
                if (pieceAtNewPos != null && pieceAtNewPos.isWhite != this.isWhite && (!board.isPinned(this, x, y) || board.wouldExposeKing(x, y, newX, newY))
                        && (x + direction >= 0 && x + direction < 8 && y - 1 >= 0 && !board.isEnPassantForced(x,y,x+direction,y-1,this.isWhite)
                        || (x + direction >= 0 && x + direction < 8 && y + 1 < 8 && !board.isEnPassantForced(x,y,x+direction,y+1,this.isWhite))))
                    legalMoves.add(new Move(x, y, newX, newY, false));
            }
        }

        // enpassant
        if (y - 1 >= 0 && board.getPiece(x, y - 1) != null && board.getPiece(x, y - 1).pieceType == PieceType.PAWN &&
                board.getPiece(x, y - 1).isWhite != this.isWhite &&
                ((Pawn) board.getPiece(x, y - 1)).hasMovedTwoSquares && (!board.isPinned(this, x, y) || board.wouldExposeKing(x, y, x + direction, y - 1))) {
            legalMoves.add(new Move(x, y, x + direction, y - 1, false)); // en passant
            enPassant = true;
        }

        if (y + 1 < 8 && board.getPiece(x, y + 1) != null && board.getPiece(x, y + 1).pieceType == PieceType.PAWN &&
                board.getPiece(x, y + 1).isWhite != this.isWhite &&
                ((Pawn) board.getPiece(x, y + 1)).hasMovedTwoSquares && (!board.isPinned(this, x, y) || board.wouldExposeKing(x, y, x + direction, y + 1))) {
            legalMoves.add(new Move(x, y, x + direction, y + 1, false)); // en passant
            enPassant = true;
        }

        // Promotion check
        // bialy pion promocja
        if (isWhite && x == 6 && !board.isPinned(this, 7, y) && board.wouldExposeKing(x, y, 7, y) && board.getPiece(x + direction, y) == null) {
            Move move = new Move(x, y, 7, y, true);
            legalMoves.add(move);
        }
        // czarny pion promocja
        else if (!isWhite && x == 1 && !board.isPinned(this, 0, y) && board.wouldExposeKing(x, y, 0, y) && board.getPiece(x + direction, y) == null) {
            Move move = new Move(x, y, 0, y, true);
            legalMoves.add(move);
        }

        return legalMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "P" : "p";
    }
}
