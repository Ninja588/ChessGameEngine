package com.chess;

import java.util.Collections;
import java.util.List;

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
    public static NullPiece getInstance() {
        return INSTANCE;
    }
}
