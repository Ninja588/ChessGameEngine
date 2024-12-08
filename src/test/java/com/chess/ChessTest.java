package com.chess;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChessTest {
    @Test
    void castlingUI() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        assertNotNull(gui);

        gui.chessBoard.clearBoard();
        gui.chessBoard.whiteTurn = true;

        King king = new King(true);
        Rook rook = new Rook(true);

        gui.chessBoard.setPiece(0, 4, king);
        gui.chessBoard.setPiece(0, 0, rook);
        Move kingMove = new Move(0,4,0,2,false);
        Move rookMove = new Move(0, 0, 0, 3, false);
        gui.updateCastlingUI(kingMove, rookMove,true);

        assertNotNull(gui.boardSquares[0][2]);
        assertNotNull(gui.boardSquares[0][3]);
    }

    @Test
    void legalMovesGui() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        assertNotNull(gui);

        gui.chessBoard.clearBoard();
        gui.chessBoard.whiteTurn = true;

        gui.boardSquares[4][4].doClick();
        gui.boardSquares[4][4].doClick();

        gui.chessBoard.setPiece(0,1, new King(true));
        gui.boardSquares[0][1].setIcon(new ImageIcon("pieces/whiteKing.png"));

        assertNotNull(gui.boardSquares[0][1].getIcon());

        gui.chessBoard.setPiece(4,4, new Queen(true));
        gui.chessBoard.setPiece(4,5, new Queen(false));

        gui.boardSquares[4][4].setIcon(new ImageIcon("pieces/whiteQueen.png"));
        gui.boardSquares[4][5].setIcon(new ImageIcon("pieces/blackQueen.png"));

        assertNotNull(gui.boardSquares[4][5].getIcon());

        gui.chessBoard.setPiece(4,3, new Pawn(false));
        gui.boardSquares[4][3].setIcon(new ImageIcon("pieces/blackPawn.png"));

        assertNotNull(gui.boardSquares[4][3].getIcon());

        gui.chessBoard.setPiece(3,3, new Bishop(false));
        gui.boardSquares[3][3].setIcon(new ImageIcon("pieces/blackBishop.png"));

        assertNotNull(gui.boardSquares[3][3].getIcon());

        gui.chessBoard.setPiece(3,5, new Knight(false));
        gui.boardSquares[3][5].setIcon(new ImageIcon("pieces/blackKnight.png"));

        assertNotNull(gui.boardSquares[3][5].getIcon());

        gui.chessBoard.setPiece(3,4, new Rook(false));
        gui.boardSquares[3][4].setIcon(new ImageIcon("pieces/blackRook.png"));

        assertNotNull(gui.boardSquares[3][4].getIcon());

        gui.boardSquares[4][4].doClick();
        gui.boardSquares[4][5].doClick();
    }

    @Test
    void hasMoved() {
        King king = new King(false);

        assertFalse(king.hasMoved, "Powinno byc na false");
    }

    @Test
    void chessBoardState() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);

        assertNotNull(board.generateBoardState());
    }

    @Test
    void canCastleQueenSide() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.clearBoard();

        King king = new King(true);
        Rook rook = new Rook(true);

        board.setPiece(0, 4, king);
        board.setPiece(0, 0, rook);

        assertTrue(board.canCastleQueenSide(true), "Powinien moc wykonac roszade");
    }

    @Test
    void promotionCheck() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.clearBoard();

        King king = new King(true);
        King kingBlack = new King(false);
        Pawn pawn = new Pawn(true);

        board.setPiece(5, 4, king);
        board.setPiece(2, 2, kingBlack);
        board.setPiece(6, 4, pawn);

        List<Move> actualMoves = pawn.getLegalMoves(board, 6, 4);

        assertNotNull(actualMoves);
    }

    @Test
    void promotionCheckBlack() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.clearBoard();

        King kingBlack = new King(false);
        King kingWhite = new King(true);
        Pawn pawnBlack = new Pawn(false);

        board.setPiece(2, 4, kingBlack);
        board.setPiece(7, 7, kingWhite);
        board.setPiece(1, 4, pawnBlack);

        List<Move> actualMoves2 = pawnBlack.getLegalMoves(board, 1, 4);

        assertNotNull(actualMoves2);
    }

    @Test
    void canCastleKingSide() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.clearBoard();
        King king = new King(true);
        Rook rook = new Rook(true);

        board.setPiece(0, 4, king);
        board.setPiece(0, 7, rook);

        assertTrue(board.canCastleKingSide(true), "Powinien moc wykonac roszade");
    }

    @Test
    void isWhiteTurn() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);

        assertTrue(board.isWhiteTurn(), "Powinno byc true");
    }

    @Test
    void getAllLegalMoves() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.makeAIMove(true);

        assertFalse(board.whiteTurn);
    }

    @Test
    void evaluateBoard() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.clearBoard();

        Rook rook = new Rook(true);
        board.setPiece(0, 4, rook);

        int score = board.evaluateBoard();

        assertEquals(board.evaluateBoard(), score, "Wynik nie powinine sie roznic.");
    }

    @Test
    void minimax() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        board.clearBoard();
        King king = new King(true);
        Rook rook = new Rook(false);

        board.setPiece(0, 4, king);
        board.setPiece(0, 7, rook);

        assertTrue(board.isInCheck(true), "Powinien byc w szachu");
    }

    @Test
    void isStalemate() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
        Knight knight = new Knight(false);

        board.setPiece(5, 5, knight);

        assertEquals(board.getPiece(5,5), knight, "Powininen sie znajdowac skoczek na 5,5");
    }

    @Test
    void makeMove() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);

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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);

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
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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
    void testMove() {
        Move move = new Move(0,0,1,1,false);

        assertEquals(1, move.getEndX());
        assertEquals(1, move.getEndY());

        assertFalse(move.isPromotion());

        assertEquals("startX: 0 startY: 0 endX: 1 endY: 1", move.toString());
    }

    @Test
    void isPinned() {
        ChessGameGUI gui = new ChessGameGUI(false, null, null, null, null, true);
        ChessBoard board = new ChessBoard(gui);
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