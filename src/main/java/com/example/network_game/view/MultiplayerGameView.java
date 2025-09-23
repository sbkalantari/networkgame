package com.example.network_game.view;

import com.example.network_game.model.*;
import com.example.network_game.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Map;

public class MultiplayerGameView extends GameView {
    private MultiplayerGameModel multiplayerModel;
    private ControllableReferenceSystem selectedSystem;
    private PacketType selectedPacketType;
    private Point mousePosition;


    private JButton readyButton;
    private JButton extendTimeButton;
    private JLabel setupTimerLabel;
    private JLabel playerScoreLabel;
    private JLabel gameStateLabel;

    public MultiplayerGameView(MultiplayerGameModel model, CardLayout cardLayout, JPanel cardPanel) {
        super(model, cardLayout, cardPanel);
        this.multiplayerModel = model;
        setupMultiplayerUI();
    }

    @Override
    public void setMousePos(Point point) {
        super.setMousePos(point);
        handleControllableSystemInteraction(point);
    }

    private void togglePlayerReady() {
        boolean newReadyState = !multiplayerModel.isLocalPlayerReady();
        multiplayerModel.setPlayerReady(newReadyState);
        updateMultiplayerUI();
    }

    private void requestExtendedTime() {
        multiplayerModel.requestExtendedTime();
        updateMultiplayerUI();
    }

    private void setupMultiplayerUI() {

        readyButton = new JButton("Ready");
        readyButton.setBounds(650, 50, 80, 30);
        readyButton.addActionListener(e -> togglePlayerReady());
        add(readyButton);

        extendTimeButton = new JButton("Extend Time");
        extendTimeButton.setBounds(650, 90, 100, 30);
        extendTimeButton.addActionListener(e -> requestExtendedTime());
        add(extendTimeButton);

        setupTimerLabel = new JLabel("Setup Time: 30");
        setupTimerLabel.setBounds(650, 130, 120, 20);
        setupTimerLabel.setForeground(Color.WHITE);
        add(setupTimerLabel);

        playerScoreLabel = new JLabel("Score: 0 - 0");
        playerScoreLabel.setBounds(650, 150, 120, 20);
        playerScoreLabel.setForeground(Color.WHITE);
        add(playerScoreLabel);

        gameStateLabel = new JLabel("Waiting for players...");
        gameStateLabel.setBounds(650, 170, 150, 20);
        gameStateLabel.setForeground(Color.YELLOW);
        add(gameStateLabel);

        updateMultiplayerUI();
    }

    private void handleControllableSystemInteraction(Point point) {

        for (ControllableReferenceSystem system : multiplayerModel.getControllableSystems()) {
            if (system.contains(point)) {

                selectedSystem = system;
                System.out.println("Controllable system selected: " + system.getId());
                break;
            }
        }
    }


    private void updateMultiplayerUI() {
        MultiplayerGameModel.MultiplayerGameState state = multiplayerModel.getGameState();


        readyButton.setText(multiplayerModel.isLocalPlayerReady() ? "Not Ready" : "Ready");
        readyButton.setEnabled(
                state == MultiplayerGameModel.MultiplayerGameState.NETWORK_SETUP_PHASE ||
                        state == MultiplayerGameModel.MultiplayerGameState.EXTENDED_SETUP_PHASE
        );


        extendTimeButton.setEnabled(state == MultiplayerGameModel.MultiplayerGameState.NETWORK_SETUP_PHASE);


        long remainingTimeSeconds = multiplayerModel.getRemainingSetupTime() / 1000;
        setupTimerLabel.setText("Setup Time: " + remainingTimeSeconds);


        Map<MultiplayerGameModel.PlayerType, Integer> scores = multiplayerModel.getPlayerScores();
        int p1Score = scores.getOrDefault(MultiplayerGameModel.PlayerType.PLAYER_ONE, 0);
        int p2Score = scores.getOrDefault(MultiplayerGameModel.PlayerType.PLAYER_TWO, 0);
        playerScoreLabel.setText("Score: " + p1Score + " - " + p2Score);


        switch (state) {
            case WAITING_FOR_PLAYERS:
                gameStateLabel.setText("Waiting for players...");
                break;
            case NETWORK_SETUP_PHASE:
                gameStateLabel.setText("Build your network!");
                break;
            case EXTENDED_SETUP_PHASE:
                gameStateLabel.setText("Extended time active");
                break;
            case GAME_ACTIVE:
                gameStateLabel.setText("Game in progress");
                break;
            case GAME_FINISHED:
                gameStateLabel.setText("Game finished");
                break;
            default:
                gameStateLabel.setText("Unknown state");
                break;
        }

        repaint();
    }
}