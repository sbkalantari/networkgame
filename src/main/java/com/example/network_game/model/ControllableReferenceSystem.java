package com.example.network_game.model;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllableReferenceSystem {
    private String id;
    private Point position;
    private Map<PacketType, Integer> ammunition;
    private Map<PacketType, Long> packetCooldowns;
    private long systemCooldown;
    private double cooldownMultiplier;


    private static final long SYSTEM_BASE_COOLDOWN = 3000;
    private static final long MESSENGER_COOLDOWN = 1000;
    private static final long TEXT_COOLDOWN = 2000;
    private static final long VIDEO_COOLDOWN = 3000;
    private static final long AUDIO_COOLDOWN = 2500;
    private static final long LARGE_COOLDOWN = 5000;
    private static final long TROJAN_COOLDOWN = 4000;


    private static final int SYSTEM_WIDTH = 60;
    private static final int SYSTEM_HEIGHT = 40;
    private Color baseColor = new Color(100, 100, 150);
    private Color activeColor = new Color(150, 150, 200);
    private Color cooldownColor = new Color(80, 80, 80);

    public ControllableReferenceSystem(String id, int x, int y, Map<PacketType, Integer> initialAmmunition) {
        this.id = id;
        this.position = new Point(x, y);
        this.ammunition = new ConcurrentHashMap<>(initialAmmunition);
        this.packetCooldowns = new ConcurrentHashMap<>();
        this.systemCooldown = 0;
        this.cooldownMultiplier = 1.0;

        initializePacketCooldowns();
    }

    private void initializePacketCooldowns() {
        for (PacketType type : PacketType.values()) {
            packetCooldowns.put(type, 0L);
        }
    }

    public boolean canLaunchPacket(PacketType packetType) {
        long currentTime = System.currentTimeMillis();


        if (currentTime < systemCooldown) {
            return false;
        }


        Long packetCooldownTime = packetCooldowns.get(packetType);
        if (packetCooldownTime != null && currentTime < packetCooldownTime) {
            return false;
        }


        Integer ammoCount = ammunition.get(packetType);
        return ammoCount != null && ammoCount > 0;
    }

    public boolean launchPacket(PacketType packetType, MultiplayerGameModel.PlayerType targetPlayer,
                                MultiplayerGameModel gameModel) {
        if (!canLaunchPacket(packetType)) {
            return false;
        }


        ammunition.put(packetType, ammunition.get(packetType) - 1);


        long currentTime = System.currentTimeMillis();
        systemCooldown = currentTime + (long)(SYSTEM_BASE_COOLDOWN * cooldownMultiplier);

        long packetCooldownDuration = getPacketCooldownDuration(packetType);
        packetCooldowns.put(packetType, currentTime + (long)(packetCooldownDuration * cooldownMultiplier));


        launchPacketIntoNetwork(packetType, targetPlayer, gameModel);

        System.out.println("Launched " + packetType + " packet targeting " + targetPlayer.getDisplayName());
        return true;
    }

    private long getPacketCooldownDuration(PacketType packetType) {
        switch (packetType) {
            case MESSENGER:
                return MESSENGER_COOLDOWN;
            case TEXT_INCOMPATIBLE:
                return TEXT_COOLDOWN;
            case VIDEO_ACCELERATING:
                return VIDEO_COOLDOWN;
            case AUDIO_DECELERATING:
                return AUDIO_COOLDOWN;
            case LARGE_SMALL:
            case LARGE_MEDIUM:
            case LARGE_BIG:
            case LARGE_HUGE:
                return LARGE_COOLDOWN;
            case TROJAN:
                return TROJAN_COOLDOWN;
            default:
                return MESSENGER_COOLDOWN;
        }
    }

    private void launchPacketIntoNetwork(PacketType packetType, MultiplayerGameModel.PlayerType targetPlayer,
                                         MultiplayerGameModel gameModel) {

        Pocket newPacket = new Pocket();
        newPacket.setPacketType(packetType);
        newPacket.setPlayerType(targetPlayer);
        newPacket.setShape(packetType == PacketType.MESSENGER || packetType == PacketType.TEXT_INCOMPATIBLE ?
                ShapeType.Square : ShapeType.Triangle);


        if (!gameModel.getSystems().isEmpty()) {
            Hop referenceSystem = gameModel.getSystems().get(0);
            newPacket.setCurrentHop(referenceSystem);
            newPacket.setState(PocketState.InSource);
        }


        gameModel.getPockets().add(newPacket);
    }

    public void addAmmunition(PacketType packetType, int amount) {
        ammunition.put(packetType, ammunition.getOrDefault(packetType, 0) + amount);
    }

    public void update(double deltaTime) {


    }

    public void increaseCooldownMultiplier(double increase) {
        this.cooldownMultiplier += increase;
    }

    public void resetCooldownMultiplier() {
        this.cooldownMultiplier = 1.0;
    }

    public boolean isOnCooldown() {
        return System.currentTimeMillis() < systemCooldown;
    }

    public boolean isPacketOnCooldown(PacketType packetType) {
        Long cooldownTime = packetCooldowns.get(packetType);
        return cooldownTime != null && System.currentTimeMillis() < cooldownTime;
    }

    public long getRemainingSystemCooldown() {
        long remaining = systemCooldown - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public long getRemainingPacketCooldown(PacketType packetType) {
        Long cooldownTime = packetCooldowns.get(packetType);
        if (cooldownTime == null) return 0;

        long remaining = cooldownTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public boolean contains(Point point) {
        return point.x >= position.x && point.x <= position.x + SYSTEM_WIDTH &&
                point.y >= position.y && point.y <= position.y + SYSTEM_HEIGHT;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, SYSTEM_WIDTH, SYSTEM_HEIGHT);
    }

    public void render(Graphics2D g2d) {

        Color renderColor = baseColor;
        if (isOnCooldown()) {
            renderColor = cooldownColor;
        }


        g2d.setColor(renderColor);
        g2d.fillRect(position.x, position.y, SYSTEM_WIDTH, SYSTEM_HEIGHT);


        g2d.setColor(Color.WHITE);
        g2d.drawRect(position.x, position.y, SYSTEM_WIDTH, SYSTEM_HEIGHT);


        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        FontMetrics fm = g2d.getFontMetrics();
        String displayId = id.replace("Control_System_", "CS");
        int textX = position.x + (SYSTEM_WIDTH - fm.stringWidth(displayId)) / 2;
        int textY = position.y + SYSTEM_HEIGHT / 2 + fm.getAscent() / 2;
        g2d.drawString(displayId, textX, textY);
    }

    public void renderAmmunitionPanel(Graphics2D g2d, Point hoverPoint) {
        if (!contains(hoverPoint)) {
            return;
        }


        int panelWidth = 200;
        int panelHeight = 150;
        int panelX = position.x + SYSTEM_WIDTH + 10;
        int panelY = position.y;


        if (panelX + panelWidth > 800) {
            panelX = position.x - panelWidth - 10;
        }
        if (panelY + panelHeight > 600) {
            panelY = 600 - panelHeight;
        }


        g2d.setColor(new Color(50, 50, 50, 200));
        g2d.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(panelX, panelY, panelWidth, panelHeight);


        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Ammunition", panelX + 5, panelY + 15);


        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        int yOffset = 35;

        for (Map.Entry<PacketType, Integer> entry : ammunition.entrySet()) {
            PacketType packetType = entry.getKey();
            Integer count = entry.getValue();


            Color textColor = Color.WHITE;
            if (count == 0) {
                textColor = Color.RED;
            } else if (isPacketOnCooldown(packetType)) {
                textColor = Color.GRAY;
            }

            g2d.setColor(textColor);
            String text = packetType.name() + ": " + count;
            g2d.drawString(text, panelX + 5, panelY + yOffset);


            if (isPacketOnCooldown(packetType)) {
                long cooldown = getRemainingPacketCooldown(packetType);
                g2d.setColor(Color.YELLOW);
                g2d.drawString("(" + (cooldown / 1000.0) + "s)", panelX + 120, panelY + yOffset);
            }

            yOffset += 15;
        }


        if (isOnCooldown()) {
            g2d.setColor(Color.ORANGE);
            long systemCooldownRemaining = getRemainingSystemCooldown();
            g2d.drawString("System Cooldown: " + (systemCooldownRemaining / 1000.0) + "s",
                    panelX + 5, panelY + panelHeight - 10);
        }
    }


    public String getId() { return id; }
    public Point getPosition() { return position; }
    public Map<PacketType, Integer> getAmmunition() { return ammunition; }
    public double getCooldownMultiplier() { return cooldownMultiplier; }


    public void setPosition(Point position) { this.position = position; }
    public void setPosition(int x, int y) { this.position = new Point(x, y); }
}