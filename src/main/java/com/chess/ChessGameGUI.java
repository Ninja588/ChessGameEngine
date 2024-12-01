package com.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class ChessGameGUI implements ChessBoard.PromotionListener {
    protected static final JButton[][] boardSquares = new JButton[8][8];
    private static final JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private static JButton selectedSquare = null;
    private static int selectedX = -1;
    private static int selectedY = -1;
    protected static final ChessBoard chessBoard = new ChessBoard();
    private final Timer gameCheckTimer;
    private final Timer moveTimer;
    protected static boolean playerIsWhite;
    private static boolean AIenabled;

    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    private static Color lightSquaresColor, darkSquaresColor;

    private final EvaluationBar evaluationBar;

    public ChessGameGUI(boolean AIenabled, JPanel mainPanel, CardLayout cardLayout, Color lightSquaresColor, Color darkSquaresColor, boolean playerIsWhite) {
        ChessGameGUI.cardLayout = cardLayout;
        ChessGameGUI.mainPanel = mainPanel;
        ChessGameGUI.lightSquaresColor = lightSquaresColor;
        ChessGameGUI.darkSquaresColor = darkSquaresColor;

        chessBoard.setPromotionListener(this);
        chessBoard.resetBoard();

        ChessGameGUI.playerIsWhite = playerIsWhite;

        resetGUIBoard();
        initializeBoard();

        evaluationBar = new EvaluationBar();

        ChessGameGUI.AIenabled = AIenabled;

        flipBoard();

        gameCheckTimer = new Timer(100, e -> {
            checkGameConditions();
            updateEvaluation();
        });
        gameCheckTimer.start();

        moveTimer = new Timer(150, e -> showLegalMoves());
        moveTimer.start();

        if(!playerIsWhite && AIenabled) chessBoard.makeAIMove(true);

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
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 63, 65));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

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
    }

    static final JPanel labeledBoardPanel = new JPanel(new BorderLayout());
    private static void initializeBoard() {
        // etykiety A,B,C... 1,2,3...
        JPanel topLabels = new JPanel(new GridLayout(1, 9));
        if(playerIsWhite) {
            for(char c = 'A'; c <= 'H'; c++) {
                JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 15));
                topLabels.setBackground(new Color(255, 249, 192, 255));
                topLabels.add(label);
            }
            labeledBoardPanel.add(topLabels, BorderLayout.SOUTH);

            JPanel sideLabels = new JPanel(new GridLayout(8, 1));
            for(int i = 8; i >= 1; i--) {
                JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 15));
                label.setPreferredSize(new Dimension(18, 10));
                sideLabels.setBackground(new Color(255, 249, 192, 255));
                sideLabels.add(label);
            }
            labeledBoardPanel.add(sideLabels, BorderLayout.EAST);
        } else {
            for(char c = 'H'; c >= 'A'; c--) {
                JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 15));
                topLabels.setBackground(new Color(255, 249, 192, 255));
                topLabels.add(label);
            }
            labeledBoardPanel.add(topLabels, BorderLayout.NORTH);

            JPanel sideLabels = new JPanel(new GridLayout(8, 1));
            for(int i = 1; i <= 8; i++) {
                JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 15));
                label.setPreferredSize(new Dimension(18, 10));
                sideLabels.setBackground(new Color(255, 249, 192, 255));
                sideLabels.add(label);
            }
            labeledBoardPanel.add(sideLabels, BorderLayout.EAST);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                JButton square = new JButton();
                square.addActionListener(ChessGameGUI::handleMove);
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

    private static void addPiece(JButton square, int i, int j) {
        String color = (i < 2) ? "white" : "black";
        String pieceName = "";

        if(i == 0 || i == 7) {
            if(j == 0 || j == 7) pieceName = "Rook";
            if(j == 1 || j == 6) pieceName = "Knight";
            if(j == 2 || j == 5) pieceName = "Bishop";
            if(j == 3) pieceName = "Queen";
            if(j == 4) pieceName = "King";
        } else if(i == 1 || i == 6) {
            pieceName = "Pawn";
        }

        if(!pieceName.isEmpty()) {
            square.setIcon(new ImageIcon("pieces/" + color + pieceName + ".png"));
        }
    }

    public static int tempX, tempY;
    public static String tempS;
    public static boolean tempIsWhite;
    public static boolean player = true;

    private void checkGameConditions() {
        if(chessBoard.isCheckmate(true)) {
            SoundManager.playSound("game-end.wav");
            JOptionPane.showMessageDialog(boardPanel, "Mat! Czarne wygrywaja!");
            returnToMenu();
        } else if(chessBoard.isCheckmate(false)) {
            SoundManager.playSound("game-end.wav");
            JOptionPane.showMessageDialog(boardPanel, "Mat! Biale wygrywaja!");
            returnToMenu();
        } else if(chessBoard.isStalemate(true) || chessBoard.isStalemate(false)) {
            SoundManager.playSound("game-end.wav");
            JOptionPane.showMessageDialog(boardPanel, "Remis!");
            returnToMenu();
        }
    }

    private void returnToMenu() {
        gameCheckTimer.stop();
        moveTimer.stop();

        cardLayout.show(mainPanel, "Main Menu");
    }

    protected static void showLegalMoves() {
        if(selectedX!=-1 && selectedY!=-1) {
            Piece selectedPiece = chessBoard.getPiece(selectedX, selectedY);
            String tempName;

            if(selectedPiece != null && chessBoard.getPiece(selectedX, selectedY).isWhite == playerIsWhite || (selectedPiece != null && !AIenabled)) {
                List<Move> pieceLegalMoves = selectedPiece.getLegalMoves(chessBoard, selectedX, selectedY);
                for(Move move : pieceLegalMoves) {
                    if(chessBoard.getPiece(move.endX, move.endY) != null) {
                        tempName = switch (chessBoard.getPiece(move.endX, move.endY).pieceType) {
                            case PAWN -> "Pawn";
                            case KNIGHT -> "Knight";
                            case KING -> "King";
                            case ROOK -> "Rook";
                            case QUEEN -> "Queen";
                            case BISHOP -> "Bishop";
                        };
                        boardSquares[move.endX][move.endY].setIcon(new ImageIcon("squareTexture/" + (chessBoard.getPiece(move.endX, move.endY).isWhite ? "white" : "black") + tempName + "Attacked.png"));
                        continue;
                    }
                    boardSquares[move.endX][move.endY].setIcon(new ImageIcon("squareTexture/squareMove.png"));
                }
            }
        }
    }

    private static void handleMove(ActionEvent e) {
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
                        boardSquares[tempX][tempY].setIcon(new ImageIcon("pieces/" + (tempIsWhite ? "white" : "black") + tempS + ".png"));
                    }
                }
                for(Move move : pieceLegalMoves) {
                    if(chessBoard.getPiece(move.endX, move.endY) == selectedPiece) continue;
                    if(chessBoard.getPiece(move.endX, move.endY) != null) {
                        String tempName = switch (chessBoard.getPiece(move.endX, move.endY).pieceType) {
                            case PAWN -> "Pawn";
                            case KNIGHT -> "Knight";
                            case KING -> "King";
                            case ROOK -> "Rook";
                            case QUEEN -> "Queen";
                            case BISHOP -> "Bishop";
                        };
                        boardSquares[move.endX][move.endY].setIcon(new ImageIcon("pieces/" + (chessBoard.getPiece(move.endX, move.endY).isWhite ? "white" : "black") + tempName + ".png"));
                        continue;
                    }
                    boardSquares[move.endX][move.endY].setIcon(null);
                }

                //chessBoard.printBoard();
            } else {
                System.out.println("Brak wybranej figury");
            }

            selectedSquare = null;
            selectedX = -1;
            selectedY = -1;
            player = false;
        }

        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        if(!chessBoard.isWhiteTurn() && !player && playerIsWhite && AIenabled) { // czarny ai
            chessBoard.makeAIMove(false);
            player = true;
            //chessBoard.printBoard();
        } else if(chessBoard.isWhiteTurn() && !player && !playerIsWhite && AIenabled) { // bialy ai
            chessBoard.makeAIMove(true);
            player = true;
            //chessBoard.printBoard();
        }
    }

    public static void updateBoard(int x, int y, int endX, int endY, String name, boolean isWhite) {
        boardSquares[x][y].setIcon(null);
        boardSquares[endX][endY].setIcon(new ImageIcon("pieces/" + (isWhite ? "white" : "black") + name + ".png"));
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public static void updateCastlingUI(int kingStartX, int kingStartY, int kingEndX, int kingEndY,
                                        int rookStartX, int rookStartY, int rookEndX, int rookEndY,
                                        boolean isWhite) {
        boardSquares[kingEndX][kingEndY].setIcon(new ImageIcon("pieces/" + (isWhite ? "white" : "black") + "King.png"));
        boardSquares[rookEndX][rookEndY].setIcon(new ImageIcon("pieces/" + (isWhite ? "white" : "black") + "Rook.png"));
        boardSquares[kingStartX][kingStartY].setIcon(null);
        boardSquares[rookStartX][rookStartY].setIcon(null);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public static void flipBoard() {
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
        String[] options = {"Krolowka", "Wieza", "Goniec", "Skoczek"};
        String choice = (String) JOptionPane.showInputDialog(null, "Wybierz figure:", "Promocja piona",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if(choice == null) choice = "Krolowka";

        Piece promotedPiece;
        tempX = x;
        tempY = y;
        switch(choice) {
            case "Krolowka":
                promotedPiece = new Queen(isWhite);
                tempS = "Queen";
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
                tempS = "Bishop";
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
            case "Skoczek":
                promotedPiece = new Knight(isWhite);
                tempS = "Knight";
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }
}