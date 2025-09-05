package com.example.network_game.controller;

import com.example.network_game.view.SettingsPanel;

import javax.swing.*;
import java.awt.*;

public class SettingsController {
    private final SettingsPanel view;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

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