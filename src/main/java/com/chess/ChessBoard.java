package com.chess;

import java.security.SecureRandom;
import java.util.*;

/**
 * Klasa odpowiadajaca za logike na szachownicy
 */
public class ChessBoard {
    /**
     * Zmienna odpowiadajaca za sledzenie tury
     */
    protected boolean whiteTurn = true;
    /**
     * Tablica 8x8 przechowywujaca wszystkie bierki
     */
    private Piece[][] board;
    /**
     * Zmienna pomagajaca w promowaniu pionow na figury
     */
    private PromotionListener promotionListener;
    /**
     * Mapa pomagajca utrzymywac kontrole nad remisem po 3 powtorzonych pozycjach
     */
    private Map<String, Integer> boardStates = new HashMap<>();
    /**
     * Zmienna GUI
     */
    ChessGameGUI gui;

    /**
     * Metoda generujaca stan aktualnej szachownicy
     * @return Slowna reprezentacja szachownicy
     */
    public String generateBoardState() {
        StringBuilder boardState = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null) {
                    boardState.append(piece.getSymbol());
                } else {
                    boardState.append(".");
                }
            }
        }
        return boardState.toString();
    }

    /**
     * Czysci plansze ze wszystkich bierek
     */
    public void clearBoard() {
        for(int i = 0;i<8;i++) {
            for(int j=0;j<8;j++) {
                board[i][j] = null;
            }
        }
    }

    /**
     * Metoda zapisujaca do mapy stan szachownicy
     * @param isWhiteTurn parametr sprawdzajacy kogo jest aktualna tura (true - bialy, false - czarny)
     */
    public void recordBoardState(boolean isWhiteTurn) {
        String boardState = generateBoardState() + (isWhiteTurn ? "W" : "B");
        boardStates.put(boardState, boardStates.getOrDefault(boardState, 0) + 1);
    }

    /**
     * Metoda sprawdzajaca czy jest remis poprzez powtorzewnie pozycji 3 razy
     * @return true - jest remis, false - nie ma remisu
     */
    public boolean isThreefoldRepetition() {
        for(int count : boardStates.values()) {
            if(count >= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Konstruktor klasy odpowiadajacej za szachownice
     * @param gui GUI
     */
    public ChessBoard(ChessGameGUI gui) {
        this.gui = gui;
        resetBoard();
    }

    /**
     * Metoda resetujaca wszystko w klasie
     */
    public void resetBoard() {
        board = new Piece[8][8];
        capturedPieces = new ArrayDeque<>();
        boardStates = new HashMap<>();
        whiteTurn = true;

        initializeBoard();
    }

    /**
     * Metoda zwracaja kogo jest tura
     * @return true - jest tura bialych, false - jest tura czarnych
     */
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    /**
     * Metoda zmieniajaca ture
     */
    public void toggleTurn() {
        whiteTurn = !whiteTurn;
    }

    /**
     * Interfejs wspomagajacy awans piona na figury
     */
    public interface PromotionListener {
        /**
         * Deklaracja metody odpowiadajacej za awans piona
         * @param x koordynat x piona
         * @param y koordynat y piona
         * @param isWhite kolor piona
         */
        void onPromotion(int x, int y, boolean isWhite);
    }

    /**
     * Metoda ustawiajaca odpowiedni listener
     * @param listener listener awansu
     */
    public void setPromotionListener(PromotionListener listener) {
        this.promotionListener = listener;
    }

    /**
     * Metoda inicjalizujaca wszystkie figury na szachownicy
     */
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

    /**
     * Metoda wstawiajaca figure na dane pole
     * @param x koordynat x na ktorym ma zostac wstawiona figura
     * @param y koordynat y na ktorym ma zostac wstawiona figura
     * @param piece figura, ktora ma zostac wstawiona
     */
    public void setPiece(int x, int y, Piece piece)
    {
        board[x][y] = piece;
    }

    /**
     * Metoda zwracajaca figure z danego pola
     * @param x koordynat x na ktorym chcemy sprawdzic figure
     * @param y koordynat y na ktorym chcemy sprawdzic figure
     * @return figura na danym polu
     */
    public Piece getPiece(int x, int y) {
        return board[x][y];
    }

    /**
     * Metoda sprawdzajaca czy jest mozliwa roszada po stronie krola
     * @param isWhite kolor figury
     * @return true - da sie, false - nie da sie
     */
    public boolean canCastleKingSide(boolean isWhite) {
        int row = isWhite ? 0 : 7; // wybierz wiersz dla czarnego/bialego krola
        Piece king = board[row][4]; // krol jest na (row, 4)
        Piece rook = board[row][7]; // rook jest na (row, 7)

        // sprawdzanie czy krol/rook poruszyli sie
        if(king == null || rook == null || king.hasMoved() || rook.hasMoved()) {
            return false;
        }

        // sprawdzanie czy pola pomiedzy rookiem/krolem sa puste
        for(int i = 5; i < 7; i++) {
            if(board[row][i] != null) {
                return false; // jak tu dojdzie to znaczy ze jest cos miedzy nimi
            }
        }

        // sprawdzanie czy krol jest w szachu albo moglby dostac szacha podczas roszady
        return (!isInCheck(isWhite) && isInCheckAfterMove(isWhite, row, 5) && isInCheckAfterMove(isWhite, row, 6)) || (king.pieceType != Piece.PieceType.KING || ((King) king).castle);// wszystko git
    }

    /**
     * Metoda sprawdzajaca czy jest mozliwa roszada po stronie krolowki
     * @param isWhite kolor figury
     * @return true - da sie, false - nie da sie
     */
    public boolean canCastleQueenSide(boolean isWhite) {
        int row = isWhite ? 0 : 7; // wybierz wiersz dla czarnego/bialego krola
        Piece king = board[row][4]; // krol jest na (row, 4)
        Piece rook = board[row][0]; // rook jest na (row, 0)

        // sprawdzanie czy krol/rook poruszyli sie
        if(king == null || rook == null || king.hasMoved() || rook.hasMoved()) {
            return false;
        }

        // sprawdzanie czy pola pomiedzy rookiem/krolem sa puste
        for(int i = 1; i < 4; i++) {
            if(board[row][i] != null) {
                return false; // jak tu dojdzie to znaczy ze jest cos miedzy nimi
            }
        }

        // sprawdzanie czy krol jest w szachu albo moglby dostac szacha podczas roszady
        return (!isInCheck(isWhite) && isInCheckAfterMove(isWhite, row, 2) && isInCheckAfterMove(isWhite, row, 3)) || (king.pieceType != Piece.PieceType.KING || ((King) king).castle);// wszystko git
    }

    /**
     * Zmienna pomocnicza czy zostal odtworzony dzwiek szacha
     */
    private boolean checkSound = false;

    /**
     * Metoda sprawdzajaca czy krol jest w szachu
     * @param isWhite kolor figury
     * @return true - krol jest w szachu, false - krol nie jest w szachu
     */
    public boolean isInCheck(boolean isWhite) {
        int kingX = -1;
        int kingY = -1;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        if(kingX == -1) {
            return false;
        }

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.isWhite != isWhite) {
                    List<Move> legalMoves = piece.getLegalMoves(this, i, j);
                    for(Move move : legalMoves) {
                        if(move.endX == kingX && move.endY == kingY) {
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

    /**
     * Metoda pomocnicza sprawdzajaca czy dane pole jest atakowane przez jakas figure
     * @param x koordynat x pola, ktore sprawdzamy
     * @param y koordynat y pola, ktore sprawdzamy
     * @param byWhite kolor atakujacego
     * @return true - pole jest atakowane, false - pole nie jest atakowane
     */
    public boolean isSquareAttacked(int x, int y, boolean byWhite) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.isWhite == byWhite) {
                    if(piece.pieceType == Piece.PieceType.PAWN) {
                        // pion ataki
                        int direction = byWhite ? 1 : -1;
                        if((x == i + direction && (y == j + 1 || y == j - 1))) {
                            return true;
                        }
                    } else if(piece.pieceType == Piece.PieceType.KNIGHT) {
                        // skoczek ataki
                        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
                        for(int[] move : knightMoves) {
                            if(x == i + move[0] && y == j + move[1]) {
                                return true;
                            }
                        }
                    } else if(piece.pieceType == Piece.PieceType.ROOK) {
                        // rook ataki
                        if ((x == i || y == j) && isPathClear(i, j, x, y)) {
                            return true;
                        }
                    } else {
                        boolean bishopMoves = Math.abs(x - i) == Math.abs(y - j);
                        if(piece.pieceType == Piece.PieceType.BISHOP) {
                            // goniec ataki
                            if (bishopMoves && isPathClear(i, j, x, y)) {
                                return true;
                            }
                        } else if(piece.pieceType == Piece.PieceType.QUEEN) {
                            // krolowka ataki
                            if (((x == i || y == j) || bishopMoves) && isPathClear(i, j, x, y)) {
                                return true;
                            }
                        } else if(piece.pieceType == Piece.PieceType.KING) {
                            // krol ataki
                            int[][] kingMoves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                            for(int[] move : kingMoves) {
                                if(x == i + move[0] && y == j + move[1]) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // metoda sprawdzajaca czy sciezka jest wolna dla rook, goncow, krolowek

    /**
     * Metoda sprawdzajaca czy sciezka po ktorej poruszala by sie wieza, goniec lub krolowka jest wolna
     * @param startX koordynat startowy x
     * @param startY koordynat startowy y
     * @param endX koordynat koncowy x
     * @param endY koordynat koncowy y
     * @return true - droga jest wolna, false - nie jest
     */
    private boolean isPathClear(int startX, int startY, int endX, int endY) {
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);

        int x = startX + dx;
        int y = startY + dy;

        while(x != endX || y != endY) {
            if(board[x][y] != null) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    /**
     * Metoda sprawdzajaca czy jest mat na planszy
     * @param isWhite kolor dla ktorego bylby mat
     * @return true - jest mat, false - nie ma mata
     */
    public boolean isCheckmate(boolean isWhite) {
        if(!isInCheck(isWhite)) {
            return false; // nie w szachu
        }

        // sprawdzanie czy krol moze sie poruszyc
        int kingX = -1;
        int kingY = -1;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        if(kingX == -1) {
            return false;
        }

        List<Move> kingMoves = board[kingX][kingY].getLegalMoves(this, kingX, kingY);
        for(Move move : kingMoves) {
            makeMove(new Move(kingX, kingY, move.endX, move.endY,false));
            if(!isInCheck(isWhite)) {
                undoMove(new Move(kingX, kingY, move.endX, move.endY,false));
                return false; // krol moze sie poruszyc
            }
            undoMove(new Move(kingX, kingY, move.endX, move.endY,false));
        }

        // sprawdzanie czy cos moze zablokowac/zbic figure atakujaca
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.isWhite == isWhite) {
                    List<Move> legalMoves = piece.getLegalMoves(this, i, j);
                    for(Move move : legalMoves) {
                        makeMove(new Move(i, j, move.endX, move.endY,false));
                        if(!isInCheck(isWhite)) {
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

    /**
     * Metoda sprawdzajaca czy jest remis
     * @param isWhite kolor dla ktorego sie sprawdza
     * @return true - jest remis, false - nie ma
     */
    public boolean isStalemate(boolean isWhite) {
        if(isThreefoldRepetition()) {
            return true; // powtorzenie pozycji 3 razy (stalemate)
        }

        if(isInCheck(isWhite)) {
            return false; // w szachu, wiec nie moze byc stalemate
        }

        // sprawdzanie czy sa jakiekolwiek legalne ruchy dla wszystkich figur
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.isWhite == isWhite) {
                    List<Move> legalMoves = piece.getLegalMoves(this, i, j);
                    for(Move move : legalMoves) {
                        makeMove(new Move(i, j, move.endX, move.endY,false));
                        boolean check = isInCheck(isWhite);
                        undoMove(new Move(i, j, move.endX, move.endY,false));
                        if(!check) {
                            return false; // jest jakis
                        }
                    }
                }
            }
        }

        // brak ruchow, nie w szachu wiec stalemate
        return true;
    }

    /**
     * Kolekcja pomocnicza przechowywujaca figury
     */
    private Deque<Piece> capturedPieces = new ArrayDeque<>();

    /**
     * Metoda symulujaca ruch
     * @param move ruch do wykonania
     */
    public void makeMove(Move move) {
        Piece piece = board[move.startX][move.startY];
        Piece targetPiece = board[move.endX][move.endY];

        capturedPieces.push(targetPiece != null ? targetPiece : NullPiece.getInstance());

        board[move.endX][move.endY] = piece;
        board[move.startX][move.startY] = null;
    }

    /**
     * Metoda cofajaca ruch zasymulowany
     * @param move ruch do cofniecia
     */
    public void undoMove(Move move) {
        Piece piece = board[move.endX][move.endY];

        board[move.startX][move.startY] = piece;

        Piece capturedPiece = capturedPieces.pop();
        board[move.endX][move.endY] = capturedPiece instanceof NullPiece ? null : capturedPiece;
    }

    /**
     * Metoda sprawdzajaca czy krol bylby w szachu gdyby sie gdzies poruszyl
     * @param isWhite kolor krola
     * @param row pozycja x gdzie by sie poruszyl
     * @param col pozycja y gdzie by sie poruszyl
     * @return true - szach, false - brak szacha
     */
    public boolean isInCheckAfterMove(boolean isWhite, int row, int col) {
        int kingX = -1;
        int kingY = -1;

        // znajdowanie pozycji krola
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        // jak nie znaleziono krola (bron boze zeby tak bylo) to nie da sie
        if(kingX == -1) {
            return true;
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

        return !inCheck; // zwroci prawde jak krol bedzie w szachu a inaczej falsz
    }

    /**
     * Zmienna mowiaca czy ruch jest dobry
     */
    protected boolean ok; // zmienna czy ruch jest git
    /**
     * Zmienna pomocnicza do enpassant dla bialych
     */
    protected boolean enpassantWhite;
    /**
     * Zmienna pomocnicza do enpassant dla bialych
     */
    protected boolean enpassantBlack;
    /**
     * Zmienna sledzaca czy pion awansowal
     */
    protected boolean promoted = false;
    /**
     * Zmienna pomocnicza czy byl ruch piona
     */
    private boolean wasPawnMove = false;
    private static final String CAPTURE_SOUND = "capture.wav";

    /**
     * Metoda odpowiadajaca za ruch figur
     * @param startX pozycja startowa x
     * @param startY pozycja startowa y
     * @param endX pozycja koncowa x
     * @param endY pozycja koncowa y
     * @param isAiTurn parametr odpowiadajacy za to czy jest ruch AI
     */
    public void movePiece(int startX, int startY, int endX, int endY, boolean isAiTurn) {
        if(isCheckmate(true) || isCheckmate(false) || isStalemate(true) || isStalemate(false)) {
            return;
        }

        if(!wasPawnMove) {
            resetEnPassant();
        }

        Piece piece = board[startX][startY];
        enpassantWhite = false;
        enpassantBlack = false;
        ok = false;
        promoted = false;

        if(piece != null && whiteTurn == piece.isWhite) {
            // sprawdzanie czy na danym polu jest figura tego samego koloru
            Piece destinationPiece = board[endX][endY];
            if(destinationPiece == null || destinationPiece.isWhite != piece.isWhite) {
                // sprawdzanie czy ruch ktory sie wybralo jest w liscie legalnych ruchow figury
                List<Move> legalMoves = piece.getLegalMoves(this, startX, startY);
                boolean isValidMove = legalMoves.stream().anyMatch(move -> move.endX == endX && move.endY == endY);

                if(isValidMove) {
                    if(piece.pieceType == Piece.PieceType.KING && !((King) piece).castle && (canCastleKingSide(piece.isWhite) || canCastleQueenSide(piece.isWhite))) {
                        // roszada krola, krolowki
                        if(endY == 6 && (endX == 0 || endX == 7)) {
                            board[endX][endY] = piece;
                            board[startX][startY] = null;

                            Piece rook = board[endX][7];
                            board[endX][5] = rook;
                            board[endX][7] = null;

                            piece.setMoved();
                            rook.setMoved();
                            Move kingMove = new Move(startX,startY,endX,endY,false);
                            Move rookMove = new Move(endX, 7, endX, 5, false);
                            gui.updateCastlingUI(kingMove, rookMove, piece.isWhite);
                            SoundManager.playSound("castle.wav");
                            toggleTurn();
                            recordBoardState(isWhiteTurn());
                            ((King) piece).castle = true;
                            return;
                        } else if(endY == 2 && (endX == 0 || endX == 7)) { //
                            board[endX][endY] = piece;
                            board[startX][startY] = null;

                            Piece rook = board[endX][0];
                            board[endX][3] = rook;
                            board[endX][0] = null;

                            piece.setMoved();
                            rook.setMoved();

                            Move kingMove = new Move(startX,startY,endX,endY,false);
                            Move rookMove = new Move(endX, 0, endX, 3, false);
                            gui.updateCastlingUI(kingMove, rookMove, piece.isWhite);
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
                    if(destinationPiece != null) {
                        SoundManager.playSound(CAPTURE_SOUND);
                    }
                    // piony mechaniki (prosze zabijcie tego co wymyslic en passant)
                    if(piece.pieceType == Piece.PieceType.PAWN) {
                        wasPawnMove = true;
                        // en passant
                        if(Math.abs(endX - startX) == 2) {
                            ((Pawn) piece).hasMovedTwoSquares = true;
                        } else {
                            resetEnPassant();
                        }
                        if(((Pawn) piece).enPassant) {
                            if(piece.isWhite) {
                                board[endX - 1][endY] = null;
                                enpassantWhite = true;
                                if(board[startX+1][startY] == null) SoundManager.playSound(CAPTURE_SOUND);
                            } else {
                                board[endX + 1][endY] = null;
                                enpassantBlack = true;
                                if(board[startX-1][startY] == null) SoundManager.playSound(CAPTURE_SOUND);
                            }
                        }
                        // promo piona na inne figury
                        if ((endX == 7 || endX == 0) && promotionListener != null && !isAiTurn) {
                            promotionListener.onPromotion(endX, endY, piece.isWhite);
                            SoundManager.playSound("promote.wav");
                            promoted = true;
                        }
                        SoundManager.playSound("move.wav");
                        recordBoardState(isWhiteTurn());
                        toggleTurn();

                        return;
                    }
                    SoundManager.playSound("move.wav");
                    recordBoardState(isWhiteTurn());
                    toggleTurn();
                    wasPawnMove = false;
                } else {
                    ok = false;
                }
            }
        }
    }
    /**
     * Funkcja sprawdzajaca pola po stronie króla (wspomaga roszade króla)
     * @param isWhite kolor figury (true bialy, false czarny)
     * @param y pozycja y na planszy
     * @param piece figura dla której jest sprawdzane
     * @return true jesli jest wolne, false jesli nie
     */
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

    /**
     * Funkcja sprawdzajaca pola po stronie królówki (wspomaga roszade króla)
     * @param isWhite kolor figury (true bialy, false czarny)
     * @param y pozycja y na planszy
     * @param piece figura dla której jest sprawdzane
     * @return true jesli jest wolne, false jesli nie
     */
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

    /**
     * Funkcja resetujaca flage piona który poruszył się dwa pola
     */
    private void resetEnPassant() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece!=null && piece.pieceType == Piece.PieceType.PAWN) {
                    ((Pawn) piece).hasMovedTwoSquares = false;
                }
            }
        }
    }

    /**
     * Funkcja sprawdzająca czy figura jest spinowana do króla
     * @param piece figura którą się sprawdza
     * @param x koordynat x gdzie znajduje sie figura
     * @param y koordynat y gdzie znajduje sie figura
     * @return true jesli jest przypinowana false w innym wypadku
     */
    public boolean isPinned(Piece piece, int x, int y) {
        int kingX = -1;
        int kingY = -1;

        // znalezenie pozycji krola
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null && board[i][j].pieceType == Piece.PieceType.KING && board[i][j].isWhite == piece.isWhite) {
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
        boolean isPinned;

        Piece tempPiecePawn1 = null;
        Piece tempPiecePawn2 = null;

        if(tempPiece != null && tempPiece.pieceType == Piece.PieceType.PAWN && x<8 && y<7 && x>0 && y>0) {
            tempPiecePawn1 = getPiece(x,y-1);
            tempPiecePawn2 = getPiece(x,y+1);
            if(tempPiecePawn1 != null && tempPiecePawn1.isWhite != tempPiece.isWhite && (tempPiecePawn1.pieceType == Piece.PieceType.PAWN && ((Pawn) tempPiecePawn1).hasMovedTwoSquares)) {
                board[x][y-1] = null;
                ((Pawn) tempPiece).forward = false;
            }
            else if(tempPiecePawn2 != null && tempPiecePawn2.isWhite != tempPiece.isWhite && tempPiecePawn2.pieceType == Piece.PieceType.PAWN && ((Pawn) tempPiecePawn2).hasMovedTwoSquares) {
                board[x][y+1] = null;
                ((Pawn) tempPiece).forward = false;
            }
        }

        isPinned = isSquareAttacked(kingX, kingY, !piece.isWhite);

        if(tempPiece!=null && tempPiece.pieceType == Piece.PieceType.PAWN && !isPinned && !((Pawn) tempPiece).forcedEnPssant) ((Pawn) tempPiece).forward = true;

        // figura wraca na swoje miejsce
        board[x][y] = tempPiece;
        if(y-1>=0 && tempPiecePawn1!=null) board[x][y-1] = tempPiecePawn1;
        if(y+1<8 && tempPiecePawn2!=null) board[x][y+1] = tempPiecePawn2;

        return isPinned;
    }

    /**
     * Metoda sprawdzajaca czy enpassant jest wymuszone
     * @param startX pozycja startowa x piona
     * @param startY pozycja startowa y piona
     * @param endX pozycja koncowa x piona
     * @param endY pozycja koncowa y piona
     * @param isWhite kolor piona
     * @return true - jest wymuszony, false - nie jest
     */
    protected boolean isEnPassantForced(int startX, int startY, int endX, int endY, boolean isWhite) {
        Piece movingPawn = board[startX][startY];
        Piece targetPiece = board[endX][endY];

        board[endX][endY] = movingPawn;

        int kingX = -1;
        int kingY = -1;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == isWhite) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }

        boolean forced = isSquareAttacked(kingX, kingY, !isWhite);

        board[startX][startY] = movingPawn;
        board[endX][endY] = targetPiece;

        return forced;
    }

    /**
     * Metoda sprawdzajaca czy po ruchu figury, krol zostalby odkrutu na atak figury innego koloru
     * @param startX pozycja startowa x
     * @param startY pozycja startowa y
     * @param endX pozycja koncowa x
     * @param endY pozycja koncowa y
     * @return true - krol zostalby odkryty, false - nie zostalby
     */
    public boolean wouldExposeKing(int startX, int startY, int endX, int endY) {
        Piece movingPiece = board[startX][startY];
        Piece targetPiece = board[endX][endY];

        // chwilowo przenies figure
        setPiece(endX, endY, movingPiece);
        setPiece(startX, startY, null);

        // znajdz pozycje krola
        int kingX = -1;
        int kingY = -1;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.pieceType == Piece.PieceType.KING && piece.isWhite == movingPiece.isWhite) {
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

        return !inCheck;
    }

    // do ai potrzebne rzeczy

    /**
     * Metoda zwracajaca wszystkie mozliwe ruchy danego koloru
     * @param isWhite kolor do sprawdzenia
     * @return lista wszystkich mozliwych ruchow danego koloru
     */
    public List<Move> getAllLegalMoves(boolean isWhite) {
        List<Move> allLegalMoves = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null && piece.isWhite == isWhite) {
                    allLegalMoves.addAll(piece.getLegalMoves(this, i, j));
                }
            }
        }
        return allLegalMoves;
    }

    /**
     * Metoda odpowiadajaca za ruch AI
     * @param isWhite kolor ktorym sie porusza
     */
    public void makeAIMove(boolean isWhite) {
        List<Move> legalMoves = getAllLegalMoves(isWhite);

        if(!legalMoves.isEmpty()) {
            Move bestMove = null;
            int bestValue = isWhite ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for(Move move : legalMoves) {
                makeMove(move);
                int boardValue = minimax(3, Integer.MIN_VALUE, Integer.MAX_VALUE,!isWhite);
                undoMove(move);
                if(isWhite && boardValue > bestValue || !isWhite && boardValue < bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            }

            SecureRandom rand = new SecureRandom();
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
                if(tempPiece.pieceType == Piece.PieceType.PAWN && (bestMove.endX == 7 || bestMove.endX == 0)) {
                    Piece promotedPiece = getBestPromotionPiece(isWhite);
                    board[bestMove.endX][bestMove.endY] = promotedPiece;
                    gui.updateBoard(bestMove.startX, bestMove.startY, bestMove.endX, bestMove.endY, "Queen", isWhite);
                    SoundManager.playSound("promote.wav");
                }
                else
                    gui.updateBoard(bestMove.startX, bestMove.startY, bestMove.endX, bestMove.endY, tempName, isWhite);
            }
        }
    }

    /**
     * Metoda wybierajaca najlepszy mozliwy ruch o podanej glebokosci
     * @param depth glebokosc sprawdzania
     * @param alpha najmniejsza wartosc
     * @param beta najwieksza wartosc
     * @param isMaximizing dla kogo jest obliczany ruch
     * @return oszacowana wartosc
     */
    public int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
        if(depth == 0) {
            return evaluateBoard();
        }

        if(isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Move> legalMoves = getAllLegalMoves(true);
            for(Move move : legalMoves) {
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
            for(Move move : legalMoves) {
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

    /**
     * Metoda pomocnicza ktora oszacowywuje wartosc szachownicy na podstawie wartosci figur oraz tego na jakim miejscu sie znajduja
     * @return oszacowana wartosc szachownicy
     */
    public int evaluateBoard() {
        int score = 0;

        boolean isEndGame = isEndGame();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if(piece != null) {
                    int index = i*8+j;
                    score += pieceValue(piece);

                    switch(piece.pieceType) {
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
                            if (piece.isWhite) {
                                if (isEndGame) {
                                    score += WHITE_KING_ENDGAME_TABLE[index];
                                } else {
                                    score += WHITE_KING_TABLE[index];
                                }
                            } else {
                                if (isEndGame) {
                                    score += BLACK_KING_ENDGAME_TABLE[index];
                                } else {
                                    score += BLACK_KING_TABLE[index];
                                }
                            }
                            break;
                    }
                }
            }
        }
        return score;
    }

    /**
     * Metoda sprawdzajaca czy jest koncowa faza gry
     * @return true - jest, false - nie ma
     */
    private boolean isEndGame()
    {
        Piece queen1 = null;
        Piece queen2 = null;
        int tempCounter = 0;

        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                Piece temp = board[i][j];
                if(temp != null) {
                    if(temp.pieceType == Piece.PieceType.QUEEN && temp.isWhite) queen1 = temp;
                    else if(temp.pieceType == Piece.PieceType.QUEEN) queen2 = temp;
                    if(temp.pieceType != Piece.PieceType.PAWN && temp.pieceType != Piece.PieceType.KING) tempCounter++;
                }
            }
        }

        if(tempCounter <= 3) return true;
        else return queen1 == null && queen2 == null;
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
            -30,-40,-40,-50,-50,-40,-40,-30
    };

    private static final int[] BLACK_KING_ENDGAME_TABLE = new int[] {
            50,  40,  30,  20,  20,  30,  40,  50,
            30,  20,  10,   0,   0,  10,  20,  30,
            30,  10, -20, -30, -30, -20,  10,  30,
            30,  10, -30, -40, -40, -30,  10,  30,
            30,  10, -30, -40, -40, -30,  10,  30,
            30,  10, -20, -30, -30, -20,  10,  30,
            30,  30,   0,   0,   0,   0,  30,  30,
            50,  30,  30,  30,  30,  30,  30,  50
    };


    private static final int[] WHITE_KING_ENDGAME_TABLE = new int[] {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -50,-40,-30,-20,-20,-30,-40,-50
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
            -20,-10,-10, -5, -5,-10,-10,-20
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
            10, 10, 10, 10, 10, 10, 10, 10
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
            -50,-40,-30,-30,-30,-30,-40,-50
    };

    /**
     * Metoda zwracajaca wartosc podanej figury
     * @param piece figura dla ktorej sie sprawdza wartosc
     * @return wartosc figury, dla bialych wartosc jest na +, dla czarnych na -
     */
    private int pieceValue(Piece piece) {
        int value = 0;
        switch(piece.pieceType) {
            case KING -> value = 200000;
            case QUEEN -> value = 900;
            case ROOK -> value = 500;
            case BISHOP, KNIGHT -> value = 300;
            case PAWN -> value = 100;
        }
        return piece.isWhite ? value : -value; // biale + / czarne -
    }

    /**
     * Metoda zwracajaca najlepsza figure dla bota, ktory awansuje piona
     * @param isWhite kolor jakim gra bot
     * @return zwraca krolowke jako najlepsza figure do awansu
     */
    private Piece getBestPromotionPiece(boolean isWhite) {
        return new Queen(isWhite);
    }
}
