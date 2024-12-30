package com.chess;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;

import static java.lang.Integer.parseInt;

public class ChessMenu {
    private boolean playerVsAI = true;
    protected JFrame frame;
    protected JPanel mainPanel;
    protected CardLayout cardLayout;
    private int width;
    private int height;
    private Color lightSquaresColor=null;
    private Color darkSquaresColor=null;
    private GraphicsDevice gd;

    private static final String ARIAL = "Arial";
    private static final String MAIN_MENU = "Main Menu";
    private static final String SETTINGS = "Settings";

    public ChessMenu(JFrame frame, GraphicsDevice gd) {
        if(isHeadless()) {
            this.frame = new JFrame("Szachy");
            this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            width = this.gd.getDisplayMode().getWidth();
            height = this.gd.getDisplayMode().getHeight();
            this.frame.setUndecorated(true);
            this.frame.setExtendedState(Frame.MAXIMIZED_BOTH);

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);

            JPanel menuPanel = createMenuPanel();
            mainPanel.add(menuPanel, MAIN_MENU);

            JPanel settingsPanel = createSettingsPanel();
            mainPanel.add(settingsPanel, SETTINGS);

            this.frame.add(mainPanel);
            this.frame.setVisible(true);
        } else {
            this.frame = frame;
            this.gd = gd;
            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);

            JPanel menuPanel = createMenuPanel();
            mainPanel.add(menuPanel, MAIN_MENU);

            JPanel settingsPanel = createSettingsPanel();
            mainPanel.add(settingsPanel, SETTINGS);
        }
    }

    private boolean isHeadless() {
        return !GraphicsEnvironment.isHeadless();
    }

    protected JPanel createMenuPanel() {
        JPanel menuPanel = new BackgroundPanel("menu.png");
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        menuPanel.add(Box.createVerticalGlue());

        JButton startButton = createButton("Start");
        startButton.addActionListener(e->startGame());
        menuPanel.add(createCenteredComponent(startButton));

        menuPanel.add(Box.createVerticalStrut(20));

        JButton settingsButton = createButton("Ustawienia");
        settingsButton.addActionListener(e -> cardLayout.show(mainPanel, SETTINGS));
        menuPanel.add(createCenteredComponent(settingsButton));

        menuPanel.add(Box.createVerticalStrut(20));

        JButton exitButton = createButton("Wyjście");
        exitButton.addActionListener(e->System.exit(0));
        menuPanel.add(createCenteredComponent(exitButton));

        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private Component createCenteredComponent(JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.add(Box.createHorizontalGlue());
        panel.add(component);
        panel.add(Box.createHorizontalGlue());
        return panel;
    }

    protected JPanel createSettingsPanel() {
        JPanel settingsPanel = new BackgroundPanel("menu.png");
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        settingsPanel.add(Box.createVerticalGlue());

        JLabel label = new JLabel("Wybierz tryb:", SwingConstants.CENTER);
        label.setFont(new Font(ARIAL, Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(createCenteredComponent(label));

        String[] gameModes = {"Gracz vs AI", "Gracz vs Gracz"};
        JComboBox<String> modeComboBox = new JComboBox<>(gameModes);
        modeComboBox.setFont(new Font(ARIAL, Font.PLAIN, 18));
        modeComboBox.setMaximumSize(new Dimension(200,30));
        modeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeComboBox.addActionListener(e->playerVsAI = "Gracz vs AI".equals(modeComboBox.getSelectedItem()));
        modeComboBox.setName("modeComboBox");
        settingsPanel.add(createCenteredComponent(modeComboBox));

        settingsPanel.add(Box.createVerticalStrut(20));

        JCheckBox fullscreenCheckbox = new JCheckBox("Fullscreen", true);

        String nativeRes = width+"x"+height+" (native)";
        final String[] lastRes = {nativeRes};
        String[] resolutions = {nativeRes, "800x600", "1366x768", "1600x900", "1920x1080", "2560x1440"};

        JComboBox<String> resolutionComboBox = new JComboBox<>(resolutions);
        resolutionComboBox.setFont(new Font(ARIAL, Font.PLAIN, 18));
        resolutionComboBox.setMaximumSize(new Dimension(200,30));
        resolutionComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        resolutionComboBox.addActionListener(e -> {
            String selectedResolution = (String) resolutionComboBox.getSelectedItem();
            if(selectedResolution != null && !fullscreenCheckbox.isSelected()) {
                switch(selectedResolution) {
                    case "800x600":
                        width = 800;
                        height = 600;
                        lastRes[0] = width+"x"+height;
                        frame.setSize(width, height);
                        break;
                    case "1366x768":
                        width = 1366;
                        height = 768;
                        lastRes[0] = width+"x"+height;
                        frame.setSize(width, height);
                        break;
                    case "1600x900":
                        width = 1600;
                        height = 900;
                        lastRes[0] = width+"x"+height;
                        frame.setSize(width, height);
                        break;
                    case "1920x1080":
                        width = 1920;
                        height = 1080;
                        lastRes[0] = width+"x"+height;
                        frame.setSize(width, height);
                        break;
                    case "2560x1440":
                        width = 2560;
                        height = 1440;
                        lastRes[0] = width+"x"+height;
                        frame.setSize(width, height);
                        break;
                    default:
                        break;
                }
            } else resolutionComboBox.setSelectedIndex(0);
        });
        resolutionComboBox.setName("resolutionComboBox");
        settingsPanel.add(createCenteredComponent(resolutionComboBox));

        settingsPanel.add(Box.createVerticalStrut(20));

        fullscreenCheckbox.setFont(new Font(ARIAL, Font.PLAIN, 18));
        fullscreenCheckbox.setMaximumSize(new Dimension(200, 30));
        fullscreenCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullscreenCheckbox.setOpaque(false);
        fullscreenCheckbox.addActionListener(e -> {
            if(fullscreenCheckbox.isSelected()) {
                frame.dispose();
                frame.setUndecorated(true);
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                frame.setVisible(true);
                if(isHeadless()) {
                    gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    width = gd.getDisplayMode().getWidth();
                    height = gd.getDisplayMode().getHeight();
                }
                resolutionComboBox.setSelectedIndex(0);
            } else {
                frame.dispose();
                frame.setUndecorated(false);
                frame.setSize(width, height);
                frame.setExtendedState(Frame.NORMAL);
                frame.setVisible(true);
                for(int i=0;i<resolutions.length;i++) {
                    if(resolutions[i].equals(lastRes[0])) {
                        resolutionComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
        settingsPanel.add(createCenteredComponent(fullscreenCheckbox));

        settingsPanel.add(Box.createVerticalStrut(20));

        JButton lightSquareColorButton = new JButton("Kolor jasnych pól");
        lightSquareColorButton.setFont(new Font(ARIAL, Font.PLAIN, 18));
        lightSquareColorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        lightSquareColorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(null, "Kolor jasnych pól", Color.WHITE);
            if(chosenColor != null) {
                lightSquaresColor = chosenColor;
            }
        });
        settingsPanel.add(createCenteredComponent(lightSquareColorButton));

        settingsPanel.add(Box.createVerticalStrut(20));

        JButton darkSquareColorButton = new JButton("Kolor ciemnych pól");
        darkSquareColorButton.setFont(new Font(ARIAL, Font.PLAIN, 18));
        darkSquareColorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        darkSquareColorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(null, "Kolor ciemnych pól", Color.WHITE);
            if(chosenColor != null) {
                darkSquaresColor = chosenColor;
            }
        });
        settingsPanel.add(createCenteredComponent(darkSquareColorButton));

        settingsPanel.add(Box.createVerticalStrut(20));

        JButton resetSquareColorButton = new JButton("Reset kolorów pól do domyślnych");
        resetSquareColorButton.setFont(new Font(ARIAL, Font.PLAIN, 18));
        resetSquareColorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetSquareColorButton.addActionListener(e -> {
            darkSquaresColor = null;
            lightSquaresColor = null;
        });
        settingsPanel.add(createCenteredComponent(resetSquareColorButton));

        settingsPanel.add(Box.createVerticalStrut(20));

        JButton backButton = createButton("Powrót");
        backButton.addActionListener(e->cardLayout.show(mainPanel, MAIN_MENU));
        settingsPanel.add(createCenteredComponent(backButton));

        settingsPanel.add(Box.createVerticalGlue());

        JLabel authors = new JLabel("<html>Autorzy:<br/>Kamil Derszniak<br/>Julia Danilczuk</html>", SwingConstants.CENTER);
        authors.setFont(new Font(ARIAL, Font.BOLD, 24));
        authors.setForeground(Color.WHITE);
        authors.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        settingsPanel.add(authors);

        return settingsPanel;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(ARIAL, Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 63, 65));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void startGame() {
        SecureRandom rand = new SecureRandom();
        ChessGameGUI gameGUI  = new ChessGameGUI(playerVsAI, mainPanel, cardLayout, lightSquaresColor, darkSquaresColor, rand.nextBoolean());

        mainPanel.add(gameGUI.getGamePanel(), "Game");
        cardLayout.show(mainPanel, "Game");
    }

    public static class BackgroundPanel extends JPanel {
        protected final transient Image backgroundImage;

        public BackgroundPanel(String path) {
            backgroundImage = new ImageIcon(path).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
