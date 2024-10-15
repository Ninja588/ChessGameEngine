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

}
