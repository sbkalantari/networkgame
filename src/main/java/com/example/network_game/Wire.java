package com.example.network_game;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class Wire implements ValidDevice {
    private Port source;
    private Port destination;
    private double length;
    private long creationTime;
    private List<WireCurve> curves;
    private List<Point> pathPoints;
    private boolean valid;
    private static final int MAX_CURVES = 3;

    public Wire() {
        this.creationTime = System.currentTimeMillis();
        this.curves = new ArrayList<>();
        this.pathPoints = new ArrayList<>();
        this.valid = true;
    }

    public boolean addCurve(Point position, double radius) {
        if (curves.size() >= MAX_CURVES) {
            return false;
        }

        WireCurve curve = new WireCurve(position, radius);
        curves.add(curve);
        updatePathPoints();
        updateLength();
        return true;
    }

    public boolean removeCurve(int index) {
        if (index >= 0 && index < curves.size()) {
            curves.remove(index);
            updatePathPoints();
            updateLength();
            return true;
        }
        return false;
    }

    public void updatePathPoints() {
        pathPoints.clear();

        if (source == null || destination == null) {
            return;
        }

        pathPoints.add(source.getPosition());

        
        for (WireCurve curve : curves) {
            if (curve.isActive()) {
                pathPoints.add(curve.getPosition());
            }
        }

        pathPoints.add(destination.getPosition());
    }

    public Point getPointAt(double progress) {
        
        if (progress <= 0) return getPathPoints().get(0);
        if (progress >= 1) return getPathPoints().get(getPathPoints().size() - 1);

        Path2D.Double path = getPath();
        
        final double flatness = 0.5;
        PathIterator it = path.getPathIterator(null, flatness);

        double[] coords = new double[6];
        double lastX = 0, lastY = 0;
        double totalLength = 0;
        java.util.List<Point2D.Double> points = new java.util.ArrayList<>();

        while (!it.isDone()) {
            int segType = it.currentSegment(coords);
            if (segType == PathIterator.SEG_MOVETO) {
                lastX = coords[0];
                lastY = coords[1];
                points.add(new Point2D.Double(lastX, lastY));
            } else if (segType == PathIterator.SEG_LINETO) {
                lastX = coords[0];
                lastY = coords[1];
                points.add(new Point2D.Double(lastX, lastY));
            }
            it.next();
        }

        
        java.util.List<Double> segLengths = new java.util.ArrayList<>();
        totalLength = 0;
        for (int i = 1; i < points.size(); i++) {
            double dx = points.get(i).x - points.get(i - 1).x;
            double dy = points.get(i).y - points.get(i - 1).y;
            double segLen = Math.hypot(dx, dy);
            segLengths.add(segLen);
            totalLength += segLen;
        }

        
        double targetLen = progress * totalLength;
        double currLen = 0;
        for (int i = 1; i < points.size(); i++) {
            double segLen = segLengths.get(i - 1);
            if (currLen + segLen >= targetLen) {
                double remain = targetLen - currLen;
                double ratio = remain / segLen;
                double x = points.get(i - 1).x + (points.get(i).x - points.get(i - 1).x) * ratio;
                double y = points.get(i - 1).y + (points.get(i).y - points.get(i - 1).y) * ratio;
                return new Point((int) x, (int) y);
            }
            currLen += segLen;
        }
        
        Point2D.Double last = points.get(points.size() - 1);
        return new Point((int) last.x, (int) last.y);
    }


    public void updateLength() {
        this.length = WireValidator.calculateCurvedWireLength(pathPoints);
    }

    public Path2D.Double getPath() {
        Path2D.Double path = new Path2D.Double();
        List<Point> pts = getPathPoints();
        if (pts.size() < 2) return path;

        path.moveTo(pts.get(0).x, pts.get(0).y);

        if (pts.size() == 2) {
            path.lineTo(pts.get(1).x, pts.get(1).y); 
        } else {
            for (int i = 1; i < pts.size() - 1; i++) {
                Point prev = pts.get(i - 1);
                Point curr = pts.get(i);
                Point next = pts.get(i + 1);

                
                
                double cx = curr.x;
                double cy = curr.y;
                double nx = (curr.x + next.x) / 2.0;
                double ny = (curr.y + next.y) / 2.0;
                path.quadTo(cx, cy, nx, ny);
            }
            
            Point secondLast = pts.get(pts.size() - 2);
            Point last = pts.get(pts.size() - 1);
            path.lineTo(last.x, last.y);
        }
        return path;
    }

    @Override
    public boolean IsValid() {
        if (source == null || destination == null) {
            return false;
        }

        
        boolean basicValid = source.getType() == PortType.Output &&
                destination.getType() == PortType.Input &&
                destination.getHopConnected() != source.getHopConnected() &&
                destination.getShape() == source.getShape();

        return basicValid && valid;
    }

    public boolean validatePath(List<Hop> systems) {
        if (systems == null) return true;

        this.valid = WireValidator.isWirePathValid(pathPoints, systems, source.getHopConnected(), destination.getHopConnected());
        return this.valid;
    }

    
    public Port getSource() { return source; }
    public void setSource(Port source) {
        this.source = source;
        updatePathPoints();
        updateLength();
    }

    public Port getDestination() { return destination; }
    public void setDestination(Port destination) {
        this.destination = destination;
        updatePathPoints();
        updateLength();
    }

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }

    public long getCreationTime() { return creationTime; }

    public List<WireCurve> getCurves() { return new ArrayList<>(curves); }
    public List<Point> getPathPoints() { return new ArrayList<>(pathPoints); }

    public int getCurveCount() { return curves.size(); }
    public boolean canAddCurve() { return curves.size() < MAX_CURVES; }
}
