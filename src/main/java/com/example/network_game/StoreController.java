package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class StoreController {
    private StorePanel view;
    private GameModel model;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private GameController gameController;

    public StoreController(StorePanel view, GameModel model, CardLayout cardLayout, JPanel cardPanel, GameController gameController) {
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
        view.getBackButton().addActionListener(e -> {
            gameController.resumeGame();
            cardLayout.show(cardPanel, "Game");
        });
    }

    private void purchaseOAtar() {
        if (model.spendCoins(3)) {
            model.activateOAtar();
            view.updateCoinLabel(model.getCoins());
            JOptionPane.showMessageDialog(view, "O' Atar activated: Impact waves disabled for 10 seconds.");
        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 3 coins.");
        }
    }

    private void purchaseOAiryaman() {
        if (model.spendCoins(4)) {
            model.activateOAiryaman();
            view.updateCoinLabel(model.getCoins());
            JOptionPane.showMessageDialog(view, "O' Airyaman activated: Collisions disabled for 5 seconds.");
        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 4 coins.");
        }
    }

    private void purchaseOAnahita() {
        if (model.spendCoins(5)) {
            model.activateOAnahita();
            view.updateCoinLabel(model.getCoins());
            JOptionPane.showMessageDialog(view, "O' Anahita activated: Packet health reset.");
        } else {
            JOptionPane.showMessageDialog(view, "Not enough coins! Need 5 coins.");
        }
    }
}