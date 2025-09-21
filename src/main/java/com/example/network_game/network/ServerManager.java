package com.example.network_game.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager implements NetworkManager {
    private static final int MAX_PLAYERS = 10;

    private ServerSocket serverSocket;
    private ConnectionStatus status;
    private String serverId;
    private int port;

    private Map<String, ClientConnection> connectedClients;
    private NetworkMessageHandler messageHandler;
    private ConnectionStatusHandler statusHandler;
    private ExecutorService executorService;
    private boolean isRunning;

    public ServerManager() {
        this.status = ConnectionStatus.DISCONNECTED;
        this.connectedClients = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        this.serverId = "Server_" + System.currentTimeMillis();
        this.isRunning = false;
    }

    @Override
    public boolean startServer(int port) {
        if (status != ConnectionStatus.DISCONNECTED) {
            return false;
        }

        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
            setStatus(ConnectionStatus.CONNECTED);
            isRunning = true;

            
            executorService.submit(this::acceptConnections);

            System.out.println("Server started on port " + port);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            setStatus(ConnectionStatus.ERROR);
            return false;
        }
    }

    private void acceptConnections() {
        while (isRunning && serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();

                if (connectedClients.size() >= MAX_PLAYERS) {
                    
                    rejectConnection(clientSocket, "Server full");
                    continue;
                }

                
                executorService.submit(() -> handleNewClient(clientSocket));

            } catch (Exception e) {
                if (isRunning) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        }
    }

    private void handleNewClient(Socket clientSocket) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            
            NetworkMessage request = (NetworkMessage) in.readObject();

            if (request.getType() == NetworkMessage.MessageType.CONNECTION_REQUEST) {
                String playerId = request.getPlayerId();
                String macAddress = (String) request.getData();

                
                NetworkMessage response = new NetworkMessage(
                        NetworkMessage.MessageType.CONNECTION_RESPONSE,
                        serverId,
                        true
                );
                out.writeObject(response);

                
                ClientConnection clientConn = new ClientConnection(
                        playerId, macAddress, clientSocket, in, out
                );

                connectedClients.put(playerId, clientConn);

                
                startClientMessageHandler(clientConn);

                
                if (statusHandler != null) {
                    statusHandler.onPlayerConnected(playerId);
                }

                System.out.println("Player connected: " + playerId);

            } else {
                rejectConnection(clientSocket, "Invalid connection request");
            }

        } catch (Exception e) {
            System.err.println("Error handling new client: " + e.getMessage());
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                clientSocket.close();
            } catch (Exception closeEx) {
                
            }
        }
    }

    private void rejectConnection(Socket clientSocket, String reason) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            NetworkMessage rejection = new NetworkMessage(
                    NetworkMessage.MessageType.CONNECTION_RESPONSE,
                    serverId,
                    false
            );
            out.writeObject(rejection);
            out.close();
            clientSocket.close();

            System.out.println("Connection rejected: " + reason);

        } catch (Exception e) {
            
        }
    }

    private void startClientMessageHandler(ClientConnection clientConn) {
        executorService.submit(() -> {
            while (isRunning && !clientConn.socket.isClosed()) {
                try {
                    NetworkMessage message = (NetworkMessage) clientConn.in.readObject();

                    if (message.getType() == NetworkMessage.MessageType.PING) {
                        
                        NetworkMessage pong = new NetworkMessage(
                                NetworkMessage.MessageType.PONG,
                                serverId,
                                message.getData()
                        );
                        clientConn.out.writeObject(pong);
                        clientConn.out.flush();

                    } else if (message.getType() == NetworkMessage.MessageType.DISCONNECT) {
                        break;

                    } else {
                        
                        if (messageHandler != null) {
                            messageHandler.handleMessage(message);
                        }
                    }

                } catch (EOFException e) {
                    
                    break;
                } catch (Exception e) {
                    if (isRunning) {
                        System.err.println("Error handling client message: " + e.getMessage());
                    }
                    break;
                }
            }

            
            disconnectClient(clientConn.playerId);
        });
    }

    private void disconnectClient(String playerId) {
        ClientConnection clientConn = connectedClients.remove(playerId);
        if (clientConn != null) {
            try {
                clientConn.socket.close();
            } catch (Exception e) {
                
            }

            if (statusHandler != null) {
                statusHandler.onPlayerDisconnected(playerId);
            }

            System.out.println("Player disconnected: " + playerId);
        }
    }

    @Override
    public boolean connectToServer(String host, int port) {
        
        return false;
    }

    @Override
    public void disconnect() {
        isRunning = false;

        
        for (String playerId : connectedClients.keySet()) {
            disconnectClient(playerId);
        }

        
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            
        }

        setStatus(ConnectionStatus.DISCONNECTED);
        System.out.println("Server stopped");
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        
        broadcastMessage(message);
    }

    public void sendMessageToPlayer(String playerId, NetworkMessage message) {
        ClientConnection clientConn = connectedClients.get(playerId);
        if (clientConn != null) {
            try {
                clientConn.out.writeObject(message);
                clientConn.out.flush();
            } catch (Exception e) {
                System.err.println("Error sending message to player " + playerId + ": " + e.getMessage());
                disconnectClient(playerId);
            }
        }
    }

    public void broadcastMessage(NetworkMessage message) {
        for (ClientConnection clientConn : connectedClients.values()) {
            try {
                clientConn.out.writeObject(message);
                clientConn.out.flush();
            } catch (Exception e) {
                System.err.println("Error broadcasting to " + clientConn.playerId + ": " + e.getMessage());
                disconnectClient(clientConn.playerId);
            }
        }
    }

    private void setStatus(ConnectionStatus newStatus) {
        ConnectionStatus oldStatus = this.status;
        this.status = newStatus;

        if (statusHandler != null && oldStatus != newStatus) {
            statusHandler.onConnectionStatusChanged(oldStatus, newStatus);
        }
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return status;
    }

    @Override
    public String getPlayerId() {
        return serverId;
    }

    @Override
    public void setMessageHandler(NetworkMessageHandler handler) {
        this.messageHandler = handler;
    }

    @Override
    public void setConnectionStatusHandler(ConnectionStatusHandler handler) {
        this.statusHandler = handler;
    }

    public int getConnectedPlayerCount() {
        return connectedClients.size();
    }

    public Map<String, String> getConnectedPlayers() {
        Map<String, String> players = new ConcurrentHashMap<>();
        for (Map.Entry<String, ClientConnection> entry : connectedClients.entrySet()) {
            players.put(entry.getKey(), entry.getValue().macAddress);
        }
        return players;
    }

    public void shutdown() {
        disconnect();

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    
    private static class ClientConnection {
        final String playerId;
        final String macAddress;
        final Socket socket;
        final ObjectInputStream in;
        final ObjectOutputStream out;

        ClientConnection(String playerId, String macAddress, Socket socket,
                         ObjectInputStream in, ObjectOutputStream out) {
            this.playerId = playerId;
            this.macAddress = macAddress;
            this.socket = socket;
            this.in = in;
            this.out = out;
        }
    }
}