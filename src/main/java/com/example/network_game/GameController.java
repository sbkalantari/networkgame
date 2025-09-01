package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private GameModel model;
    private boolean beforeStopState;
    private final GameView view;
    private Timer animationTimer;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameStageController stageController;
    private int currentStage;
    private Wire selectedWireForCurveDrag = null;
    private WireCurve selectedCurve = null;

    public GameController(GameModel model, GameView view, CardLayout cardLayout, JPanel cardPanel) {
        this.model = model;
        this.view = view;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.stageController = new GameStageController(new GameStagesPanel(cardLayout, cardPanel), cardLayout, cardPanel, view, this);
        this.currentStage = 1;
        setupListeners();
        setupPlayButton();
        setupAnimationTimer();
    }

    public void setGameModel(GameModel model) {
        this.model = model;
        stopAnimation();
        view.setGameModel(model);
        view.repaint();
    }

    private void setupListeners() {

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Wire wire : model.getWires()) {
                    for (WireCurve curve : wire.getCurves()) {
                        if (curve.contains(e.getPoint())) {
                            selectedWireForCurveDrag = wire;
                            selectedCurve = curve;
                            return;
                        }
                    }
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    double[] targetTime = new double[1];
                    if (model.isTimeBarClicked(e.getPoint(), targetTime)) {
                        double currentProgressTime = model.getTemporalProgress() / 100.0 * model.getStageDuration();
                        boolean isBackward = targetTime[0] < currentProgressTime;
                        if (!model.isGameOver() && !model.isLevelPassed()) {
                            model.jumpToTime(targetTime[0]);
                            view.notifyTimeJump(isBackward);
                        } else {
                            System.out.println("Time jump ignored: Game is over or level passed");
                        }
                    } else {
                        handleLeftClick(e.getPoint());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(e.getPoint());
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    handleMiddleClick(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedWireForCurveDrag = null;
                selectedCurve = null;
            }
        });

        view.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                view.setMousePos(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedCurve != null && selectedWireForCurveDrag != null) {

                    Point originalPos = selectedCurve.getPosition();
                    double originalLength = selectedWireForCurveDrag.getLength();


                    selectedCurve.setPosition(e.getPoint());
                    selectedWireForCurveDrag.updatePathPoints();
                    selectedWireForCurveDrag.updateLength();

                    double newLength = selectedWireForCurveDrag.getLength();
                    double lengthDifference = newLength - originalLength;


                    if (model.getTotalWireLength() + lengthDifference > model.getMaxWireLength()) {

                        selectedCurve.setPosition(originalPos);
                        selectedWireForCurveDrag.updatePathPoints();
                        selectedWireForCurveDrag.updateLength();
                        System.out.println("Wire length limit exceeded, curve movement reverted.");
                    } else {

                        model.updateTotalWireLength(lengthDifference);
                    }

                    view.repaint();
                }
            }
        });

        view.getReturnToMenuButton().addActionListener(e -> {
            model.resetStage();
            currentStage = 1;
            cardLayout.show(cardPanel, "MainMenu");
        });

        view.getNextStageButton().addActionListener(e -> {

            currentStage = (currentStage % 5) + 1;
            stageController.selectStage(currentStage);
            System.out.println("Advanced to Stage " + currentStage);
            view.repaint();
        });

        view.getStoreButton().addActionListener(e -> {
            pauseGame();
            cardLayout.show(cardPanel, "Store");
        });

        view.getPlayButton().addActionListener(e -> {
            if (model.IsValid()) {
                model.startPlaying();
                animationTimer.start();
                view.repaint();
            }
        });
    }

    private void setupPlayButton() {

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
        beforeStopState = model.getPlaying();
        model.setIsPlaying(false);
    }

    public void resumeGame() {
        if (!model.isGameOver() && !model.isLevelPassed()) {
            model.setIsPlaying(beforeStopState);
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
                if (model.connectPorts(model.getSelectedPort(), clickedPort) != null) {
                    AudioManager.playConnectSound();
                    System.out.println("Port connected, connect sound played");
                }
                model.setSelectedPort(null);
            }
            view.repaint();
        }
    }

    private void handleMiddleClick(Point clickPoint) {
        if (model.isGameOver() || model.isLevelPassed()) return;

        Wire wireForCurve = model.findWireAt(clickPoint);
        if (wireForCurve != null && wireForCurve.canAddCurve()) {
            if (model.spendCoins(1)) {
                List<Point> tempPathPoints = new ArrayList<>(wireForCurve.getPathPoints());
                tempPathPoints.add(tempPathPoints.size() - 1, clickPoint);

                double newWireLength = WireValidator.calculateCurvedWireLength(tempPathPoints);
                double lengthDifference = newWireLength - wireForCurve.getLength();


                if (model.getTotalWireLength() + lengthDifference <= model.getMaxWireLength()) {
                    wireForCurve.addCurve(clickPoint, 20.0);

                    model.updateTotalWireLength(lengthDifference);
                    AudioManager.playConnectSound();
                    System.out.println("Wire curve added for 1 coin");
                    view.repaint();
                } else {

                    model.addCoins(1);
                    System.out.println("Wire length limit exceeded, can't add curve.");
                }
            }
        }
    }


    private void handleRightClick(Point clickPoint) {
        if (model.isGameOver() || model.isLevelPassed()) return;
        Wire wireToRemove = model.findWireAt(clickPoint);
        if (wireToRemove != null) {
            model.removeWire(wireToRemove);
            AudioManager.playCutSound();
            System.out.println("Wire removed, cut sound played");
            view.repaint();
        }
    }
}