package com.example.network_game.view;

import com.example.network_game.controller.GameController;
import com.example.network_game.controller.NetworkGameController;
import com.example.network_game.model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameModel gameModel;
    private final GameView gameView;
    private final GameController gameController;
    private NetworkGameController networkController;

    private JLabel connectionStatusLabel;
    private JButton startServerButton;
    private JButton connectToServerButton;
    private JButton offlineModeButton;
    private JButton disconnectButton;
    private JTextField serverHostField;
    private JTextField serverPortField;

    public MainMenu(CardLayout cardLayout, JPanel cardPanel, GameModel gameModel,
                    GameView gameView, GameController gameController) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.gameModel = gameModel;
        this.gameView = gameView;
        this.gameController = gameController;

        initializeNetworking();
        setupUI();
    }

    private void initializeNetworking() {
        networkController = new NetworkGameController(gameController, gameModel, gameView, this);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 50));


        JLabel titleLabel = new JLabel("Network Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));


        connectionStatusLabel = new JLabel("Offline Mode", SwingConstants.CENTER);
        connectionStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        connectionStatusLabel.setForeground(Color.YELLOW);
        connectionStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(connectionStatusLabel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));


        JPanel networkPanel = createNetworkPanel();
        contentPanel.add(networkPanel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));


        JPanel gamePanel = createGamePanel();
        contentPanel.add(gamePanel);

        add(contentPanel, BorderLayout.CENTER);


        updateConnectionStatus("Offline Mode");
    }

    private JPanel createNetworkPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Network Options",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE
        ));


        JPanel serverConfigPanel = new JPanel(new FlowLayout());
        serverConfigPanel.setOpaque(false);

        JLabel hostLabel = new JLabel("Host:");
        hostLabel.setForeground(Color.WHITE);
        serverConfigPanel.add(hostLabel);

        serverHostField = new JTextField("localhost", 10);
        serverConfigPanel.add(serverHostField);

        JLabel portLabel = new JLabel("Port:");
        portLabel.setForeground(Color.WHITE);
        serverConfigPanel.add(portLabel);

        serverPortField = new JTextField("8080", 5);
        serverConfigPanel.add(serverPortField);

        panel.add(serverConfigPanel);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        startServerButton = createMenuButton("Start Server");
        startServerButton.addActionListener(e -> startServer());
        buttonPanel.add(startServerButton);

        connectToServerButton = createMenuButton("Connect to Server");
        connectToServerButton.addActionListener(e -> connectToServer());
        buttonPanel.add(connectToServerButton);

        disconnectButton = createMenuButton("Disconnect");
        disconnectButton.addActionListener(e -> disconnect());
        disconnectButton.setEnabled(false);
        buttonPanel.add(disconnectButton);

        offlineModeButton = createMenuButton("Offline Mode");
        offlineModeButton.addActionListener(e -> setOfflineMode());
        buttonPanel.add(offlineModeButton);

        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Game Options",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton singlePlayerButton = createMenuButton("Single Player");
        singlePlayerButton.addActionListener(e -> startSinglePlayer());
        buttonPanel.add(singlePlayerButton);

        JButton multiPlayerButton = createMenuButton("Multiplayer");
        multiPlayerButton.addActionListener(e -> startMultiplayer());
        buttonPanel.add(multiPlayerButton);

        JButton stagesButton = createMenuButton("Stages");
        stagesButton.addActionListener(e -> cardLayout.show(cardPanel, "Stages"));
        buttonPanel.add(stagesButton);

        JButton settingsButton = createMenuButton("Settings");
        settingsButton.addActionListener(e -> cardLayout.show(cardPanel, "Settings"));
        buttonPanel.add(settingsButton);

        JButton exitButton = createMenuButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        panel.add(buttonPanel);

        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));


        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(100, 150, 200));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private void startServer() {
        String portText = serverPortField.getText().trim();

        int port;
        try {
            port = Integer.parseInt(portText);
            if (port <= 0 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid port number. Please enter a number between 1 and 65535.",
                    "Invalid Port",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = networkController.startAsServer(port);
        if (success) {
            updateNetworkButtonStates(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to start server. Port might be in use.",
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void connectToServer() {
        String host = serverHostField.getText().trim();
        String portText = serverPortField.getText().trim();

        if (host.isEmpty()) {
            host = "localhost";
        }

        int port;
        try {
            port = Integer.parseInt(portText);
            if (port <= 0 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid port number. Please enter a number between 1 and 65535.",
                    "Invalid Port",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = networkController.connectToServer(host, port);
        if (!success) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to server.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnect() {
        networkController.disconnect();
        updateNetworkButtonStates(false);
    }

    private void setOfflineMode() {
        networkController.setOfflineMode();
        updateNetworkButtonStates(false);
    }

    private void startSinglePlayer() {
        if (networkController.isOfflineMode()) {
            cardLayout.show(cardPanel, "Game");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please disconnect from network or switch to offline mode for single player.",
                    "Network Active",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void startMultiplayer() {
        if (!networkController.isConnected()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "You need to be connected to a server for multiplayer.\\n" +
                            "Would you like to start a server or connect to one?",
                    "Network Required",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                startServer();
            } else if (option == JOptionPane.NO_OPTION) {
                connectToServer();
            }
            return;
        }

        // TODO: Start multiplayer game
        JOptionPane.showMessageDialog(this,
                "Multiplayer functionality will be implemented soon (well tonight is the deadline so its never)",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateNetworkButtonStates(boolean connected) {
        startServerButton.setEnabled(!connected);
        connectToServerButton.setEnabled(!connected);
        disconnectButton.setEnabled(connected);
        serverHostField.setEnabled(!connected);
        serverPortField.setEnabled(!connected);
    }

    public void updateConnectionStatus(String status) {
        if (connectionStatusLabel != null) {
            connectionStatusLabel.setText(status);


            boolean connected = networkController.isConnected();
            updateNetworkButtonStates(connected);


            if (status.contains("Connected") || status.contains("Server Running")) {
                connectionStatusLabel.setForeground(Color.GREEN);
            } else if (status.contains("Connecting") || status.contains("Reconnecting")) {
                connectionStatusLabel.setForeground(Color.YELLOW);
            } else if (status.contains("Error") || status.contains("Failed")) {
                connectionStatusLabel.setForeground(Color.RED);
            } else {
                connectionStatusLabel.setForeground(Color.YELLOW);
            }
        }
    }

    public NetworkGameController getNetworkController() {
        return networkController;
    }


    public void cleanup() {
        if (networkController != null) {
            networkController.shutdown();
        }
    }
}