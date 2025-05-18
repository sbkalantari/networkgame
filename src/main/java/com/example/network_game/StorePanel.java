package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class StorePanel extends JPanel {
    private JButton oAtarButton;
    private JButton oAiryamanButton;
    private JButton oAnahitaButton;
    private JButton backButton;
    private JLabel coinLabel;

    public StorePanel(CardLayout cardLayout, JPanel cardPanel, GameModel model) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setupComponents(model);
    }

    private void setupComponents(GameModel model) {
        
        JLabel titleLabel = new JLabel("Store", SwingConstants.CENTER);
        titleLabel.setBounds(200, 100, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);

        
        coinLabel = new JLabel("Coins: " + model.getCoins(), SwingConstants.CENTER);
        coinLabel.setBounds(200, 150, 400, 30);
        coinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        coinLabel.setForeground(Color.WHITE);
        add(coinLabel);

        
        oAtarButton = new JButton("O' Atar (3 coins)");
        oAtarButton.setBounds(300, 200, 200, 40);
        oAtarButton.setFont(new Font("Arial", Font.PLAIN, 16));
        oAtarButton.setBackground(Color.BLACK);
        oAtarButton.setForeground(Color.CYAN);
        add(oAtarButton);

        
        oAiryamanButton = new JButton("O' Airyaman (4 coins)");
        oAiryamanButton.setBounds(300, 260, 200, 40);
        oAiryamanButton.setFont(new Font("Arial", Font.PLAIN, 16));
        oAiryamanButton.setBackground(Color.BLACK);
        oAiryamanButton.setForeground(Color.CYAN);
        add(oAiryamanButton);

        
        oAnahitaButton = new JButton("O' Anahita (5 coins)");
        oAnahitaButton.setBounds(300, 320, 200, 40);
        oAnahitaButton.setFont(new Font("Arial", Font.PLAIN, 16));
        oAnahitaButton.setBackground(Color.BLACK);
        oAnahitaButton.setForeground(Color.CYAN);
        add(oAnahitaButton);

        
        backButton = new JButton("Back to Game");
        backButton.setBounds(300, 380, 200, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.CYAN);
        add(backButton);
    }

    public JButton getOAtarButton() {
        return oAtarButton;
    }

    public JButton getOAiryamanButton() {
        return oAiryamanButton;
    }

    public JButton getOAnahitaButton() {
        return oAnahitaButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public void updateCoinLabel(int coins) {
        coinLabel.setText("Coins: " + coins);
    }
}