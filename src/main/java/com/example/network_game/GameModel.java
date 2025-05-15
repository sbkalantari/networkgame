package com.example.network_game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private List<Hop> systems;
    private List<Wire> wires;
    private Port selectedPort;

    public GameModel() {
        systems = new ArrayList<>();
        wires = new ArrayList<>();
        selectedPort = null;
        initializeSystems();
    }

    private void initializeSystems() {
        systems.add(createSystem(100, 100, 2, 2));
        systems.add(createSystem(500, 100, 2, 2));
        systems.add(createSystem(100, 400, 2, 2));
        systems.add(createSystem(500, 400, 2, 2));
    }

    private Hop createSystem(int x, int y, int inputPorts, int outputPorts) {
        Hop system = new Hop();
        int systemWidth = 100;
        int systemHeight = 80;

        for (int i = 0; i < inputPorts; i++) {
            Port port = new Port();
            port.setType(PortType.Input);
            port.setShape(ShapeType.Square);
            port.setHopConnected(system);
            port.setPosition(x, y + 20 + i * 40);
            system.ports.add(port);
        }

        for (int i = 0; i < outputPorts; i++) {
            Port port = new Port();
            port.setType(PortType.Output);
            port.setShape(ShapeType.Square);
            port.setHopConnected(system);
            port.setPosition(x + systemWidth, y + 20 + i * 40);
            system.ports.add(port);
        }

        system.setPosition(x, y);
        system.setSize(systemWidth, systemHeight);
        return system;
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
        if(p1.getWire() != null || p2.getWire() != null) {
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

    private boolean isValidConnection(Port p1, Port p2) {
        return p1.getHopConnected() != p2.getHopConnected() &&
                p1.getType() != p2.getType() &&
                p1.getShape() == p2.getShape() &&
                p1.getWire() == null && p2.getWire() == null;
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

    public Port getSelectedPort() {
        return selectedPort;
    }

    public void setSelectedPort(Port port) {
        this.selectedPort = port;
    }
}