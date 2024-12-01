package com.chess;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChessTest {

    @Test
    void legalMovesGui() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        assertNotNull(gui);

        ChessGameGUI.chessBoard.clearBoard();
        ChessGameGUI.chessBoard.whiteTurn = true;

        ChessGameGUI.chessBoard.setPiece(0,1, new King(true));
        ChessGameGUI.boardSquares[0][1].setIcon(new ImageIcon("pieces/whiteKing.png"));

        assertNotNull(ChessGameGUI.boardSquares[0][1].getIcon());

        ChessGameGUI.chessBoard.setPiece(4,4, new Queen(true));
        ChessGameGUI.chessBoard.setPiece(4,5, new Queen(false));

        ChessGameGUI.boardSquares[4][4].setIcon(new ImageIcon("pieces/whiteQueen.png"));
        ChessGameGUI.boardSquares[4][5].setIcon(new ImageIcon("pieces/blackQueen.png"));

        assertNotNull(ChessGameGUI.boardSquares[4][5].getIcon());

        ChessGameGUI.chessBoard.setPiece(4,3, new Pawn(false));
        ChessGameGUI.boardSquares[4][3].setIcon(new ImageIcon("pieces/blackPawn.png"));

        assertNotNull(ChessGameGUI.boardSquares[4][3].getIcon());

        ChessGameGUI.chessBoard.setPiece(3,3, new Bishop(false));
        ChessGameGUI.boardSquares[3][3].setIcon(new ImageIcon("pieces/blackBishop.png"));

        assertNotNull(ChessGameGUI.boardSquares[3][3].getIcon());

        ChessGameGUI.chessBoard.setPiece(3,5, new Knight(false));
        ChessGameGUI.boardSquares[3][5].setIcon(new ImageIcon("pieces/blackKnight.png"));

        assertNotNull(ChessGameGUI.boardSquares[3][5].getIcon());

        ChessGameGUI.chessBoard.setPiece(3,4, new Rook(false));
        ChessGameGUI.boardSquares[3][4].setIcon(new ImageIcon("pieces/blackRook.png"));

        assertNotNull(ChessGameGUI.boardSquares[3][4].getIcon());

        ChessGameGUI.boardSquares[4][4].doClick();
        ChessGameGUI.boardSquares[4][5].doClick();
        //ChessGameGUI.showLegalMoves();
    }

    @Test
    void hasMoved() {
        King king = new King(false);

        assertFalse(king.hasMoved, "Powinno byc na false");
    }

    @Test
    void chessBoardState() {
        ChessBoard board = new ChessBoard();

        assertNotNull(board.generateBoardState());
    }

    @Test
    void canCastleQueenSide() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();

        King king = new King(true);
        Rook rook = new Rook(true);

        board.setPiece(0, 4, king);
        board.setPiece(0, 0, rook);

        assertTrue(board.canCastleQueenSide(true), "Powinien moc wykonac roszade");
    }

    @Test
    void canCastleKingSide() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        King king = new King(true);
        Rook rook = new Rook(true);

        board.setPiece(0, 4, king);
        board.setPiece(0, 7, rook);

        assertTrue(board.canCastleKingSide(true), "Powinien moc wykonac roszade");
    }

    @Test
    void isWhiteTurn() {
        ChessBoard board = new ChessBoard();

        assertTrue(board.isWhiteTurn(), "Powinno byc true");
    }

    @Test
    void getAllLegalMoves() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();

        Rook rook = new Rook(true);
        board.setPiece(0, 4, rook);

        List<Move> actualMoves = rook.getLegalMoves(board, 0, 4);

        assertEquals(board.getAllLegalMoves(true), actualMoves, "Ruchy powinny sie zgadzac.");
    }

    @Test
    void getGamePanel() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, false);
        JPanel panel = gui.getGamePanel();

        assertNotNull(panel, "Panel powininen byc rozny od null");
    }

    @Test
    void getGamePanelWhite() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        JPanel panel = gui.getGamePanel();

        assertNotNull(panel, "Panel powininen byc rozny od null");
    }

    @Test
    void aiMove() {
        ChessBoard board = new ChessBoard();
        board.makeAIMove(true);
    }

    @Test
    void evaluateBoard() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();

        Rook rook = new Rook(true);
        board.setPiece(0, 4, rook);

        int score = board.evaluateBoard();

        assertEquals(board.evaluateBoard(), score, "Wynik nie powinine sie roznic.");
    }

    @Test
    void minimax() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();

        Rook rook = new Rook(true);
        board.setPiece(0, 4, rook);
        Rook rook1 = new Rook(false);
        board.setPiece(6, 4, rook1);

        int eval = board.minimax(3,Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        assertEquals(board.minimax(3,Integer.MIN_VALUE, Integer.MAX_VALUE, true), eval, "Evaluation nie powinno sie roznic");
    }

    @Test
    void isCheckmate() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        King king = new King(true);
        Rook rook = new Rook(false);
        Rook rook1 = new Rook(false);

        board.setPiece(0, 4, king);
        board.setPiece(0, 7, rook);
        board.setPiece(1, 7, rook1);

        assertTrue(board.isCheckmate(true), "Powinien byc mat");
    }

    @Test
    void isInCheck() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        King king = new King(true);
        Rook rook = new Rook(false);

        board.setPiece(0, 4, king);
        board.setPiece(0, 7, rook);

        assertTrue(board.isInCheck(true), "Powinien byc w szachu");
    }

    @Test
    void isStalemate() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();

        King king = new King(true);

        King king1 = new King(false);
        Queen queen = new Queen(false);

        board.setPiece(0, 7, king);
        board.setPiece(7, 0, king1);
        board.setPiece(1, 5, queen);

        assertTrue(board.isStalemate(true), "Powinien byc stalemate");
    }

    @Test
    void setMoved() {
        King king = new King(false);

        king.setMoved();

        assertTrue(king.hasMoved, "Powinno byc na true");
    }

    @Test
    void getLegalMoves() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        Piece knight = new Knight(true);
        int x = 4, y = 4;
        board.setPiece(x, y, knight);

        List<Move> actualMoves = knight.getLegalMoves(board, x, y);

        List<Move> expectedMoves = List.of(
                new Move(4, 4, 6, 5, false), new Move(4, 4, 6, 3, false),
                new Move(4, 4, 2, 5, false), new Move(4, 4, 2, 3, false),
                new Move(4, 4, 5, 6, false), new Move(4, 4, 5, 2, false),
                new Move(4, 4, 3, 6, false), new Move(4, 4, 3, 2, false)
        );

        assertEquals(expectedMoves, actualMoves, "Legalne ruchy np dla skoczka powinny sie zgadzac z oczekiwanymi");
    }

    @Test
    void getSymbol() {
        Piece whitePiece = new Pawn(true);

        String symbol = whitePiece.getSymbol();

        assertEquals("P", symbol, "Symbol powinien byc 'P'");
    }

    @Test
    void setPiece() {
        ChessBoard board = new ChessBoard();
        Knight knight = new Knight(false);

        board.setPiece(5, 5, knight);

        assertEquals(board.getPiece(5,5), knight, "Powininen sie znajdowac skoczek na 5,5");
    }

    @Test
    void makeMove() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        Pawn pawn = new Pawn(true);
        board.setPiece(5,4,pawn);

        List<Move> actualMoves = pawn.getLegalMoves(board, 5, 4);

        for(Move move : actualMoves) {
            board.makeMove(move);
        }

        assertEquals(board.getPiece(6,4), pawn, "Te figury powinny byc takie same.");
    }

    @Test
    void pawnMoves() { //en passant bialy
        ChessBoard board = new ChessBoard();

        board.movePiece(1,1,3,1,false);
        board.movePiece(6,5,4,5,false);
        board.movePiece(3,1,4,1,false);
        board.movePiece(6,2,4,2,false);
        board.movePiece(4,1,5,2,false);

        assertNotNull(board.getPiece(5,2));
        assertTrue(board.getPiece(5,2).isWhite);
    }

    @Test
    void pawnMovesBlack() { //en passant czarny
        ChessBoard board = new ChessBoard();

        board.movePiece(1,1,3,1,false);
        board.movePiece(6,5,4,5,false);
        board.movePiece(3,1,4,1,false);
        board.movePiece(4,5,3,5,false);
        board.movePiece(1,6,3,6,false);
        board.movePiece(3,5,2,6,false);

        assertNotNull(board.getPiece(2,6));
        assertFalse(board.getPiece(2,6).isWhite);
    }

    @Test
    void undoMove() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        Pawn pawn = new Pawn(true);
        board.setPiece(5,4,pawn);

        List<Move> actualMoves = pawn.getLegalMoves(board, 5, 4);

        for(Move move : actualMoves) {
            board.makeMove(move);
            board.undoMove(move);
        }

        assertNull(board.getPiece(6, 4), "Nie powinno tam byc nic.");
    }

    @Test
    void getPreferredSize() {
        EvaluationBar evaluationBar = new EvaluationBar();

        Dimension pref = evaluationBar.getPreferredSize();

        assertEquals(evaluationBar.getPreferredSize(), pref, "Wymiar powininen byc taki sam.");
    }

    @Test
    void testMove() {
        Move move = new Move(0,0,1,1,false);

        assertEquals(1, move.getEndX());
        assertEquals(1, move.getEndY());

        assertFalse(move.isPromotion());

        assertEquals("startX: 0 startY: 0 endX: 1 endY: 1", move.toString());
    }

    @Test
    void testPrintBoard() {
        ChessBoard board = new ChessBoard();
        board.printBoard();
    }

    @Test
    void isPinned() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        Pawn pawn = new Pawn(true);
        King king = new King(true);
        Queen queen = new Queen(false);
        board.setPiece(5,4,pawn);
        board.setPiece(6,4,king);
        board.setPiece(3,4,queen);

        assertTrue(board.isPinned(pawn, 5, 4), "Powinna byc przypinowana do krola.");
    }
}