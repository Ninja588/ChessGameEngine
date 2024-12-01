package com.chess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationBarTest {
    private EvaluationBar evaluationBar;

    @BeforeEach
    void setUp() {
        evaluationBar = new EvaluationBar();
    }

    @Test
    void initialization() {
        assertNotNull(evaluationBar);
        assertEquals(0.0, evaluationBar.getEvaluation());
    }

    @Test
    void setEvaluation() {
        evaluationBar.setEvaluation(0.5);
        assertEquals(0.5, evaluationBar.getEvaluation());
    }

    @Test
    void paintComponent() {
        BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        evaluationBar.setSize(evaluationBar.getPreferredSize());
        evaluationBar.setEvaluation(0.75);
        evaluationBar.paintComponent(g2d);

        g2d.dispose();
    }
}
