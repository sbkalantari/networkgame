package com.example.network_game.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ClientManager implements NetworkManager {
    private static final int PING_INTERVAL = 5000; 
    private static final int CONNECTION_TIMEOUT = 10000; 

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ConnectionStatus status;
    private String playerId;
    private String serverHost;
    private int serverPort;

    private NetworkMessageHandler messageHandler;
    private ConnectionStatusHandler statusHandler;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutor;
    private boolean isRunning;

    public ClientManager() {
        this.status = ConnectionStatus.DISCONNECTED;
        this.executorService = Executors.newCachedThreadPool();
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
        this.playerId = generatePlayerId();
        this.isRunning = false;
    }

    @Override
    public boolean startServer(int port) {
        
        return false;
    }

    @Override
    public boolean connectToServer(String host, int port) {
        if (status == ConnectionStatus.CONNECTED || status == ConnectionStatus.CONNECTING) {
            return false;
        }

        this.serverHost = host;
        this.serverPort = port;

        executorService.submit(this::attemptConnection);
        return true;
    }

    private void attemptConnection() {
        setStatus(ConnectionStatus.CONNECTING);

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverHost, serverPort), CONNECTION_TIMEOUT);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            
            NetworkMessage connectionRequest = new NetworkMessage(
                    NetworkMessage.MessageType.CONNECTION_REQUEST,
                    playerId,
                    getMacAddress()
            );
            out.writeObject(connectionRequest);

            
            NetworkMessage response = (NetworkMessage) in.readObject();
            if (response.getType() == NetworkMessage.MessageType.CONNECTION_RESPONSE) {
                Boolean accepted = (Boolean) response.getData();
                if (accepted) {
                    setStatus(ConnectionStatus.CONNECTED);
                    isRunning = true;
                    startMessageListener();
                    startPingScheduler();

                    if (statusHandler != null) {
                        statusHandler.onPlayerConnected(playerId);
                    }
                } else {
                    disconnect();
                    if (statusHandler != null) {
                        statusHandler.onConnectionError("Connection rejected by server");
                    }
                }
            }

        } catch (Exception e) {
            setStatus(ConnectionStatus.ERROR);
            if (statusHandler != null) {
                statusHandler.onConnectionError("Failed to connect: " + e.getMessage());
            }
            disconnect();
        }
    }

    private void startMessageListener() {
        executorService.submit(() -> {
            while (isRunning && socket != null && !socket.isClosed()) {
                try {
                    NetworkMessage message = (NetworkMessage) in.readObject();
                    if (messageHandler != null) {
                        messageHandler.handleMessage(message);
                    }
                } catch (EOFException e) {
                    
                    break;
                } catch (Exception e) {
                    if (isRunning) {
                        System.err.println("Error receiving message: " + e.getMessage());
                    }
                    break;
                }
            }

            if (isRunning) {
                setStatus(ConnectionStatus.DISCONNECTED);
                if (statusHandler != null) {
                    statusHandler.onPlayerDisconnected(playerId);
                }
            }
        });
    }

    private void startPingScheduler() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (status == ConnectionStatus.CONNECTED) {
                NetworkMessage ping = new NetworkMessage(
                        NetworkMessage.MessageType.PING,
                        playerId,
                        System.currentTimeMillis()
                );
                sendMessage(ping);
            }
        }, PING_INTERVAL, PING_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disconnect() {
        isRunning = false;

        try {
            if (out != null && status == ConnectionStatus.CONNECTED) {
                NetworkMessage disconnect = new NetworkMessage(
                        NetworkMessage.MessageType.DISCONNECT,
                        playerId,
                        null
                );
                out.writeObject(disconnect);
            }
        } catch (Exception e) {
            
        }

        closeConnections();
        setStatus(ConnectionStatus.DISCONNECTED);

        if (statusHandler != null) {
            statusHandler.onPlayerDisconnected(playerId);
        }
    }

    private void closeConnections() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            
        }

        out = null;
        in = null;
        socket = null;
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        if (status != ConnectionStatus.CONNECTED || out == null) {
            return;
        }

        try {
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            setStatus(ConnectionStatus.ERROR);
        }
    }

    private void setStatus(ConnectionStatus newStatus) {
        ConnectionStatus oldStatus = this.status;
        this.status = newStatus;

        if (statusHandler != null && oldStatus != newStatus) {
            statusHandler.onConnectionStatusChanged(oldStatus, newStatus);
        }
    }

    private String generatePlayerId() {
        return "Player_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    private String getMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            if (ni != null) {
                byte[] hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        sb.append(String.format("%02X%s", hardwareAddress[i],
                                (i < hardwareAddress.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            
        }
        return "MAC_" + System.currentTimeMillis();
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return status;
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }

    @Override
    public void setMessageHandler(NetworkMessageHandler handler) {
        this.messageHandler = handler;
    }

    @Override
    public void setConnectionStatusHandler(ConnectionStatusHandler handler) {
        this.statusHandler = handler;
    }

    public void shutdown() {
        disconnect();

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdown();
        }
    }
}