package com.example.network_game.network;

public interface ConnectionStatusHandler {
    void onConnectionStatusChanged(NetworkManager.ConnectionStatus oldStatus, NetworkManager.ConnectionStatus newStatus);
    void onPlayerConnected(String playerId);
    void onPlayerDisconnected(String playerId);
    void onConnectionError(String error);
}