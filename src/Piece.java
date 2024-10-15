import java.util.List;
public abstract class Piece {
    protected boolean isWhite;
    protected boolean hasMoved;

    public Piece(boolean isWhite, boolean hasMoved) {
        this.isWhite = isWhite;
        this.hasMoved = false;
    }

    public void setMoved() {
        this.hasMoved = true;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public abstract List<Move> getLegalMoves(ChessBoard board, int x, int y);
    public abstract String getSymbol();
}
