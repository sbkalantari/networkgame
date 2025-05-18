package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameController {
    private GameModel model;
    private GameView view;
    private Timer animationTimer;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public GameController(GameModel model, GameView view, CardLayout cardLayout, JPanel cardPanel) {
        this.model = model;
        this.view = view;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setupListeners();
        setupPlayButton();
        setupAnimationTimer();
    }

    public void setGameModel(GameModel model) {
        this.model = model;
        stopAnimation();
        view.repaint();
    }

    private void setupListeners() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    double[] targetTime = new double[1];
                    if (model.isTimeBarClicked(e.getPoint(), targetTime)) {
                        boolean isBackward = targetTime[0] < model.getTemporalProgress() / 100.0 * model.getStageDuration();
                        model.jumpToTime(targetTime[0]);
                        view.notifyTimeJump(isBackward);
                    } else {
                        handleLeftClick(e.getPoint());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(e.getPoint());
                }
            }
        });

        view.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                view.setMousePos(e.getPoint());
            }
        });

        view.getReturnToMenuButton().addActionListener(e -> {
            model.resetStage();
            cardLayout.show(cardPanel, "MainMenu");
        });

        view.getNextStageButton().addActionListener(e -> {
            model.resetStage();
            model.startPlaying();
            animationTimer.start();
            view.repaint();
        });

        view.getStoreButton().addActionListener(e -> {
            pauseGame();
            cardLayout.show(cardPanel, "Store");
        });
    }

    private void setupPlayButton() {
        JButton playButton = new JButton("Play");
        playButton.setBounds(10, 70, 80, 30);
        playButton.setFont(new Font("Arial", Font.PLAIN, 12));
        playButton.addActionListener(e -> {
            model.startPlaying();
            animationTimer.start();
            view.repaint();
        });
        view.add(playButton);
    }

    private void setupAnimationTimer() {
        animationTimer = new Timer(16, e -> {
            if (model.isPlaying()) {
                model.updatePockets(0.016);
                view.repaint();
            }
        });
        animationTimer.setRepeats(true);
    }

    public void pauseGame() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        model.setIsPlaying(false);
    }

    public void resumeGame() {
        if (!model.isGameOver() && !model.isLevelPassed()) {
            model.setIsPlaying(true);
            animationTimer.start();
        }
    }

    private void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    private void handleLeftClick(Point clickPoint) {
        if (model.isGameOver() || model.isLevelPassed()) return;
        Port clickedPort = model.findPortAt(clickPoint);
        if (clickedPort != null) {
            if (model.getSelectedPort() == null) {
                model.setSelectedPort(clickedPort);
            } else {
                model.connectPorts(model.getSelectedPort(), clickedPort);
                model.setSelectedPort(null);
            }
            view.repaint();
        }
    }

    private void handleRightClick(Point clickPoint) {
        if (model.isGameOver() || model.isLevelPassed()) return;
        Wire wireToRemove = model.findWireAt(clickPoint);
        model.removeWire(wireToRemove);
        view.repaint();
    }
}