package test.Model.Dungeon;

import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.element.Pillar;
import main.Model.util.Point;
import main.Model.util.PillarType;
import main.Model.util.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class DungeonTest {

    private Dungeon dungeon;
    private static final int TEST_WIDTH = 10;
    private static final int TEST_HEIGHT = 5;
    private static final String TEST_DIFFICULTY = "Medium";

    @BeforeEach
    void setUp() {
        dungeon = new Dungeon(TEST_WIDTH, TEST_HEIGHT, TEST_DIFFICULTY, true);
    }

    @Test
    @DisplayName("Constructor should create dungeon with correct dimensions")
    void testConstructorValidDimensions() {
        assertEquals(TEST_WIDTH, dungeon.getWidth());
        assertEquals(TEST_HEIGHT, dungeon.getHeight());
        assertEquals(TEST_DIFFICULTY, dungeon.getDifficulty());
        assertNotNull(dungeon.getHeroSpawnPoint());
        assertNotNull(dungeon.getExitPoint());
    }

    @Test
    @DisplayName("Constructor should throw exception for invalid dimensions")
    void testConstructorInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () ->
                new Dungeon(0, 5, "Easy", true));
        assertThrows(IllegalArgumentException.class, () ->
                new Dungeon(5, 0, "Easy", true));
        assertThrows(IllegalArgumentException.class, () ->
                new Dungeon(-1, 5, "Easy", true));
        assertThrows(IllegalArgumentException.class, () ->
                new Dungeon(5, -1, "Easy", true));
    }

    @Test
    @DisplayName("getRoom should return correct room for valid coordinates")
    void testGetRoomValidCoordinates() {
        Room room = dungeon.getRoom(0, 0);
        assertNotNull(room);
        assertEquals(new Point(0, 0), room.getPosition());

        Room roomByPoint = dungeon.getRoom(new Point(0, 0));
        assertNotNull(roomByPoint);
        assertEquals(room, roomByPoint);
    }

    @Test
    @DisplayName("getRoom should return null for invalid coordinates")
    void testGetRoomInvalidCoordinates() {
        assertNull(dungeon.getRoom(-1, 0));
        assertNull(dungeon.getRoom(0, -1));
        assertNull(dungeon.getRoom(TEST_WIDTH, 0));
        assertNull(dungeon.getRoom(0, TEST_HEIGHT));
        assertNull(dungeon.getRoom(null));
    }

    @Test
    @DisplayName("Hero spawn point should be valid")
    void testHeroSpawnPoint() {
        Point spawnPoint = dungeon.getHeroSpawnPoint();
        assertNotNull(spawnPoint);
        assertTrue(spawnPoint.getX() >= 0 && spawnPoint.getX() < TEST_WIDTH);
        assertTrue(spawnPoint.getY() >= 0 && spawnPoint.getY() < TEST_HEIGHT);

        Room spawnRoom = dungeon.getRoom(spawnPoint);
        assertNotNull(spawnRoom);
        assertEquals(RoomType.ENTRANCE, spawnRoom.getRoomType());
    }

    @Test
    @DisplayName("Exit point should be valid")
    void testExitPoint() {
        Point exitPoint = dungeon.getExitPoint();
        assertNotNull(exitPoint);
        assertTrue(exitPoint.getX() >= 0 && exitPoint.getX() < TEST_WIDTH);
        assertTrue(exitPoint.getY() >= 0 && exitPoint.getY() < TEST_HEIGHT);

        Room exitRoom = dungeon.getRoom(exitPoint);
        assertNotNull(exitRoom);
        // Exit room might be BOSS type if boss is spawned, otherwise EXIT
        assertTrue(exitRoom.getRoomType() == RoomType.EXIT ||
                exitRoom.getRoomType() == RoomType.BOSS);
    }

    @Test
    @DisplayName("Initial pillar count should be correct")
    void testInitialPillarCount() {
        assertEquals(4, dungeon.getTotalPillars());
        assertEquals(0, dungeon.getActivatedPillars());
        assertFalse(dungeon.areAllPillarsActivated());
    }

    @Test
    @DisplayName("Pillar activation should work correctly")
    void testPillarActivation() {
        assertEquals(0, dungeon.getActivatedPillars());

        dungeon.recordPillarActivation();
        assertEquals(1, dungeon.getActivatedPillars());
        assertFalse(dungeon.areAllPillarsActivated());

        dungeon.recordPillarActivation();
        dungeon.recordPillarActivation();
        dungeon.recordPillarActivation();

        assertEquals(4, dungeon.getActivatedPillars());
        assertTrue(dungeon.areAllPillarsActivated());
    }

    @Test
    @DisplayName("Demo dungeon should have boss pre-spawned")
    void testDemoDungeonBossSpawning() {
        // Demo dungeon has boss pre-spawned in the demo layout
        assertTrue(dungeon.isBossSpawned());

        // Check that exit room is boss room
        Room exitRoom = dungeon.getRoom(dungeon.getExitPoint());
        assertEquals(RoomType.BOSS, exitRoom.getRoomType());
        assertFalse(exitRoom.getMonsters().isEmpty());
    }

    @Test
    @DisplayName("Non-demo dungeon should not have boss initially")
    void testNonDemoDungeonBossSpawning() {
        // Create dungeon without demo generation
        Dungeon nonDemoDungeon = new Dungeon(5, 5, "Easy", false);

        // Should not have boss initially
        assertFalse(nonDemoDungeon.isBossSpawned());
        assertEquals(0, nonDemoDungeon.getTotalPillars());
    }


    @Test
    @DisplayName("Map string should contain hero position")
    void testMapString() {
        Point heroPosition = new Point(0, 0);
        String mapString = dungeon.getMapString(heroPosition);

        assertNotNull(mapString);
        assertFalse(mapString.isEmpty());
        assertTrue(mapString.contains("[H]")); // Hero position marker
    }

    @Test
    @DisplayName("Map string should show visited rooms correctly")
    void testMapStringVisitedRooms() {
        Point heroPosition = dungeon.getHeroSpawnPoint();
        Room spawnRoom = dungeon.getRoom(heroPosition);
        spawnRoom.setVisited(true);

        String mapString = dungeon.getMapString(heroPosition);

        // Should contain hero marker and entrance marker for visited spawn room
        assertTrue(mapString.contains("[H]"));
    }

    @Test
    @DisplayName("Dungeon should have correct room connectivity")
    void testRoomConnectivity() {
        // Check that spawn room has at least one door
        Room spawnRoom = dungeon.getRoom(dungeon.getHeroSpawnPoint());
        assertTrue(spawnRoom.hasNorthDoor() || spawnRoom.hasEastDoor() ||
                spawnRoom.hasSouthDoor() || spawnRoom.hasWestDoor());
    }

    @Test
    @DisplayName("Dungeon should contain all required pillar types")
    void testPillarTypes() {
        int pillarCount = 0;
        boolean hasAbstraction = false, hasEncapsulation = false,
                hasInheritance = false, hasPolymorphism = false;

        for (int y = 0; y < TEST_HEIGHT; y++) {
            for (int x = 0; x < TEST_WIDTH; x++) {
                Room room = dungeon.getRoom(x, y);
                if (room.hasPillar()) {
                    pillarCount++;
                    PillarType type = room.getPillar().getType();
                    switch (type) {
                        case ABSTRACTION -> hasAbstraction = true;
                        case ENCAPSULATION -> hasEncapsulation = true;
                        case INHERITANCE -> hasInheritance = true;
                        case POLYMORPHISM -> hasPolymorphism = true;
                    }
                }
            }
        }

        assertEquals(4, pillarCount);
        assertTrue(hasAbstraction);
        assertTrue(hasEncapsulation);
        assertTrue(hasInheritance);
        assertTrue(hasPolymorphism);
    }

    @Test
    @DisplayName("JSON serialization should work correctly")
    void testJsonSerialization() {
        String json = dungeon.toJson();
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.contains("width"));
        assertTrue(json.contains("height"));
        assertTrue(json.contains("difficulty"));
    }

    @Test
    @DisplayName("JSON deserialization should recreate dungeon correctly")
    void testJsonDeserialization() {
        // Activate some pillars to test state preservation
        dungeon.recordPillarActivation();
        dungeon.recordPillarActivation();

        String json = dungeon.toJson();
        Dungeon restoredDungeon = Dungeon.fromJson(json);

        assertNotNull(restoredDungeon);
        assertEquals(dungeon.getWidth(), restoredDungeon.getWidth());
        assertEquals(dungeon.getHeight(), restoredDungeon.getHeight());
        assertEquals(dungeon.getDifficulty(), restoredDungeon.getDifficulty());
        assertEquals(dungeon.getTotalPillars(), restoredDungeon.getTotalPillars());
        assertEquals(dungeon.getActivatedPillars(), restoredDungeon.getActivatedPillars());
        assertEquals(dungeon.getHeroSpawnPoint(), restoredDungeon.getHeroSpawnPoint());
        assertEquals(dungeon.getExitPoint(), restoredDungeon.getExitPoint());
    }

    @Test
    @DisplayName("JSON deserialization should handle invalid JSON")
    void testJsonDeserializationInvalidJson() {
        Dungeon result = Dungeon.fromJson("invalid json");
        assertNull(result);

        result = Dungeon.fromJson("");
        assertNull(result);

        result = Dungeon.fromJson(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Constructor with theNewDungeon=false should not generate layout")
    void testConstructorNoGeneration() {
        Dungeon noDemoGeneration = new Dungeon(5, 5, "Easy", false);

        assertEquals(5, noDemoGeneration.getWidth());
        assertEquals(5, noDemoGeneration.getHeight());
        assertEquals("Easy", noDemoGeneration.getDifficulty());
        assertEquals(0, noDemoGeneration.getTotalPillars()); // No pillars generated
        assertEquals(0, noDemoGeneration.getActivatedPillars());
    }

    @Test
    @DisplayName("Demo layout should have linear structure")
    void testDemoLayout() {
        // Test the demo layout structure
        Point spawn = dungeon.getHeroSpawnPoint();
        Point exit = dungeon.getExitPoint();

        assertEquals(new Point(0, 0), spawn);
        assertEquals(new Point(9, 0), exit);

        // Check that rooms are connected in a line
        for (int x = 0; x < 9; x++) {
            Room currentRoom = dungeon.getRoom(x, 0);
            Room nextRoom = dungeon.getRoom(x + 1, 0);

            assertTrue(currentRoom.hasEastDoor());
            assertTrue(nextRoom.hasWestDoor());
        }
    }

    @Test
    @DisplayName("Multiple pillar activations beyond total should not break system")
    void testExcessivePillarActivations() {
        // Activate more pillars than exist
        for (int i = 0; i < 10; i++) {
            dungeon.recordPillarActivation();
        }

        assertEquals(10, dungeon.getActivatedPillars());
        assertTrue(dungeon.areAllPillarsActivated());
        assertTrue(dungeon.isBossSpawned());
    }
}
