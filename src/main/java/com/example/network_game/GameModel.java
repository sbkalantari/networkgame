package com.example.network_game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class GameModel implements ValidDevice {
    private List<Hop> systems;
    private List<Wire> wires;
    private List<Pocket> pockets;
    private Port selectedPort;
    private boolean isPlaying;
    private double maxWireLength;
    private double totalWireLength;
    private int totalPackets;
    private int lostPackets;
    private int coins;
    private double stageDuration;
    private double currentTime;
    private int inProgress;
    private boolean isGameOver;
    private boolean isLevelPassed;
    private boolean isOAtarActive;
    private boolean isOAiryamanActive;
    private Timer oAtarTimer;
    private Timer oAiryamanTimer;

    public GameModel() {
        this(new int[][]{{100, 100}, {500, 100}, {100, 400}, {500, 400}}, 60.0, 3000.0);
    }

    public GameModel(int[][] systemPositions, double stageDuration, double maxWireLength) {
        systems = new ArrayList<>();
        wires = new ArrayList<>();
        pockets = new ArrayList<>();
        selectedPort = null;
        isPlaying = false;
        this.maxWireLength = maxWireLength;
        totalWireLength = 0.0;
        totalPackets = 0;
        lostPackets = 0;
        coins = 0;
        this.stageDuration = stageDuration;
        currentTime = 0.0;
        inProgress = 0;
        isGameOver = false;
        isLevelPassed = false;
        isOAtarActive = false;
        isOAiryamanActive = false;
        initializeSystems(systemPositions);
    }

    private void initializeSystems(int[][] systemPositions) {
        for (int[] pos : systemPositions) {
            systems.add(createSystem(pos[0], pos[1], 2, 2));
        }
    }

    private Hop createSystem(int x, int y, int inputPorts, int outputPorts) {
        Hop system = new Hop();
        int systemWidth = 100;
        int systemHeight = 80;
        int offset = 10;

        double inputStep = inputPorts > 1 ? (systemHeight - 2 * offset) / (double)(inputPorts - 1) : 0;
        double outputStep = outputPorts > 1 ? (systemHeight - 2 * offset) / (double)(outputPorts - 1) : 0;

        
        for (int i = 0; i < inputPorts; i++) {
            Port port = new Port();
            port.setType(PortType.Input);
            port.setShape(i == 0 ? ShapeType.Square : ShapeType.Triangle);
            port.setHopConnected(system);
            int portY = inputPorts == 1 ? y + systemHeight / 2 : y + offset + (int)(i * inputStep);
            port.setPosition(x, portY);
            system.ports.add(port);
        }

        
        for (int i = 0; i < outputPorts; i++) {
            Port port = new Port();
            port.setType(PortType.Output);
            port.setShape(i == 0 ? ShapeType.Square : ShapeType.Triangle);
            port.setHopConnected(system);
            int portY = outputPorts == 1 ? y + systemHeight / 2 : y + offset + (int)(i * outputStep);
            port.setPosition(x + systemWidth, portY);
            system.ports.add(port);
        }

        system.setPosition(x, y);
        system.setSize(systemWidth, systemHeight);
        return system;
    }

    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void activateOAtar() {
        isOAtarActive = true;
        if (oAtarTimer != null && oAtarTimer.isRunning()) {
            oAtarTimer.stop();
        }
        oAtarTimer = new Timer(10000, e -> {
            isOAtarActive = false;
            oAtarTimer = null;
        });
        oAtarTimer.setRepeats(false);
        oAtarTimer.start();
    }

    public void activateOAiryaman() {
        isOAiryamanActive = true;
        if (oAiryamanTimer != null && oAiryamanTimer.isRunning()) {
            oAiryamanTimer.stop();
        }
        oAiryamanTimer = new Timer(5000, e -> {
            isOAiryamanActive = false;
            oAiryamanTimer = null;
        });
        oAiryamanTimer.setRepeats(false);
        oAiryamanTimer.start();
    }

    public void activateOAnahita() {
        for (Pocket pocket : pockets) {
            pocket.resetHealth();
        }
    }

    public boolean isOAtarActive() {
        return isOAtarActive;
    }

    public boolean isOAiryamanActive() {
        return isOAiryamanActive;
    }

    public void startPlaying() {
        if (!isPlaying) {
            isPlaying = true;
            pockets.clear();
            Hop referenceSystem = systems.get(0);
            for (int i = 0; i < 100; i++) {
                Pocket pocket = new Pocket();
                pocket.setShape(i % 2 == 0 ? ShapeType.Square : ShapeType.Triangle);
                pocket.setCurrentHop(referenceSystem);
                pockets.add(pocket);
            }
            totalPackets = pockets.size();
            lostPackets = 0;
            coins = 0;
            currentTime = 0.0;
            inProgress = 0;
            isGameOver = false;
            isLevelPassed = false;
            isOAtarActive = false;
            isOAiryamanActive = false;
            if (oAtarTimer != null) oAtarTimer.stop();
            if (oAiryamanTimer != null) oAiryamanTimer.stop();
        }
    }

    public void updatePockets(double deltaTime) {
        if (!isPlaying || isGameOver || isLevelPassed) return;

        currentTime += deltaTime;
        if (currentTime >= stageDuration) {
            currentTime = stageDuration;
            isPlaying = false;
            isGameOver = getTotalSent() > 0 && (lostPackets / (double) getTotalSent()) > 0.5;
            isLevelPassed = !isGameOver;
            if (isLevelPassed)
                AudioManager.playStageCompleteSound();
            return;
        }

        List<Pocket> pocketsToRemove = new ArrayList<>();

        if (!isOAiryamanActive) {
            for (int i = 0; i < pockets.size(); i++) {
                Pocket p1 = pockets.get(i);
                if (p1.getState() != PocketState.InProgress || p1.getCurrentWire() == null) continue;
                boolean safe = true;
                for (int j = i + 1; j < pockets.size(); j++) {
                    Pocket p2 = pockets.get(j);
                    if (p2.getState() != PocketState.InProgress || p2.getCurrentWire() == null) continue;
                    Point c1 = p1.getCenterOfMass(p1.getCurrentWire().getSource().getPosition(), p1.getCurrentWire().getDestination().getPosition());
                    Point c2 = p2.getCenterOfMass(p2.getCurrentWire().getSource().getPosition(), p2.getCurrentWire().getDestination().getPosition());
                    if (c1 != null && c2 != null && p1.getShape(c1).intersects(p2.getShape(c2).getBounds2D())) {
                        safe = false;
                        if (p1.getLastHit() == p2) {
                            continue;
                        }
                        if (p1.gotHit(p2))
                            pocketsToRemove.add(p1);
                        if (p2.gotHit(p1))
                            pocketsToRemove.add(p2);
                        Point collisionPoint = new Point((c1.x + c2.x) / 2, (c1.y + c2.y) / 2);
                        if (!isOAtarActive) {
                            for (Pocket nearby : pockets) {
                                if (nearby.getState() == PocketState.InProgress && nearby.getCurrentWire() != null) {
                                    nearby.applyImpactDisplacement(
                                            collisionPoint,
                                            nearby.getCurrentWire().getSource().getPosition(),
                                            nearby.getCurrentWire().getDestination().getPosition()
                                    );
                                }
                            }
                        }
                        break;
                    }
                }
                if (safe) {
                    p1.setLastHit(null);
                }
            }
        }

        for (Pocket pocket : pockets) {
            if (pocket.getState() == PocketState.InProgress && pocket.getCurrentWire() != null && pocket.isCenterOfMassOutsideShape()) {
                pocket.setState(PocketState.Lost);
                pocketsToRemove.add(pocket);
            }
        }

        boolean found = false;
        for (Pocket pocket : pocketsToRemove) {
            if (pocket.getState() != PocketState.Lost) continue;
            lostPackets++;
            inProgress--;
            found = true;
        }
        if (found) {
            AudioManager.playDamageSound();
        }

        pockets.removeAll(pocketsToRemove);
        pocketsToRemove.clear();

        for (Pocket pocket : pockets) {
            if (pocket.getState() == PocketState.Lost || pocket.getState() == PocketState.Delivered) continue;
            if (pocket.getCurrentHop() != null) {
                Port outputPort = chooseOutputPort(pocket);
                if (outputPort != null && outputPort.getWire() != null && outputPort.getWire().IsValid()) {
                    pocket.setCurrentWire(outputPort.getWire());
                    pocket.setProgress(0);
                    pocket.setCurrentHop(null);
                    coins += pocket.getCoinReward();
                    if (pocket.getState() == PocketState.InSource) {
                        pocket.setState(PocketState.InProgress);
                        inProgress++;
                    }
                }
            } else if (pocket.getCurrentWire() != null) {
                pocket.updateProgress(deltaTime);
                if (pocket.getProgress() >= 1) {
                    Port destPort = pocket.getCurrentWire().getDestination();
                    Hop destHop = destPort.getHopConnected();
                    if (destHop.ports.size() <= 5) {
                        pocket.setCurrentHop(destHop);
                        pocket.setCurrentWire(null);
                        coins += pocket.getCoinReward();
                        if (destHop == systems.get(0)) {
                            pocket.setState(PocketState.Delivered);
                            inProgress--;
                        }
                    } else {
                        pocketsToRemove.add(pocket);
                    }
                }
            }
        }
        pockets.removeAll(pocketsToRemove);

        if (getTotalSent() > 0 && (lostPackets / (double) getTotalSent()) > 0.5) {
            isGameOver = true;
            isPlaying = false;
        }

        if (inProgress != 0 && (getTotalSent() == 0 || (lostPackets / (double) getTotalSent()) <= 0.5)) {
            found = false;
            for (Pocket pocket : pockets) {
                if (pocket.getState() == PocketState.InProgress) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                isLevelPassed = true;
                isPlaying = false;
                AudioManager.playStageCompleteSound();
            }
        }
    }

    public void jumpToTime(double targetTime) {
        if (!isPlaying || isGameOver || isLevelPassed || !IsValid()) return;

        targetTime = Math.min(targetTime, stageDuration);
        targetTime = Math.max(targetTime, 0.0);

        List<Wire> savedWires = new ArrayList<>(wires);
        double savedWireLength = totalWireLength;

        isPlaying = false;
        startPlaying();

        wires.clear();
        wires.addAll(savedWires);
        totalWireLength = savedWireLength;
        for (Wire wire : wires) {
            wire.getSource().setWire(wire);
            wire.getDestination().setWire(wire);
        }

        if (targetTime > 0) {
            int steps = (int) (targetTime / 0.016);
            double stepDeltaTime = targetTime / steps;
            for (int i = 0; i < steps && isPlaying && !isGameOver && !isLevelPassed; i++) {
                updatePockets(stepDeltaTime);
            }
            currentTime = targetTime;
        }
    }

    public void resetStage() {
        pockets.clear();
        wires.clear();
        totalWireLength = 0.0;
        totalPackets = 0;
        lostPackets = 0;
        coins = 0;
        currentTime = 0.0;
        inProgress = 0;
        isGameOver = false;
        isLevelPassed = false;
        isPlaying = false;
        selectedPort = null;
        isOAtarActive = false;
        isOAiryamanActive = false;
        if (oAtarTimer != null) oAtarTimer.stop();
        if (oAiryamanTimer != null) oAiryamanTimer.stop();
    }

    private Port chooseOutputPort(Pocket pocket) {
        Hop hop = pocket.getCurrentHop();
        List<Port> compatiblePorts = new ArrayList<>();
        for (Port port : hop.ports) {
            if (port.getType() == PortType.Output && port.getWire() != null && port.getWire().IsValid()) {
                if (port.getShape() == pocket.getShapeType()) {
                    compatiblePorts.add(port);
                }
            }
        }
        for (Port port : compatiblePorts) {
            if (isPortEmpty(port)) return port;
        }
        return null;
    }

    private boolean isPortEmpty(Port port) {
        for (Pocket pocket : pockets) {
            if (pocket.getCurrentWire() == port.getWire() && pocket.getState() != PocketState.Lost) return false;
        }
        return true;
    }

    public Port findPortAt(Point point) {
        for (Hop system : systems) {
            for (Port port : system.ports) {
                if (port.contains(point)) {
                    return port;
                }
            }
        }
        return null;
    }

    public Wire findWireAt(Point point) {
        for (Wire wire : wires) {
            if (isPointNearLine(point, wire.getSource().getPosition(), wire.getDestination().getPosition())) {
                return wire;
            }
        }
        return null;
    }

    private boolean isPointNearLine(Point p, Point p1, Point p2) {
        double distance = Math.abs((p2.y - p1.y) * p.x - (p2.x - p1.x) * p.y + p2.x * p1.y - p2.y * p1.x) /
                Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x, 2));
        return distance < 5;
    }

    public Wire connectPorts(Port p1, Port p2) {
        if (p1.getWire() != null || p2.getWire() != null) {
            return null;
        }
        double wireLength = calculateWireLength(p1.getPosition(), p2.getPosition());
        if (totalWireLength + wireLength > maxWireLength) {
            return null;
        }
        Wire wire = new Wire();
        if (p1.getType() == PortType.Input) {
            Port temp = p1;
            p1 = p2;
            p2 = temp;
        }
        wire.setSource(p1);
        wire.setDestination(p2);
        wire.setLength(wireLength);
        
        Hop destHop = p2.getHopConnected();
        boolean hasMatchingOutput = false;
        for (Port port : destHop.ports) {
            if (port.getType() == PortType.Output && port.getShape() == p1.getShape()) {
                hasMatchingOutput = true;
                break;
            }
        }
        if (!hasMatchingOutput) {
            return null;
        }
        p1.setWire(wire);
        p2.setWire(wire);
        wires.add(wire);
        totalWireLength += wireLength;
        return wire;
    }

    public void removeWire(Wire wire) {
        if (wire != null) {
            totalWireLength -= wire.getLength();
            wire.getSource().setWire(null);
            wire.getDestination().setWire(null);
            wires.remove(wire);
        }
    }

    private double calculateWireLength(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }

    public boolean isTemporaryWireValid(Point mousePos) {
        if (selectedPort == null) return true;
        double tempLength = calculateWireLength(selectedPort.getPosition(), mousePos);
        return totalWireLength + tempLength <= maxWireLength;
    }

    public boolean isTimeBarClicked(Point point, double[] targetTime) {
        int barX = 10;
        int barY = 40;
        int barWidth = 780;
        int barHeight = 20;
        if (point.x >= barX && point.x <= barX + barWidth && point.y >= barY && point.y <= barY + barHeight) {
            double clickRatio = (point.x - barX) / (double) barWidth;
            targetTime[0] = clickRatio * stageDuration;
            return true;
        }
        return false;
    }

    public List<Hop> getSystems() {
        return systems;
    }

    public List<Wire> getWires() {
        return wires;
    }

    public List<Pocket> getPockets() {
        return pockets;
    }

    public Port getSelectedPort() {
        return selectedPort;
    }

    public void setSelectedPort(Port port) {
        this.selectedPort = port;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public double getMaxWireLength() {
        return maxWireLength;
    }

    public double getTotalWireLength() {
        return totalWireLength;
    }

    public int getTotalSent() {
        return inProgress + lostPackets;
    }

    public int getLostPackets() {
        return lostPackets;
    }

    public int getCoins() {
        return coins;
    }

    public double getTemporalProgress() {
        return (currentTime / stageDuration) * 100.0;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isLevelPassed() {
        return isLevelPassed;
    }

    public double getStageDuration() {
        return stageDuration;
    }

    public void setIsPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean getPlaying(){
        return isPlaying;
    }

    public boolean IsValid() {
        for (Hop hop : systems)
            if (!hop.IsValid())
                return false;
        return true;
    }
}