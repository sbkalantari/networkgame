package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class NetworkGame extends JFrame {
    public NetworkGame() {
        setTitle("Network Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setPreferredSize(new Dimension(800, 600));

        
        GameModel initialModel = new GameModel(); 
        GameView view = new GameView(initialModel, cardLayout, cardPanel);
        GameController controller = new GameController(initialModel, view, cardLayout, cardPanel);
        GameStagesPanel stagesPanel = new GameStagesPanel(cardLayout, cardPanel);
        GameStageController stageController = new GameStageController(stagesPanel, cardLayout, cardPanel, view, controller);

        
        cardPanel.add(new MainMenu(cardLayout, cardPanel, initialModel), "MainMenu");
        cardPanel.add(view, "Game");
        cardPanel.add(stagesPanel, "Stages");
        cardPanel.add(createPlaceholderPanel("Settings", cardLayout, cardPanel), "Settings");

        
        add(cardPanel);
        pack();
        setLocationRelativeTo(null);
        cardLayout.show(cardPanel, "MainMenu");
    }

    private JPanel createPlaceholderPanel(String title, CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(800, 600));
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(null);

        JLabel label = new JLabel(title + " (Coming Soon)", SwingConstants.CENTER);
        label.setBounds(200, 200, 400, 50);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.CYAN);
        panel.add(label);

        JButton backButton = new JButton("Back to Menu");
        backButton.setBounds(300, 400, 200, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.CYAN);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        panel.add(backButton);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NetworkGame game = new NetworkGame();
            game.setVisible(true);
        });
    }
}