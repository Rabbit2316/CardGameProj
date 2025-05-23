package GUI;

import CombatMap.MapData;
import CombatMap.MapGameplayPane;
import CombatMap.MapGui;
import Entities.Enemies.*;
import MainPackage.Config;
import MainPackage.Game;
import MainPackage.NorthPanel;
import MAP.gamePanel; // Import your game panel
import RandomEncounter.EncounterPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.List;


public class RootContainer extends JFrame {

    private JPanel currentScreen; // Tracks which panel is in the center
    public BattleGUI gameScreen;
    public JPanel menuScreen;
    public MapGui mapScreen;
    private JPanel containerPanel; // The main container using BorderLayout
    public Game game;
    public gamePanel worldPanel; // Reference to the gamePanel (Shop System)
    public EncounterPanel encounterPanel;

    public RootContainer(Game game) {
        setTitle("Card Game");
        setSize(Config.frameSize.width+100, Config.frameSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.game = game; // Store game reference

        // Panel that holds the swappable screens
        containerPanel = new JPanel(new BorderLayout());
        add(containerPanel, BorderLayout.CENTER);

        // Initialize screens
        gameScreen = new BattleGUI(game);
        menuScreen = new MainMenuPanel(this);
        //menuScreen = createMenuScreen();
        worldPanel = new gamePanel(); // Initialize shop system
        mapScreen = new MapGui();
        encounterPanel = new EncounterPanel(this);

        // Add and configure GlassPane
        MapGameplayPane glassPane = new MapGameplayPane(this);
        setGlassPane(glassPane);
        glassPane.setVisible(true); // Ensure it's visible

        JPanel devOptions = new JPanel();
        //Dev panel
        if(Config.debug) {
            devOptions = createMenuScreen();

        }
        // Start on the menu
        if(Config.debug) {
            showScreen(devOptions);
        } else {
            showScreen(menuScreen);
        }


        setVisible(true);
    }

    public JPanel createMenuScreen() {
        JPanel menu = new JPanel();
        menu.setBackground(Color.BLACK);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> {
            try {
                gameScreen.newFight(startFight(2));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Game.gui.gameScreen.glassPane.setVisible(true);
            Game.gui.gameScreen.cardLayout.show(Game.gui.gameScreen.centerContainer, "main");
            Game.unslotAllCards();
            gameScreen.center.revalidate();
            gameScreen.center.repaint();
            showScreen(gameScreen);
        });
        menu.add(startButton);
        JButton encounter1 = new JButton("Encounter fresh");
        encounter1.addActionListener(e -> {
            try {
                gameScreen.newFight(startFight(1));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Game.gui.gameScreen.cardLayout.show(Game.gui.gameScreen.centerContainer, "main");
            Game.unslotAllCards();
            gameScreen.center.revalidate();
            gameScreen.center.repaint();
            showScreen(gameScreen);
        });
        menu.add(encounter1);

        // Open the shop system
        JButton worldButton = new JButton("Open World");
        worldButton.addActionListener(e -> {
            showScreen(worldPanel); // Switch to gamePanel (Shop System)
            worldPanel.startGameThread(); // Start the game loop if it's not running
            worldPanel.requestFocusInWindow();
        });
        menu.add(worldButton);


        JButton mapTestButton = new JButton("Map01 Test");
        mapTestButton.addActionListener(e -> {
            try {
                CombatMap.MapData mapData = CombatMap.MapLoader.loadMap("Resources/maps/map01.json");
                mapScreen.setMapData(mapData); // set new map data
                ((CombatMap.MapGameplayPane) getGlassPane()).setMapData(mapData); // update node overlay
                showScreen(mapScreen); // now show the updated screen
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        menu.add(mapTestButton);


        JButton randomFightButton = new JButton("Random Fight");
        randomFightButton.addActionListener(e -> {
            try {
                gameScreen.newFight(startRandomFight(40, 0)); // Example: maxWeight = 10, minWeight = 0
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Game.gui.gameScreen.glassPane.setVisible(true);
            Game.gui.gameScreen.cardLayout.show(Game.gui.gameScreen.centerContainer, "main");
            Game.unslotAllCards();
            gameScreen.center.revalidate();
            gameScreen.center.repaint();
            showScreen(gameScreen);
        });
        menu.add(randomFightButton);
        return menu;
    }


    public NorthPanel startFight(int num) throws IOException {
        switch(num) {
            case 1:
                ArrayList<Enemy> entities = new ArrayList<>();
                entities.add(new SatyrFemale());
                entities.add(new VampireCountess());
                entities.add(new Orc());

                return new NorthPanel(entities);
            case 2:
                ArrayList<Enemy> entities1 = new ArrayList<>();
                Goblin goblin = new Goblin();
                Orc orc = new Orc();
                entities1.add(goblin);
                entities1.add(orc);
                NorthPanel encounter = new NorthPanel(entities1);
                //encounter.positionEnemy(goblin, 950, 150);
                encounter.createSpawnZone(750, 175, 100, 100);
                encounter.createSpawnZone(900, 175, 100, 100);
                encounter.populateSpawnZones();
                return encounter;
            case 3:
                ArrayList<Enemy> entities2 = new ArrayList<>();
                entities2.add(new SatyrFemale());
                //entities2.add(new SpearBoneMan());
                entities2.add(new Goblin());

                return new NorthPanel(entities2);
            case 4:
                ArrayList<Enemy> entities3 = new ArrayList<>();
                entities3.add(new SatyrFemale());
                entities3.add(new SpearBoneMan());
                //entities3.add(new Goblin());

                return new NorthPanel(entities3);
            case 5:
                ArrayList<Enemy> entities5 = new ArrayList<>();
                entities5.add(new SatyrFemale());

                //entities3.add(new Goblin());

                return new NorthPanel(entities5);
            case 6:
                ArrayList<Enemy> entities6 = new ArrayList<>();
                entities6.add(new Goblin());

                //entities3.add(new Goblin());

                return new NorthPanel(entities6);

        }
        return null;
    }

    public void showScreen(JPanel newScreen) {
        if (newScreen == null) {
            System.err.println("Error: Attempted to show a null screen!");
            return;
        }

        if (currentScreen != null) {
            containerPanel.remove(currentScreen);
        }

        currentScreen = newScreen;
        containerPanel.add(newScreen, BorderLayout.CENTER);
        containerPanel.revalidate();
        containerPanel.repaint();

        // If switching to the map screen, activate and resize the GlassPane
        if (newScreen instanceof MapGui) {
            MapGui mapGui = (MapGui) newScreen;
            MapGameplayPane glassPane = (MapGameplayPane) getGlassPane();

            if (mapGui.getMapData() != null) {
                glassPane.setMapData(mapGui.getMapData());

                // Set the glass pane size to match the MapPanel inside MapGui
                Component mapPanel = mapGui.getMapPanel();
                if (mapPanel != null) {
                    glassPane.setBounds(mapPanel.getBounds());
                }

                glassPane.setVisible(true);
                System.out.println("MapGlassPane activated and resized.");
            } else {
                System.err.println("Warning: Map data is null, cannot update GlassPane.");
            }
        } else {
            // Hide the GlassPane when switching away from the map
            getGlassPane().setVisible(false);
            System.out.println("MapGlassPane deactivated.");
        }
    }

    public void returnToOverworld() {
        showScreen(worldPanel);              // Show the overworld map
        gamePanel.gameState = gamePanel.S_PLAY;    // Make sure the state is PLAYING
        worldPanel.startGameThread();        // Restart the game loop (if needed)
        worldPanel.requestFocusInWindow();   // Ensure keyboard input is focused
    }


    public NorthPanel startRandomFight(int maxWeight, int minWeight) throws IOException {
        ArrayList<Enemy> availableEnemies = new ArrayList<>();
        availableEnemies.add(new Goblin());         // Weight: 5
        availableEnemies.add(new Orc());            // Weight: 10
               // Weight: 20
        availableEnemies.add(new SpearBoneMan());   // Weight: 10
        availableEnemies.add(new SatyrFemale());    // Weight: 10
        availableEnemies.add(new Knight());    // Weight: 10
        availableEnemies.add(new Slime());    // Weight: 10
        availableEnemies.add(new VampireCountess());    // Weight: 10

        ArrayList<Enemy> selectedEnemies = new ArrayList<>();
        HashSet<Class<?>> selectedEnemyTypes = new HashSet<>();
        int remainingWeight = maxWeight;
        Random random = new Random();

        // Filter valid enemies
        ArrayList<Enemy> validEnemies = new ArrayList<>();
        for (Enemy e : availableEnemies) {
            if (e.getWeight() >= minWeight && e.getWeight() <= maxWeight) {
                validEnemies.add(e);
            }
        }

        // Loop with filtered enemy list
        while (!validEnemies.isEmpty() && selectedEnemies.size() < 3) {
            List<Enemy> possibleEnemies = new ArrayList<>();

            for (Enemy e : validEnemies) {
                if (!selectedEnemyTypes.contains(e.getClass()) && e.getWeight() <= remainingWeight) {
                    possibleEnemies.add(e);
                }
            }

            if (possibleEnemies.isEmpty()) {
                break;
            }

            Enemy chosenEnemy = possibleEnemies.get(random.nextInt(possibleEnemies.size()));
            selectedEnemies.add(chosenEnemy);
            selectedEnemyTypes.add(chosenEnemy.getClass());
            remainingWeight -= chosenEnemy.getWeight();
        }

        NorthPanel encounter = new NorthPanel(selectedEnemies);

        encounter.createSpawnZone((int)(500 * Config.scaleFactor), (int)(175 * Config.scaleFactor), (int)(100 * Config.scaleFactor), (int)(100 * Config.scaleFactor));
        encounter.createSpawnZone((int)(650 * Config.scaleFactor), (int)(175 * Config.scaleFactor), (int)(100 * Config.scaleFactor), (int)(100 * Config.scaleFactor));
        encounter.createSpawnZone((int)(800 * Config.scaleFactor), (int)(175 * Config.scaleFactor), (int)(100 * Config.scaleFactor), (int)(100 * Config.scaleFactor));
        encounter.populateSpawnZones();

        return encounter;
    }



    public void loadMapAndSwitch(String path, int dungeonIndex) {
        MapData mapData = CombatMap.CombatMapManager.getOrLoadMap(path); //Use persisted version
        mapScreen.setMapData(mapData);
        mapScreen.setCurrentDungeonIndex(dungeonIndex);

        // Update the glass pane too
        MapGameplayPane glassPane = (MapGameplayPane) getGlassPane();
        glassPane.setMapData(mapData);

        showScreen(mapScreen);
    }


}