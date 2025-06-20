package main.Model.dungeon;

import main.Model.character.Monster;
import main.Model.character.MonsterFactory;
import main.Model.element.*;
import main.Model.util.MonsterType;
import main.Model.util.PillarType;
import main.Model.util.Point;
import main.Model.util.RoomType;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Represents the entire dungeon, composed of multiple rooms.
 * Uses DFS to generate a randomized layout.
 * IMPORTANT: FULL DUNGEON PRINT MENU OPTION IS IN HELP MENU, BUTTON CALLED "VISION CHEAT"
 * THIS VISION CHEAT SHOWS FULL DUNGEON ON THE MINIMAP.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class Dungeon {
    private final Room[][] myRooms;
    private final int myWidth;
    private final int myHeight;
    private Point myHeroSpawnPoint;
    private Point myExitPoint;
    private int myTotalPillars;
    private int myActivatedPillars; // This should be updated by GameController based on Pillar activation
    private boolean myBossSpawned;
    private String myDifficulty;
    private final MonsterFactory myMonsterFactory;

    /**
     * Constructor for the dungeon
     *
     * @param theWidth dungeon width
     * @param theHeight dungeon height
     * @param theDifficulty dungeon difficulty
     * @param theNewDungeon keeps track if this is a new or loaded dungeon
     */
    public Dungeon(final int theWidth, final int theHeight, final String theDifficulty, final boolean theNewDungeon) {
        if (theWidth <= 0 || theHeight <= 0) {
            throw new IllegalArgumentException("Dungeon dimensions must be positive.");
        }
        this.myWidth = theWidth;
        this.myHeight = theHeight;
        this.myRooms = new Room[theHeight][theWidth];
        this.myDifficulty = theDifficulty;
        this.myTotalPillars = 0;
        this.myActivatedPillars = 0;
        this.myBossSpawned = false;
        this.myMonsterFactory = new MonsterFactory();
        // This stops a load from save file from creating new random placements.
        if (theNewDungeon) {
            //generateDemoLayout(); // Demo purposes
             generateLayout();
        }
    }

    /**
     * Generates a randomized dungeon layout using Depth-First Search (DFS).
     * Ensures connectivity and places special rooms.
     */
    private void generateLayout() {
        Random random = new Random();

        // 1. Initialize all rooms with all walls up (no doors)
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                myRooms[y][x] = new Room(new Point(x, y), RoomType.EMPTY);
            }
        }

        // 2. DFS for Maze Generation
        Stack<Point> stack = new Stack<>();
        boolean[][] visited = new boolean[myHeight][myWidth];
        myHeroSpawnPoint = new Point(0, 0); // Define start
        myExitPoint = new Point(myWidth - 1, myHeight - 1); // Define end

        Point current = myHeroSpawnPoint;
        visited[current.getY()][current.getX()] = true;
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.peek();
            List<Point> neighbors = getUnvisitedNeighbors(current, visited);

            if (!neighbors.isEmpty()) {
                Point next = neighbors.get(random.nextInt(neighbors.size()));
                removeWall(current, next); // Carve path (add doors)
                visited[next.getY()][next.getX()] = true;
                stack.push(next);
            } else {
                stack.pop(); // Backtrack
            }
        }

        // Optional: Add some loops by removing a few extra walls
        int extraDoors = (myWidth * myHeight) / 10; // e.g., 10% extra doors
        for (int i = 0; i < extraDoors; i++) {
            int randX = random.nextInt(myWidth);
            int randY = random.nextInt(myHeight);
            Point roomPoint = new Point(randX, randY);
            List<Point> allNeighbors = getAllNeighbors(roomPoint);
            if (!allNeighbors.isEmpty()) {
                removeWall(roomPoint, allNeighbors.get(random.nextInt(allNeighbors.size())));
            }
        }


        // 3. Set Start/Exit Types
        getRoom(myHeroSpawnPoint).setRoomType(RoomType.ENTRANCE);
        getRoom(myExitPoint).setRoomType(RoomType.EXIT);

        // 4. Place Pillars (Ensure 4 are placed and reachable)
        myTotalPillars = 0;
        PillarType[] pillarTypes = PillarType.values();
        List<Point> availableSpots = new ArrayList<>();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Point p = new Point(x, y);
                // Add spots that are not start/exit
                if (!p.equals(myHeroSpawnPoint) && !p.equals(myExitPoint)) {
                    availableSpots.add(p);
                }
            }
        }
        Collections.shuffle(availableSpots, random); // Randomize potential spots

        int pillarsToPlace = Math.min(4, pillarTypes.length); // Ensure we don't exceed available pillar types
        Set<Point> pillarLocations = new HashSet<>(); // Track pillar locations
        for (int i = 0; i < pillarsToPlace && !availableSpots.isEmpty(); i++) {
            Point pillarPoint = availableSpots.remove(0); // Pick a random spot
            Room room = getRoom(pillarPoint);

            // Place the pillar
            room.setPillar(new Pillar(pillarTypes[i]));
            pillarLocations.add(pillarPoint); // Track this location
            myTotalPillars++;

            System.out.println("Placed pillar " + (i + 1) + ": " + pillarTypes[i].getDisplayName() +
                    " at " + pillarPoint);
        }

        // 5. Place Other Rooms (Monsters, Traps)
        int monsterCount = (myWidth * myHeight) / 5; // Adjust density as needed
        int trapCount = (myWidth * myHeight) / 10;
        int chestCount = (myWidth * myHeight) / 10;
        int potionCount = (myWidth * myHeight) / 10;

        availableSpots.removeIf(point -> {
            Room room = getRoom(point);
            return room.getRoomType() != RoomType.EMPTY || pillarLocations.contains(point);
        });

        while ((monsterCount > 0 || trapCount > 0 || chestCount > 0) && !availableSpots.isEmpty()) {
            Point spot = availableSpots.remove(0); // Pick another random spot
            Room room = getRoom(spot);

            if (room.getRoomType() == RoomType.EMPTY && !room.hasPillar()) {
                if (monsterCount > 0 && random.nextBoolean()) { // Alternate placing monsters/traps
                    room.setRoomType(RoomType.MONSTER);
                    // Add an actual monster
                    addMonsterToRoom(room, spot);
                    monsterCount--;
                } else if (trapCount > 0) {
                    room.setTrap(new Trap("Floor Spikes", "Sharp spikes emerge from the floor.", 5 + random.nextInt(10)));
                    trapCount--;
                } else if (chestCount > 0) {
                    createChest(room);
                    room.setRoomType(RoomType.TREASURE);
                    chestCount--;
                }
            }
        }

        // 6. Place potions in remaining empty rooms
        // Get remaining empty spots (avoiding all special rooms)
        List<Point> potionSpots = new ArrayList<>();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Point p = new Point(x, y);
                Room room = getRoom(p);
                if (room.getRoomType() == RoomType.EMPTY && !room.hasPillar()) {
                    potionSpots.add(p);
                }
            }
        }
        Collections.shuffle(potionSpots, random);

        int potionsPlaced = 0;
        while (potionCount > 0 && potionsPlaced < potionSpots.size()) {
            Point spot = potionSpots.get(potionsPlaced);
            Room room = getRoom(spot);

            if (potionCount % 2 == 0) {
                room.addItem(new HealthPotion("Health Potion", "Heals 50", 50));
            } else {
                room.addItem(new VisionPotion("Vision Potion", "Reveals nearby tiles", this));
            }

            potionCount--;
            potionsPlaced++;
        }

        System.out.println("Dungeon generation complete:");
        System.out.println("- Total Pillars: " + myTotalPillars);
        System.out.println("- Expected Pillars: 4");

        // Debug: Count actual pillars in dungeon
        int actualPillars = 0;
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                if (getRoom(x, y).hasPillar()) {
                    actualPillars++;
                }
            }
        }
        System.out.println("- Actual Pillars in Dungeon: " + actualPillars);

        if (actualPillars != 4) {
            System.err.println("WARNING: Pillar count mismatch! Expected 4, found " + actualPillars);
        }
    }

    /**
     * Helper to find all *unvisited* neighbors for DFS.
     */
    private List<Point> getUnvisitedNeighbors(final Point theCurrent, final boolean[][] theVisited) {
        List<Point> neighbors = new ArrayList<>();
        int x = theCurrent.getX();
        int y = theCurrent.getY();

        // North
        if (y > 0 && !theVisited[y - 1][x]) neighbors.add(new Point(x, y - 1));
        // South
        if (y < myHeight - 1 && !theVisited[y + 1][x]) neighbors.add(new Point(x, y + 1));
        // West
        if (x > 0 && !theVisited[y][x - 1]) neighbors.add(new Point(x - 1, y));
        // East
        if (x < myWidth - 1 && !theVisited[y][x + 1]) neighbors.add(new Point(x + 1, y));

        return neighbors;
    }

    /**
     * Helper to find *all* neighbors (for adding loops).
     */
    private List<Point> getAllNeighbors(final Point theCurrent) {
        List<Point> neighbors = new ArrayList<>();
        int x = theCurrent.getX();
        int y = theCurrent.getY();

        if (y > 0) neighbors.add(new Point(x, y - 1));
        if (y < myHeight - 1) neighbors.add(new Point(x, y + 1));
        if (x > 0) neighbors.add(new Point(x - 1, y));
        if (x < myWidth - 1) neighbors.add(new Point(x + 1, y));

        return neighbors;
    }


    /**
     * Helper to "remove a wall" between two adjacent rooms by setting their door flags.
     */
    private void removeWall(final Point theCurrent, final Point theNext) {
        Room currentRoom = getRoom(theCurrent);
        Room nextRoom = getRoom(theNext);

        // Moving Right (East)
        if (theCurrent.getX() < theNext.getX()) {
            currentRoom.setEastDoor(true);
            nextRoom.setWestDoor(true);
        }
        // Moving Left (West)
        else if (theCurrent.getX() > theNext.getX()) {
            currentRoom.setWestDoor(true);
            nextRoom.setEastDoor(true);
        }
        // Moving Down (South)
        else if (theCurrent.getY() < theNext.getY()) {
            currentRoom.setSouthDoor(true);
            nextRoom.setNorthDoor(true);
        }
        // Moving Up (North)
        else if (theCurrent.getY() > theNext.getY()) {
            currentRoom.setNorthDoor(true);
            nextRoom.setSouthDoor(true);
        }
    }

    /**
     * Gets room at a location
     * @param theX the x coordinate of room
     * @param theY the y coordinate of room
     * @return a room.
     */
    public Room getRoom(final int theX, final int theY) {
        if (theX >= 0 && theX < myWidth && theY >= 0 && theY < myHeight) {
            return myRooms[theY][theX];
        }
        return null;
    }

    /**
     * Same as the other getRoom, but uses a point.
     * @param thePoint point for locating room
     * @return a room.
     */
    public Room getRoom(final Point thePoint) {
        if (thePoint == null) return null;
        return getRoom(thePoint.getX(), thePoint.getY());
    }

    /**
     * gets dungeon width
     * @return width
     */
    public int getWidth() {
        return myWidth;
    }

    /**
     * gets dungeon height
     * @return dungeon height
     */
    public int getHeight() {
        return myHeight;
    }

    /**
     * finds where hero spawned
     * @return Point of hero spawn
     */
    public Point getHeroSpawnPoint() {
        return myHeroSpawnPoint;
    }

    /**
     * finds dungeon exit
     * @return exit point
     */
    public Point getExitPoint() {
        return myExitPoint;
    }

    /**
     * gets total pillars
     * @return pillar count
     */
    public int getTotalPillars() {
        return myTotalPillars;
    }

    /**
     * gets activated pillars
     * @return activated pillar count
     */
    public int getActivatedPillars() {
        return myActivatedPillars;
    }

    /**
     * Called by the GameController when a pillar is confirmed activated by a hero.
     */
    public void recordPillarActivation() {
        this.myActivatedPillars++;
        System.out.println("Dungeon records pillar activation. Total now: " + myActivatedPillars + "/" + myTotalPillars);
        if (areAllPillarsActivated() && !myBossSpawned) {
            spawnBoss();
        }
    }

    /**
     * checks if all pillars on
     * @return true if yes
     */
    public boolean areAllPillarsActivated() {
        // Ensure there are pillars to activate for this condition to be meaningful
        return myTotalPillars > 0 && myActivatedPillars >= myTotalPillars;
    }

    /**
     * checks if boss was spawned
     * @return true if yes
     */
    public boolean isBossSpawned() {
        return myBossSpawned;
    }

    /**
     * spawns boss.
     */
    private void spawnBoss() {
        if (myBossSpawned) return;

        Room bossRoom = getRoom(myExitPoint);
        if (bossRoom != null) {
            bossRoom.setRoomType(RoomType.BOSS);
            if (!bossRoom.getMonsters().isEmpty()) {
                bossRoom.getMonsters().clear();
            }

            // Assuming MonsterType.GIANT is defined as a boss type
            MonsterType bossType = MonsterType.GIANT;
            // Not sure if try catch are the best things here but gets rid of SQL error.
            Monster boss = myMonsterFactory.getMonster(bossType, bossRoom.getPosition());
            bossRoom.addMonster(boss);
            this.myBossSpawned = true;
            System.out.println("All pillars activated! The " + boss.getName() + " has appeared in the " + bossRoom.getRoomType().getDisplayName() + " room!");

        } else {
            System.err.println("Error: Could not find designated boss room at " + myExitPoint + " to spawn boss.");
        }
    }

    /**
     * gets game difficulty
     * @return string of difficulty
     */
    public String getDifficulty() {
        return myDifficulty;
    }

    /**
     * gets map in string form
     * @param theHeroCurrentPosition hero's current location
     * @return map string
     */
    public String getMapString(final Point theHeroCurrentPosition) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Room room = myRooms[y][x];
                if (room.getPosition().equals(theHeroCurrentPosition)) {
                    sb.append("[H]");
                } else if (room.isVisited()) {
                    switch (room.getRoomType()) {
                        case ENTRANCE: sb.append("[E]"); break;
                        case EXIT: sb.append("[X]"); break;
                        case BOSS: sb.append("[B]"); break;
                        case PILLAR: sb.append(room.getPillar() != null && room.getPillar().isActivated() ? "[A]" : "[P]"); break;
                        case MONSTER: sb.append(room.getMonsters().isEmpty() ? "[ ]" : "[M]"); break;
                        case TREASURE: sb.append(room.getItems().isEmpty() ? "[ ]" : "[$]"); break;
                        case TRAP: sb.append(room.getTrap() != null && room.getTrap().isSprung() ? "[_]" : "[!]"); break;
                        case EMPTY: sb.append("[ ]"); break;
                        default: sb.append("[?]"); break;
                    }
                } else {
                    sb.append("[#]");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * gives percent chances of each monster spawning
     * @param theRoom room to spawn
     * @param theSpot point to spawn at
     */
    private void addMonsterToRoom(final Room theRoom, final Point theSpot) {
        Random rand = new Random();
        double percentChance = rand.nextDouble(1);
        if (percentChance < .3) {
            theRoom.addMonster(myMonsterFactory.getMonster(MonsterType.GOBLIN, theSpot));
        } else if (percentChance < .5) {
            theRoom.addMonster(myMonsterFactory.getMonster(MonsterType.SKELETON, theSpot));
        } else if (percentChance < .7) {
            theRoom.addMonster(myMonsterFactory.getMonster(MonsterType.SLIME, theSpot));
        } else if (percentChance < .8) {
            theRoom.addMonster(myMonsterFactory.getMonster(MonsterType.ORC, theSpot));
        } else if (percentChance < .9) {
            theRoom.addMonster(myMonsterFactory.getMonster(MonsterType.BIG_SLIME, theSpot));
        } else {
            theRoom.addMonster(myMonsterFactory.getMonster(MonsterType.WIZARD, theSpot));
        }
    }

    /**
     * makes a chest in the room
     * @param theRoom room for the chest
     */
    private void createChest(final Room theRoom) {
        Random rand = new Random();
        int itemAmount = rand.nextInt(5) + 1;
        List<Item> chestItems = new ArrayList<>();
        while (itemAmount != 0) {
            if (itemAmount % 2 == 0) {
                chestItems.add(new HealthPotion("Health Potion", "Heals 50", 50));
            } else {
                chestItems.add(new VisionPotion("Vision Potion", "Reveals nearby tiles", this));
            }

            itemAmount--;
        }
        theRoom.setChest(chestItems);
    }

    /**
     * serializes dungeon to JSON format for saving
     * Converts entire dungeon state including dimensions, pillar status,
     * spawn points, and all room data into a JSON string that can be stored in database
     *
     * @return JSON string representation of dungeon, or null if serialization fails
     */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DungeonSaveData saveData = new DungeonSaveData();
            saveData.width = myWidth;
            saveData.height = myHeight;
            saveData.difficulty = myDifficulty;
            saveData.totalPillars = myTotalPillars;
            saveData.activatedPillars = myActivatedPillars;
            saveData.bossSpawned = myBossSpawned;
            saveData.heroSpawnX = myHeroSpawnPoint.getX();
            saveData.heroSpawnY = myHeroSpawnPoint.getY();
            saveData.exitX = myExitPoint.getX();
            saveData.exitY = myExitPoint.getY();

            //serialize room data
            saveData.roomData = new RoomSaveData[myHeight][myWidth];
            for (int y = 0; y < myHeight; y++) {
                for (int x = 0; x < myWidth; x++) {
                    Room room = myRooms[y][x];
                    RoomSaveData roomSave = new RoomSaveData();
                    roomSave.x = x;
                    roomSave.y = y;
                    roomSave.roomType = room.getRoomType().name();
                    roomSave.visited = room.isVisited();
                    roomSave.chestOpened = room.getChestOpened();
                    roomSave.hasNorthDoor = room.hasNorthDoor();
                    roomSave.hasEastDoor = room.hasEastDoor();
                    roomSave.hasSouthDoor = room.hasSouthDoor();
                    roomSave.hasWestDoor = room.hasWestDoor();

                    //save pillar data if present
                    if (room.hasPillar()) {
                        roomSave.pillarType = room.getPillar().getType().name();
                        roomSave.pillarActivated = room.getPillar().isActivated();
                    }

                    //save trap data if present
                    if (room.hasTrap()) {
                        roomSave.trapName = room.getTrap().getName();
                        roomSave.trapDamage = room.getTrap().getDamage();
                        roomSave.trapSprung = room.getTrap().isSprung();
                    }

                    // If it has a monster save it
                    if (room.getRoomType() == RoomType.MONSTER && !room.getMonsters().isEmpty()) {
                        // Assuming here that there is only one monster per room
                        roomSave.monster = room.getMonsters().getFirst().getType();
                        roomSave.isDefeated = false;
                    } else {
                        // Otherwise just assign goblin to monster to avoid null errors.
                        // isDefeated being true should stop monster from being spawned.
                        roomSave.monster = MonsterType.GOBLIN;
                        roomSave.isDefeated = true;
                    }

                    if (room.getRoomType() == RoomType.TREASURE && !room.getChest().isEmpty()) {
                        roomSave.itemCount = 0;
                        for (Item item : room.getChest()) {
                            roomSave.itemCount++;
                        }
                        roomSave.potionType = "Chest";
                    } else if (!room.getItems().isEmpty()) {
                        roomSave.itemCount = 1;
                        roomSave.potionType = room.getItems().getFirst().getName();
                    }

                    saveData.roomData[y][x] = roomSave;
                }
            }

            return mapper.writeValueAsString(saveData);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing dungeon: " + e.getMessage());
            return null;
        }
    }


    /**
     * deserializes a dungeon from JSON format
     * create new Dungeon instance from saved JSON data, restoring all dungeon state
     * including room layout, doors, pillars, traps, and other dungeon properties
     *
     * @param json JSON string containing serialized dungeon data
     * @return A new Dungeon instance restored from the JSON data, or null if deserialization fails
     */
    public static Dungeon fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DungeonSaveData saveData = mapper.readValue(json, DungeonSaveData.class);

            //create new dungeon with saved dimensions and difficulty
            Dungeon dungeon = new Dungeon(saveData.width, saveData.height, saveData.difficulty, false);

            //restore dungeon state
            dungeon.myTotalPillars = saveData.totalPillars;
            dungeon.myActivatedPillars = saveData.activatedPillars;
            dungeon.myBossSpawned = saveData.bossSpawned;
            dungeon.myHeroSpawnPoint = new Point(saveData.heroSpawnX, saveData.heroSpawnY);
            dungeon.myExitPoint = new Point(saveData.exitX, saveData.exitY);


            for (int y = 0; y < dungeon.getHeight(); y++) {
                for (int x = 0; x < dungeon.getWidth(); x++) {
                    dungeon.myRooms[y][x] = new Room(new Point(x, y), RoomType.EMPTY);
                }
            }

            //restore room data
            for (int y = 0; y < saveData.height; y++) {
                for (int x = 0; x < saveData.width; x++) {
                    RoomSaveData roomSave = saveData.roomData[y][x];
                    Room room = dungeon.myRooms[y][x];

                    room.setRoomType(RoomType.valueOf(roomSave.roomType));
                    room.setVisited(roomSave.visited);
                    room.setChestOpened(roomSave.chestOpened);
                    room.setItemsCollected(roomSave.itemsCollected);
                    room.setNorthDoor(roomSave.hasNorthDoor);
                    room.setEastDoor(roomSave.hasEastDoor);
                    room.setSouthDoor(roomSave.hasSouthDoor);
                    room.setWestDoor(roomSave.hasWestDoor);

                    //restore pillar if present
                    if (roomSave.pillarType != null) {
                        PillarType pillarType = PillarType.valueOf(roomSave.pillarType);
                        Pillar pillar = new Pillar(pillarType);
                        if (roomSave.pillarActivated) {
                            room.removePillar();
                        } else {
                            room.setPillar(pillar);
                        }
                    }

                    if (room.getRoomType() == RoomType.MONSTER && !roomSave.isDefeated) {
                        MonsterFactory m = new MonsterFactory();
                        room.addMonster(m.getMonster(roomSave.monster, room.getPosition()));
                    }

                    if (room.getRoomType() == RoomType.TREASURE) {
                        int itemsLeft = roomSave.itemCount;
                        while (itemsLeft != 0) {
                            if (itemsLeft % 2 == 0) {
                                room.getChest().add(new HealthPotion("Health Potion", "Heals 50", 50));
                            } else {
                                room.getChest().add(new VisionPotion("Vision Potion", "Reveals nearby tiles", dungeon));
                            }
                            itemsLeft--;
                        }
                    } else if (roomSave.itemCount == 1 && !roomSave.potionType.equals("Chest")) {
                        if (roomSave.potionType.equals("Health Potion")) {
                            room.addItem(new HealthPotion("Health Potion", "Heals 50", 50));
                        } else {
                            room.addItem(new VisionPotion("Vision Potion", "Reveals nearby tiles", dungeon));
                        }
                    }

                    //restore trap if present
                    if (roomSave.trapName != null) {
                        Trap trap = new Trap(roomSave.trapName, "Restored trap", roomSave.trapDamage);
                        if (roomSave.trapSprung) {
                            //implement a way to set trap as sprung
                            // trap.setSprung(true);
                        }
                        room.setTrap(trap);
                    }

                    // Restore boss if spawned
                    if (room.getRoomType() == RoomType.BOSS && dungeon.myBossSpawned) {
                        dungeon.myBossSpawned = false;
                        dungeon.spawnBoss();
                    }
                }
            }

            return dungeon;
        } catch (Exception e) {
            System.err.println("Error deserializing dungeon: " + e.getMessage());
            return null;
        }
    }

    // DEMO DUNGEON FOR PRESENTATION
    // Add this method to your Dungeon.java class

    // Add this method to your Dungeon.java class

    /**
     * Creates a simple linear demo dungeon in a straight line.
     * Layout: START -> CHEST -> PILLAR1 -> ITEM -> PILLAR2 -> TRAP -> PILLAR3 -> MONSTER -> PILLAR4 -> BOSS
     * Total: 10 rooms in a straight horizontal line
     */
    private void generateDemoLayout() {
        // Force dungeon to be 10x1 for straight line
        if (myWidth < 10) {
            System.err.println("Warning: Demo dungeon needs at least 10 width. Current: " + myWidth);
        }

        // 1. Initialize all rooms as EMPTY
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                myRooms[y][x] = new Room(new Point(x, y), RoomType.EMPTY);
            }
        }

        // 2. Create straight line path (horizontal)
        for (int x = 0; x < Math.min(10, myWidth) - 1; x++) {
            removeWall(new Point(x, 0), new Point(x + 1, 0));
        }

        // 3. Set up the linear demo sequence
        myHeroSpawnPoint = new Point(0, 0);
        myExitPoint = new Point(9, 0);

        setupLinearDemo();

        System.out.println("=== LINEAR DEMO DUNGEON CREATED ===");
        System.out.println("Path: START -> CHEST -> PILLAR1 -> ITEM -> PILLAR2 -> TRAP -> PILLAR3 -> MONSTER -> PILLAR4 -> BOSS");
        printDemoLayout();
    }

    /**
     * Sets up the linear demo with features in sequence
     */
    private void setupLinearDemo() {
        // Room 0: START (0,0)
        getRoom(0, 0).setRoomType(RoomType.ENTRANCE);

        // Room 1: CHEST (1,0)
        Room chestRoom = getRoom(1, 0);
        createDemoChest(chestRoom);

        // Room 2: PILLAR 1 - Abstraction (2,0)
        Room pillar1Room = getRoom(2, 0);
        pillar1Room.setPillar(new Pillar(PillarType.ABSTRACTION));
        myTotalPillars++;

        // Room 3: HEALTH POTION ITEM (3,0)
        Room itemRoom = getRoom(3, 0);
        itemRoom.addItem(new HealthPotion("Health Potion", "Restores 50 health", 50));

        // Room 4: PILLAR 2 - Encapsulation (4,0)
        Room pillar2Room = getRoom(4, 0);
        pillar2Room.setPillar(new Pillar(PillarType.ENCAPSULATION));
        myTotalPillars++;

        // Room 5: TRAP (5,0)
        Room trapRoom = getRoom(5, 0);
        Trap spikeTrap = new Trap("Demo Spike Trap", "Sharp spikes emerge from the floor", 10);
        trapRoom.setTrap(spikeTrap);

        // Room 6: PILLAR 3 - Inheritance (6,0)
        Room pillar3Room = getRoom(6, 0);
        pillar3Room.setPillar(new Pillar(PillarType.INHERITANCE));
        myTotalPillars++;

        // Room 7: MONSTER (7,0)
        Room monsterRoom = getRoom(7, 0);
        monsterRoom.setRoomType(RoomType.MONSTER);
        Monster goblin = myMonsterFactory.getMonster(MonsterType.GOBLIN, new Point(7, 0));
        monsterRoom.addMonster(goblin);

        // Room 8: PILLAR 4 - Polymorphism (8,0)
        Room pillar4Room = getRoom(8, 0);
        pillar4Room.setPillar(new Pillar(PillarType.POLYMORPHISM));
        myTotalPillars++;

        // Room 9: BOSS ROOM (9,0)
        Room bossRoom = getRoom(9, 0);
        bossRoom.setRoomType(RoomType.BOSS);
        Monster boss = myMonsterFactory.getMonster(MonsterType.GIANT, new Point(9, 0));
        bossRoom.addMonster(boss);
        myBossSpawned = true;

        System.out.println("Linear demo setup complete:");
        System.out.println("Room 0: START");
        System.out.println("Room 1: CHEST");
        System.out.println("Room 2: PILLAR (Abstraction)");
        System.out.println("Room 3: HEALTH POTION");
        System.out.println("Room 4: PILLAR (Encapsulation)");
        System.out.println("Room 5: TRAP");
        System.out.println("Room 6: PILLAR (Inheritance)");
        System.out.println("Room 7: MONSTER (Goblin)");
        System.out.println("Room 8: PILLAR (Polymorphism)");
        System.out.println("Room 9: BOSS (Giant)");
    }

    /**
     * Creates a chest with demo items - gives player gold and useful items
     */
    private void createDemoChest(Room room) {
        List<Item> chestItems = new ArrayList<>();

        // Add helpful items for the demo
        chestItems.add(new HealthPotion("Chest Health Potion", "Restores 75 health", 75));
        chestItems.add(new VisionPotion("Vision Potion", "Reveals nearby rooms", this));

        room.setChest(chestItems);
        System.out.println("Demo chest created with 2 items");
    }

    // Simplified layout printer for linear dungeon
    public void printDemoLayout() {
        System.out.println("\n=== LINEAR DEMO DUNGEON ===");
        System.out.print("Layout: ");

        for (int x = 0; x < Math.min(10, myWidth); x++) {
            Room room = getRoom(x, 0);
            String symbol = getLinearRoomSymbol(room, x);
            System.out.print(symbol);
            if (x < Math.min(9, myWidth - 1)) {
                System.out.print(" -> ");
            }
        }

        System.out.println("\n");
        System.out.println("Legend:");
        System.out.println("S = Start, C = Chest, P = Pillar, H = Health Potion");
        System.out.println("T = Trap, M = Monster, B = Boss");
        System.out.println("===========================\n");
    }

    private String getLinearRoomSymbol(Room room, int x) {
        if (x == 0) return "S";  // Start
        if (x == 1) return "C";  // Chest
        if (x == 2) return "P1"; // Pillar 1
        if (x == 3) return "H";  // Health Potion
        if (x == 4) return "P2"; // Pillar 2
        if (x == 5) return "T";  // Trap
        if (x == 6) return "P3"; // Pillar 3
        if (x == 7) return "M";  // Monster
        if (x == 8) return "P4"; // Pillar 4
        if (x == 9) return "B";  // Boss
        return "?";
    }



    //inner classes for save data
    private static class DungeonSaveData {
        public int width;
        public int height;
        public String difficulty;
        public int totalPillars;
        public int activatedPillars;
        public boolean bossSpawned;
        public int heroSpawnX;
        public int heroSpawnY;
        public int exitX;
        public int exitY;
        public RoomSaveData[][] roomData;
    }

    private static class RoomSaveData {
        public int x;
        public int y;
        public String roomType;
        public boolean visited;
        public boolean chestOpened;
        public boolean itemsCollected;
        public boolean hasNorthDoor;
        public boolean hasEastDoor;
        public boolean hasSouthDoor;
        public boolean hasWestDoor;
        public String pillarType;
        public boolean pillarActivated;
        public String trapName;
        public int trapDamage;
        public boolean trapSprung;
        public MonsterType monster;
        public boolean isDefeated;
        public int itemCount;
        public String potionType;
    }


}
