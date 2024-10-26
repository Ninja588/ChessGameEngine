import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessMenu {
    private static boolean playerVsAI = true;
    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    public ChessMenu() {
        frame = new JFrame("Szachy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, "Main Menu");

        JPanel settingsPanel = createSettingsPanel();
        mainPanel.add(settingsPanel, "Settings");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new BackgroundPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        menuPanel.add(Box.createVerticalGlue());

        JButton startButton = createButton("Start");
        startButton.addActionListener(e->startGame());
        menuPanel.add(createCenteredComponent(startButton));

        menuPanel.add(Box.createVerticalStrut(20));

        JButton settingsButton = createButton("Ustawienia");
        settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "Settings"));
        menuPanel.add(createCenteredComponent(settingsButton));

        menuPanel.add(Box.createVerticalStrut(20));

        JButton exitButton = createButton("Wyjście");
        exitButton.addActionListener(e->System.exit(0));
        menuPanel.add(createCenteredComponent(exitButton));

        menuPanel.add(Box.createVerticalGlue());

//        JLabel authors = new JLabel("<html>Kamil Derszniak<br/>Julia Danilczuk</html>", SwingConstants.CENTER);
//        authors.setFont(new Font("Arial", Font.BOLD, 24));
//        authors.setForeground(Color.WHITE);
//        authors.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
//        menuPanel.add(authors);

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

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new BackgroundPanel();
        //settingsPanel.setBackground(new Color(70, 130, 180));
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        settingsPanel.add(Box.createVerticalGlue());

        JLabel label = new JLabel("Wybierz tryb:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(createCenteredComponent(label));

        String[] gameModes = {"Gracz vs AI", "Gracz vs Gracz"};
        JComboBox<String> modeComboBox = new JComboBox<>(gameModes);
        modeComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        modeComboBox.setMaximumSize(new Dimension(200,30));
        modeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeComboBox.addActionListener(e->playerVsAI = "Gracz vs AI".equals(modeComboBox.getSelectedItem()));

        settingsPanel.add(createCenteredComponent(modeComboBox));

        settingsPanel.add(Box.createVerticalStrut(20));

        JButton backButton = createButton("Powrót");
        backButton.addActionListener(e->cardLayout.show(mainPanel, "Main Menu"));
        settingsPanel.add(createCenteredComponent(backButton));

        settingsPanel.add(Box.createVerticalGlue());

        JLabel authors = new JLabel("<html>Autorzy:<br/>Kamil Derszniak<br/>Julia Danilczuk</html>", SwingConstants.CENTER);
        authors.setFont(new Font("Arial", Font.BOLD, 24));
        authors.setForeground(Color.WHITE);
        authors.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        settingsPanel.add(authors);

        return settingsPanel;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 63, 65));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessMenu::new);
    }

    private static void startGame() {

        ChessGameGUI gameGUI  = new ChessGameGUI(playerVsAI, mainPanel, cardLayout, frame);

        mainPanel.add(gameGUI.getGamePanel(), "Game");
        cardLayout.show(mainPanel, "Game");

        //frame.dispose();
    }

    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("menu.png").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
