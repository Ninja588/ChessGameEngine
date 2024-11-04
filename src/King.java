import java.util.List;
import java.util.ArrayList;

public class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite, false, PieceType.KING);
    }

    public boolean castle = false;

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();

        // ruchy krola
        int[][] moves = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] move : moves) {
            int newX = x + move[0];
            int newY = y + move[1];

            if (isValidMove(board, newX, newY)) {
                Piece pieceAtNewPos = board.getPiece(newX, newY);
                boolean isOpponentPiece = pieceAtNewPos != null && pieceAtNewPos.isWhite != this.isWhite;

                if (pieceAtNewPos == null || isOpponentPiece) {
                    // jezeli pole nie jest atakowane lub figura na nim nie jest chrononia krol moze tam wejsc
                    if (!board.wouldExposeKing(x,y,newX,newY)) { // !board.isSquareAttacked(newX, newY, !this.isWhite) &&
                        legalMoves.add(new Move(x, y, newX, newY, false));
                    }
                    if(!castle) {
                        if(!this.isWhite) {
                            if(board.checkRowKingSide(false, 4, this)) {
                                legalMoves.add(new Move(x, y, 7, 6, false));
                            }
                            if(board.checkRowQueenSide(false, 4, this)) {
                                legalMoves.add(new Move(x, y, 7, 2, false));
                            }
                        }
                        else {
                            if(board.checkRowKingSide(true, 4, this)) {
                                legalMoves.add(new Move(x, y, 0, 6, false));
                            }
                            if(board.checkRowQueenSide(true, 4, this)) {
                                legalMoves.add(new Move(x, y, 0, 2, false));
                            }
                        }
                    }
                }
            }
        }

        return legalMoves;
    }

    private boolean isValidMove(ChessBoard board, int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8 && (board.getPiece(x, y) == null || board.getPiece(x, y).isWhite != this.isWhite);
    }

    @Override
    public String getSymbol() {
        return isWhite ? "K" : "k";
    }
}
