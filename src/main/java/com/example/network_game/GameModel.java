package com.example.network_game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    private List<Hop> systems;
    private List<Wire> wires;
    private List<Pocket> pockets;
    private Port selectedPort;
    private boolean isPlaying;
    private Random random;

    public GameModel() {
        systems = new ArrayList<>();
        wires = new ArrayList<>();
        pockets = new ArrayList<>();
        selectedPort = null;
        isPlaying = false;
        random = new Random();
        initializeSystems();
    }

    private void initializeSystems() {
        systems.add(createSystem(100, 100, 2, 2));
        systems.add(createSystem(500, 100, 2, 3));
        systems.add(createSystem(100, 400, 3, 2));
        systems.add(createSystem(500, 400, 2, 2));
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
            port.setShape(ShapeType.Square);
            port.setHopConnected(system);
            int portY = inputPorts == 1 ? y + systemHeight / 2 : y + offset + (int)(i * inputStep);
            port.setPosition(x, portY);
            system.ports.add(port);
        }

        for (int i = 0; i < outputPorts; i++) {
            Port port = new Port();
            port.setType(PortType.Output);
            port.setShape(ShapeType.Square);
            port.setHopConnected(system);
            int portY = outputPorts == 1 ? y + systemHeight / 2 : y + offset + (int)(i * outputStep);
            port.setPosition(x + systemWidth, portY);
            system.ports.add(port);
        }

        system.setPosition(x, y);
        system.setSize(systemWidth, systemHeight);
        return system;
    }

    public void startPlaying() {
        if (!isPlaying) {
            isPlaying = true;
            Hop referenceSystem = systems.get(0);
            for (int i = 0; i < 5; i++) {
                Pocket pocket = new Pocket();
                pocket.setShape(random.nextBoolean() ? ShapeType.Square : ShapeType.Triangle);
                pocket.setCurrentHop(referenceSystem);
                pockets.add(pocket);
            }
        }
    }

    public void updatePockets(double deltaTime) {
        if (!isPlaying) return;

        List<Pocket> pocketsToRemove = new ArrayList<>();
        for (Pocket pocket : pockets) {
            if (pocket.getCurrentHop() != null) {
                Port outputPort = chooseOutputPort(pocket);
                if (outputPort != null && outputPort.getWire() != null && outputPort.getWire().IsValid()) {
                    pocket.setCurrentWire(outputPort.getWire());
                    pocket.setProgress(0);
                    pocket.setCurrentHop(null);
                }
            } else if (pocket.getCurrentWire() != null) {
                pocket.updateProgress(deltaTime);
                if (pocket.getProgress() >= 1) {
                    Port destPort = pocket.getCurrentWire().getDestination();
                    Hop destHop = destPort.getHopConnected();
                    if (destHop.ports.size() <= 5) {
                        pocket.setCurrentHop(destHop);
                        pocket.setCurrentWire(null);
                    } else {
                        pocketsToRemove.add(pocket);
                    }
                }
            }
        }
        pockets.removeAll(pocketsToRemove);
    }

    private Port chooseOutputPort(Pocket pocket) {
        Hop hop = pocket.getCurrentHop();
        List<Port> compatiblePorts = new ArrayList<>();
        List<Port> emptyPorts = new ArrayList<>();
        for (Port port : hop.ports) {
            if (port.getType() == PortType.Output && port.getWire() != null && port.getWire().IsValid()) {
                if (port.getShape() == pocket.getShape()) {
                    compatiblePorts.add(port);
                }
                emptyPorts.add(port);
            }
        }
        for (Port port : compatiblePorts) {
            if (isPortEmpty(port)) return port;
        }
        List<Port> availablePorts = new ArrayList<>();
        for (Port port : emptyPorts) {
            if (isPortEmpty(port)) availablePorts.add(port);
        }
        return availablePorts.isEmpty() ? null : availablePorts.get(random.nextInt(availablePorts.size()));
    }

    private boolean isPortEmpty(Port port) {
        for (Pocket pocket : pockets) {
            if (pocket.getCurrentWire() == port.getWire()) return false;
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
        Wire wire = new Wire();
        if (p1.getType() == PortType.Input) {
            Port temp = p1;
            p1 = p2;
            p2 = temp;
        }
        wire.setSource(p1);
        wire.setDestination(p2);
        p1.setWire(wire);
        p2.setWire(wire);
        wires.add(wire);
        return wire;
    }

    public void removeWire(Wire wire) {
        if (wire != null) {
            wire.getSource().setWire(null);
            wire.getDestination().setWire(null);
            wires.remove(wire);
        }
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
}