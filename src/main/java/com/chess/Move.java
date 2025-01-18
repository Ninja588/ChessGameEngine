package com.chess;

import java.util.Objects;

/**
 * Klasa ruchu
 */
public class Move {
    /**
     * Zmienna zawierajaca pozycje startowa X
     */
    public final int startX;
    /**
     * Zmienna zawierajaca pozycje startowa Y
     */
    public final int startY;
    /**
     * Zmienna zawierajaca pozycje koncowa X
     */
    public final int endX;
    /**
     * Zmienna zawierajaca pozycje koncowa Y
     */
    public final int endY;
    private final boolean isPromotion;

    /**
     * Konstuktor ruchu
     * @param startX koordynat startowy X
     * @param startY koordynat startowy Y
     * @param endX koordynat koncowy X
     * @param endY koordynat koncowy Y
     * @param isPromotion true - jest awans, false - nie ma awansu
     */
    public Move(int startX, int startY, int endX, int endY, boolean isPromotion) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.isPromotion = isPromotion;
    }

    /**
     * Metoda zwracajaca koordynat koncowy X
     * @return zwraca koordynat koncowy X
     */
    public int getEndX()
    {
        return this.endX;
    }

    /**
     * Metoda zwracajaca koordynat koncowy Y
     * @return zwraca koordynat koncowy Y
     */
    public int getEndY()
    {
        return this.endY;
    }

    /**
     * Metoda zwracajaca slowna reprezentacje ruchu
     * @return zwraca slowna reprezentacje danego ruchu
     */
    @Override
    public String toString() {
        return "startX: " + startX + " startY: " + startY + " endX: " + endX + " endY: " + endY;
    }

    /**
     * Metoda sprawdzajaca czy ruch jest taki sam jak inny
     * @param obj obiekt ruchu do sprawdzenia
     * @return true - jest taki sam, fasle - nie jest
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return this.startX == move.startX && this.startY == move.startY &&
                this.endX == move.endX && this.endY == move.endY;
    }

    /**
     * Metoda zwracajaca hashCode obiektu
     * @return zwraca hashCode obiektu
     */
    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, endX, endY, isPromotion);
    }

    /**
     * Metoda zwracajaca to czy ruch jest do awansu piona
     * @return zwraca pole 'isPromotion'
     */
    protected boolean isPromotion() {
        return this.isPromotion;
    }
}
