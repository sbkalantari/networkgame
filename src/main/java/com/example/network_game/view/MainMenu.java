package com.example.network_game.view;

import com.example.network_game.controller.GameController;
import com.example.network_game.model.GameModel;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    private final JButton startButton;
    private final JButton stagesButton;
    private final JButton settingsButton;
    private final JButton exitButton;

    public MainMenu(CardLayout cardLayout, JPanel cardPanel, GameModel model, GameView gameView, GameController gameController) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("Network Game", SwingConstants.CENTER);
        titleLabel.setBounds(200, 100, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);

        startButton = new JButton("Start Game");
        startButton.setBounds(300, 200, 200, 40);
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(Color.CYAN);
        startButton.addActionListener(e -> {
            model.resetStage();
            gameView.setGameModel(model);
            gameController.setGameModel(model);
            cardLayout.show(cardPanel, "Game");
            System.out.println("Game started");
        });
        add(startButton);

        stagesButton = new JButton("Game Stages");
        stagesButton.setBounds(300, 260, 200, 40);
        stagesButton.setFont(new Font("Arial", Font.PLAIN, 16));
        stagesButton.setBackground(Color.BLACK);
        stagesButton.setForeground(Color.CYAN);
        stagesButton.addActionListener(e -> cardLayout.show(cardPanel, "Stages"));
        add(stagesButton);

        settingsButton = new JButton("Game Settings");
        settingsButton.setBounds(300, 320, 200, 40);
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        settingsButton.setBackground(Color.BLACK);
        settingsButton.setForeground(Color.CYAN);
        settingsButton.addActionListener(e -> cardLayout.show(cardPanel, "Settings"));
        add(settingsButton);

        exitButton = new JButton("Exit Game");
        exitButton.setBounds(300, 380, 200, 40);
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.CYAN);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);
    }
}