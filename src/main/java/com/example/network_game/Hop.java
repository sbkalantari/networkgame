package com.example.network_game;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.Random;

public class Hop implements ValidDevice {

    private ArrayList<Port> ports = new ArrayList<>();
    private Queue<Pocket> pockets = new LinkedList<>();
    private int x, y, width, height;

    private SystemType systemType;
    private boolean active;
    private long lastActivity;
    private double detectionRadius; 

    private Random random = new Random();

    private long deactivationTimer = 0;
    private static final long ANTI_TROJAN_COOLDOWN = 5000; 

    public Hop() {
        this.systemType = SystemType.NORMAL;
        this.active = true;
        this.lastActivity = System.currentTimeMillis();
        this.detectionRadius = 100.0;
    }

    public Hop(SystemType type) {
        this();
        this.systemType = type;
    }

    public Pocket processPacket(Pocket packet, List<Hop> allSystems) {
        
        if (!active) {
            if (deactivationTimer > 0 && System.currentTimeMillis() - deactivationTimer > ANTI_TROJAN_COOLDOWN) {
                active = true; 
                deactivationTimer = 0;
            } else {
                
                return packet;
            }
        }

        lastActivity = System.currentTimeMillis();

        switch (systemType) {
            case SPY:
                return processSpySystem(packet, allSystems);
            case SABOTAGE:
                return processSabotageSystem(packet);
            case VPN:
                return processVPNSystem(packet);
            case ANTI_TROJAN:
                return processAntiTrojanSystem(packet, allSystems);
            case NORMAL:
            case DISTRIBUTE:
            case MERGE:
            default:
                return processNormalSystem(packet);
        }
    }

    
    private Pocket processSpySystem(Pocket packet, List<Hop> allSystems) {
        if (packet.getPacketType() == PacketType.CONFIDENTIAL) {
            packet.setState(PocketState.Lost);
            return packet;
        }
        if (packet.getPacketType() == PacketType.PROTECTED) {
            
            return packet;
        }
        
        List<Hop> spySystems = new ArrayList<>();
        for (Hop system : allSystems) {
            if (system.getSystemType() == SystemType.SPY && system != this && system.isActive()) {
                spySystems.add(system);
            }
        }
        if (!spySystems.isEmpty()) {
            Hop targetSpy = spySystems.get(random.nextInt(spySystems.size()));
            packet.setCurrentHop(targetSpy);
        }
        return packet;
    }

    
    private Pocket processSabotageSystem(Pocket packet) {
        if (packet.getPacketType() == PacketType.PROTECTED) {
            return packet; 
        }

        
        if (packet.getNoiseLevel() == 0) {
            packet.addNoise(1);
        }

        
        if (random.nextDouble() < 0.3) {
            packet.setPacketType(PacketType.TROJAN);
            packet.setRouteToIncompatible(true); 
        }
        return packet;
    }

    
    private Pocket processVPNSystem(Pocket packet) {
        packet.setPacketType(PacketType.PROTECTED);
        packet.setProtectedByVPN(this);
        return packet;
    }

    
    private Pocket processAntiTrojanSystem(Pocket packet, List<Hop> allSystems) {
        Point center = new Point(x + width / 2, y + height / 2);
        if (packet.getPacketType() == PacketType.TROJAN) {
            double distance = Math.sqrt(
                    Math.pow(packet.getCurrentHop().getX() - center.x, 2) +
                            Math.pow(packet.getCurrentHop().getY() - center.y, 2));
            if (distance <= detectionRadius) {
                packet.setPacketType(PacketType.MESSENGER);
                active = false;
                deactivationTimer = System.currentTimeMillis();
                return packet;
            }
        }
        return packet;
    }

    
    private Pocket processNormalSystem(Pocket packet) {
        return packet;
    }

    
    public Color getSystemColor() {
        if (!active) {
            return Color.DARK_GRAY;
        }
        switch (systemType) {
            case SPY:
                return new Color(255, 100, 255); 
            case SABOTAGE:
                return new Color(255, 50, 50); 
            case VPN:
                return new Color(50, 255, 50); 
            case ANTI_TROJAN:
                return new Color(255, 255, 50); 
            case DISTRIBUTE:
                return new Color(100, 100, 255); 
            case MERGE:
                return new Color(255, 165, 0); 
            case NORMAL:
            default:
                return new Color(0, 150, 150); 
        }
    }

    @Override
    public boolean IsValid() {
        for (Port p : ports) {
            if (!p.IsValid()) return false;
        }
        return true;
    }

    

    public void setPosition(int x, int y) { this.x = x; this.y = y;}
    public void setSize(int width, int height) { this.width = width; this.height = height; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public SystemType getSystemType() { return systemType; }
    public void setSystemType(SystemType systemType) { this.systemType = systemType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public long getLastActivity() { return lastActivity; }
    public double getDetectionRadius() { return detectionRadius; }
    public void setDetectionRadius(double radius) { this.detectionRadius = radius; }
    public ArrayList<Port> getPorts() { return ports; }
    public Queue<Pocket> getPockets() { return pockets; }
}
