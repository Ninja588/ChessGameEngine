package com.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

/**
 * Klasa odpowiadajca za GUI podczas gry
 */
public class ChessGameGUI implements ChessBoard.PromotionListener {
    /**
     * Tablica przyciskow odwzorowywujaca szachownica 8x8
     */
    protected final JButton[][] boardSquares = new JButton[8][8];
    private JButton selectedSquare = null;
    private int selectedX = -1;
    private int selectedY = -1;
    /**
     * Zmienna szachownicy
     */
    protected final ChessBoard chessBoard = new ChessBoard(this);
    private final Timer gameCheckTimer;
    private final Timer moveTimer;
    private boolean playerIsWhite;
    private boolean aiEnabled;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private Color lightSquaresColor;
    private Color darkSquaresColor;

    private final EvaluationBar evaluationBar;

    private static final JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private static final JPanel labeledBoardPanel = new JPanel(new BorderLayout());
    private static final String WHITE = "white";
    private static final String BLACK = "black";
    private static final String KNIGHT = "Knight";
    private static final String BISHOP = "Bishop";
    private static final String QUEEN = "Queen";
    private static final String KROLOWKA = "Krolowka";
    private static final String PATH = "pieces/";
    private static final String ARIAL = "Arial";
    private static final String GAME_END = "game-end.wav";

    /**
     * Konstruktor GUI
     * @param aiEnabled odpowiada za to czy AI jest wlaczone
     * @param mainPanel glowny panel GUI
     * @param cardLayout glowny layout GUI
     * @param lightSquaresColor kolor jasnych pol szachownicy
     * @param darkSquaresColor kolor ciemnych pol szachownicy
     * @param playerIsWhite odpowiada za to czy gracz gra bialymi
     */
    public ChessGameGUI(boolean aiEnabled, JPanel mainPanel, CardLayout cardLayout, Color lightSquaresColor, Color darkSquaresColor, boolean playerIsWhite) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.lightSquaresColor = lightSquaresColor;
        this.darkSquaresColor = darkSquaresColor;

        chessBoard.setPromotionListener(this);
        chessBoard.resetBoard();

        this.playerIsWhite = playerIsWhite;

        resetGUIBoard();
        initializeBoard();

        evaluationBar = new EvaluationBar();

        this.aiEnabled = aiEnabled;

        flipBoard();

        gameCheckTimer = new Timer(100, e -> {
            if(!GraphicsEnvironment.isHeadless()) {
                checkGameConditions();
            }
            updateEvaluation();
        });
        gameCheckTimer.start();

        moveTimer = new Timer(150, e -> showLegalMoves());
        moveTimer.start();

        if(!playerIsWhite && aiEnabled) chessBoard.makeAIMove(true);

        SwingUtilities.invokeLater(() -> {
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    boardSquares[i][j].dispatchEvent(new MouseEvent(boardSquares[i][j],
                            MouseEvent.MOUSE_DRAGGED,
                            System.currentTimeMillis(),
                            0, 0, 0, 1, false));
                }
            }
        });
    }

    private void updateEvaluation() {
        double evaluation = (double)chessBoard.evaluateBoard()/1000;

        evaluationBar.setEvaluation(evaluation);
        evaluationBar.revalidate();
        evaluationBar.repaint();
    }

    private static JButton createButton() {
        JButton button = new JButton("Powrót");
        button.setFont(new Font(ARIAL, Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 63, 65));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    /**
     * Metoda tworzaca panel gry
     * @return panel gry
     */
    public JPanel getGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());

        // powrot do menu
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(200,200));
        JButton backButton = createButton();
        backButton.addActionListener(e->returnToMenu());
        rightPanel.add(backButton);

        // evaluation
        JPanel evaluationPanel = new JPanel(new BorderLayout());
        evaluationPanel.add(evaluationBar, BorderLayout.CENTER);
        evaluationPanel.setPreferredSize(evaluationPanel.getPreferredSize());

        boardPanel.setPreferredSize(new Dimension(600, 600));

        gamePanel.add(evaluationPanel, BorderLayout.WEST);
        // text
        gamePanel.add(rightPanel, BorderLayout.EAST);
        // szachownica
        gamePanel.add(labeledBoardPanel, BorderLayout.CENTER);

        return gamePanel;
    }

    private static void resetGUIBoard() {
        boardPanel.removeAll();
        boardPanel.revalidate();
        boardPanel.repaint();

        labeledBoardPanel.removeAll();
        labeledBoardPanel.revalidate();
        labeledBoardPanel.repaint();
    }

    private void initializeBoard() {
        // etykiety A,B,C... 1,2,3...
        JPanel topLabels = new JPanel(new GridLayout(1, 9));
        if(playerIsWhite) {
            for(char c = 'A'; c <= 'H'; c++) {
                JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
                label.setFont(new Font(ARIAL, Font.BOLD, 15));
                topLabels.setBackground(new Color(255, 249, 192, 255));
                topLabels.add(label);
            }
            labeledBoardPanel.add(topLabels, BorderLayout.SOUTH);

            JPanel sideLabels = new JPanel(new GridLayout(8, 1));
            for(int i = 8; i >= 1; i--) {
                JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                label.setFont(new Font(ARIAL, Font.BOLD, 15));
                label.setPreferredSize(new Dimension(18, 10));
                sideLabels.setBackground(new Color(255, 249, 192, 255));
                sideLabels.add(label);
            }
            labeledBoardPanel.add(sideLabels, BorderLayout.EAST);
        } else {
            for(char c = 'H'; c >= 'A'; c--) {
                JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
                label.setFont(new Font(ARIAL, Font.BOLD, 15));
                topLabels.setBackground(new Color(255, 249, 192, 255));
                topLabels.add(label);
            }
            labeledBoardPanel.add(topLabels, BorderLayout.NORTH);

            JPanel sideLabels = new JPanel(new GridLayout(8, 1));
            for(int i = 1; i <= 8; i++) {
                JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                label.setFont(new Font(ARIAL, Font.BOLD, 15));
                label.setPreferredSize(new Dimension(18, 10));
                sideLabels.setBackground(new Color(255, 249, 192, 255));
                sideLabels.add(label);
            }
            labeledBoardPanel.add(sideLabels, BorderLayout.EAST);
        }

        initializeBoardSquares();
    }

    private void initializeBoardSquares() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                JButton square = new JButton();
                square.addActionListener(this::handleMove);
                square.setFocusPainted(false);
                if((i + j) % 2 == 0) {
                    square.setBackground(Objects.requireNonNullElseGet(darkSquaresColor, () -> new Color(118, 150, 86)));
                } else {
                    square.setBackground(Objects.requireNonNullElseGet(lightSquaresColor, () -> new Color(238, 238, 210)));
                }
                boardSquares[i][j] = square;
                gbc.gridx = i;
                gbc.gridy = j;
                boardPanel.add(square, gbc);
                addPiece(square, i, j);
            }
        }
        labeledBoardPanel.add(boardPanel, BorderLayout.CENTER);
    }

    private void addPiece(JButton square, int i, int j) {
        String color = (i < 2) ? WHITE : BLACK;
        String pieceName = "";

        if(i == 0 || i == 7) {
            if(j == 0 || j == 7) pieceName = "Rook";
            if(j == 1 || j == 6) pieceName = KNIGHT;
            if(j == 2 || j == 5) pieceName = BISHOP;
            if(j == 3) pieceName = QUEEN;
            if(j == 4) pieceName = "King";
        } else if(i == 1 || i == 6) {
            pieceName = "Pawn";
        }

        if(!pieceName.isEmpty()) {
            square.setIcon(new ImageIcon(PATH + color + pieceName + ".png"));
        }
    }

    private int tempX;
    private int tempY;
    private String tempS;
    private boolean tempIsWhite;
    private boolean player;

    private void checkGameConditions() {
        if(chessBoard.isCheckmate(true)) {
            SoundManager.playSound(GAME_END);
            JOptionPane.showMessageDialog(boardPanel, "Mat! Czarne wygrywaja!");
            returnToMenu();
        } else if(chessBoard.isCheckmate(false)) {
            SoundManager.playSound(GAME_END);
            JOptionPane.showMessageDialog(boardPanel, "Mat! Biale wygrywaja!");
            returnToMenu();
        } else if(chessBoard.isStalemate(true) || chessBoard.isStalemate(false)) {
            SoundManager.playSound(GAME_END);
            JOptionPane.showMessageDialog(boardPanel, "Remis!");
            returnToMenu();
        }
    }

    private void returnToMenu() {
        gameCheckTimer.stop();
        moveTimer.stop();

        cardLayout.show(mainPanel, "Main Menu");
    }

    private void showLegalMoves() {
        if(selectedX!=-1 && selectedY!=-1) {
            Piece selectedPiece = chessBoard.getPiece(selectedX, selectedY);
            String tempName;

            if(selectedPiece != null && chessBoard.getPiece(selectedX, selectedY).isWhite == playerIsWhite || (selectedPiece != null && !aiEnabled)) {
                List<Move> pieceLegalMoves = selectedPiece.getLegalMoves(chessBoard, selectedX, selectedY);
                for(Move move : pieceLegalMoves) {
                    if(chessBoard.getPiece(move.endX, move.endY) != null) {
                        tempName = switch (chessBoard.getPiece(move.endX, move.endY).pieceType) {
                            case PAWN -> "Pawn";
                            case KNIGHT -> KNIGHT;
                            case KING -> "King";
                            case ROOK -> "Rook";
                            case QUEEN -> QUEEN;
                            case BISHOP -> BISHOP;
                        };
                        boardSquares[move.endX][move.endY].setIcon(new ImageIcon("squareTexture/" + (chessBoard.getPiece(move.endX, move.endY).isWhite ? WHITE : BLACK) + tempName + "Attacked.png"));
                        continue;
                    }
                    boardSquares[move.endX][move.endY].setIcon(new ImageIcon("squareTexture/squareMove.png"));
                }
            }
        }
    }

    private void handleMove(ActionEvent e) {
        JButton clickedSquare = (JButton) e.getSource();

        if(selectedSquare == null) {
            if(clickedSquare.getIcon() != null) {
                selectedSquare = clickedSquare;
                for(int i = 0; i < 8; i++) {
                    for(int j = 0; j < 8; j++) {
                        if(boardSquares[i][j] == clickedSquare) {
                            selectedX = i;
                            selectedY = j;
                            break;
                        }
                    }
                }
            }
        } else {
            int targetX = -1;
            int targetY = -1;

            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if(boardSquares[i][j] == clickedSquare) {
                        targetX = i;
                        targetY = j;
                        break;
                    }
                }
            }

            Piece selectedPiece = chessBoard.getPiece(selectedX, selectedY);

            if(selectedPiece != null) {
                List<Move> pieceLegalMoves = selectedPiece.getLegalMoves(chessBoard, selectedX, selectedY);
                chessBoard.movePiece(selectedX, selectedY, targetX, targetY,false);

                if(chessBoard.ok) {
                    clickedSquare.setIcon(selectedSquare.getIcon());
                    selectedSquare.setIcon(null);
                    if(chessBoard.enpassantWhite) boardSquares[targetX-1][targetY].setIcon(null);
                    if(chessBoard.enpassantBlack) boardSquares[targetX+1][targetY].setIcon(null);

                    if(chessBoard.promoted) {
                        boardSquares[tempX][tempY].setIcon(null);
                        boardSquares[tempX][tempY].setIcon(new ImageIcon(PATH + (tempIsWhite ? WHITE : BLACK) + tempS + ".png"));
                    }
                }
                for(Move move : pieceLegalMoves) {
                    if(chessBoard.getPiece(move.endX, move.endY) != null) {
                        String tempName = switch (chessBoard.getPiece(move.endX, move.endY).pieceType) {
                            case PAWN -> "Pawn";
                            case KNIGHT -> KNIGHT;
                            case KING -> "King";
                            case ROOK -> "Rook";
                            case QUEEN -> QUEEN;
                            case BISHOP -> BISHOP;
                        };
                        boardSquares[move.endX][move.endY].setIcon(new ImageIcon(PATH + (chessBoard.getPiece(move.endX, move.endY).isWhite ? WHITE : BLACK) + tempName + ".png"));
                        continue;
                    }
                    boardSquares[move.endX][move.endY].setIcon(null);
                }
            }

            selectedSquare = null;
            selectedX = -1;
            selectedY = -1;
            player = false;
        }

        if(!chessBoard.isWhiteTurn() && !player && playerIsWhite && aiEnabled) { // czarny ai
            chessBoard.makeAIMove(false);
            player = true;
        } else if(chessBoard.isWhiteTurn() && !player && !playerIsWhite && aiEnabled) { // bialy ai
            chessBoard.makeAIMove(true);
            player = true;
        }
    }

    /**
     * Metoda aktualizujaca szachownice
     * @param x koordynat startowy x
     * @param y koordynat startowy y
     * @param endX koordynat koncowy x
     * @param endY koordynat koncowy y
     * @param name nazwa figury
     * @param isWhite kolor figury
     */
    public void updateBoard(int x, int y, int endX, int endY, String name, boolean isWhite) {
        boardSquares[x][y].setIcon(null);
        boardSquares[endX][endY].setIcon(new ImageIcon(PATH + (isWhite ? WHITE : BLACK) + name + ".png"));
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Aktualizuje szachownice tak aby krol i wieza znalazly sie na dobrych polach po roszadzie
     * @param kingMove ruch krola
     * @param rookMove ruch wiezy
     * @param isWhite kolor figur
     */
    public void updateCastlingUI(Move kingMove, Move rookMove, boolean isWhite) {
        boardSquares[kingMove.endX][kingMove.endY].setIcon(new ImageIcon(PATH + (isWhite ? WHITE : BLACK) + "King.png"));
        boardSquares[rookMove.endX][rookMove.endY].setIcon(new ImageIcon(PATH + (isWhite ? WHITE : BLACK) + "Rook.png"));
        boardSquares[kingMove.startX][kingMove.startY].setIcon(null);
        boardSquares[rookMove.startX][rookMove.startY].setIcon(null);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void flipBoard() {
        boardPanel.removeAll();

        if(playerIsWhite) {
            for(int i = 7; i >= 0; i--) {
                for(int j = 0; j < 8; j++) {
                    boardPanel.add(boardSquares[i][j]);
                }
            }
        } else {
            for(int i = 0; i < 8; i++) {
                for(int j = 7; j >= 0; j--) {
                    boardPanel.add(boardSquares[i][j]);
                }
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    @Override
    public void onPromotion(int x, int y, boolean isWhite) {
        String[] options = {KROLOWKA, "Wieza", "Goniec", "Skoczek"};
        String choice = (String) JOptionPane.showInputDialog(null, "Wybierz figure:", "Promocja piona",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if(choice == null) choice = KROLOWKA;

        Piece promotedPiece;
        tempX = x;
        tempY = y;
        switch(choice) {
            default:
            case KROLOWKA:
                promotedPiece = new Queen(isWhite);
                tempS = QUEEN;
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
            case "Wieza":
                promotedPiece = new Rook(isWhite);
                tempS = "Rook";
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
            case "Goniec":
                promotedPiece = new Bishop(isWhite);
                tempS = BISHOP;
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
            case "Skoczek":
                promotedPiece = new Knight(isWhite);
                tempS = KNIGHT;
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }
}