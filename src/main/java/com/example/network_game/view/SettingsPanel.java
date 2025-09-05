package com.example.network_game.view;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JSlider volumeSlider;
    private JButton backButton;

    public SettingsPanel(CardLayout cardLayout, JPanel cardPanel) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setupComponents();
    }

    private void setupComponents() {

        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setBounds(200, 100, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);


        JLabel volumeLabel = new JLabel("Sound Volume:", SwingConstants.CENTER);
        volumeLabel.setBounds(200, 200, 400, 30);
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        volumeLabel.setForeground(Color.CYAN);
        add(volumeLabel);


        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setBounds(300, 230, 200, 40);
        volumeSlider.setBackground(Color.DARK_GRAY);
        volumeSlider.setForeground(Color.CYAN);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setFont(new Font("Arial", Font.PLAIN, 12));
        add(volumeSlider);


        backButton = new JButton("Back to Menu");
        backButton.setBounds(300, 300, 200, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.CYAN);
        add(backButton);
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public JButton getBackButton() {
        return backButton;
    }
}