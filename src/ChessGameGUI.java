import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class ChessGameGUI implements ChessBoard.PromotionListener {
    private static JButton[][] boardSquares = new JButton[8][8];
    private static JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private static JButton selectedSquare = null;
    private static int selectedX = -1;
    private static int selectedY = -1;
    private static ChessBoard chessBoard = new ChessBoard();
    private Timer gameCheckTimer;
    private static boolean playerIsWhite;
    private static boolean AIenabled;

    public ChessGameGUI(boolean AIenabled) {
        chessBoard.setPromotionListener(this);
        chessBoard.resetBoard();

        JFrame frame = new JFrame("Szachy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(boardPanel);

        resetGUIBoard();

        initializeBoard();
        Random rand = new Random();
        playerIsWhite = rand.nextBoolean();

        ChessGameGUI.AIenabled = AIenabled;

        if(playerIsWhite) {
            flipBoard();
        }

        gameCheckTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkGameConditions();
            }
        });
        gameCheckTimer.start();
        if(!playerIsWhite && AIenabled) chessBoard.makeAIMove(true);

        frame.setVisible(true);
    }

//    public static void main(String[] args) {
//        //new ChessGameGUI(false);
//    }

    private static void resetGUIBoard() {
        boardPanel.removeAll();
        //initializeBoard();
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private static void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton square = new JButton();
                square.addActionListener(ChessGameGUI::handleMove);

                if ((i + j) % 2 == 0) {
                    square.setBackground(Color.GRAY);
                } else {
                    square.setBackground(Color.WHITE);
                }

                boardSquares[i][j] = square;
                boardPanel.add(square);

                addPiece(square, i, j);
            }
        }
    }

    private static void addPiece(JButton square, int i, int j) {
        String color = (i < 2) ? "white" : "black";
        String pieceName = "";

        if (i == 0 || i == 7) {
            if (j == 0 || j == 7) pieceName = "Rook";
            if (j == 1 || j == 6) pieceName = "Knight";
            if (j == 2 || j == 5) pieceName = "Bishop";
            if (j == 3) pieceName = "Queen";
            if (j == 4) pieceName = "King";
        } else if (i == 1 || i == 6) {
            pieceName = "Pawn";
        }

        if (!pieceName.isEmpty()) {
            square.setIcon(new ImageIcon("pieces/" + color + pieceName + ".png"));
        }
    }

    public static int tempX, tempY;
    public static String tempS;
    public static boolean tempIsWhite;
    public static boolean player = true;

    private void checkGameConditions() {
        if (chessBoard.isCheckmate(true)) {
            SoundManager.playSound("game-end.wav");
            JOptionPane.showMessageDialog(boardPanel, "Mat! Czarny wygrywaja!");
            returnToMenu();
        } else if (chessBoard.isCheckmate(false)) {
            SoundManager.playSound("game-end.wav");
            JOptionPane.showMessageDialog(boardPanel, "Mat! Biale wygrywaja!");
            returnToMenu();
        } else if (chessBoard.isStalemate(true) || chessBoard.isStalemate(false)) {
            SoundManager.playSound("game-end.wav");
            JOptionPane.showMessageDialog(boardPanel, "Stalemate!");
            returnToMenu();
        }
    }

    private void returnToMenu() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(boardPanel);
        gameCheckTimer.stop();
        frame.dispose();

        new ChessMenu();
    }

    private static void handleMove(ActionEvent e) {
        JButton clickedSquare = (JButton) e.getSource();

        if (selectedSquare == null) {
            if (clickedSquare.getIcon() != null) {
                selectedSquare = clickedSquare;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (boardSquares[i][j] == clickedSquare) {
                            selectedX = i;
                            selectedY = j;
                            break;
                        }
                    }
                }
                //System.out.println("Wybrano fiugre na (" + selectedX + ", " + selectedY + ")");
            }
        } else {
            int targetX = -1;
            int targetY = -1;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (boardSquares[i][j] == clickedSquare) {
                        targetX = i;
                        targetY = j;
                        break;
                    }
                }
            }

            //System.out.println("Poruszanie sie do (" + targetX + ", " + targetY + ")");

            Piece selectedPiece = chessBoard.getPiece(selectedX, selectedY);

            //System.out.println(selectedPiece);
            if (selectedPiece != null) {
                chessBoard.movePiece(selectedX, selectedY, targetX, targetY,false);

                if(chessBoard.ok) {
                    //animateMove(selectedX, selectedY, targetX, targetY, "Pawn", selectedPiece.isWhite);
                    clickedSquare.setIcon(selectedSquare.getIcon());
                    selectedSquare.setIcon(null);
                    if(chessBoard.enpassantWhite) boardSquares[targetX-1][targetY].setIcon(null);
                    if(chessBoard.enpassantBlack) boardSquares[targetX+1][targetY].setIcon(null);
                    //System.out.println("RUCHY KROLA BIALEGO 0,4:");
                    //chessBoard.testKingMoves();

                    if(chessBoard.promoted) {
                        boardSquares[tempX][tempY].setIcon(null);
                        boardSquares[tempX][tempY].setIcon(new ImageIcon("pieces/" + (tempIsWhite ? "white" : "black") + tempS + ".png"));
                    }
                }

//                if(chessBoard.isCheckmate(true)) {
//                    System.out.println("checkmate (czarne wygraly)");
//                }
//                else if(chessBoard.isCheckmate(false)) {
//                    System.out.println("checkmate (biale wygraly)");
//                }
//                if (chessBoard.isStalemate(true) || chessBoard.isStalemate(false)) {
//                    System.out.println("stalemate");
//                }

                //System.out.println("Poruszono figure do (" + targetX + ", " + targetY + ")");
                chessBoard.printBoard();
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
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        if (!chessBoard.isWhiteTurn() && !player && playerIsWhite && AIenabled) { // czarny ai
            chessBoard.makeAIMove(false);
            player = true;
            chessBoard.printBoard();
        } else if (chessBoard.isWhiteTurn() && !player && !playerIsWhite && AIenabled) { // bialy ai
            chessBoard.makeAIMove(true);
            player = true;
            chessBoard.printBoard();
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

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                boardPanel.add(boardSquares[i][j]);
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
        Piece promotedPiece;
        tempX = x;
        tempY = y;
        switch (choice) {
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
            default:
                promotedPiece = new Queen(isWhite);
                tempS = "Queen";
                tempIsWhite = isWhite;
                chessBoard.setPiece(x, y, promotedPiece);
                break;
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }
}