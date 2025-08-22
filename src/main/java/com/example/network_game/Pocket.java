package com.example.network_game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Pocket {
    private ShapeType shape;
    private double size;
    private double speed;
    private double currentSpeed = 0; 
    private int coinReward;
    private Hop currentHop;
    private Wire currentWire;
    private double progress;
    private PocketState state;
    private Point shapeOffset;
    private int health;
    private Pocket lastHit;
    private PacketType packetType = PacketType.MESSENGER;
    private int noiseLevel = 0;
    private Hop vpnSource = null;
    private boolean routeToIncompatible = false;


    private static final double SPEED_DAMAGE_THRESHOLD = 300.0; 
    private static final double ACCELERATION_WIRE_LENGTH_THRESHOLD = 200.0; 

    public Pocket() {
        this.progress = 0;
        this.state = PocketState.InSource;
        this.size = 2;
        this.speed = 100;
        this.coinReward = 1;
        this.shapeOffset = new Point(0, 0);
        this.health = 2;
    }

    public void setShape(ShapeType shape) {
        this.shape = shape;
        if (shape == ShapeType.Square) {
            this.size = 2;
            this.coinReward = 1;
            this.speed = 100;
        } else {
            this.size = 3;
            this.coinReward = 2;
            this.speed = 120;
        }
    }

    public void updateProgress(double deltaTime) {
        if (currentWire == null || state == PocketState.Lost) return;

        Port destPort = currentWire.getDestination();
        double effectiveSpeed = speed;

        
        if (destPort.getShape() != shape) {
            if (shape == ShapeType.Square) {
                effectiveSpeed *= 0.5; 
            } else {
                effectiveSpeed *= 1.5; 
            }
        }

        
        switch (packetType) {
            case CONFIDENTIAL:
                
                if (hasOtherPacketsInDestination()) {
                    effectiveSpeed *= 0.7;
                }
                break;
            case PROTECTED:
                
                effectiveSpeed *= (0.8 + Math.random() * 0.4); 
                break;
            case TROJAN:
                
                effectiveSpeed *= (0.5 + Math.random() * 1.0); 
                break;
        }

        double wireLength = currentWire.getLength();

        
        if (wireLength > ACCELERATION_WIRE_LENGTH_THRESHOLD) {
            double accelerationFactor = 1.0 + (wireLength - ACCELERATION_WIRE_LENGTH_THRESHOLD) / 1000.0;
            effectiveSpeed *= accelerationFactor;
        }

        
        currentSpeed = effectiveSpeed;

        progress += (effectiveSpeed * deltaTime) / wireLength;
        if (progress > 1) progress = 1;
    }

    
    public boolean willDamageSystem() {
        return currentSpeed > SPEED_DAMAGE_THRESHOLD;
    }

    







    public boolean isRouteToIncompatible() {
        return routeToIncompatible;
    }

    public void setRouteToIncompatible(boolean routeToIncompatible) {
        this.routeToIncompatible = routeToIncompatible;
    }

    public void setProtectedByVPN(Hop vpnSource) {
        this.vpnSource = vpnSource;
    }
    private boolean hasOtherPacketsInDestination() {
        
        return false;
    }

    
    public PacketType getPacketType() { return packetType; }
    public void setPacketType(PacketType packetType) { this.packetType = packetType; }
    public int getNoiseLevel() { return noiseLevel; }
    public void addNoise(int noise) { this.noiseLevel += noise; }
    public Hop getVpnSource() { return vpnSource; }
    public void setVpnSource(Hop vpnSource) { this.vpnSource = vpnSource; }
    public double getCurrentSpeed() { return currentSpeed; }

    
    public boolean gotHit(Pocket pocket) {
        System.out.println("got hit");
        this.health--;
        this.lastHit = pocket;
        if (health <= 0) {
            state = PocketState.Lost;
            return true;
        }
        return false;
    }

    public void resetHealth() {
        this.health = 2;
        this.lastHit = null;
    }

    public Shape getShape(Point center) {
        int x = center.x + shapeOffset.x;
        int y = center.y + shapeOffset.y;
        if (shape == ShapeType.Square) {
            return new Rectangle2D.Double(x - 8, y - 8, 16, 16);
        } else {
            return new Polygon(
                    new int[]{x, x - 10, x + 10},
                    new int[]{y - 10, y + 10, y + 10},
                    3
            );
        }
    }

    public void setState(PocketState state) {
        this.state = state;
    }

    public PocketState getState() {
        return state;
    }

    public Point getCenterOfMass() {
        if (currentWire == null) return null;
        return currentWire.getPointAt(progress);
    }

    public void applyImpactDisplacement(Point collisionPoint, Point src, Point dest) {
        if (currentWire == null || state != PocketState.InProgress) return;

        Point center = getCenterOfMass();
        double distance = Math.sqrt(Math.pow(center.x - collisionPoint.x, 2) + Math.pow(center.y - collisionPoint.y, 2));
        double radius = 100.0;
        if (distance < radius && distance > 0) {
            double strength = (radius - distance) / radius;
            double maxDisplacement = 5.0;
            double wireDx = dest.x - src.x;
            double wireDy = dest.y - src.y;
            double wireLength = Math.sqrt(wireDx * wireDx + wireDy * wireDy);
            if (wireLength == 0) return;

            double perpDx = -wireDy / wireLength;
            double perpDy = wireDx / wireLength;
            double dx = center.x - collisionPoint.x;
            double dy = center.y - collisionPoint.y;
            double dot = dx * perpDx + dy * perpDy;
            double displacement = strength * maxDisplacement * Math.signum(dot);

            shapeOffset.x += (int)(perpDx * displacement);
            shapeOffset.y += (int)(perpDy * displacement);
        }
    }

    public boolean isCenterOfMassOutsideShape() {
        if (currentWire == null) return false;
        Point center = getCenterOfMass();
        Shape shape = getShape(center);
        return !shape.contains(center.x, center.y);
    }

    public ShapeType getShapeType() {
        return shape;
    }

    public double getSize() {
        return size;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public Hop getCurrentHop() {
        return currentHop;
    }

    public void setCurrentHop(Hop hop) {
        this.currentHop = hop;
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public void setCurrentWire(Wire wire) {
        this.currentWire = wire;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Pocket getLastHit() {
        return lastHit;
    }

    public void setLastHit(Pocket lastHit) {
        this.lastHit = lastHit;
    }
}