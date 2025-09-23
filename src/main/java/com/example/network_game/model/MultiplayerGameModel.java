package com.example.network_game.model;

import com.example.network_game.controller.NetworkGameController;
import java.awt.Color;
import javax.swing.Timer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerGameModel extends GameModel {

    private static final int NETWORK_SETUP_TIME = 30000;
    private static final int EXTENDED_SETUP_TIME = 30000;
    private static final double FEEDBACK_LOOP_MULTIPLIER = 1.0;


    public enum PlayerType {
        PLAYER_ONE(Color.BLUE, "Player 1"),
        PLAYER_TWO(Color.RED, "Player 2");

        private final Color color;
        private final String displayName;

        PlayerType(Color color, String displayName) {
            this.color = color;
            this.displayName = displayName;
        }

        public Color getColor() { return color; }
        public String getDisplayName() { return displayName; }
    }


    public enum MultiplayerGameState {
        WAITING_FOR_PLAYERS,
        NETWORK_SETUP_PHASE,
        EXTENDED_SETUP_PHASE,
        GAME_ACTIVE,
        GAME_FINISHED
    }

    private MultiplayerGameState gameState;
    private PlayerType localPlayerType;
    private PlayerType remotePlayerType;
    private NetworkGameController networkController;


    private long setupStartTime;
    private long setupTimeLimit;
    private boolean localPlayerReady;
    private boolean remotePlayerReady;
    private boolean extendedTimeRequested;


    private List<ControllableReferenceSystem> controllableSystems;
    private Map<String, Integer> playerAmmunition;


    private boolean showOpponentNetwork;
    private List<Wire> opponentWires;
    private List<Hop> opponentSystems;


    private Map<PlayerType, Integer> playerScores;
    private Map<PlayerType, Integer> packetsDelivered;
    private Map<PlayerType, Integer> packetsLost;

    public MultiplayerGameModel(NetworkGameController networkController) {
        super();
        this.networkController = networkController;
        this.gameState = MultiplayerGameState.WAITING_FOR_PLAYERS;
        this.controllableSystems = new ArrayList<>();
        this.playerAmmunition = new ConcurrentHashMap<>();
        this.opponentWires = new ArrayList<>();
        this.opponentSystems = new ArrayList<>();
        this.showOpponentNetwork = false;
        this.playerScores = new EnumMap<>(PlayerType.class);
        this.packetsDelivered = new EnumMap<>(PlayerType.class);
        this.packetsLost = new EnumMap<>(PlayerType.class);

        initializeMultiplayerComponents();
    }

    private void initializeMultiplayerComponents() {

        localPlayerType = PlayerType.PLAYER_ONE;
        remotePlayerType = PlayerType.PLAYER_TWO;


        for (PlayerType player : PlayerType.values()) {
            playerScores.put(player, 0);
            packetsDelivered.put(player, 0);
            packetsLost.put(player, 0);
        }


        playerAmmunition.put(localPlayerType.name(), 0);
        playerAmmunition.put(remotePlayerType.name(), 0);


        createControllableSystems();
    }

    private void createControllableSystems() {

        ControllableReferenceSystem system1 = new ControllableReferenceSystem(
                "Control_System_1",
                150, 50,
                createInitialAmmunition()
        );

        ControllableReferenceSystem system2 = new ControllableReferenceSystem(
                "Control_System_2",
                450, 50,
                createInitialAmmunition()
        );

        ControllableReferenceSystem system3 = new ControllableReferenceSystem(
                "Control_System_3",
                300, 300,
                createInitialAmmunition()
        );

        controllableSystems.add(system1);
        controllableSystems.add(system2);
        controllableSystems.add(system3);
    }

    private Map<PacketType, Integer> createInitialAmmunition() {
        Map<PacketType, Integer> ammo = new EnumMap<>(PacketType.class);


        ammo.put(PacketType.MESSENGER, 10);
        ammo.put(PacketType.TEXT_INCOMPATIBLE, 5);
        ammo.put(PacketType.VIDEO_ACCELERATING, 3);
        ammo.put(PacketType.AUDIO_DECELERATING, 3);
        ammo.put(PacketType.LARGE_SMALL, 2);
        ammo.put(PacketType.LARGE_MEDIUM, 2);
        ammo.put(PacketType.LARGE_BIG, 1);
        ammo.put(PacketType.TROJAN, 2);

        return ammo;
    }

    public void startNetworkSetupPhase() {
        if (gameState != MultiplayerGameState.WAITING_FOR_PLAYERS) {
            return;
        }

        gameState = MultiplayerGameState.NETWORK_SETUP_PHASE;
        setupStartTime = System.currentTimeMillis();
        setupTimeLimit = NETWORK_SETUP_TIME;
        localPlayerReady = false;
        remotePlayerReady = false;
        extendedTimeRequested = false;

        System.out.println("Network setup phase started - 30 seconds to build your network!");
    }

    public void requestExtendedTime() {
        if (gameState != MultiplayerGameState.NETWORK_SETUP_PHASE || extendedTimeRequested) {
            return;
        }

        extendedTimeRequested = true;
        gameState = MultiplayerGameState.EXTENDED_SETUP_PHASE;
        setupTimeLimit += EXTENDED_SETUP_TIME;


        applyWrathEffects();

        System.out.println("Extended time granted - penalties applied!");
    }

    private void applyWrathEffects() {
        long elapsedTime = System.currentTimeMillis() - setupStartTime - NETWORK_SETUP_TIME;

        if (elapsedTime < 10000) {
            applyWrathOfPenia();
        } else if (elapsedTime < 20000) {
            applyWrathOfAergia();
        } else {
            applyWrathOfAcceleration();
        }
    }

    private void applyWrathOfPenia() {

        Timer wrathTimer = new Timer(2000, e -> {
            if (gameState == MultiplayerGameState.EXTENDED_SETUP_PHASE) {
                addRandomPacketToOpponent();
            }
        });
        wrathTimer.start();


        Timer stopTimer = new Timer(10000, e -> wrathTimer.stop());
        stopTimer.setRepeats(false);
        stopTimer.start();
    }

    private void applyWrathOfAergia() {

        for (ControllableReferenceSystem system : controllableSystems) {
            system.increaseCooldownMultiplier(0.1);
        }
    }

    private void applyWrathOfAcceleration() {


        for (Pocket pocket : getPockets()) {
            pocket.setAccelerationFactor(1.3);
        }
    }

    private void addRandomPacketToOpponent() {
        if (!controllableSystems.isEmpty()) {
            ControllableReferenceSystem randomSystem = controllableSystems.get(
                    (int) (Math.random() * controllableSystems.size())
            );

            PacketType[] types = PacketType.values();
            PacketType randomType = types[(int) (Math.random() * types.length)];

            randomSystem.addAmmunition(randomType, 1);
        }
    }

    public void setPlayerReady(boolean ready) {
        localPlayerReady = ready;


        if (networkController != null) {
            networkController.sendPlayerAction("PLAYER_READY:" + ready);
        }

        checkBothPlayersReady();
    }

    public void setRemotePlayerReady(boolean ready) {
        remotePlayerReady = ready;
        checkBothPlayersReady();
    }

    private void checkBothPlayersReady() {
        if (localPlayerReady && remotePlayerReady) {
            startMultiplayerGame();
        }
    }

    private void startMultiplayerGame() {
        gameState = MultiplayerGameState.GAME_ACTIVE;
        showOpponentNetwork = true;


        initializeMultiplayerPockets();


        startPlaying();

        System.out.println("Multiplayer game started!");
    }

    private void initializeMultiplayerPockets() {

        getPockets().clear();


        Hop referenceSystem = getSystems().get(0);


        for (int i = 0; i < 50; i++) {
            Pocket pocket = new Pocket();
            pocket.setShape(i % 2 == 0 ? ShapeType.Square : ShapeType.Triangle);
            pocket.setCurrentHop(referenceSystem);
            pocket.setPlayerType(PlayerType.PLAYER_ONE);
            getPockets().add(pocket);
        }


        for (int i = 0; i < 50; i++) {
            Pocket pocket = new Pocket();
            pocket.setShape(i % 2 == 0 ? ShapeType.Square : ShapeType.Triangle);
            pocket.setCurrentHop(referenceSystem);
            pocket.setPlayerType(PlayerType.PLAYER_TWO);
            getPockets().add(pocket);
        }
    }

    public boolean launchPacketFromControllableSystem(String systemId, PacketType packetType,
                                                      PlayerType targetPlayer) {
        ControllableReferenceSystem system = findControllableSystem(systemId);
        if (system == null) {
            return false;
        }

        return system.launchPacket(packetType, targetPlayer, this);
    }

    private ControllableReferenceSystem findControllableSystem(String systemId) {
        return controllableSystems.stream()
                .filter(system -> system.getId().equals(systemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updatePockets(double deltaTime) {
        super.updatePockets(deltaTime);


        updateMultiplayerScoring();
        updateSetupTimer();
        updateControllableSystems(deltaTime);


        checkMultiplayerGameEnd();
    }

    private void updateMultiplayerScoring() {

        int p1Delivered = 0, p1Lost = 0;
        int p2Delivered = 0, p2Lost = 0;

        for (Pocket pocket : getPockets()) {
            PlayerType playerType = pocket.getPlayerType();
            if (playerType != null) {
                if (pocket.getState() == PocketState.Delivered) {
                    if (playerType == PlayerType.PLAYER_ONE) p1Delivered++;
                    else p2Delivered++;
                } else if (pocket.getState() == PocketState.Lost) {
                    if (playerType == PlayerType.PLAYER_ONE) p1Lost++;
                    else p2Lost++;
                }
            }
        }

        packetsDelivered.put(PlayerType.PLAYER_ONE, p1Delivered);
        packetsDelivered.put(PlayerType.PLAYER_TWO, p2Delivered);
        packetsLost.put(PlayerType.PLAYER_ONE, p1Lost);
        packetsLost.put(PlayerType.PLAYER_TWO, p2Lost);


        playerScores.put(PlayerType.PLAYER_ONE,
                p1Delivered - (int)(p1Lost * 1.5));
        playerScores.put(PlayerType.PLAYER_TWO,
                p2Delivered - (int)(p2Lost * 1.5));
    }

    private void updateSetupTimer() {
        if (gameState == MultiplayerGameState.NETWORK_SETUP_PHASE ||
                gameState == MultiplayerGameState.EXTENDED_SETUP_PHASE) {

            long elapsed = System.currentTimeMillis() - setupStartTime;
            if (elapsed >= setupTimeLimit) {

                if (gameState == MultiplayerGameState.NETWORK_SETUP_PHASE) {
                    startMultiplayerGame();
                } else {

                    startMultiplayerGame();
                }
            }
        }
    }

    private void updateControllableSystems(double deltaTime) {
        for (ControllableReferenceSystem system : controllableSystems) {
            system.update(deltaTime);
        }
    }

    private void checkMultiplayerGameEnd() {
        if (gameState != MultiplayerGameState.GAME_ACTIVE) {
            return;
        }


        boolean allPacketsProcessed = getPockets().stream()
                .allMatch(pocket -> pocket.getState() == PocketState.Delivered ||
                        pocket.getState() == PocketState.Lost);

        if (allPacketsProcessed || getCurrentTime() >= getStageDuration()) {
            endMultiplayerGame();
        }
    }

    private void endMultiplayerGame() {
        gameState = MultiplayerGameState.GAME_FINISHED;
        setIsPlaying(false);


        PlayerType winner = determineWinner();

        System.out.println("Multiplayer game ended!");
        System.out.println("Winner: " + (winner != null ? winner.getDisplayName() : "Tie"));
        System.out.println("Player 1 Score: " + playerScores.get(PlayerType.PLAYER_ONE));
        System.out.println("Player 2 Score: " + playerScores.get(PlayerType.PLAYER_TWO));
    }

    private PlayerType determineWinner() {
        int p1Score = playerScores.get(PlayerType.PLAYER_ONE);
        int p2Score = playerScores.get(PlayerType.PLAYER_TWO);

        if (p1Score > p2Score) return PlayerType.PLAYER_ONE;
        if (p2Score > p1Score) return PlayerType.PLAYER_TWO;
        return null;
    }

    public void onPacketDelivered(PlayerType playerType) {

        if (playerType == PlayerType.PLAYER_ONE) {
            addAmmunitionToPlayer(PlayerType.PLAYER_TWO, PacketType.MESSENGER, 1);
        } else {
            addAmmunitionToPlayer(PlayerType.PLAYER_ONE, PacketType.MESSENGER, 1);
        }
    }

    private void addAmmunitionToPlayer(PlayerType playerType, PacketType packetType, int amount) {

        if (!controllableSystems.isEmpty()) {
            ControllableReferenceSystem system = controllableSystems.get(0);
            system.addAmmunition(packetType, amount);
        }
    }

    public void updateOpponentNetwork(List<Wire> opponentWires, List<Hop> opponentSystems) {
        this.opponentWires = new ArrayList<>(opponentWires);
        this.opponentSystems = new ArrayList<>(opponentSystems);
    }


    public MultiplayerGameState getGameState() { return gameState; }
    public PlayerType getLocalPlayerType() { return localPlayerType; }
    public PlayerType getRemotePlayerType() { return remotePlayerType; }
    public boolean isLocalPlayerReady() { return localPlayerReady; }
    public boolean isRemotePlayerReady() { return remotePlayerReady; }
    public boolean isShowOpponentNetwork() { return showOpponentNetwork; }
    public List<Wire> getOpponentWires() { return opponentWires; }
    public List<Hop> getOpponentSystems() { return opponentSystems; }
    public List<ControllableReferenceSystem> getControllableSystems() { return controllableSystems; }
    public Map<PlayerType, Integer> getPlayerScores() { return playerScores; }
    public Map<PlayerType, Integer> getPacketsDelivered() { return packetsDelivered; }
    public Map<PlayerType, Integer> getPacketsLost() { return packetsLost; }

    public long getRemainingSetupTime() {
        if (gameState == MultiplayerGameState.NETWORK_SETUP_PHASE ||
                gameState == MultiplayerGameState.EXTENDED_SETUP_PHASE) {
            long elapsed = System.currentTimeMillis() - setupStartTime;
            return Math.max(0, setupTimeLimit - elapsed);
        }
        return 0;
    }

    public void setLocalPlayerType(PlayerType playerType) {
        this.localPlayerType = playerType;
        this.remotePlayerType = (playerType == PlayerType.PLAYER_ONE) ?
                PlayerType.PLAYER_TWO : PlayerType.PLAYER_ONE;
    }
}