package com.example.network_game.controller;

import com.example.network_game.model.GameModel;
import com.example.network_game.network.*;
import com.example.network_game.view.GameView;
import com.example.network_game.view.MainMenu;

import javax.swing.*;

public class NetworkGameController implements NetworkMessageHandler, ConnectionStatusHandler {
    private NetworkManager networkManager;
    private GameController gameController;
    private GameModel gameModel;
    private GameView gameView;
    private MainMenu mainMenu;
    private boolean isServer;
    private boolean isOfflineMode;


    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8080;

    public NetworkGameController(GameController gameController, GameModel gameModel,
                                 GameView gameView, MainMenu mainMenu) {
        this.gameController = gameController;
        this.gameModel = gameModel;
        this.gameView = gameView;
        this.mainMenu = mainMenu;
        this.isOfflineMode = true;
    }

    public boolean startAsServer(int port) {
        if (networkManager != null) {
            networkManager.disconnect();
        }

        networkManager = new ServerManager();
        networkManager.setMessageHandler(this);
        networkManager.setConnectionStatusHandler(this);



        boolean success = networkManager.startServer(port);
        if (success) {
            isServer = true;
            isOfflineMode = false;
            updateConnectionStatus("Server started on port " + port);
        } else {
            updateConnectionStatus("Failed to start server");
        }

        return success;
    }

    public boolean connectToServer(String host, int port) {
        if (networkManager != null) {
            networkManager.disconnect();
        }

        networkManager = new ClientManager();
        networkManager.setMessageHandler(this);
        networkManager.setConnectionStatusHandler(this);

        boolean success = networkManager.connectToServer(host != null ? host : DEFAULT_SERVER_HOST,
                port > 0 ? port : DEFAULT_SERVER_PORT);
        if (success) {
            isServer = false;
            isOfflineMode = false;
            updateConnectionStatus("Connecting to server...");
        } else {
            updateConnectionStatus("Failed to connect to server");
        }

        return success;
    }

    public boolean connectToServer() {
        return connectToServer(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
    }

    public void disconnect() {
        if (networkManager != null) {
            networkManager.disconnect();
            networkManager = null;
        }
        isOfflineMode = true;
        updateConnectionStatus("Disconnected - Offline Mode");
    }

    public void setOfflineMode() {
        disconnect();
        isOfflineMode = true;
        updateConnectionStatus("Offline Mode");
    }

    @Override
    public void handleMessage(NetworkMessage message) {
        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case GAME_STATE_UPDATE:
                    handleGameStateUpdate(message);
                    break;
                case PLAYER_ACTION:
                    handlePlayerAction(message);
                    break;
                case PLAYER_INPUT:
                    handlePlayerInput(message);
                    break;
                case PING:
                    handlePing(message);
                    break;
                case PONG:
                    handlePong(message);
                    break;
                default:
                    System.out.println("Unhandled message type: " + message.getType());
                    break;
            }
        });
    }

    private void handleGameStateUpdate(NetworkMessage message) {
        // TODO: Implement game state synchronization
        System.out.println("Received game state update from " + message.getPlayerId());
    }

    private void handlePlayerAction(NetworkMessage message) {
        // TODO: Implement player action handling
        System.out.println("Received player action from " + message.getPlayerId());
    }

    private void handlePlayerInput(NetworkMessage message) {
        // TODO: Implement player input handling
        System.out.println("Received player input from " + message.getPlayerId());
    }

    private void handlePing(NetworkMessage message) {

        if (!isServer && networkManager != null) {
            NetworkMessage pong = new NetworkMessage(
                    NetworkMessage.MessageType.PONG,
                    networkManager.getPlayerId(),
                    message.getData()
            );
            networkManager.sendMessage(pong);
        }
    }

    private void handlePong(NetworkMessage message) {

        long pingTime = System.currentTimeMillis() - (Long) message.getData();
        System.out.println("Ping: " + pingTime + "ms");
    }

    @Override
    public void onConnectionStatusChanged(NetworkManager.ConnectionStatus oldStatus,
                                          NetworkManager.ConnectionStatus newStatus) {
        SwingUtilities.invokeLater(() -> {
            String statusMessage;
            switch (newStatus) {
                case CONNECTING:
                    statusMessage = "Connecting...";
                    break;
                case CONNECTED:
                    statusMessage = isServer ? "Server Running" : "Connected to Server";
                    break;
                case RECONNECTING:
                    statusMessage = "Reconnecting...";
                    break;
                case ERROR:
                    statusMessage = "Connection Error";
                    break;
                case DISCONNECTED:
                default:
                    statusMessage = "Disconnected - Offline Mode";
                    isOfflineMode = true;
                    break;
            }
            updateConnectionStatus(statusMessage);
        });
    }

    @Override
    public void onPlayerConnected(String playerId) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Player connected: " + playerId);
            updateConnectionStatus("Player connected: " + playerId);
        });
    }

    @Override
    public void onPlayerDisconnected(String playerId) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Player disconnected: " + playerId);
            updateConnectionStatus("Player disconnected: " + playerId);
        });
    }

    @Override
    public void onConnectionError(String error) {
        SwingUtilities.invokeLater(() -> {
            System.err.println("Connection error: " + error);
            JOptionPane.showMessageDialog(
                    gameView,
                    "Connection Error: " + error,
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE
            );
            setOfflineMode();
        });
    }

    private void updateConnectionStatus(String status) {
        if (mainMenu != null) {
            mainMenu.updateConnectionStatus(status);
        }
        System.out.println("Connection Status: " + status);
    }

    public void sendGameStateUpdate(Object gameState) {
        if (networkManager != null && !isOfflineMode) {
            NetworkMessage message = new NetworkMessage(
                    NetworkMessage.MessageType.GAME_STATE_UPDATE,
                    networkManager.getPlayerId(),
                    gameState
            );
            networkManager.sendMessage(message);
        }
    }

    public void sendPlayerAction(Object action) {
        if (networkManager != null && !isOfflineMode) {
            NetworkMessage message = new NetworkMessage(
                    NetworkMessage.MessageType.PLAYER_ACTION,
                    networkManager.getPlayerId(),
                    action
            );
            networkManager.sendMessage(message);
        }
    }

    public void sendPlayerInput(Object input) {
        if (networkManager != null && !isOfflineMode) {
            NetworkMessage message = new NetworkMessage(
                    NetworkMessage.MessageType.PLAYER_INPUT,
                    networkManager.getPlayerId(),
                    input
            );
            networkManager.sendMessage(message);
        }
    }

    public boolean isConnected() {
        return networkManager != null &&
                networkManager.getConnectionStatus() == NetworkManager.ConnectionStatus.CONNECTED;
    }

    public boolean isServer() {
        return isServer;
    }

    public boolean isOfflineMode() {
        return isOfflineMode;
    }

    public String getPlayerId() {
        return networkManager != null ? networkManager.getPlayerId() : "OfflinePlayer";
    }

    public int getConnectedPlayerCount() {
        if (networkManager instanceof ServerManager) {
            return ((ServerManager) networkManager).getConnectedPlayerCount();
        }
        return isConnected() ? 1 : 0;
    }

    public void shutdown() {
        if (networkManager != null) {
            if (networkManager instanceof ClientManager) {
                ((ClientManager) networkManager).shutdown();
            } else if (networkManager instanceof ServerManager) {
                ((ServerManager) networkManager).shutdown();
            }
        }
    }
}