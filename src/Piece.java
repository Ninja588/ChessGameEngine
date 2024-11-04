import java.util.List;

public abstract class Piece {

    public enum PieceType { PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING }
    protected boolean isWhite;
    protected boolean hasMoved;

    public PieceType pieceType;

    public Piece(boolean isWhite, boolean hasMoved, PieceType pieceType) {
        this.isWhite = isWhite;
        this.hasMoved = false;
        this.pieceType = pieceType;
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
