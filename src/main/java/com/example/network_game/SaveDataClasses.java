package com.example.network_game;

import java.io.Serializable;
import java.util.List;


class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    long timestamp;
    double currentTime;
    double stageDuration;
    double maxWireLength;
    double totalWireLength;
    int coins;
    int totalPackets;
    int lostPackets;
    boolean isPlaying;
    boolean isGameOver;
    boolean isLevelPassed;

    List<SystemSaveData> systems;
    List<WireSaveData> wires;
    List<PacketSaveData> packets;

    String checksum;
}


class SystemSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    int x, y, width, height;
    SystemType systemType;
    boolean active;
    int packetsProcessed;
    List<PortSaveData> ports;
}


class PortSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    int x, y;
    PortType type;
    ShapeType shape;
    boolean hasWire;
}


class WireSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    int sourceSystemIndex;
    int sourcePortIndex;
    int destSystemIndex;
    int destPortIndex;
    double length;
    List<CurveSaveData> curves;
}


class CurveSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    int x, y;
    double radius;
    boolean active;
}


class PacketSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    ShapeType shapeType;
    PacketType packetType;
    double progress;
    PocketState state;
    int noiseLevel;
    double currentSpeed;
    int currentSystemIndex = -1;
    int currentWireIndex = -1;
}