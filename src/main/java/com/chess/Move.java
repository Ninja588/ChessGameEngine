package com.chess;

public class Move {
    public final int startX;
    public final int startY;
    public final int endX;
    public final int endY;
    private boolean isPromotion;

    public Move(int startX, int startY, int endX, int endY, boolean isPromotion) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.isPromotion = isPromotion;
    }

    public int getEndX()
    {
        return this.endX;
    }
    public int getEndY()
    {
        return this.endY;
    }
    public boolean isPromotion()
    {
        return this.isPromotion;
    }

    @Override
    public String toString() {
        return "startX: " + startX + " startY: " + startY + " endX: " + endX + " endY: " + endY;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return this.startX == move.startX && this.startY == move.startY &&
                this.endX == move.endX && this.endY == move.endY;
    }

}
