package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class GameStagesPanel extends JPanel {
    private JButton stage1Button;
    private JButton stage2Button;
    private JButton stage3Button;
    private JButton backButton;

    public GameStagesPanel(CardLayout cardLayout, JPanel cardPanel) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setupComponents();
    }

    private void setupComponents() {
        
        JLabel titleLabel = new JLabel("Select Stage", SwingConstants.CENTER);
        titleLabel.setBounds(200, 100, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);

        
        stage1Button = new JButton("Stage 1");
        stage1Button.setBounds(300, 200, 200, 40);
        stage1Button.setFont(new Font("Arial", Font.PLAIN, 16));
        stage1Button.setBackground(Color.BLACK);
        stage1Button.setForeground(Color.CYAN);
        add(stage1Button);


        stage2Button = new JButton("Stage 2");
        stage2Button.setBounds(300, 260, 200, 40);
        stage2Button.setFont(new Font("Arial", Font.PLAIN, 16));
        stage2Button.setBackground(Color.BLACK);
        stage2Button.setForeground(Color.CYAN);
        add(stage2Button);

        stage3Button = new JButton("Stage 3");
        stage3Button.setBounds(300, 320, 200, 40);
        stage3Button.setFont(new Font("Arial", Font.PLAIN, 16));
        stage3Button.setBackground(Color.BLACK);
        stage3Button.setForeground(Color.CYAN);
        add(stage3Button);

        
        backButton = new JButton("Back to Menu");
        backButton.setBounds(300, 320, 200, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.CYAN);
        add(backButton);
    }

    public JButton getStage1Button() {
        return stage1Button;
    }

    public JButton getStage2Button() {
        return stage2Button;
    }

    public JButton getStage3Button() {return stage3Button;}

    public JButton getBackButton() {
        return backButton;
    }
}