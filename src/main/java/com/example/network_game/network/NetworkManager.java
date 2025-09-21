package com.example.network_game.network;

public interface NetworkManager {
    enum ConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING,
        ERROR
    }

    boolean startServer(int port);
    boolean connectToServer(String host, int port);
    void disconnect();
    void sendMessage(NetworkMessage message);
    ConnectionStatus getConnectionStatus();
    String getPlayerId();
    void setMessageHandler(NetworkMessageHandler handler);
    void setConnectionStatusHandler(ConnectionStatusHandler handler);
}