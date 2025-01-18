package com.chess;

import java.util.Collections;
import java.util.List;

/**
 * Klasa odpowiadjaca za typ figury, ktora ma reprezentowac null
 */
public class NullPiece extends Piece{
    private static final NullPiece INSTANCE = new NullPiece();
    private NullPiece() {
        super(false, null);
    }

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        return Collections.emptyList();
    }

    @Override
    public String getSymbol() {
        return null;
    }

    /**
     * Metoda zwracajaca instancje figury null
     * @return instancja figury null
     */
    public static NullPiece getInstance() {
        return INSTANCE;
    }
}
