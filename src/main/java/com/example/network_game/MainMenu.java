package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private GameModel model;

    public MainMenu(CardLayout cardLayout, JPanel cardPanel, GameModel model) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setupComponents();
    }

    private void setupComponents() {
        
        JLabel titleLabel = new JLabel("Network Game", SwingConstants.CENTER);
        titleLabel.setBounds(200, 100, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);

        
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(300, 200, 200, 40);
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(Color.CYAN);
        startButton.addActionListener(e -> {
            model.resetStage();
            cardLayout.show(cardPanel, "Game");
        });
        add(startButton);

        
        JButton stagesButton = new JButton("Game Stages");
        stagesButton.setBounds(300, 260, 200, 40);
        stagesButton.setFont(new Font("Arial", Font.PLAIN, 16));
        stagesButton.setBackground(Color.BLACK);
        stagesButton.setForeground(Color.CYAN);
        stagesButton.addActionListener(e -> {
            model.resetStage();
            cardLayout.show(cardPanel, "Stages");
        });
        add(stagesButton);

        
        JButton settingsButton = new JButton("Game Settings");
        settingsButton.setBounds(300, 320, 200, 40);
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        settingsButton.setBackground(Color.BLACK);
        settingsButton.setForeground(Color.CYAN);
        settingsButton.addActionListener(e -> cardLayout.show(cardPanel, "Settings"));
        add(settingsButton);

        
        JButton exitButton = new JButton("Exit Game");
        exitButton.setBounds(300, 380, 200, 40);
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.CYAN);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);
    }
}