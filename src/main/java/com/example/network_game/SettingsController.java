package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsController {
    private SettingsPanel view;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public SettingsController(SettingsPanel view, CardLayout cardLayout, JPanel cardPanel) {
        this.view = view;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setupListeners();
    }

    private void setupListeners() {
        view.getVolumeSlider().addChangeListener(e -> {
            float volume = view.getVolumeSlider().getValue() / 100.0f;
            AudioManager.setMasterVolume(volume);
            System.out.println("Volume set to: " + volume); 
        });

        view.getBackButton().addActionListener(e -> {
            System.out.println("Returning to Main Menu from Settings"); 
            cardLayout.show(cardPanel, "MainMenu");
        });
    }
}