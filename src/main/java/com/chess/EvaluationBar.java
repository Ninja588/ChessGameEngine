package com.chess;

import javax.swing.*;
import java.awt.*;

public class EvaluationBar extends JPanel{
    private double evaluation;

    public EvaluationBar() {
        this.evaluation = 0.0;
    }

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
}
