package com.example.network_game.save;

import com.example.network_game.controller.GameController;
import com.example.network_game.model.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class SaveManager {
    private static final String SAVE_FILE = "game_save.dat";
    private static final String TEMP_SAVE_FILE = "game_save_temp.dat";
    private static final String KEY_FILE = "save.key";
    private static final int AUTOSAVE_INTERVAL = 3000;

    private final GameModel model;
    private final GameController gameController;
    private Timer autosaveTimer;
    private final ReentrantLock saveLock = new ReentrantLock();
    private SecretKey encryptionKey;

    public SaveManager(GameModel model, GameController gameController) {
        this.model = model;
        this.gameController = gameController;
        initializeEncryption();
        setupAutosave();
    }

    private void initializeEncryption() {
        try {
            File keyFile = new File(KEY_FILE);
            if (keyFile.exists()) {

                byte[] keyBytes = java.nio.file.Files.readAllBytes(keyFile.toPath());
                encryptionKey = new SecretKeySpec(keyBytes, "AES");
            } else {

                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(256);
                encryptionKey = keyGen.generateKey();


                try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                    fos.write(encryptionKey.getEncoded());
                }
            }
        } catch (Exception e) {
            System.err.println("Encryption initialization failed: " + e.getMessage());

            encryptionKey = null;
        }
    }

    private void setupAutosave() {
        autosaveTimer = new Timer();
        autosaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (model.isPlaying() && !model.isGameOver() && !model.isLevelPassed()) {
                    autosave();
                }
            }
        }, AUTOSAVE_INTERVAL, AUTOSAVE_INTERVAL);
    }

    public synchronized void autosave() {
        if (!saveLock.tryLock()) return;

        try {
            GameSaveData saveData = createSaveData();
            writeSaveDataToFile(saveData, TEMP_SAVE_FILE);


            if (validateSaveFile(TEMP_SAVE_FILE)) {
                File tempFile = new File(TEMP_SAVE_FILE);
                File saveFile = new File(SAVE_FILE);

                if (saveFile.exists()) {
                    saveFile.delete();
                }
                tempFile.renameTo(saveFile);

                System.out.println("Autosave completed successfully");
            }
        } catch (Exception e) {
            System.err.println("Autosave failed: " + e.getMessage());
        } finally {
            saveLock.unlock();
        }
    }

    public synchronized boolean saveGame() {
        saveLock.lock();
        try {
            GameSaveData saveData = createSaveData();
            writeSaveDataToFile(saveData, SAVE_FILE);
            return validateSaveFile(SAVE_FILE);
        } catch (Exception e) {
            System.err.println("Save failed: " + e.getMessage());
            return false;
        } finally {
            saveLock.unlock();
        }
    }

    public synchronized boolean hasValidSave() {
        File saveFile = new File(SAVE_FILE);
        return saveFile.exists() && validateSaveFile(SAVE_FILE);
    }

    public synchronized boolean loadGame() {
        if (!hasValidSave()) return false;

        saveLock.lock();
        try {
            GameSaveData saveData = readSaveDataFromFile(SAVE_FILE);
            if (validateSaveData(saveData)) {
                applySaveData(saveData);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Load failed: " + e.getMessage());
        } finally {
            saveLock.unlock();
        }
        return false;
    }

    public synchronized void deleteSave() {
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        File tempFile = new File(TEMP_SAVE_FILE);
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    private GameSaveData createSaveData() {
        GameSaveData data = new GameSaveData();


        data.timestamp = System.currentTimeMillis();
        data.currentTime = model.getCurrentTime();
        data.stageDuration = model.getStageDuration();
        data.maxWireLength = model.getMaxWireLength();
        data.totalWireLength = model.getTotalWireLength();
        data.coins = model.getCoins();
        data.totalPackets = model.getTotalPackets();
        data.lostPackets = model.getLostPackets();
        data.isPlaying = model.isPlaying();
        data.isGameOver = model.isGameOver();
        data.isLevelPassed = model.isLevelPassed();


        data.systems = new ArrayList<>();
        for (Hop system : model.getSystems()) {
            SystemSaveData sysData = new SystemSaveData();
            sysData.x = system.getX();
            sysData.y = system.getY();
            sysData.width = system.getWidth();
            sysData.height = system.getHeight();
            sysData.systemType = system.getSystemType();
            sysData.active = system.isActive();
            sysData.packetsProcessed = system.getPacketsProcessed();


            sysData.ports = new ArrayList<>();
            for (Port port : system.getPorts()) {
                PortSaveData portData = new PortSaveData();
                portData.x = port.getX();
                portData.y = port.getY();
                portData.type = port.getType();
                portData.shape = port.getShape();
                portData.hasWire = (port.getWire() != null);
                sysData.ports.add(portData);
            }

            data.systems.add(sysData);
        }


        data.wires = new ArrayList<>();
        for (Wire wire : model.getWires()) {
            WireSaveData wireData = new WireSaveData();
            wireData.sourceSystemIndex = findSystemIndex(wire.getSource().getHopConnected());
            wireData.sourcePortIndex = findPortIndex(wire.getSource());
            wireData.destSystemIndex = findSystemIndex(wire.getDestination().getHopConnected());
            wireData.destPortIndex = findPortIndex(wire.getDestination());
            wireData.length = wire.getLength();


            wireData.curves = new ArrayList<>();
            for (WireCurve curve : wire.getCurves()) {
                CurveSaveData curveData = new CurveSaveData();
                curveData.x = curve.getPosition().x;
                curveData.y = curve.getPosition().y;
                curveData.radius = curve.getRadius();
                curveData.active = curve.isActive();
                wireData.curves.add(curveData);
            }

            data.wires.add(wireData);
        }


        data.packets = new ArrayList<>();
        for (Pocket packet : model.getPockets()) {
            PacketSaveData packetData = new PacketSaveData();
            packetData.shapeType = packet.getShapeType();
            packetData.packetType = packet.getPacketType();
            packetData.progress = packet.getProgress();
            packetData.state = packet.getState();
            packetData.noiseLevel = packet.getNoiseLevel();
            packetData.currentSpeed = packet.getCurrentSpeed();

            if (packet.getCurrentHop() != null) {
                packetData.currentSystemIndex = findSystemIndex(packet.getCurrentHop());
            }
            if (packet.getCurrentWire() != null) {
                packetData.currentWireIndex = findWireIndex(packet.getCurrentWire());
            }

            data.packets.add(packetData);
        }


        data.checksum = generateChecksum(data);

        return data;
    }

    private void writeSaveDataToFile(GameSaveData data, String filename) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(data);
        oos.close();

        byte[] serializedData = baos.toByteArray();


        if (encryptionKey != null) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            serializedData = cipher.doFinal(serializedData);
        }

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(serializedData);
        }
    }

    private GameSaveData readSaveDataFromFile(String filename) throws Exception {
        byte[] fileData;
        try (FileInputStream fis = new FileInputStream(filename)) {
            fileData = fis.readAllBytes();
        }


        if (encryptionKey != null) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
            fileData = cipher.doFinal(fileData);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
        ObjectInputStream ois = new ObjectInputStream(bais);
        GameSaveData data = (GameSaveData) ois.readObject();
        ois.close();

        return data;
    }

    private boolean validateSaveFile(String filename) {
        try {
            GameSaveData data = readSaveDataFromFile(filename);
            return validateSaveData(data);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateSaveData(GameSaveData data) {
        if (data == null) return false;


        String calculatedChecksum = generateChecksum(data);
        if (!calculatedChecksum.equals(data.checksum)) {
            System.err.println("Save data checksum mismatch - possible tampering detected");
            return false;
        }


        if (data.currentTime < 0 || data.currentTime > data.stageDuration) return false;
        if (data.totalWireLength < 0 || data.totalWireLength > data.maxWireLength) return false;
        if (data.coins < 0) return false;
        if (data.systems == null || data.wires == null || data.packets == null) return false;


        for (WireSaveData wire : data.wires) {
            if (wire.sourceSystemIndex < 0 || wire.sourceSystemIndex >= data.systems.size()) return false;
            if (wire.destSystemIndex < 0 || wire.destSystemIndex >= data.systems.size()) return false;
        }

        return true;
    }

    private String generateChecksum(GameSaveData data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");


            md.update(Double.toString(data.currentTime).getBytes());
            md.update(Integer.toString(data.coins).getBytes());
            md.update(Double.toString(data.totalWireLength).getBytes());
            md.update(Integer.toString(data.systems.size()).getBytes());
            md.update(Integer.toString(data.wires.size()).getBytes());
            md.update(Integer.toString(data.packets.size()).getBytes());
            md.update(Long.toString(data.timestamp).getBytes());

            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "invalid";
        }
    }

    private void applySaveData(GameSaveData data) {

        gameController.setGameModel(model);

        model.setCurrentTime(data.currentTime);
        model.setTotalWireLength(data.totalWireLength);
        model.setCoins(data.coins);
        model.setTotalPackets(data.totalPackets);
        model.setLostPackets(data.lostPackets);
        model.setIsPlaying(false);


        model.getSystems().clear();
        model.getWires().clear();
        model.getPockets().clear();


        for (SystemSaveData sysData : data.systems) {
            Hop system = new Hop(sysData.systemType);
            system.setPosition(sysData.x, sysData.y);
            system.setSize(sysData.width, sysData.height);
            system.setActive(sysData.active);


            for (PortSaveData portData : sysData.ports) {
                Port port = new Port();
                port.setPosition(portData.x, portData.y);
                port.setType(portData.type);
                port.setShape(portData.shape);
                port.setHopConnected(system);
                system.getPorts().add(port);
            }

            model.getSystems().add(system);
        }


        for (WireSaveData wireData : data.wires) {
            Hop sourceSystem = model.getSystems().get(wireData.sourceSystemIndex);
            Hop destSystem = model.getSystems().get(wireData.destSystemIndex);
            Port sourcePort = sourceSystem.getPorts().get(wireData.sourcePortIndex);
            Port destPort = destSystem.getPorts().get(wireData.destPortIndex);

            Wire wire = new Wire();
            wire.setSource(sourcePort);
            wire.setDestination(destPort);
            wire.setLength(wireData.length);


            for (CurveSaveData curveData : wireData.curves) {
                Point curvePos = new Point(curveData.x, curveData.y);
                wire.addCurve(curvePos, curveData.radius);
            }

            sourcePort.setWire(wire);
            destPort.setWire(wire);
            model.getWires().add(wire);
        }


        for (PacketSaveData packetData : data.packets) {
            Pocket packet = new Pocket();
            packet.setShape(packetData.shapeType);
            packet.setPacketType(packetData.packetType);
            packet.setProgress(packetData.progress);
            packet.setState(packetData.state);
            packet.addNoise(packetData.noiseLevel);

            if (packetData.currentSystemIndex >= 0) {
                packet.setCurrentHop(model.getSystems().get(packetData.currentSystemIndex));
            }
            if (packetData.currentWireIndex >= 0) {
                packet.setCurrentWire(model.getWires().get(packetData.currentWireIndex));
            }

            model.getPockets().add(packet);
        }

        System.out.println("Game loaded successfully from save data");
    }


    private int findSystemIndex(Hop system) {
        return model.getSystems().indexOf(system);
    }

    private int findPortIndex(Port port) {
        Hop system = port.getHopConnected();
        return system.getPorts().indexOf(port);
    }

    private int findWireIndex(Wire wire) {
        return model.getWires().indexOf(wire);
    }

    public void shutdown() {
        if (autosaveTimer != null) {
            autosaveTimer.cancel();
        }
    }
}