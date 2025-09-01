package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class StoreController {
    private final StorePanel view;
    private final GameModel model;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameController gameController;
    private boolean waitingForSystemSelection = false;
    private boolean waitingForWireSelection = false;
    private String currentScrollType = "";

    public StoreController(StorePanel view, GameModel model, CardLayout cardLayout,
                           JPanel cardPanel, GameController gameController) {
        this.view = view;
        this.model = model;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.gameController = gameController;
        setupListeners();
    }

    private void setupListeners() {

        view.getOAtarButton().addActionListener(e -> purchaseOAtar());
        view.getOAiryamanButton().addActionListener(e -> purchaseOAiryaman());
        view.getOAnahitaButton().addActionListener(e -> purchaseOAnahita());


        view.getScrollAergiaButton().addActionListener(e -> purchaseScrollAergia());
        view.getScrollSisyphusButton().addActionListener(e -> purchaseScrollSisyphus());
        view.getScrollEliphasButton().addActionListener(e -> purchaseScrollEliphas());

        view.getBackButton().addActionListener(e -> {
            gameController.resumeGame();
            cardLayout.show(cardPanel, "Game");
        });
    }

    private void purchaseOAtar() {
        if (model.spendCoins(3)) {
            model.activateOAtar();
            view.updateCoinLabel(model.getCoins());
            JOptionPane.showMessageDialog(view,
                    "O' Atar activated: Impact waves disabled for 10 seconds.");
        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 3 coins.");
        }
    }

    private void purchaseOAiryaman() {
        if (model.spendCoins(4)) {
            model.activateOAiryaman();
            view.updateCoinLabel(model.getCoins());
            JOptionPane.showMessageDialog(view,
                    "O' Airyaman activated: Collisions disabled for 5 seconds.");
        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 4 coins.");
        }
    }

    private void purchaseOAnahita() {
        if (model.spendCoins(5)) {
            model.activateOAnahita();
            view.updateCoinLabel(model.getCoins());
            JOptionPane.showMessageDialog(view,
                    "O' Anahita activated: Packet health reset.");
        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 5 coins.");
        }
    }

    private void purchaseScrollAergia() {
        if (model.spendCoins(10)) {
            view.updateCoinLabel(model.getCoins());
            waitingForWireSelection = true;
            currentScrollType = "Aergia";


            gameController.resumeGame();
            cardLayout.show(cardPanel, "Game");

            JOptionPane.showMessageDialog(cardPanel,
                    "Scroll of Aergia purchased! Click on a wire to stop packet acceleration for 20 seconds.",
                    "Scroll Purchased", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 10 coins.");
        }
    }

    private void purchaseScrollSisyphus() {
        if (model.spendCoins(15)) {
            view.updateCoinLabel(model.getCoins());
            waitingForSystemSelection = true;
            currentScrollType = "Sisyphus";


            gameController.resumeGame();
            cardLayout.show(cardPanel, "Game");

            JOptionPane.showMessageDialog(cardPanel,
                    "Scroll of Sisyphus purchased! Click on a system to move it to a new location.",
                    "Scroll Purchased", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 15 coins.");
        }
    }

    private void purchaseScrollEliphas() {
        if (model.spendCoins(20)) {
            view.updateCoinLabel(model.getCoins());
            waitingForWireSelection = true;
            currentScrollType = "Eliphas";


            gameController.resumeGame();
            cardLayout.show(cardPanel, "Game");

            JOptionPane.showMessageDialog(cardPanel,
                    "Scroll of Eliphas purchased! Click on a wire to reset packet center of mass for 30 seconds.",
                    "Scroll Purchased", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 20 coins.");
        }
    }


    public boolean handleWireClick(Wire wire, Point clickPoint) {
        if (!waitingForWireSelection) return false;

        if ("Aergia".equals(currentScrollType)) {
            model.activateScrollAergia(wire, clickPoint);
            JOptionPane.showMessageDialog(cardPanel,
                    "Scroll of Aergia activated! Packet acceleration stopped at this point for 20 seconds.");
        } else if ("Eliphas".equals(currentScrollType)) {
            model.activateScrollEliphas(wire, clickPoint);
            JOptionPane.showMessageDialog(cardPanel,
                    "Scroll of Eliphas activated! Packet center of mass will be reset at this point for 30 seconds.");
        }

        waitingForWireSelection = false;
        currentScrollType = "";
        return true;
    }


    public boolean isWaitingForWireSelection() {
        return waitingForWireSelection;
    }

    public boolean isWaitingForSystemSelection() {
        return waitingForSystemSelection;
    }

    public String getCurrentScrollType() {
        return currentScrollType;
    }


    public void cancelSelection() {
        waitingForWireSelection = false;
        waitingForSystemSelection = false;
        currentScrollType = "";
    }
}