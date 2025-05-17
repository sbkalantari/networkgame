package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public SettingsPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setupComponents();
    }

    private void setupComponents() {
        
        JLabel titleLabel = new JLabel("Game Settings", SwingConstants.CENTER);
        titleLabel.setBounds(200, 100, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel);

        
        JLabel volumeLabel = new JLabel("Audio Volume:", SwingConstants.RIGHT);
        volumeLabel.setBounds(200, 200, 150, 30);
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        volumeLabel.setForeground(Color.WHITE);
        add(volumeLabel);

        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setBounds(360, 200, 200, 30);
        volumeSlider.setBackground(Color.DARK_GRAY);
        volumeSlider.setForeground(Color.CYAN);
        volumeSlider.addChangeListener(e -> {
            
            System.out.println("Volume set to: " + volumeSlider.getValue());
        });
        add(volumeSlider);

        
        JButton keyBindingsButton = new JButton("Customize Key Bindings");
        keyBindingsButton.setBounds(300, 260, 200, 40);
        keyBindingsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        keyBindingsButton.setBackground(Color.BLACK);
        keyBindingsButton.setForeground(Color.CYAN);
        keyBindingsButton.addActionListener(e -> showKeyBindingsDialog());
        add(keyBindingsButton);

        
        JButton backButton = new JButton("Back to Menu");
        backButton.setBounds(300, 400, 200, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.CYAN);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        add(backButton);
    }

    private void showKeyBindingsDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Customize Key Bindings");
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(0, 1));

        
        String[] actions = {"Toggle HUD", "Open Store", "Time Jump"};
        String[] keys = {"H", "S", "Left Click"};
        for (int i = 0; i < actions.length; i++) {
            JButton button = new JButton(actions[i] + ": " + keys[i]);
            int k = i;
            button.addActionListener(e -> {
                button.setText(actions[k] + ": Press a key...");
                button.requestFocus();
                button.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent ke) {
                        String newKey = KeyEvent.getKeyText(ke.getKeyCode());
                        
                        System.out.println("Assigned " + newKey + " to " + actions[k]);
                        button.setText(actions[k] + ": " + newKey);
                        button.removeKeyListener(this);
                    }
                });
            });
            dialog.add(button);
        }

        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }
}