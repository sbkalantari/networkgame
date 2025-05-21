package com.example.network_game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GameView extends JPanel {
    private GameModel model;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton returnToMenuButton;
    private JButton nextStageButton;
    private JButton storeButton;
    private JButton playButton;
    private Point mousePos;
    private static boolean stageCompletePlayed = false; 
    private Port hoveredPort; 

    public GameView(GameModel model, CardLayout cardLayout, JPanel cardPanel) {
        this.model = model;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        setBackground(Color.BLACK); 
        setupButtons();
        setFocusable(true);
    }

    private void setupButtons() {
        
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Color buttonColor = new Color(0, 200, 200); 
        Color buttonHover = new Color(200, 0, 200); 

        returnToMenuButton = new JButton("Return to Menu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getMousePosition() != null && contains(getMousePosition()) ? buttonHover : buttonColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        returnToMenuButton.setBounds(10, 10, 150, 30);
        returnToMenuButton.setFont(buttonFont);
        returnToMenuButton.setForeground(Color.WHITE);
        returnToMenuButton.setContentAreaFilled(false);
        returnToMenuButton.setBorderPainted(false);
        add(returnToMenuButton);

        nextStageButton = new JButton("Next Stage") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getMousePosition() != null && contains(getMousePosition()) ? buttonHover : buttonColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        nextStageButton.setBounds(10, 50, 150, 30);
        nextStageButton.setFont(buttonFont);
        returnToMenuButton.setForeground(Color.WHITE);
        nextStageButton.setForeground(Color.WHITE);
        nextStageButton.setContentAreaFilled(false);
        nextStageButton.setBorderPainted(false);
        add(nextStageButton);

        playButton = new JButton("Play") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getMousePosition() != null && contains(getMousePosition()) ? buttonHover : buttonColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        playButton.setBounds(10, 90, 80, 30);
        playButton.setFont(buttonFont);
        playButton.setForeground(Color.WHITE);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        add(playButton);

        storeButton = new JButton("Store") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getMousePosition() != null && contains(getMousePosition()) ? buttonHover : buttonColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        storeButton.setBounds(10, 130, 80, 30);
        storeButton.setFont(buttonFont);
        storeButton.setForeground(Color.WHITE);
        storeButton.setContentAreaFilled(false);
        storeButton.setBorderPainted(false);
        add(storeButton);
    }

    
    private double calculateWireLength(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        g2d.setColor(new Color(10, 10, 30)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());

        
        for (int i = 0; i < model.getSystems().size(); i++) {
            Hop system = model.getSystems().get(i);
            int x = system.getX();
            int y = system.getY();
            int width = system.getWidth();
            int height = system.getHeight();
            boolean isSource = (i == 0); 
            
            if (isSource) {
                
                g2d.setPaint(new RadialGradientPaint(x + width / 2, y + height / 2, 50, new float[]{0, 1}, new Color[]{new Color(200, 0, 200, 100), new Color(200, 0, 200, 0)}));
                g2d.fill(new Ellipse2D.Double(x - 10, y - 10, width + 20, height + 20));
                
                g2d.setColor(new Color(200, 0, 200));
            } else {
                g2d.setColor(new Color(0, 150, 150)); 
            }
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            g2d.setColor(new Color(0, 255, 255)); 
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, width, height, 10, 10);
            
            g2d.setColor(system.IsValid() ? new Color(0, 200, 0) : new Color(255, 0, 0));
            g2d.fillOval(x + width - 15, y + 5, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(x + width - 15, y + 5, 10, 10);
        }

        
        for (Hop system : model.getSystems()) {
            for (Port port : system.ports) {
                Point pos = port.getPosition();
                Shape shape = port.getShape() == ShapeType.Square ?
                        new Rectangle2D.Double(pos.x - 5, pos.y - 5, 10, 10) :
                        new Polygon(new int[]{pos.x - 5, pos.x + 5, pos.x}, new int[]{pos.y - 5, pos.y - 5, pos.y + 5}, 3);
                boolean isSelected = port == model.getSelectedPort();
                boolean isHovered = port == hoveredPort;
                
                if (isSelected || isHovered) {
                    g2d.setColor(new Color(0, 255, 255, 100));
                    g2d.fill(new Ellipse2D.Double(pos.x - 10, pos.y - 10, 20, 20));
                }
                g2d.setColor(port.getType() == PortType.Input ? new Color(100, 100, 255) : new Color(255, 100, 100));
                g2d.fill(shape);
                g2d.setColor(Color.WHITE);
                g2d.draw(shape);
            }
        }

        
        double tempWireLength = 0.0;
        if (model.getSelectedPort() != null && mousePos != null) {
            Point src = model.getSelectedPort().getPosition();
            tempWireLength = calculateWireLength(src, mousePos);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f)); 
            g2d.setColor(model.isTemporaryWireValid(mousePos) ? new Color(0, 0, 255) : new Color(255, 0, 0)); 
            g2d.drawLine(src.x, src.y, mousePos.x, mousePos.y);
        }

        
        g2d.setStroke(new BasicStroke(2));
        for (Wire wire : model.getWires()) {
            Point src = wire.getSource().getPosition();
            Point dst = wire.getDestination().getPosition();
            g2d.setColor(wire.IsValid() ? new Color(0, 200, 0) : new Color(255, 0, 0)); 
            g2d.drawLine(src.x, src.y, dst.x, dst.y);
            
            if (System.currentTimeMillis() - wire.getCreationTime() < 500) {
                g2d.setColor(new Color(255, 255, 0, 150));
                g2d.fillOval(src.x - 5, src.y - 5, 10, 10);
                g2d.fillOval(dst.x - 5, dst.y - 5, 10, 10);
            }
        }

        
        for (Pocket pocket : model.getPockets()) {
            if (pocket.getState() == PocketState.InProgress && pocket.getCurrentWire() != null) {
                Point src = pocket.getCurrentWire().getSource().getPosition();
                Point dst = pocket.getCurrentWire().getDestination().getPosition();
                double progress = pocket.getProgress();
                int x = (int) (src.x + (dst.x - src.x) * progress);
                int y = (int) (src.y + (dst.y - src.y) * progress);
                Shape shape = pocket.getShapeType() == ShapeType.Square ?
                        new Rectangle2D.Double(x - 7.5, y - 7.5, 15, 15) : 
                        new Polygon(new int[]{x - 7, x + 7, x}, new int[]{y - 7, y - 7, y + 7}, 3); 
                g2d.setColor(pocket.getShapeType() == ShapeType.Square ? new Color(0, 200, 200) : new Color(200, 0, 200));
                g2d.fill(shape);
                g2d.setColor(Color.WHITE);
                g2d.draw(shape);
                
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - 2, y - 2, 4, 4);
            }
        }

        
        int barX = 10, barY = 500, barWidth = 780, barHeight = 20;
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 5, 5);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(barX, barY, barWidth, barHeight, 5, 5);
        int progressWidth = (int) (model.getTemporalProgress() / 100.0 * barWidth);
        g2d.setColor(new Color(0, 200, 200));
        g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 5, 5);

        
        int wireBarY = 530;
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRoundRect(barX, wireBarY, barWidth, barHeight, 5, 5);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(barX, wireBarY, barWidth, barHeight, 5, 5);
        double totalWireLength = model.getTotalWireLength() + tempWireLength;
        double wireUsagePercentage = totalWireLength / model.getMaxWireLength();
        int wireUsageWidth = (int) (wireUsagePercentage * barWidth);
        
        if (wireUsagePercentage < 0.5) {
            g2d.setColor(new Color(0, 200, 0)); 
        } else if (wireUsagePercentage <= 0.8) {
            g2d.setColor(Color.YELLOW); 
        } else {
            g2d.setColor(new Color(255, 0, 0)); 
        }
        g2d.fillRoundRect(barX, wireBarY, wireUsageWidth, barHeight, 5, 5);

        
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(600, 540, 190, 70, 10, 10); 
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(600, 540, 190, 70, 10, 10);
        g2d.setColor(new Color(0, 200, 200));
        g2d.drawString("Coins: " + model.getCoins(), 610, 560);
        g2d.drawString("Stage: " + (model.getStageDuration() == 60.0 ? 1 : 2), 710, 560);
        
        double lostPercentage = model.getTotalSent() > 0 ? (model.getLostPackets() / (double) model.getTotalSent()) * 100 : 0;
        g2d.drawString("Lost: " + String.format("%.1f%%", lostPercentage), 610, 580);

        
        if (model.isLevelPassed() && !stageCompletePlayed) {
            AudioManager.playStageCompleteSound();
            stageCompletePlayed = true;
            System.out.println("Level passed, stage complete sound played");
        } else if (!model.isLevelPassed()) {
            stageCompletePlayed = false;
        }
    }

    public void setGameModel(GameModel model) {
        this.model = model;
        stageCompletePlayed = false;
        repaint();
    }

    public void setMousePos(Point point) {
        this.mousePos = point;
        hoveredPort = model.findPortAt(point);
        repaint();
    }

    public void notifyTimeJump(boolean isBackward) {
        repaint(); 
        System.out.println("Time jump triggered, isBackward=" + isBackward + ", Temporal Progress=" + model.getTemporalProgress());
    }

    public JButton getReturnToMenuButton() {
        return returnToMenuButton;
    }

    public JButton getNextStageButton() {
        return nextStageButton;
    }

    public JButton getStoreButton() {
        return storeButton;
    }

    public JButton getPlayButton() {
        return playButton;
    }
}