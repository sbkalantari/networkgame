package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class GameStageController {
    private GameStagesPanel view;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private GameModel currentModel;
    private GameView gameView;
    private GameController gameController;

    public GameStageController(GameStagesPanel view, CardLayout cardLayout, JPanel cardPanel, GameView gameView, GameController gameController) {
        this.view = view;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.gameView = gameView;
        this.gameController = gameController;
        setupListeners();
    }

    private void setupListeners() {
        view.getStage1Button().addActionListener(e -> selectStage1());
        view.getStage2Button().addActionListener(e -> selectStage2());
        view.getStage3Button().addActionListener(e -> selectStage3());
        view.getBackButton().addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
    }

    public void selectStage1() {
        currentModel = createStage1Model();
        updateGameModel();
        cardLayout.show(cardPanel, "Game");
    }

    public void selectStage2() {
        currentModel = createStage2Model();
        updateGameModel();
        cardLayout.show(cardPanel, "Game");
    }

    public void selectStage3() {
        currentModel = createStage3Model();
        updateGameModel();
        cardLayout.show(cardPanel, "Game");
    }

    private GameModel createStage1Model() {
        
        int systemWidth = 60;
        int systemHeight = 50;
        int[][] positions = {
                {100, 100},
                {340, 100},
                {580, 100},
                {100, 380},
                {340, 380},
        };
        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.SPY,
                SystemType.VPN,
                SystemType.SABOTAGE,
                SystemType.NORMAL
        };
        return new GameModel(positions, types, 75.0, 3500.0);
    }

    private GameModel createStage2Model() {
        
        int systemWidth = 60;
        int systemHeight = 50;
        int[][] positions = {
                {100, 100},
                {180, 100},
                {260, 100},
                {100, 180},
                {180, 180},
                {260, 180}
        };
        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.SPY,
                SystemType.VPN,
                SystemType.SABOTAGE,
                SystemType.ANTI_TROJAN,
                SystemType.NORMAL
        };
        return new GameModel(positions, types, 60.0, 2000.0);
    }

    private GameModel createStage3Model() {
        
        int systemWidth = 60;
        int systemHeight = 50;
        int[][] positions = {
                {100, 100},
                {180, 100},
                {260, 100},
                {100, 180},
                {180, 180},
                {260, 180},
                {180, 260}
        };
        SystemType[] types = {
                SystemType.NORMAL,
                SystemType.SPY,
                SystemType.VPN,
                SystemType.SABOTAGE,
                SystemType.ANTI_TROJAN,
                SystemType.DISTRIBUTE,
                SystemType.MERGE
        };
        return new GameModel(positions, types, 50.0, 2500.0);
    }

    private void updateGameModel() {
        gameView.setGameModel(currentModel);
        gameController.setGameModel(currentModel);
    }

    public GameModel getCurrentModel() {
        return currentModel;
    }
}