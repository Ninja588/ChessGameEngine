import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class ChessBoard {
    private boolean whiteTurn = true;
    private Piece[][] board;
    private PromotionListener promotionListener;
    private Map<String, Integer> boardStates = new HashMap<>();

    public String generateBoardState() {
        StringBuilder boardState = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    boardState.append(piece.getSymbol());
                } else {
                    boardState.append(".");
                }
            }
        }
        return boardState.toString();
    }

    public void recordBoardState(boolean isWhiteTurn) {
        String boardState = generateBoardState() + (isWhiteTurn ? "W" : "B");
        boardStates.put(boardState, boardStates.getOrDefault(boardState, 0) + 1);
    }

    public boolean isThreefoldRepetition() {
        for (int count : boardStates.values()) {
            if (count >= 3) {
                return true;
            }
        }
        return false;
    }

    public ChessBoard() {
        //board = new Piece[8][8];
        //initializeBoard();
        resetBoard();
    }

    public void resetBoard() {
        board = new Piece[8][8];
        capturedPieces = new Stack<>();
        boardStates = new HashMap<>();
        whiteTurn = true;

        initializeBoard();
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void toggleTurn() {
        whiteTurn = !whiteTurn;
    }

    public interface PromotionListener {
        void onPromotion(int x, int y, boolean isWhite);
    }
    public void setPromotionListener(PromotionListener listener) {
        this.promotionListener = listener;
    }

    private void initializeBoard() {
        // biale figury
        // rook
        board[0][0] = new Rook(true);
        board[0][7] = new Rook(true);
        // queen
        board[0][3] = new Queen(true);
        // knight
        board[0][1] = new Knight(true);
        board[0][6] = new Knight(true);
        // bishop
        board[0][2] = new Bishop(true);
        board[0][5] = new Bishop(true);
        // king
        board[0][4] = new King(true);
        // pawns
        for(int i=0;i<8;i++) {
            board[1][i] = new Pawn(true);
        }

        // czarne figury
        // rooks
        board[7][7] = new Rook(false);
        board[7][0] = new Rook(false);
        // queen
        board[7][3] = new Queen(false);
        // knight
        board[7][1] = new Knight(false);
        board[7][6] = new Knight(false);
        // bishop
        board[7][2] = new Bishop(false);
        board[7][5] = new Bishop(false);
        // king
        board[7][4] = new King(false);
        // pawns
        for(int i=0;i<8;i++) {
            board[6][i] = new Pawn(false);
        }
    }

    public void setPiece(int x, int y, Piece piece)
    {
        board[x][y] = piece;
    }

    public Piece getPiece(int x, int y) {
        return board[x][y];
    }

    public boolean canCastleKingSide(boolean isWhite) {
        int row = isWhite ? 0 : 7; // wybierz wiersz dla czarnego/bialego krola
        Piece king = board[row][4]; // krol jest na (row, 4)
        Piece rook = board[row][7]; // rook jest na (row, 7)

        // sprawdzanie czy krol/rook poruszyli sie
        if (king == null || rook == null || king.hasMoved() || rook.hasMoved()) {
            return false;
        }

        // sprawdzanie czy pola pomiedzy rookiem/krolem sa puste
        for (int i = 5; i < 7; i++) {
            if (board[row][i] != null) {
                return false; // jak tu dojdzie to znaczy ze jest cos miedzy nimi
            }
        }

        // sprawdzanie czy krol jest w szachu albo moglby dostac szacha podczas roszady
        if((isInCheck(isWhite) || isInCheckAfterMove(isWhite, row, 5) || isInCheckAfterMove(isWhite, row, 6)) && (king.pieceType == Piece.PieceType.KING && !((King) king).castle)) {
            return false;
        }

        return true; // wszystko git
    }

    public boolean canCastleQueenSide(boolean isWhite) {
        int row = isWhite ? 0 : 7; // wybierz wiersz dla czarnego/bialego krola
        Piece king = board[row][4]; // krol jest na (row, 4)
        Piece rook = board[row][0]; // rook jest na (row, 0)
        //System.out.println("hasmoved: "+ king.hasMoved() + " rook.hasmoved()" + rook.hasMoved());

        // sprawdzanie czy krol/rook poruszyli sie
        if (king == null || rook == null || king.hasMoved() || rook.hasMoved()) {
            //System.out.println("hasmoved: "+ king.hasMoved() + " rook.hasmoved()" + rook.hasMoved());
            return false;
        }

        // sprawdzanie czy pola pomiedzy rookiem/krolem sa puste
        for (int i = 1; i < 4; i++) {
            if (board[row][i] != null) {
                return false; // jak tu dojdzie to znaczy ze jest cos miedzy nimi
            }
        }

        // sprawdzanie czy krol jest w szachu albo moglby dostac szacha podczas roszady
        if((isInCheck(isWhite) || isInCheckAfterMove(isWhite, row, 2) || isInCheckAfterMove(isWhite, row, 3)) && (king.pieceType == Piece.PieceType.KING && !((King) king).castle)) {
            return false;
        }

        return true; // wszystko git
    }

    private boolean checkSound = false;

    public boolean isInCheck(boolean isWhite) {
        int kingX = -1;
        int kingY = -1;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        if (kingX == -1) {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.isWhite != isWhite) {
                    List<Move> legalMoves = piece.getLegalMoves(this, i, j);
                    for (Move move : legalMoves) {
                        if (move.endX == kingX && move.endY == kingY) {
                            if((piece.pieceType == Piece.PieceType.PAWN && (move.endX + 1 == kingX || move.endX - 1 == kingX))) continue;
                            if(!checkSound) {
                                SoundManager.playSound("move-check.wav");
                                checkSound = true;
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isSquareAttacked(int x, int y, boolean byWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.isWhite == byWhite) {
                    if (piece.pieceType == Piece.PieceType.PAWN) {
                        // pion ataki
                        int direction = byWhite ? 1 : -1;
                        if ((x == i + direction && (y == j + 1 || y == j - 1))) {
                            return true;
                        }
                    } else if (piece.pieceType == Piece.PieceType.KNIGHT) {
                        // skoczek ataki
                        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
                        for (int[] move : knightMoves) {
                            if (x == i + move[0] && y == j + move[1]) {
                                return true;
                            }
                        }
                    } else if (piece.pieceType == Piece.PieceType.ROOK) {
                        // rook ataki
                        if (x == i || y == j) {
                            if (isPathClear(i, j, x, y)) {
                                return true;
                            }
                        }
                    } else if (piece.pieceType == Piece.PieceType.BISHOP) {
                        // goniec ataki
                        if (Math.abs(x - i) == Math.abs(y - j)) {
                            if (isPathClear(i, j, x, y)) {
                                return true;
                            }
                        }
                    } else if (piece.pieceType == Piece.PieceType.QUEEN) {
                        // krolowka ataki
                        if ((x == i || y == j) || (Math.abs(x - i) == Math.abs(y - j))) {
                            if (isPathClear(i, j, x, y)) {
                                return true;
                            }
                        }
                    } else if (piece.pieceType == Piece.PieceType.KING) {
                        // krol ataki
                        int[][] kingMoves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                        for (int[] move : kingMoves) {
                            if (x == i + move[0] && y == j + move[1]) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // metoda sprawdzajaca czy sciezka jest wolna dla rook, goncow, krolowek
    private boolean isPathClear(int startX, int startY, int endX, int endY) {
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);

        int x = startX + dx;
        int y = startY + dy;

        while (x != endX || y != endY) {
            if (board[x][y] != null) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }


    public boolean isCheckmate(boolean isWhite) {
        if (!isInCheck(isWhite)) {
            return false; // nie w szachu
        }

        // sprawdzanie czy krol moze sie poruszyc
        int kingX = -1;
        int kingY = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        if (kingX == -1) {
            return false;
        }

        List<Move> kingMoves = board[kingX][kingY].getLegalMoves(this, kingX, kingY);
        for (Move move : kingMoves) {
            makeMove(new Move(kingX, kingY, move.endX, move.endY,false));
            if (!isInCheck(isWhite)) {
                undoMove(new Move(kingX, kingY, move.endX, move.endY,false));
                return false; // krol moze sie poruszyc
            }
            undoMove(new Move(kingX, kingY, move.endX, move.endY,false));
        }

        // sprawdzanie czy cos moze zablokowac/zbic figure atakujaca
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.isWhite == isWhite) {
                    List<Move> legalMoves = piece.getLegalMoves(this, i, j);
                    for (Move move : legalMoves) {
                        makeMove(new Move(i, j, move.endX, move.endY,false));
                        if (!isInCheck(isWhite)) {
                            undoMove(new Move(i, j, move.endX, move.endY,false));
                            return false; // moze
                        }
                        undoMove(new Move(i, j, move.endX, move.endY,false));
                    }
                }
            }
        }

        return true; // mat
    }

    public boolean isStalemate(boolean isWhite) {
        if (isThreefoldRepetition()) {
            return true; // powtorzenie pozycji 3 razy (stalemate)
        }

        if (isInCheck(isWhite)) {
            return false; // w szachu, wiec nie moze byc stalemate
        }

        // sprawdzanie czy sa jakiekolwiek legalne ruchy dla wszystkich figur
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.isWhite == isWhite) {
                    List<Move> legalMoves = piece.getLegalMoves(this, i, j);
                    for (Move move : legalMoves) {
                        makeMove(new Move(i, j, move.endX, move.endY,false));
                        boolean check = isInCheck(isWhite);
                        undoMove(new Move(i, j, move.endX, move.endY,false));
                        if (!check) {
                            return false; // jest jakis
                        }
                    }
                }
            }
        }

        // brak ruchow, nie w szachu wiec stalemate
        return true;
    }

    private Stack<Piece> capturedPieces = new Stack<>();

    public void makeMove(Move move) {
        Piece piece = board[move.startX][move.startY];
        Piece targetPiece = board[move.endX][move.endY];

        capturedPieces.push(targetPiece);

        board[move.endX][move.endY] = piece;
        board[move.startX][move.startY] = null;
    }

    public void undoMove(Move move) {
        Piece piece = board[move.endX][move.endY];

        board[move.startX][move.startY] = piece;

        Piece capturedPiece = capturedPieces.pop();
        if (capturedPiece != null) {
            board[move.endX][move.endY] = capturedPiece;
        } else {
            board[move.endX][move.endY] = null;
        }
    }


    // sprawdzanie czy krol bylby w szachu gdyby sie gdzies poruszyl
    public boolean isInCheckAfterMove(boolean isWhite, int row, int col) {
        int kingX = -1;
        int kingY = -1;

        // znajdowanie pozycji krola
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        // jak nie znaleziono krola (bron boze zeby tak bylo) to nie da sie
        if (kingX == -1) {
            return false;
        }

        // zapisanie aktualnego stanu
        Piece originalPiece = board[kingX][kingY];
        board[kingX][kingY] = null; // usuniecia krola z oryginalnej pozycjio
        board[row][col] = originalPiece; // poruszenie krola do nowej pozycji

        // sprawdzanie czy krol jest w szachu po tym ruchu
        boolean inCheck = isInCheck(isWhite);

        // przywrocenie oryginalnego stanu
        board[row][col] = null; // wyczyszczenie nowej pozycji
        board[kingX][kingY] = originalPiece; // krol wraca na swoja dawna pozycje

        return inCheck; // zwroci prawde jak krol bedzie w szachu a inaczej falsz
    }

//    public void test(int startX, int startY)
//    {
//        Piece piece = board[startX][startY];
//        boolean isWhite = piece.isWhite;
//        canCastleQueenSide(isWhite);
//    }

    public boolean ok; // zmienna czy ruch jest git
    public boolean enpassantWhite;
    public boolean enpassantBlack;
    public boolean castleBlackQueen = false, castleBlackKing = false, castleWhiteKing = false, castleWhiteQueen = false;
    public boolean promoted = false;
    public boolean AIpromoted = false;


//    public void movePiece(int startX, int startY, int endX, int endY, boolean isAiTurn) {
//        Piece piece = board[startX][startY];
//        Piece piece2;
//        enpassantWhite = false;
//        enpassantBlack = false;
//        ok = false;
//        promoted = false;
//        AIpromoted = false;
//
//        if (piece != null && whiteTurn == piece.isWhite) {
//            // sprawdzanie czy na danym polu jest figura tego samego koloru
//            Piece destinationPiece = board[endX][endY];
//            if (destinationPiece == null || destinationPiece.isWhite != piece.isWhite) {
//                if (piece instanceof King) {
//                    boolean isWhite = piece.isWhite;
//
//                    // roszada krola, krolowki
//                    if (endY == 6 && (endX == 0 || endX == 7) && canCastleKingSide(isWhite)) { //
//                        board[endX][endY] = piece;
//                        board[startX][startY] = null;
//
//                        Piece rook = board[endX][7];
//                        board[endX][5] = rook;
//                        board[endX][7] = null;
//
//                        piece.setMoved();
//                        rook.setMoved();
//
//                        if (piece.isWhite) {
//                            castleWhiteKing = true;
//                            ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 7, endX, 5, true);
//                        } else {
//                            castleBlackKing = true;
//                            ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 7, endX, 5, false);
//                        }
//                        SoundManager.playSound("castle.wav");
//                        toggleTurn();
//                        recordBoardState();
//                        ((King) piece).castle = true;
//                        return;
//                    } else if (endY == 2 && (endX == 0 || endX == 7) && canCastleQueenSide(isWhite)) { //
//                        board[endX][endY] = piece;
//                        board[startX][startY] = null;
//
//                        Piece rook = board[endX][0];
//                        board[endX][3] = rook;
//                        board[endX][0] = null;
//
//                        piece.setMoved();
//                        rook.setMoved();
//
//                        if (piece.isWhite) {
//                            castleWhiteQueen = true;
//                            ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 0, endX, 3, true);
//                        } else {
//                            castleBlackQueen = true;
//                            ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 0, endX, 3, false);
//                        }
//                        SoundManager.playSound("castle.wav");
//                        toggleTurn();
//                        recordBoardState();
//                        ((King) piece).castle = true;
//                        return;
//                    }
//                }
//                // sprawdzanie czy ruch ktory sie wybralo jest w liscie legalnych ruchow figury
//                List<Move> legalMoves = piece.getLegalMoves(this, startX, startY);
//                boolean isValidMove = legalMoves.stream().anyMatch(move -> move.endX == endX && move.endY == endY);
//
//                if(isValidMove) {
//                    board[endX][endY] = piece; // polozenie figury na danym miejscu
//                    board[startX][startY] = null; // usuniecie figury z poprzedniego miejsca
//                    ok = true;
//                    checkSound = false;
//
//                    piece.setMoved();
//                    // zbicie figury
//                    if (destinationPiece != null) {
//                        System.out.println("Zbito: " + destinationPiece.getSymbol());
//                        SoundManager.playSound("capture.wav");
//                        //toggleTurn();
//                        //return;
//                    }
//                    // piony mechaniki (prosze zabijcie tego co wymyslic en passant)
//                    if (piece instanceof Pawn) {
//                        // en passant
//                        if (Math.abs(endX - startX) == 2) {
//                            ((Pawn) piece).hasMovedTwoSquares = true;
//                        } else {
//                            resetEnPassant(piece.isWhite);
//                        }
//                        if (((Pawn) piece).enPassant) {
//                            if (piece.isWhite) {
//                                board[endX - 1][endY] = null;
//                                enpassantWhite = true;
//                            } else {
//                                board[endX + 1][endY] = null;
//                                enpassantBlack = true;
//                            }
//                            SoundManager.playSound("capture.wav");
//                        }
//
//                        // promo piona na inne figury
//                        if ((endX == 7 || endX == 0)) {
////                        if(isAiTurn) {
////                            Piece promotedPiece = getBestPromotionPiece(piece.isWhite);
////                            board[endX][endY] = promotedPiece;
////                            ChessGameGUI.updateBoard(startX, startY, endX, endY, "Queen", piece.isWhite);
////                            AIpromoted = true;
////                        }
//                            if (promotionListener != null && !isAiTurn) {
//                                promotionListener.onPromotion(endX, endY, piece.isWhite);
//                                SoundManager.playSound("promote.wav");
//                                promoted = true;
//                            }
//                        }
//                    }
//                    SoundManager.playSound("move.wav");
//                    recordBoardState();
//                    toggleTurn();
//                } else {
//                    System.out.println("zly ruch");
//                    ok = false;
//                }
//            } else {
//                System.out.println("nie da sie zbic figury tego samego koloru");
//                ok = false;
//            }
//        }
//    }

    public void movePiece(int startX, int startY, int endX, int endY, boolean isAiTurn) {
        if (isCheckmate(true) || isCheckmate(false) || isStalemate(true) || isStalemate(false)) {
            return;
        }

        Piece piece = board[startX][startY];
        //Piece piece2;
        enpassantWhite = false;
        enpassantBlack = false;
        ok = false;
        promoted = false;
        AIpromoted = false;

        if (piece != null && whiteTurn == piece.isWhite) {
            // sprawdzanie czy na danym polu jest figura tego samego koloru
            Piece destinationPiece = board[endX][endY];
            if (destinationPiece == null || destinationPiece.isWhite != piece.isWhite) {
                // sprawdzanie czy ruch ktory sie wybralo jest w liscie legalnych ruchow figury
                List<Move> legalMoves = piece.getLegalMoves(this, startX, startY);
                boolean isValidMove = legalMoves.stream().anyMatch(move -> move.endX == endX && move.endY == endY);

                if(isValidMove) {
//                    if(piece instanceof King) {
//                        ((King) piece).checkFlag = false;
//                    }
                    if (piece.pieceType == Piece.PieceType.KING && !((King) piece).castle && (canCastleKingSide(piece.isWhite) || canCastleQueenSide(piece.isWhite))) {
                        //boolean isWhite = piece.isWhite;
                        // roszada krola, krolowki
                        if (endY == 6 && (endX == 0 || endX == 7)) { //
                            board[endX][endY] = piece;
                            board[startX][startY] = null;

                            Piece rook = board[endX][7];
                            board[endX][5] = rook;
                            board[endX][7] = null;

                            piece.setMoved();
                            rook.setMoved();

                            if (piece.isWhite) {
                                castleWhiteKing = true;
                                ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 7, endX, 5, true);
                            } else {
                                castleBlackKing = true;
                                ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 7, endX, 5, false);
                            }
                            SoundManager.playSound("castle.wav");
                            toggleTurn();
                            recordBoardState(isWhiteTurn());
                            ((King) piece).castle = true;
                            return;
                        } else if (endY == 2 && (endX == 0 || endX == 7)) { //
                            board[endX][endY] = piece;
                            board[startX][startY] = null;

                            Piece rook = board[endX][0];
                            board[endX][3] = rook;
                            board[endX][0] = null;

                            piece.setMoved();
                            rook.setMoved();

                            if (piece.isWhite) {
                                castleWhiteQueen = true;
                                ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 0, endX, 3, true);
                            } else {
                                castleBlackQueen = true;
                                ChessGameGUI.updateCastlingUI(startX, startY, endX, endY, endX, 0, endX, 3, false);
                            }
                            SoundManager.playSound("castle.wav");
                            toggleTurn();
                            recordBoardState(isWhiteTurn());
                            ((King) piece).castle = true;
                            return;
                        }
                    }
                    board[endX][endY] = piece; // polozenie figury na danym miejscu
                    board[startX][startY] = null; // usuniecie figury z poprzedniego miejsca
                    ok = true;
                    checkSound = false;

                    piece.setMoved();
                    // zbicie figury
                    if (destinationPiece != null) {
                        //System.out.println("Zbito: " + destinationPiece.getSymbol());
                        SoundManager.playSound("capture.wav");
                        //toggleTurn();
                        //return;
                    }
                    // piony mechaniki (prosze zabijcie tego co wymyslic en passant)
                    if (piece.pieceType == Piece.PieceType.PAWN) {
                        // en passant
                        if (Math.abs(endX - startX) == 2) {
                            //System.out.println("PORUSZYLEM SIE KURWA DWA POLE ENPASSANT ACITVATED");
                            ((Pawn) piece).hasMovedTwoSquares = true;
                        } else {
                            //System.out.println(" ENPASSANT WYLACZONE ");
                            resetEnPassant(piece.isWhite);
                        }
                        if (((Pawn) piece).enPassant) {
                            if (piece.isWhite) {
                                //System.out.println("EN PASSANT ZBICIE");
                                board[endX - 1][endY] = null;
                                enpassantWhite = true;
                            } else {
                                //System.out.println("EN PASSANT ZBICIE");
                                board[endX + 1][endY] = null;
                                enpassantBlack = true;
                            }
                            SoundManager.playSound("capture.wav");
                        }

                        // promo piona na inne figury
                        if ((endX == 7 || endX == 0)) {
//                        if(isAiTurn) {
//                            Piece promotedPiece = getBestPromotionPiece(piece.isWhite);
//                            board[endX][endY] = promotedPiece;
//                            ChessGameGUI.updateBoard(startX, startY, endX, endY, "Queen", piece.isWhite);
//                            AIpromoted = true;
//                        }
                            if (promotionListener != null && !isAiTurn) {
                                promotionListener.onPromotion(endX, endY, piece.isWhite);
                                SoundManager.playSound("promote.wav");
                                promoted = true;
                            }
                        }
                    }
                    SoundManager.playSound("move.wav");
                    recordBoardState(isWhiteTurn());
                    toggleTurn();
                } else {
                    System.out.println("zly ruch");
                    ok = false;
                }
            } else {
                System.out.println("nie da sie zbic figury tego samego koloru");
                ok = false;
            }
        }
    }

    public boolean checkRowKingSide(boolean isWhite, int y, Piece piece) {
        int row = isWhite ? 0 : 7;
        if(piece.hasMoved) return false;

        if(isSquareAttacked(row,y,!isWhite) || isSquareAttacked(row,y+1,!isWhite) || (isSquareAttacked(row,y+2,!isWhite))) {
            return false;
        }

        for(int i=y+1;i<7;i++) {
            if(board[row][i] != null ) return false;
        }
        return true;
    }

    public boolean checkRowQueenSide(boolean isWhite, int y, Piece piece) {
        int row = isWhite ? 0 : 7;
        if(piece.hasMoved) return false;

        if(isSquareAttacked(row,y,!isWhite) || isSquareAttacked(row,y-1,!isWhite) || (isSquareAttacked(row,y-2,!isWhite))) {
            return false;
        }

        for(int i=y-1;i>0;i--) {
            if(board[row][i] != null ) return false;
        }
        return true;
    }

    // resetowanie flagi do enpassant
    private void resetEnPassant(boolean isWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece!=null && piece.pieceType == Piece.PieceType.PAWN && piece.isWhite == isWhite) {
                    ((Pawn) piece).hasMovedTwoSquares = false;
                }
            }
        }
    }

    public boolean isPinned(Piece piece, int x, int y) {
        int kingX = -1, kingY = -1;

        // znalezenie pozycji krola
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null && board[i][j].pieceType == Piece.PieceType.KING && board[i][j].isWhite == piece.isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        // chwilowe usuniecie figury z planszy
        Piece tempPiece = board[x][y];
        board[x][y] = null;

        // sprwadzanie czy krol bylby w szachu po usunieciu figury
        boolean isPinned = isSquareAttacked(kingX, kingY, !piece.isWhite);
        if(tempPiece != null && tempPiece.pieceType == Piece.PieceType.PAWN && x<8 && y<7 && x>0 && y>0) {
            Piece tempPiecePawn1 = getPiece(x,y-1);
            Piece tempPiecePawn2 = getPiece(x,y+1);
            if(tempPiecePawn1 != null && tempPiecePawn2 != null && ((tempPiecePawn1.pieceType == Piece.PieceType.PAWN && ((Pawn) tempPiecePawn1).hasMovedTwoSquares) ||
                    (tempPiecePawn2.pieceType == Piece.PieceType.PAWN && ((Pawn) tempPiecePawn2).hasMovedTwoSquares) &&
                            isSquareAttacked(kingX, kingY, !piece.isWhite))) {
                isPinned = false;
            }
        }

        // figura wraca na swoje miejsce
        board[x][y] = tempPiece;

        return isPinned;
    }

    public boolean wouldExposeKing(int startX, int startY, int endX, int endY) {
        Piece movingPiece = board[startX][startY];
        Piece targetPiece = board[endX][endY];

        // chwilowo przenies figure
        setPiece(endX, endY, movingPiece);
        setPiece(startX, startY, null);

        // znajdz pozycje krola
        int kingX = -1, kingY = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == movingPiece.isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        // sprawdzanie czy krol jest w szachu
        boolean inCheck = isSquareAttacked(kingX, kingY, !movingPiece.isWhite);

        // powrot do poprzedniego stanu
        setPiece(startX, startY, movingPiece);
        setPiece(endX, endY, targetPiece);

        return inCheck;
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    System.out.print(piece.getSymbol() + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
    /*
    public void testQueenMoves() {
        Piece queen = board[0][3];
        if (queen instanceof Queen) {
            List<Move> legalMoves = ((Queen) queen).getLegalMoves(this, 0, 3);
            for (Move move : legalMoves) {
                System.out.println("Legal Move: (" + move.startX + ", " + move.startY + ") to (" + move.endX + ", " + move.endY + ")");
            }
        }
    }
    public void testRookMoves() {
        Piece rook = board[0][0];
        if (rook instanceof Rook) {
            List<Move> legalMoves = ((Rook) rook).getLegalMoves(this, 0, 0);
            for (Move move : legalMoves) {
                System.out.println("Dobry ruch: (" + move.startX + ", " + move.startY + ") to (" + move.endX + ", " + move.endY + ")");
            }
        }
    }
    public void testKnightMoves() {
        Piece knight = board[0][1];
        if (knight instanceof Knight) {
            List<Move> legalMoves = ((Knight) knight).getLegalMoves(this, 0, 1);
            for (Move move : legalMoves) {
                System.out.println("Dobry ruch: (" + move.startX + ", " + move.startY + ") to (" + move.endX + ", " + move.endY + ")");
            }
        }
    }
    public void testBishopMoves() {
        Piece bishop = board[0][2];
        if (bishop instanceof Bishop) {
            List<Move> legalMoves = ((Bishop) bishop).getLegalMoves(this, 0, 2);
            for (Move move : legalMoves) {
                System.out.println("Dobry ruch: (" + move.startX + ", " + move.startY + ") to (" + move.endX + ", " + move.endY + ")");
            }
        }
    }
    public void testKingMoves() {
        Piece king = board[0][4];
        if (king instanceof King) {
            List<Move> legalMoves = ((King) king).getLegalMoves(this, 0, 4);
            for (Move move : legalMoves) {
                System.out.println("Dobry ruch: (" + move.startX + ", " + move.startY + ") to (" + move.endX + ", " + move.endY + ")");
            }
        }
    }
    public void testPawnMoves() {
        Piece pawn = board[1][4];
        if (pawn instanceof Pawn) {
            List<Move> legalMoves = ((Pawn) pawn).getLegalMoves(this, 1, 4);
            for (Move move : legalMoves) {
                System.out.println("Dobry ruch: (" + move.startX + ", " + move.startY + ") to (" + move.endX + ", " + move.endY + ")");
            }
        }
    } */

    // do ai potrzebne rzeczy
    public List<Move> getAllLegalMoves(boolean isWhite) {
        List<Move> allLegalMoves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.isWhite == isWhite) {
                    allLegalMoves.addAll(piece.getLegalMoves(this, i, j));
                }
            }
        }
        return allLegalMoves;
    }

    public void makeAIMove(boolean isWhite) {
        List<Move> legalMoves = getAllLegalMoves(isWhite);
        boolean castleON = false;
        boolean castleCheck = false;
        Move tempMove = null;

        if(!legalMoves.isEmpty()) {
            Move bestMove = null;
            int bestValue = isWhite ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for (Move move : legalMoves) {
                //System.out.println("Legalny ruch (startX: " + move.startX + " startY: " + move.startY + ") (endX:" + move.endX + " endY: " + move.endY + ")");
                makeMove(move);
                int boardValue = minimax(3, Integer.MIN_VALUE, Integer.MAX_VALUE,!isWhite);
                undoMove(move);
                castleCheck = (move.endX == 7 || move.endX == 0) && (move.endY == 2 || move.endY == 6);
                if (isWhite && boardValue > bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                    tempMove = move;

                } else if(!isWhite && boardValue < bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                    tempMove = move;
                }

            }
            if(castleCheck) {
                if(tempMove != null) {
                    Piece tempPieceKing = getPiece(tempMove.startX, tempMove.startY);
                    if (tempPieceKing != null && tempPieceKing.pieceType == Piece.PieceType.KING && !((King) tempPieceKing).castle && !tempPieceKing.hasMoved) {
                        castleON = true;
                    }
                }
            }

            Random rand = new Random();
            Move move = legalMoves.get(rand.nextInt(legalMoves.size()));
            if(bestMove == null) bestMove = move;
            Piece tempPiece = getPiece(bestMove.startX, bestMove.startY);
            if(tempPiece!=null) {
                String tempName = switch (tempPiece.pieceType) {
                    case PAWN -> "Pawn";
                    case KNIGHT -> "Knight";
                    case KING -> "King";
                    case ROOK -> "Rook";
                    case QUEEN -> "Queen";
                    case BISHOP -> "Bishop";
                };
                movePiece(bestMove.startX, bestMove.startY, bestMove.endX, bestMove.endY, true);
                if (tempPiece.pieceType == Piece.PieceType.PAWN && (bestMove.endX == 7 || bestMove.endX == 0)) {
                    Piece promotedPiece = getBestPromotionPiece(isWhite);
                    board[bestMove.endX][bestMove.endY] = promotedPiece;
                    ChessGameGUI.updateBoard(bestMove.startX, bestMove.startY, bestMove.endX, bestMove.endY, "Queen", isWhite);
                    SoundManager.playSound("promote.wav");
                    AIpromoted = true;
                } else if (tempPiece.pieceType == Piece.PieceType.KING && (!((King) tempPiece).castle && (bestMove.endY == 2 &&
                        (bestMove.endX == 0 || bestMove.endX == 7)) || (bestMove.endY == 6 && (bestMove.endX == 0 || bestMove.endX == 7))) && castleON);
                else
                    ChessGameGUI.updateBoard(bestMove.startX, bestMove.startY, bestMove.endX, bestMove.endY, tempName, isWhite);
            }
        }
    }

    public int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
        if(depth == 0) {
            return evaluateBoard();
        }

        if(isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Move> legalMoves = getAllLegalMoves(true);
            for (Move move : legalMoves) {
                makeMove(move);
                int eval = minimax(depth - 1, alpha, beta, false);
                undoMove(move);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if(beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Move> legalMoves = getAllLegalMoves(false);
            for (Move move : legalMoves) {
                makeMove(move);
                int eval = minimax(depth - 1, alpha, beta, true);
                undoMove(move);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if(beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    public int evaluateBoard() {
        int score = 0;

        boolean isEndGame = findBothQueens();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    int index = i*8+j;
                    score += pieceValue(piece);

                    switch (piece.pieceType) {
                        case PAWN:
                            score += piece.isWhite ? WHITE_PAWN_TABLE[index] : BLACK_PAWN_TABLE[index];
                            break;
                        case KNIGHT:
                            score += piece.isWhite ? WHITE_KNIGHT_TABLE[index] : BLACK_KNIGHT_TABLE[index];
                            break;
                        case BISHOP:
                            score += piece.isWhite ? WHITE_BISHOP_TABLE[index] : BLACK_BISHOP_TABLE[index];
                            break;
                        case ROOK:
                            score += piece.isWhite ? WHITE_ROOK_TABLE[index] : BLACK_ROOK_TABLE[index];
                            break;
                        case QUEEN:
                            score += piece.isWhite ? WHITE_QUEEN_TABLE[index] : -BLACK_QUEEN_TABLE[index];
                            break;
                        case KING:
                            score += piece.isWhite
                                    ? (isEndGame ? WHITE_KING_ENDGAME_TABLE[index] : WHITE_KING_TABLE[index])
                                    : (isEndGame ? BLACK_KING_ENDGAME_TABLE[index] : BLACK_KING_TABLE[index]);
                            break;
                    }
                }
            }
        }
        return score;
    }

    private boolean findBothQueens()
    {
        Piece queen1 = null;
        Piece queen2 = null;
        int tempCounter = 0;

        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                Piece temp = board[i][j];
                if(temp != null) {
                    if (temp.pieceType == Piece.PieceType.QUEEN && temp.isWhite) queen1 = temp;
                    else if (temp.pieceType == Piece.PieceType.QUEEN) queen2 = temp;
                    tempCounter++;
                }
            }
        }

        if(tempCounter <= 15) return true;
        else if(queen1 != null && queen2 != null) return false;

        return true;
    }


    private static final int[] BLACK_PAWN_TABLE = new int[] {
            -80,-80,-80,-80,-80,-80,-80,-80,
            -50,-50,-50,-50,-50,-50,-50,-50,
            -10,-10,-20,-30,-30,-20,-10,-10,
             5,   5, 10,-15,-15, 10,  5, -5,
             0,   0,  0,-35,-35,  0,  0,  0,
            -5,   5, 10,-10,-10, 10,  5, -5,
            -5, -10,-10, 40, 40,-10,-10, -5,
             0,   0,  0,  0,  0,  0,  0,  0
    };

    private static final int[] WHITE_PAWN_TABLE = new int[] {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10,-40,-40, 10, 10,  5,
            5, -5,-10, 10, 10,-10, -5,  5,
            0,  0,  0, 35, 35,  0,  0,  0,
            5,  5, 10, 15, 15, 10,  5,  5,
            10, 10, 20, 35, 35, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            80, 80, 80, 80, 80, 80, 80, 80
    };

    private static final int[] BLACK_KING_TABLE = new int[] {
             30, 40, 40, 50, 50, 40, 40, 30,
             30, 40, 40, 50, 50, 40, 40, 30,
             30, 40, 40, 50, 50, 40, 40, 30,
             30, 40, 40, 50, 50, 40, 40, 30,
             20, 30, 30, 40, 40, 30, 30, 20,
             10, 20, 20, 20, 20, 20, 20, 10,
            -20,-20, 50, 50, 50, 50,-20,-20,
            -20,-25,-45, 10,  0, 10,-45,-20
    };

    private static final int[] WHITE_KING_TABLE = new int[] {
            20, 25,  45,  0,  0, 10, 45, 20,
            20, 20, -50,-50,-50,-50, 20, 20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
    };

    private static final int[] BLACK_KING_ENDGAME_TABLE = new int[] {
            50,  40,  30,  20,  20,  30,  40,  50,
            30,  20,  10,   0,   0,  10,  20,  30,
            30,  10, -20, -30, -30, -20,  10,  30,
            30,  10, -30, -40, -40, -30,  10,  30,
            30,  10, -30, -40, -40, -30,  10,  30,
            30,  10, -20, -30, -30, -20,  10,  30,
            30,  30,   0,   0,   0,   0,  30,  30,
            50,  30,  30,  30,  30,  30,  30,  50,
    };


    private static final int[] WHITE_KING_ENDGAME_TABLE = new int[] {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -50,-40,-30,-20,-20,-30,-40,-50,
    };

    private static final int[] BLACK_QUEEN_TABLE = new int[] {
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10,   0,   0,  0,  0,   5,   0, -10,
            -10,   0,   5,  5,  5,   5,   5, -10,
            -5,    0,   5,  5,  5,   5,   0,  -5,
            -5,    0,   5,  5,  5,   5,   0,  -5,
            -10,   5,   5,  5,  5,   5,   0, -10,
            -10,   0,   5,  0,  0,   0,   0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20
    };

    private static final int[] WHITE_QUEEN_TABLE = new int[] {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
             0,   0,  5,  5,  5,  5,  0, -5,
            -5,   0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20,
    };

    private static final int[] WHITE_BISHOP_TABLE = new int[] {
            -20, -20, -30, -10, -10, -30, -10, -20,
            -10,   5,   0,   0,   0,   0,   5, -10,
            -10,  10,  10,  10,  10,  10,  10, -10,
            -10,   0,  10,  10,  10,  10,   0, -10,
            -10,   5,   5,  10,  10,   5,   5, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -20, -10, -10, -10, -10, -10, -10, -20
    };

    private static final int[] BLACK_BISHOP_TABLE = new int[] {
            20, 10, 10, 10, 10, 10, 10, 20,
            10,  0,  0,  0,  0,  0,  0, 10,
            10,  0, -5,-10,-10, -5,  0, 10,
            10, -5, -5,-10,-10, -5, -5, 10,
            10,  0,-10,-10,-10,-10,  0, 10,
            10,-10,-10,-10,-10,-10,-10, 10,
            10, -5,  0,  0,  0,  0, -5, 10,
            20, 10, 30, 10, 10, 30, 10, 20
    };

    private static final int[] BLACK_ROOK_TABLE = new int[] {
            -10,-10,-10, -5, -5,-10,-10,-10,
             -5,-10,-10,-10,-10,-10,-10, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
              0,  0,  0, -5, -5,  0,  0,  0
    };

    private static final int[] WHITE_ROOK_TABLE = new int[] {
             0,  0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
             5, 10, 10, 10, 10, 10, 10,  5,
            10, 10, 10, 10, 10, 10, 10, 10,
    };

    private static final int[] BLACK_KNIGHT_TABLE = new int[] {
            50, 40, 30, 30, 30, 30, 40, 50,
            40, 20,  0,  0,  0,  0, 20, 40,
            30,  0,-10,-15,-15,-10,  0, 30,
            30, -5,-15,-20,-20,-15, -5, 30,
            30,  0,-15,-20,-20,-15,  0, 30,
            30, -5,-10,-15,-15,-10, -5, 30,
            40,-20,  0,  5,  5,  0,-20, 40,
            50, 30, 30, 30, 30, 30, 30, 50
    };

    private static final int[] WHITE_KNIGHT_TABLE = new int[] {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };

    private int pieceValue(Piece piece) {
        int value = 0;
        switch(piece.pieceType) {
            case KING -> value=20000;
            case QUEEN -> value = 900;
            case ROOK -> value = 500;
            case BISHOP, KNIGHT -> value = 300;
            case PAWN -> value = 100;
        }
        return piece.isWhite ? value : -value; // biale + / czarne -
    }
    private Piece getBestPromotionPiece(boolean isWhite) {
        return new Queen(isWhite);
    }
}
