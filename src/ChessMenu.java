import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessMenu {
    private static boolean playerVsAI = true;
    private static JFrame frame;
    public ChessMenu() {
        frame = new JFrame("Szachy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new GridLayout(4, 1));

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> startGame());
        frame.add(startButton);

        JButton settingsButton = new JButton("Ustawienia");
        settingsButton.addActionListener(e -> showSettings());
        frame.add(settingsButton);

        JButton exitButton = new JButton("Wyjscie");
        exitButton.addActionListener(e -> System.exit(0));
        frame.add(exitButton);

        frame.setVisible(true);
    }
    public static void main(String[] args) {
        new ChessMenu();
    }

    private static void startGame() {
        if (playerVsAI) {
            new ChessGameGUI(true);
        } else {
            new ChessGameGUI(false);
        }
        frame.dispose();
    }

    private static void showSettings() {
        JFrame settingsFrame = new JFrame("Ustawienia");
        settingsFrame.setSize(300, 200);
        settingsFrame.setLayout(new GridLayout(2, 1));

        // wybor trybu gry
        String[] gameModes = {"Gracz vs AI", "Gracz vs Gracz"};
        JComboBox<String> modeComboBox = new JComboBox<>(gameModes);
        modeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String selectedMode = (String) cb.getSelectedItem();
                playerVsAI = selectedMode.equals("Gracz vs AI");
            }
        });
        settingsFrame.add(new JLabel("Wybierz tryb:"));
        settingsFrame.add(modeComboBox);

        settingsFrame.setVisible(true);
    }
}
