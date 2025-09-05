package com.example.network_game.view;

import com.example.network_game.model.GameModel;

import javax.swing.*;
import java.awt.*;

public class StorePanel extends JPanel {
    private JButton oAtarButton;
    private JButton oAiryamanButton;
    private JButton oAnahitaButton;


    private JButton scrollAergiaButton;
    private JButton scrollSisyphusButton;
    private JButton scrollEliphasButton;

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
        titleLabel.setBounds(200, 50, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);

        coinLabel = new JLabel("Coins: " + model.getCoins(), SwingConstants.CENTER);
        coinLabel.setBounds(200, 100, 400, 30);
        coinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        coinLabel.setForeground(Color.WHITE);
        add(coinLabel);


        oAtarButton = createStoreButton("O' Atar (3 coins)", 150, 150);
        oAtarButton.setToolTipText("Disables impact waves for 10 seconds");
        add(oAtarButton);

        oAiryamanButton = createStoreButton("O' Airyaman (4 coins)", 450, 150);
        oAiryamanButton.setToolTipText("Disables collisions for 5 seconds");
        add(oAiryamanButton);

        oAnahitaButton = createStoreButton("O' Anahita (5 coins)", 150, 210);
        oAnahitaButton.setToolTipText("Resets all packet health");
        add(oAnahitaButton);


        scrollAergiaButton = createStoreButton("Scroll of Aergia (10 coins)", 450, 210);
        scrollAergiaButton.setToolTipText("Stops packet acceleration for 15 seconds");
        add(scrollAergiaButton);

        scrollSisyphusButton = createStoreButton("Scroll of Sisyphus (15 coins)", 150, 270);
        scrollSisyphusButton.setToolTipText("Allows moving one system to a new location");
        add(scrollSisyphusButton);

        scrollEliphasButton = createStoreButton("Scroll of Eliphas (20 coins)", 450, 270);
        scrollEliphasButton.setToolTipText("Resets all packet center of mass offsets");
        add(scrollEliphasButton);

        backButton = createStoreButton("Back to Game", 300, 350);
        add(backButton);
    }

    private JButton createStoreButton(String text, int x, int y) {
        JButton button = new JButton("<html><center>" + text + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color baseColor = new Color(0, 100, 100);
                Color hoverColor = new Color(0, 150, 150);

                g2d.setColor(getModel().isRollover() ? hoverColor : baseColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(Color.CYAN);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                super.paintComponent(g);
            }
        };

        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        return button;
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

    public JButton getScrollAergiaButton() {
        return scrollAergiaButton;
    }

    public JButton getScrollSisyphusButton() {
        return scrollSisyphusButton;
    }

    public JButton getScrollEliphasButton() {
        return scrollEliphasButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public void updateCoinLabel(int coins) {
        coinLabel.setText("Coins: " + coins);
    }
}