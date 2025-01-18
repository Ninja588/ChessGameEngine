package com.chess;

import java.util.List;

/**
 * Klasa abstrakcyjna dla wszystkich figur
 */
public abstract class Piece {

    /**
     * Enum z mozliwymi typami figur
     */
    protected enum PieceType {
        /**
         * Typ piona
         */
        PAWN,
        /**
         * Typ skoczka
         */
        KNIGHT,
        /**
         * Typ gonca
         */
        BISHOP,
        /**
         * Typ wiezy
         */
        ROOK,
        /**
         * Typ krolowki
         */
        QUEEN,
        /**
         * Typ krola
         */
        KING }

    /**
     * Zmienna koloru figury
     */
    protected final boolean isWhite;
    /**
     * Zmienna czy figura sie poruszyla
     */
    protected boolean hasMoved;

    /**
     * Zmienna z typem figury
     */
    public final PieceType pieceType;

    /**
     * Konstruktor figury
     * @param isWhite kolor figury
     * @param pieceType typ figury
     */
    protected Piece(boolean isWhite, PieceType pieceType) {
        this.isWhite = isWhite;
        this.hasMoved = false;
        this.pieceType = pieceType;
    }

    /**
     * Metoda ustawiajaca ze figura sie poruszyla
     */
    public void setMoved() {
        this.hasMoved = true;
    }

    /**
     * Metoda zwracajaca czy figura sie poruszyla
     * @return true - poruszyla sie, false - nie poruszyla sie
     */
    public boolean hasMoved() {
        return this.hasMoved;
    }

    /**
     * Lista zawierajaca wszsytkie mozliwe legalne ruchy figury
     * @param board szachownica
     * @param x koordynat x
     * @param y koordynat y
     * @return lista z wszystkimi ruchami
     */
    public abstract List<Move> getLegalMoves(ChessBoard board, int x, int y);

    /**
     * Metoda zwracajaca symbol danej figury
     * @return symbol figury
     */
    public abstract String getSymbol();
}
