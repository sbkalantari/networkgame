package com.example.network_game.network;

import java.io.Serializable;
import java.util.Map;

public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        CONNECTION_REQUEST,
        CONNECTION_RESPONSE,
        GAME_STATE_UPDATE,
        PLAYER_ACTION,
        PLAYER_INPUT,
        PING,
        PONG,
        DISCONNECT,
        ERROR
    }

    private MessageType type;
    private String playerId;
    private Object data;
    private Map<String, Object> metadata;
    private long timestamp;

    public NetworkMessage(MessageType type, String playerId, Object data) {
        this.type = type;
        this.playerId = playerId;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
