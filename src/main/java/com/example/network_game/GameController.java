package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameController {
    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftClick(e.getPoint());
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
    }

    private void handleLeftClick(Point clickPoint) {
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
        Wire wireToRemove = model.findWireAt(clickPoint);
        model.removeWire(wireToRemove);
        view.repaint();
    }
}