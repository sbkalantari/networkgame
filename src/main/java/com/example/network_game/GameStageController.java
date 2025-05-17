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

    private GameModel createStage1Model() {
        
        int[][] systemPositions = {
                {100, 100}, 
                {500, 100}, 
                {100, 400}, 
                {500, 400}  
        };
        return new GameModel(systemPositions, 60.0, 3000.0);
    }

    private GameModel createStage2Model() {
        
        int[][] systemPositions = {
                {150, 150}, 
                {450, 150}, 
                {150, 450}, 
                {450, 450}  
        };
        return new GameModel(systemPositions, 45.0, 2000.0);
    }

    private void updateGameModel() {
        
        gameView.setGameModel(currentModel);
        gameController.setGameModel(currentModel);
    }

    public GameModel getCurrentModel() {
        return currentModel;
    }
}