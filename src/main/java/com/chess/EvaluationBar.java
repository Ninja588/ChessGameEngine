package com.chess;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa odpowiadajaca za pasek kto ma przewage
 */
public class EvaluationBar extends JPanel{
    /**
     * Oszacowana przewaga
     */
    private double evaluation;

    /**
     * Konstruktor klasy ustawiajacy poczatkowa wartosc na 0.0 (remis)
     */
    public EvaluationBar() {
        this.evaluation = 0.0;
    }

    /**
     * Metoda ustawiajaca wartosc oszacowania
     * @param evaluation wartosc oszacowania
     */
    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int height = getHeight();
        int width = getWidth();

        int whiteHeight = (int) ((1 + evaluation) / 2 * height);
        int blackHeight = height - whiteHeight;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, whiteHeight);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, whiteHeight, width, blackHeight);

        String evalText = String.format("%.2f", evaluation);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(evalText);
        int textHeight = fm.getAscent();
        int x = (width - textWidth) / 2;
        int y = height / 2 + textHeight / 4;

        g.setColor(new Color(255, 0, 0));
        g.drawString(evalText, x, y);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, getHeight());
    }

    /**
     * Metoda zwracajaca oszacowana wartosc
     * @return oszacowana wartosc
     */
    public double getEvaluation() {
        return evaluation;
    }
}
