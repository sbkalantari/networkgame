package com.example.network_game.model;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Hop implements ValidDevice {
    private static final long ANTI_TROJAN_COOLDOWN = 5000;
    private static final long SYSTEM_DAMAGE_DURATION = 3000;
    private final ArrayList<Port> ports = new ArrayList<>();
    private final Queue<Pocket> pockets = new LinkedList<>();
    private int x, y, width, height;
    private SystemType systemType;
    private boolean active;
    private long lastActivity;
    private double detectionRadius;
    private final Random random = new Random();
    private long deactivationTimer = 0;
    private int packetsProcessed = 0;

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

    public void deactivateTemporarily() {
        this.active = false;
        this.deactivationTimer = System.currentTimeMillis();
    }

    public Pocket processPacket(Pocket packet, List<Hop> allSystems) {

        if (!active && deactivationTimer > 0) {
            long timePassed = System.currentTimeMillis() - deactivationTimer;
            if ((systemType == SystemType.ANTI_TROJAN && timePassed > ANTI_TROJAN_COOLDOWN) ||
                    (systemType != SystemType.ANTI_TROJAN && timePassed > SYSTEM_DAMAGE_DURATION)) {
                active = true;
                deactivationTimer = 0;
            } else {
                return packet;
            }
        }

        if (!active) return packet;

        lastActivity = System.currentTimeMillis();
        packetsProcessed++;

        switch (systemType) {
            case SPY:
                return processSpySystem(packet, allSystems);
            case SABOTAGE:
                return processSabotageSystem(packet);
            case VPN:
                return processVPNSystem(packet);
            case ANTI_TROJAN:
                return processAntiTrojanSystem(packet, allSystems);
            case DISTRIBUTE:
                return processDistributeSystem(packet);
            case MERGE:
                return processMergeSystem(packet);
            case NORMAL:
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
            packet.returnToOriginType();
            return packet;
        }


        List<Hop> activeSpy = new ArrayList<>();
        for (Hop system : allSystems) {
            if (system.getSystemType() == SystemType.SPY && system != this && system.isActive()) {
                activeSpy.add(system);
            }
        }

        if (!activeSpy.isEmpty() && random.nextDouble() < 0.3) {
            Hop targetSpy = activeSpy.get(random.nextInt(activeSpy.size()));
            packet.setCurrentHop(targetSpy);
        }

        return packet;
    }

    private Pocket processSabotageSystem(Pocket packet) {

        if (packet.getPacketType() == PacketType.PROTECTED) {
            packet.returnToOriginType();
            return packet;
        }


        if (packet.getNoiseLevel() == 0) {
            packet.addNoise(1);
        }


        if (random.nextDouble() < 0.3) {
            packet.setPacketType(PacketType.TROJAN);
        }


        packet.setRouteToIncompatible(true);

        return packet;
    }

    private Pocket processVPNSystem(Pocket packet) {

        if (packet.getPacketType().isMessengerType()) {
            packet.setPacketType(PacketType.PROTECTED);
            packet.setProtectedByVPN(this);
        }
        return packet;
    }

    private Pocket processAntiTrojanSystem(Pocket packet, List<Hop> allSystems) {

        Point center = new Point(x + width / 2, y + height / 2);

        for (Hop system : allSystems) {
            double distance = Math.sqrt(
                    Math.pow(system.getX() + system.getWidth() / 2 - center.x, 2) +
                            Math.pow(system.getY() + system.getHeight() / 2 - center.y, 2));

            if (distance <= detectionRadius) {
                for (Pocket p : system.getPockets()) {
                    if (p.getPacketType() == PacketType.TROJAN) {
                        p.setPacketType(PacketType.MESSENGER);


                        active = false;
                        deactivationTimer = System.currentTimeMillis();
                        break;
                    }
                }
            }
        }

        return packet;
    }

    private Pocket processDistributeSystem(Pocket packet) {

        if (packet.getPacketType().isLargePacket()) {
            int packetSize = packet.getPacketType().getSize();


            List<Pocket> bitPackets = new ArrayList<>();
            for (int i = 0; i < packetSize; i++) {
                Pocket bitPacket = new Pocket();
                bitPacket.setPacketType(PacketType.MESSENGER);
                bitPacket.setShape(ShapeType.Square);
                bitPackets.add(bitPacket);
            }


            packet.setPacketType(PacketType.MESSENGER);
            packet.setShape(ShapeType.Square);
        }

        return packet;
    }

    private Pocket processMergeSystem(Pocket packet) {


        return packet;
    }

    private Pocket processNormalSystem(Pocket packet) {

        if (packet.getPacketType().isLargePacket()) {
            pockets.clear();
        }


        if (packet.getPacketType().isLargePacket() && random.nextDouble() < 0.5) {
            for (Port port : ports) {
                if (random.nextDouble() < 0.3) {
                    port.setShape(port.getShape() == ShapeType.Square ?
                            ShapeType.Triangle : ShapeType.Square);
                }
            }
        }

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


    public boolean canMoveTo(int newX, int newY, List<Wire> allWires, List<Hop> allSystems) {

        Rectangle newBounds = new Rectangle(newX, newY, width, height);


        for (Hop system : allSystems) {
            if (system != this) {
                Rectangle otherBounds = new Rectangle(system.getX(), system.getY(),
                        system.getWidth(), system.getHeight());
                if (newBounds.intersects(otherBounds)) {
                    return false;
                }
            }
        }


        double totalLengthChange = 0;
        for (Port port : ports) {
            if (port.getWire() != null) {
                Wire wire = port.getWire();
                Point currentPos = port.getPosition();
                Point newPos = new Point(
                        newX + (currentPos.x - x),
                        newY + (currentPos.y - y)
                );

                Point otherEnd = port.getType() == PortType.Input ?
                        wire.getSource().getPosition() : wire.getDestination().getPosition();

                double currentLength = Math.sqrt(
                        Math.pow(currentPos.x - otherEnd.x, 2) +
                                Math.pow(currentPos.y - otherEnd.y, 2));
                double newLength = Math.sqrt(
                        Math.pow(newPos.x - otherEnd.x, 2) +
                                Math.pow(newPos.y - otherEnd.y, 2));

                totalLengthChange += (newLength - currentLength);
            }
        }

        return totalLengthChange <= 0;
    }

    public void moveTo(int newX, int newY) {
        int deltaX = newX - x;
        int deltaY = newY - y;


        this.x = newX;
        this.y = newY;


        for (Port port : ports) {
            port.setPosition(port.getX() + deltaX, port.getY() + deltaY);


            if (port.getWire() != null) {
                port.getWire().updatePathPoints();
                port.getWire().updateLength();
            }
        }
    }


    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public double getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(double radius) {
        this.detectionRadius = radius;
    }

    public ArrayList<Port> getPorts() {
        return ports;
    }

    public Queue<Pocket> getPockets() {
        return pockets;
    }

    public int getPacketsProcessed() {
        return packetsProcessed;
    }
}