package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class NetworkGame extends JFrame {
    public NetworkGame() {
        setTitle("Network Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        CardLayout cardLayout = new CardLayout() {
            @Override
            public void show(Container parent, String name) {
                super.show(parent, name);

                if ("Game".equals(name) || "Settings".equals(name)) {
                    AudioManager.playBackgroundMusic();
                    System.out.println("Showing " + name + ", background music started");
                } else {
                    AudioManager.stopAllSounds();
                    System.out.println("Showing " + name + ", background music stopped");
                }
            }
        };
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setPreferredSize(new Dimension(800, 600));


        GameModel initialModel = new GameModel();
        GameView view = new GameView(initialModel, cardLayout, cardPanel);
        GameController controller = new GameController(initialModel, view, cardLayout, cardPanel);
        GameStagesPanel stagesPanel = new GameStagesPanel(cardLayout, cardPanel);
        GameStageController stageController = new GameStageController(stagesPanel, cardLayout, cardPanel, view, controller);
        StorePanel storePanel = new StorePanel(cardLayout, cardPanel, initialModel);
        StoreController storeController = new StoreController(storePanel, initialModel, cardLayout, cardPanel, controller);
        SettingsPanel settingsPanel = new SettingsPanel(cardLayout, cardPanel);
        SettingsController settingsController = new SettingsController(settingsPanel, cardLayout, cardPanel);


        cardPanel.add(new MainMenu(cardLayout, cardPanel, initialModel, view, controller), "MainMenu");
        cardPanel.add(view, "Game");
        cardPanel.add(stagesPanel, "Stages");
        cardPanel.add(settingsPanel, "Settings");
        cardPanel.add(storePanel, "Store");


        add(cardPanel);
        pack();
        setLocationRelativeTo(null);
        cardLayout.show(cardPanel, "MainMenu");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NetworkGame game = new NetworkGame();
            game.setVisible(true);
        });
    }
}