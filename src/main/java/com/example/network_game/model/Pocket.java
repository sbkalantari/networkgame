package com.example.network_game.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Pocket {
    private static final double SPEED_DAMAGE_THRESHOLD = 300.0;
    private static final double ACCELERATION_WIRE_LENGTH_THRESHOLD = 200.0;
    private static final double PACKET_TIMEOUT = 30.0;
    private ShapeType shape;
    private double size;
    private double speed;
    private double currentSpeed = 0;
    private int coinReward;
    private Hop currentHop;
    private Wire currentWire;
    private double progress;
    private PocketState state;
    private final Point shapeOffset;
    private int health;
    private Pocket lastHit;
    private PacketType packetType = PacketType.MESSENGER;
    private int noiseLevel = 0;
    private Hop vpnSource = null;
    private boolean routeToIncompatible = false;
    private final double creationTime;
    private double accelerationFactor = 1.0;
    private boolean hasCollidedOnCurrentWire = false;
    private PacketType originType;
    private MultiplayerGameModel.PlayerType playerType;

    public Pocket() {
        this.progress = 0;
        this.state = PocketState.InSource;
        this.size = 2;
        this.speed = 100;
        this.coinReward = 1;
        this.shapeOffset = new Point(0, 0);
        this.health = 2;
        this.creationTime = System.currentTimeMillis() / 1000.0;
        this.packetType = PacketType.MESSENGER;
        this.originType = PacketType.MESSENGER;
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

    public void updateProgress(GameModel model, double deltaTime) {
        if (currentWire == null || state == PocketState.Lost) return;


        double currentTime = System.currentTimeMillis() / 1000.0;
        if (currentTime - creationTime > PACKET_TIMEOUT) {
            setState(PocketState.Lost);
            return;
        }

        Port destPort = currentWire.getDestination();
        double effectiveSpeed = speed;


        Point packetProgressPoint = currentWire.getPointAt(progress);

        if (!model.isAccelerationFrozen(currentWire, packetProgressPoint)) {

            boolean isCompatible = (destPort.getShape() == shape);


            effectiveSpeed = applyPacketTypeSpeedModifications(effectiveSpeed, isCompatible);


            double wireLength = currentWire.getLength();
            if (wireLength > ACCELERATION_WIRE_LENGTH_THRESHOLD) {
                double accelerationFactor = 1.0 + (wireLength - ACCELERATION_WIRE_LENGTH_THRESHOLD) / 1000.0;
                effectiveSpeed *= accelerationFactor;
            }
        }

        currentSpeed = effectiveSpeed;
        double wireLength = currentWire.getLength();
        progress += (effectiveSpeed * deltaTime) / wireLength;
        if (progress > 1) progress = 1;
    }


    private double applyPacketTypeSpeedModifications(double baseSpeed, boolean isCompatible) {
        switch (packetType) {
            case TEXT_INCOMPATIBLE:
                if (!isCompatible) {
                    return baseSpeed * 2.0;
                }
                return baseSpeed;

            case VIDEO_ACCELERATING:
                if (isCompatible) {
                    return baseSpeed;
                } else {
                    return baseSpeed * accelerationFactor;
                }

            case AUDIO_DECELERATING:
                if (isCompatible) {
                    return baseSpeed * accelerationFactor;
                } else {
                    return baseSpeed * 0.5;
                }

            case CONFIDENTIAL:
                if (hasOtherPacketsInDestination()) {
                    return baseSpeed * 0.7;
                }
                return baseSpeed;

            case PROTECTED:

                return baseSpeed * (0.8 + Math.random() * 0.4);

            case TROJAN:

                return baseSpeed * (0.5 + Math.random());

            case LARGE_SMALL:
            case LARGE_MEDIUM:
            case LARGE_BIG:
            case LARGE_HUGE:

                if (hasOtherPacketsInDestination()) {
                    return baseSpeed * 0.7;
                }
                return baseSpeed;

            default:
                if (!isCompatible) {
                    return shape == ShapeType.Square ? baseSpeed * 0.5 : baseSpeed * 1.5;
                }
                return baseSpeed;
        }
    }

    public boolean willDamageSystem() {
        return currentSpeed > SPEED_DAMAGE_THRESHOLD;
    }

    public void damageDestinationSystem() {
        if (currentWire != null && willDamageSystem()) {
            Hop destSystem = currentWire.getDestination().getHopConnected();
            destSystem.deactivateTemporarily();
        }
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
        if (currentWire == null) return false;
        Hop destHop = currentWire.getDestination().getHopConnected();
        return !destHop.getPockets().isEmpty();
    }


    public void handleCollision(Pocket other) {
        if (packetType == PacketType.AUDIO_DECELERATING && !hasCollidedOnCurrentWire) {

            progress = 1.0 - progress;
            hasCollidedOnCurrentWire = true;


            Port temp = currentWire.getSource();
            currentWire.setSource(currentWire.getDestination());
            currentWire.setDestination(temp);
        }
    }

    public void resetForNewWire() {
        hasCollidedOnCurrentWire = false;
        accelerationFactor = 1.0;
    }


    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        if (packetType == PacketType.PROTECTED){
            this.originType = this.packetType;
        }
        this.packetType = packetType;
        this.coinReward = packetType.getCoinReward();
        this.size = packetType.getSize();
    }

    public void returnToOriginType(){
        this.packetType = this.originType;
    }

    public int getNoiseLevel() {
        return noiseLevel;
    }

    public void addNoise(int noise) {
        this.noiseLevel += noise;
    }

    public Hop getVpnSource() {
        return vpnSource;
    }

    public void setVpnSource(Hop vpnSource) {
        this.vpnSource = vpnSource;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public boolean gotHit(Pocket pocket) {
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


        int shapeSize = (int) (8 * Math.sqrt(packetType.getSize()));

        if (shape == ShapeType.Square) {
            return new Rectangle2D.Double(x - shapeSize / 2, y - shapeSize / 2, shapeSize, shapeSize);
        } else {
            return new Polygon(
                    new int[]{x, x - shapeSize / 2, x + shapeSize / 2},
                    new int[]{y - shapeSize / 2, y + shapeSize / 2, y + shapeSize / 2},
                    3
            );
        }
    }

    public PocketState getState() {
        return state;
    }

    public void setState(PocketState state) {
        this.state = state;
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
            shapeOffset.x += (int) (perpDx * displacement);
            shapeOffset.y += (int) (perpDy * displacement);
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
        resetForNewWire();
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

    public void setPlayerType(MultiplayerGameModel.PlayerType playerType) {
        this.playerType = playerType;
    }

    public MultiplayerGameModel.PlayerType getPlayerType() {
        return playerType;
    }

    public void setAccelerationFactor(double factor) {
        this.accelerationFactor = factor;
    }

    public Color getPlayerColor() {
        if (playerType != null) {
            return playerType.getColor();
        }
        return Color.WHITE;
    }

    public boolean canMoveOnPlayerNetwork(MultiplayerGameModel.PlayerType networkOwner) {
        if (playerType == null) return true;
        return playerType == networkOwner;
    }

}