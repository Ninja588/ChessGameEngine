package com.chess;

import org.junit.jupiter.api.*;
import org.mockito.*;
import javax.swing.*;

import java.awt.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChessMenuTest {
    private ChessMenu chessMenu;
    @Mock
    private JPanel mainPanel;
    @Mock
    private CardLayout cardLayout;
    @Mock
    private GraphicsDevice gd;

    @Mock
    private GraphicsEnvironment mockGraphicsEnvironment;

    @Mock
    private JFrame frame;

    @BeforeAll
    static void setupHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        frame = mock(JFrame.class);
        gd = mock(GraphicsDevice.class);
        mockGraphicsEnvironment = mock(GraphicsEnvironment.class);
        when(mockGraphicsEnvironment.getDefaultScreenDevice()).thenReturn(gd);

        chessMenu = new ChessMenu(frame, gd);

        ChessMenu.cardLayout = cardLayout;
        ChessMenu.mainPanel = mainPanel;
    }

    @Test
    void testCreateMenuPanel() {
        JPanel menuPanel = chessMenu.createMenuPanel();
        assertNotNull(menuPanel);
        assertTrue(menuPanel.getComponentCount() > 0);
    }

    @Test
    void testSettingsPanel() {
        JPanel settingsPanel = chessMenu.createSettingsPanel();
        assertNotNull(settingsPanel);
        JComboBox<String> modeComboBox = findComboBox(settingsPanel, "modeComboBox");
        assertNotNull(modeComboBox);
        assertEquals("Gracz vs AI", modeComboBox.getSelectedItem());

        JCheckBox fullscreenCheckbox = findCheckbox(settingsPanel,"Fullscreen");
        assert fullscreenCheckbox != null;
        fullscreenCheckbox.doClick();
        assertFalse(fullscreenCheckbox.isSelected());

        JComboBox<String> resolutionComboBox = findComboBox(settingsPanel, "resolutionComboBox");
        assert resolutionComboBox != null;
        resolutionComboBox.setSelectedItem("800x600");
        assertEquals("800x600", resolutionComboBox.getSelectedItem());
        resolutionComboBox.setSelectedItem("1366x768");
        assertEquals("1366x768", resolutionComboBox.getSelectedItem());
        resolutionComboBox.setSelectedItem("1600x900");
        assertEquals("1600x900", resolutionComboBox.getSelectedItem());
        resolutionComboBox.setSelectedItem("1920x1080");
        assertEquals("1920x1080", resolutionComboBox.getSelectedItem());
        resolutionComboBox.setSelectedItem("2560x1440");
        assertEquals("2560x1440", resolutionComboBox.getSelectedItem());

        fullscreenCheckbox.doClick();
        assertTrue(fullscreenCheckbox.isSelected());

        resolutionComboBox.setSelectedItem(null);
        assertEquals(0, resolutionComboBox.getSelectedIndex());

        JButton resetButton = findButton(settingsPanel, "Reset kolorów pól do domyślnych");
        assertNotNull(resetButton);
        resetButton.doClick();
    }

    private JButton findButton(Container container, String text) {
        for(Component component : container.getComponents()) {
            if(component instanceof JButton && ((JButton) component).getText().equals(text)) {
                return (JButton) component;
            } else if(component instanceof Container) {
                JButton button = findButton((Container) component, text);
                if(button != null) {
                    return button;
                }
            }
        }
        return null;
    }

    private JCheckBox findCheckbox(Container container, String text) {
        for(Component component : container.getComponents()) {
            if(component instanceof JCheckBox && ((JCheckBox) component).getText().equals(text)) {
                return (JCheckBox) component;
            } else if(component instanceof Container) {
                JCheckBox checkBox = findCheckbox((Container) component, text);
                if(checkBox != null) {
                    return checkBox;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> findComboBox(Container container, String name) {
        for(Component component : container.getComponents()) {
            if(component instanceof JComboBox<?> && name.equals(component.getName())) {
                return (JComboBox<String>) component;
            } else if(component instanceof Container) {
                JComboBox<String> comboBox = findComboBox((Container) component, name);
                if(comboBox != null) {
                    return comboBox;
                }
            }
        }
        return null;
    }

}
