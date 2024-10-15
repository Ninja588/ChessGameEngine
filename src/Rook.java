import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite, false);
    }

    @Override
    public List<Move> getLegalMoves(ChessBoard board, int x, int y) {
        List<Move> legalMoves = new ArrayList<>();

        int[][] directions = {
                {1, 0},  // dol
                {-1, 0}, // gora
                {0, 1},  // prawo
                {0, -1}  // lewo
        };

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x;
            int newY = y;

            // poruszanie sie w strone dopoki nie bedzie konca planszy
            while (true) {
                newX += dx;
                newY += dy;

                // sprawdzanie czy nowa pozycja jest w wymiarach planszy
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                    break;
                }

                Piece pieceAtNewPos = board.getPiece(newX, newY);
                boolean isOpponentPiece = pieceAtNewPos != null && pieceAtNewPos.isWhite != this.isWhite;

                if (pieceAtNewPos == null) {
                    // puste pole, jesli po poruszaniu sie nie odkryje krola na szacha jest git
                    if (!board.isPinned(this, x, y) || !board.wouldExposeKing(x, y, newX, newY)) {
                        legalMoves.add(new Move(x, y, newX, newY, false));
                    }
                } else {
                    // sprawdanie koloru figury
                    boolean capturesPinningPiece = isOpponentPiece && board.isPinned(this, x, y) && !board.wouldExposeKing(x, y, newX, newY);

                    if (isOpponentPiece && (!board.isPinned(this, x, y) || capturesPinningPiece)) {
                        // da sie zbic figure jesli nie odkryje krola na szacha albo to jest figura ktora pinuje do krola
                        legalMoves.add(new Move(x, y, newX, newY, false));
                    }
                    break; // nie da sie przejsc przez figure
                }
            }
        }
        return legalMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "R" : "r";
    }
}
