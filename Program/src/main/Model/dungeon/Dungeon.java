package main.Model.dungeon;

import main.Model.character.Monster;
import main.Model.element.Pillar;
import main.Model.element.Trap;
import main.Model.util.MonsterType;
import main.Model.util.PillarType;
import main.Model.util.Point;
import main.Model.util.RoomType;

import java.util.Random;

/**
 * Represents the entire dungeon, composed of multiple rooms.
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

    public Dungeon(final int theWidth, final int theHeight, final String theDifficulty) {
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
        generateLayout();
    }

    private void generateLayout() {
        Random random = new Random();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                myRooms[y][x] = new Room(new Point(x, y), RoomType.EMPTY);
            }
        }

        myHeroSpawnPoint = new Point(0, 0);
        getRoom(myHeroSpawnPoint).setRoomType(RoomType.ENTRANCE);

        myExitPoint = new Point(myWidth - 1, myHeight - 1);
        getRoom(myExitPoint).setRoomType(RoomType.EXIT);

        int pillarCount = 0;
        final int MAX_PILLARS = 4;
        PillarType[] pillarTypesAvailable = PillarType.values();

        for (int i = 0; i < (myWidth * myHeight) / 4; i++) {
            int randX = random.nextInt(myWidth);
            int randY = random.nextInt(myHeight);
            Point currentPoint = new Point(randX, randY);
            Room currentRoom = getRoom(currentPoint);

            if (currentRoom == null || currentPoint.equals(myHeroSpawnPoint) || currentPoint.equals(myExitPoint) || currentRoom.getRoomType() != RoomType.EMPTY) {
                continue;
            }

            int typeRoll = random.nextInt(100);
            if (typeRoll < 30) { // Monster Room
                currentRoom.setRoomType(RoomType.MONSTER);
                // Example: Add a Goblin
                // Monster monster = new Monster(MonsterType.GOBLIN, currentPoint);
                // currentRoom.addMonster(monster);
            } else if (typeRoll < 50 && pillarCount < MAX_PILLARS && pillarTypesAvailable.length > 0) { // Pillar Room
                currentRoom.setPillar(new Pillar(pillarTypesAvailable[random.nextInt(pillarTypesAvailable.length)]));
                pillarCount++;
            } else if (typeRoll < 70) { // Treasure Room
                currentRoom.setRoomType(RoomType.TREASURE);
                // TODO: Add Item instances
            } else if (typeRoll < 85) { // Trap Room
                currentRoom.setTrap(new Trap("Floor Spikes", "Sharp spikes emerge from the floor.", 10));
            }
        }
        this.myTotalPillars = pillarCount;

        // Basic door generation
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Room room = myRooms[y][x];
                if (y > 0) room.setNorthDoor(true);
                if (y < myHeight - 1) room.setSouthDoor(true);
                if (x > 0) room.setWestDoor(true);
                if (x < myWidth - 1) room.setEastDoor(true);
            }
        }
        System.out.println("Dungeon layout generated. Total Pillars to find: " + myTotalPillars);
    }

    public Room getRoom(final int x, final int y) {
        if (x >= 0 && x < myWidth && y >= 0 && y < myHeight) {
            return myRooms[y][x];
        }
        return null;
    }

    public Room getRoom(final Point thePoint) {
        if (thePoint == null) return null;
        return getRoom(thePoint.getX(), thePoint.getY());
    }

    public int getWidth() {
        return myWidth;
    }

    public int getHeight() {
        return myHeight;
    }

    public Point getHeroSpawnPoint() {
        return myHeroSpawnPoint;
    }

    public Point getExitPoint() {
        return myExitPoint;
    }

    public int getTotalPillars() {
        return myTotalPillars;
    }

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

    public boolean areAllPillarsActivated() {
        // Ensure there are pillars to activate for this condition to be meaningful
        return myTotalPillars > 0 && myActivatedPillars >= myTotalPillars;
    }

    public boolean isBossSpawned() {
        return myBossSpawned;
    }

    private void spawnBoss() {
        if (myBossSpawned) return;

        Room bossRoom = getRoom(myExitPoint);
        if (bossRoom != null) {
            bossRoom.setRoomType(RoomType.BOSS);
            bossRoom.getMonsters().clear();

            // Assuming MonsterType.GIANT is defined as a boss type
            MonsterType bossType = MonsterType.GIANT;
            Monster boss = new Monster(
                    bossType.getName(),
                    bossType,
                    bossType.isElite(), // Or specific logic for boss elite status
                    bossType.getBaseHealth(),
                    bossRoom.getPosition()
            );
            bossRoom.addMonster(boss);
            this.myBossSpawned = true;
            System.out.println("All pillars activated! The " + boss.getName() + " has appeared in the " + bossRoom.getRoomType().getDisplayName() + " room!");
        } else {
            System.err.println("Error: Could not find designated boss room at " + myExitPoint + " to spawn boss.");
        }
    }

    public String getDifficulty() {
        return myDifficulty;
    }

    public String getMapString(final Point heroCurrentPosition) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Room room = myRooms[y][x];
                if (room.getPosition().equals(heroCurrentPosition)) {
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
}
