package com.example.network_game.controller;

import com.example.network_game.save.SaveManager;
import com.example.network_game.model.SystemType;
import com.example.network_game.model.GameModel;
import com.example.network_game.view.GameStagesPanel;
import com.example.network_game.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameStageController {
    private final GameStagesPanel view;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private GameModel currentModel;
    private final GameView gameView;
    private final GameController gameController;
    private SaveManager saveManager;
    private int currentStageNumber = 1;
    private final Random random = new Random();

    public GameStageController(GameStagesPanel view, CardLayout cardLayout, JPanel cardPanel,
                               GameView gameView, GameController gameController) {
        this.view = view;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.gameView = gameView;
        this.gameController = gameController;
        setupListeners();
    }

    private void setupListeners() {
        view.getStage1Button().addActionListener(e -> selectStage(1));
        view.getStage2Button().addActionListener(e -> selectStage(2));
        view.getStage3Button().addActionListener(e -> selectStage(3));
        view.getStage4Button().addActionListener(e -> selectStage(4));
        view.getStage5Button().addActionListener(e -> selectStage(5));
        view.getBackButton().addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
    }

    public void selectStage(int stageNumber) {
        currentStageNumber = stageNumber;
        boolean loaded = checkForExistingSave();

        if (!loaded) {
            switch (stageNumber) {
                case 1:
                    currentModel = createStage1();
                    break;
                case 2:
                    currentModel = createStage2();
                    break;
                case 3:
                    currentModel = createStage3();
                    break;
                case 4:
                    currentModel = createStage4();
                    break;
                case 5:
                    currentModel = createStage5();
                    break;
                default:
                    currentModel = createStage1();
            }
        }

        updateGameModel();
        setupSaveManager();
        cardLayout.show(cardPanel, "Game");
    }

    private boolean checkForExistingSave() {
        if (saveManager != null && saveManager.hasValidSave()) {
            int choice = JOptionPane.showConfirmDialog(
                    cardPanel,
                    "A saved game was found. Do you want to continue from where you left off?",
                    "Continue Previous Game?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                if (saveManager.loadGame()) {
                    Timer freezeTimer = new Timer(3000, e -> {
                        gameController.resumeGame();
                        currentModel.setIsPlaying(true);
                        ((Timer) e.getSource()).stop();
                    });
                    freezeTimer.setRepeats(false);
                    freezeTimer.start();

                    JOptionPane.showMessageDialog(cardPanel,
                            "Game loaded! The game will resume in 3 seconds...",
                            "Game Loaded", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(cardPanel,
                            "Failed to load saved game. Starting new game...",
                            "Load Failed", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                saveManager.deleteSave();
            }
        }
        return false;
    }

    private GameModel createStage1() {
        int[][] systemPositions = {
                {100, 100},
                {400, 100},
                {100, 300},
                {400, 300}
        };

        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.NORMAL,
                SystemType.NORMAL,
                SystemType.NORMAL
        };

        return new GameModel(systemPositions, types, 60.0, 2500.0);
    }

    private GameModel createStage2() {
        int[][] systemPositions = {
                {100, 100},
                {500, 100},
                {100, 400},
                {500, 400},
                {300, 250}
        };

        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.SPY,
                SystemType.SABOTAGE,
                SystemType.NORMAL,
                SystemType.VPN
        };

        return new GameModel(systemPositions, types, 75.0, 3500.0);
    }

    private GameModel createStage3() {
        int[][] systemPositions = {
                {50, 50},
                {200, 50},
                {350, 50},
                {500, 50},
                {50, 200},
                {200, 200},
                {350, 200},
                {500, 200},
                {275, 350}
        };

        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.ANTI_TROJAN,
                SystemType.SPY,
                SystemType.SABOTAGE,
                SystemType.VPN,
                SystemType.NORMAL,
                SystemType.NORMAL,
                SystemType.SPY,
                SystemType.ANTI_TROJAN
        };

        return new GameModel(systemPositions, types, 90.0, 4000.0);
    }

    private GameModel createStage4() {
        int[][] systemPositions = {
                {100, 100}, {300, 100}, {500, 100}, {700, 100},
                {100, 250}, {300, 250}, {500, 250}, {700, 250},
                {100, 400}, {300, 400}, {500, 400}, {700, 400}
        };

        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.DISTRIBUTE,
                SystemType.NORMAL,
                SystemType.MERGE,
                SystemType.VPN,
                SystemType.ANTI_TROJAN,
                SystemType.SABOTAGE,
                SystemType.SPY,
                SystemType.NORMAL,
                SystemType.DISTRIBUTE,
                SystemType.NORMAL,
                SystemType.MERGE
        };

        return new GameModel(systemPositions, types, 120.0, 5000.0);
    }

    private GameModel createStage5() {
        int count = 15;
        int[][] positions = generateRandomSystemPositions(count);


        SystemType[] baseTypes = {
                SystemType.NORMAL, SystemType.NORMAL, SystemType.NORMAL,
                SystemType.SPY, SystemType.SPY,
                SystemType.SABOTAGE, SystemType.SABOTAGE,
                SystemType.VPN, SystemType.VPN,
                SystemType.ANTI_TROJAN, SystemType.ANTI_TROJAN,
                SystemType.DISTRIBUTE, SystemType.DISTRIBUTE,
                SystemType.MERGE, SystemType.MERGE
        };


        SystemType[] assignedTypes = new SystemType[count];
        assignedTypes[0] = SystemType.NORMAL;

        java.util.List<SystemType> typesList = new java.util.ArrayList<>();
        for (int i = 1; i < baseTypes.length && i < count; i++) {
            typesList.add(baseTypes[i]);
        }
        java.util.Collections.shuffle(typesList);

        for (int i = 1; i < count; i++) {
            assignedTypes[i] = typesList.get(i - 1);
        }

        return new GameModel(positions, assignedTypes, 150.0, 6000.0);
    }

    private int[][] generateRandomSystemPositions(int count) {
        int[][] positions = new int[count][2];

        positions[0] = new int[]{100, 100};

        for (int i = 1; i < count; i++) {
            boolean validPosition = false;
            int attempts = 0;

            while (!validPosition && attempts < 100) {
                int x = 50 + random.nextInt(700);
                int y = 50 + random.nextInt(500);

                validPosition = true;

                for (int j = 0; j < i; j++) {
                    double dist = Math.sqrt(Math.pow(x - positions[j][0], 2) + Math.pow(y - positions[j][1], 2));
                    if (dist < 120) {
                        validPosition = false;
                        break;
                    }
                }

                if (validPosition) {
                    positions[i] = new int[]{x, y};
                }
                attempts++;
            }

            if (!validPosition) {
                positions[i] = new int[]{100 + (i % 6) * 120, 100 + (i / 6) * 120};
            }
        }

        return positions;
    }

    public void advanceToNextStage() {
        if (currentStageNumber < 5) {
            currentStageNumber++;
            if (saveManager != null) {
                saveManager.deleteSave();
            }
            selectStage(currentStageNumber);
            JOptionPane.showMessageDialog(cardPanel,
                    "Advanced to Stage " + currentStageNumber + "!\nPrevious connections remain but cannot be removed.",
                    "Stage Advanced", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(cardPanel,
                    "Congratulations! You have completed all 5 stages of Blueprint Hell!",
                    "Game Completed", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(cardPanel, "MainMenu");
        }
    }

    private void updateGameModel() {
        gameView.setGameModel(currentModel);
        gameController.setGameModel(currentModel);
    }

    private void setupSaveManager() {
        if (saveManager != null) {
            saveManager.shutdown();
        }
        saveManager = new SaveManager(currentModel, gameController);
    }

    public GameModel getCurrentModel() {
        return currentModel;
    }

    public int getCurrentStageNumber() {
        return currentStageNumber;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public void shutdown() {
        if (saveManager != null) {
            saveManager.shutdown();
        }
    }
}
