package com.example.network_game;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public class WireValidator {
    public static boolean isWirePathValid(List<Point> wirePoints, List<Hop> systems, Hop sourceSystem, Hop destinationSystem) {
        if (wirePoints.size() < 2) return false;
        for (int i = 0; i < wirePoints.size() - 1; i++) {
            Point p1 = wirePoints.get(i);
            Point p2 = wirePoints.get(i + 1);
            for (Hop system : systems) {

                if (system == sourceSystem || system == destinationSystem) continue;
                Rectangle systemBounds = new Rectangle(system.getX(), system.getY(), system.getWidth(), system.getHeight());
                if (new Line2D.Double(p1.x, p1.y, p2.x, p2.y).intersects(systemBounds)) {
                    return false;
                }
            }
        }
        return true;
    }


    private static boolean intersectsAnySystems(Point p1, Point p2, List<Hop> systems) {
        Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);

        for (Hop system : systems) {
            Rectangle systemBounds = new Rectangle(system.getX(), system.getY(),
                    system.getWidth(), system.getHeight());

            if (line.intersects(systemBounds)) {
                return true;
            }
        }
        return false;
    }

    public static double calculateCurvedWireLength(List<Point> points) {
        if (points.size() < 2) return 0;

        double totalLength = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            totalLength += Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        }
        return totalLength;
    }
}
