package test.Model.Dungeon;

import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.character.MonsterFactory;
import main.Model.dungeon.Room;
import main.Model.element.*;
import main.Model.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static main.Model.util.HeroType.WARRIOR;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTest {

    private Room room;
    private Point testPosition;
    private Hero testHero;
    private Monster testMonster;
    private MonsterFactory monsterFactory;

    @BeforeEach
    void setUp() {
        testPosition = new Point(5, 3);
        room = new Room(testPosition, RoomType.EMPTY);

        // Create test hero using HeroBuilder
        testHero = new Hero.HeroBuilder()
                .setName("TestHero")
                .setHeroType(HeroType.WARRIOR)
                .setPosition(new Point(0, 0))
                .setHealth(100)
                .setMaxHealth(100)
                .setBaseAttackDamage(15)
                .setSpecialAttackDamage(25)
                .setSpecialAttackName("Mighty Swing")
                .setCritChance(0.05)
                .setCritMultiplier(2.0)
                .setDescription("A test warrior")
                .build();

        monsterFactory = new MonsterFactory();
        testMonster = monsterFactory.getMonster(MonsterType.GOBLIN, testPosition);
    }



    @Test
    @DisplayName("Constructor should create room with valid parameters")
    void testConstructorValid() {
        assertEquals(testPosition, room.getPosition());
        assertEquals(RoomType.EMPTY, room.getRoomType());
        assertFalse(room.isVisited());
        assertTrue(room.getMonsters().isEmpty());
        assertTrue(room.getItems().isEmpty());
        assertNull(room.getPillar());
        assertNull(room.getTrap());
        assertFalse(room.getChestOpened());
        assertFalse(room.hasNorthDoor());
        assertFalse(room.hasEastDoor());
        assertFalse(room.hasSouthDoor());
        assertFalse(room.hasWestDoor());
    }

    @Test
    @DisplayName("Constructor should throw exception for null parameters")
    void testConstructorNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Room(null, RoomType.EMPTY));
        assertThrows(IllegalArgumentException.class, () ->
            new Room(testPosition, null));
    }

    @Test
    @DisplayName("Enter method should set room as visited")
    void testEnterSetsVisited() {
        assertFalse(room.isVisited());
        room.enter(testHero);
        assertTrue(room.isVisited());
    }

    @Test
    @DisplayName("Enter method should trigger trap if present")
    void testEnterTriggersTrap() {
        Trap trap = new Trap("Test Trap", "A test trap", 10);
        room.setTrap(trap);

        int initialHealth = testHero.getHealth();
        room.enter(testHero);

        assertTrue(trap.isSprung());
        assertTrue(testHero.getHealth() < initialHealth);
    }

    @Test
    @DisplayName("Enter method should not trigger already sprung trap")
    void testEnterDoesNotTriggerSprungTrap() {
        Trap trap = new Trap("Test Trap", "A test trap", 10);
        room.setTrap(trap);

        // Trigger trap first time
        room.enter(testHero);
        int healthAfterFirstTrap = testHero.getHealth();

        // Enter again - trap should not trigger again
        room.enter(testHero);
        assertEquals(healthAfterFirstTrap, testHero.getHealth());
    }

    @Test
    @DisplayName("setVisited should change visited status")
    void testSetVisited() {
        assertFalse(room.isVisited());
        room.setVisited(true);
        assertTrue(room.isVisited());
        room.setVisited(false);
        assertFalse(room.isVisited());
    }

    @Test
    @DisplayName("setRoomType should change room type")
    void testSetRoomType() {
        assertEquals(RoomType.EMPTY, room.getRoomType());
        room.setRoomType(RoomType.MONSTER);
        assertEquals(RoomType.MONSTER, room.getRoomType());
    }

    @Test
    @DisplayName("setRoomType should throw exception for null")
    void testSetRoomTypeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            room.setRoomType(null));
    }

    @Test
    @DisplayName("Monster management should work correctly")
    void testMonsterManagement() {
        assertTrue(room.getMonsters().isEmpty());

        room.addMonster(testMonster);
        assertEquals(1, room.getMonsters().size());
        assertTrue(room.getMonsters().contains(testMonster));

        room.removeMonster(testMonster);
        assertTrue(room.getMonsters().isEmpty());
    }

    @Test
    @DisplayName("addMonster should ignore null monsters")
    void testAddNullMonster() {
        room.addMonster(null);
        assertTrue(room.getMonsters().isEmpty());
    }

    @Test
    @DisplayName("Item management should work correctly")
    void testItemManagement() {
        HealthPotion potion = new HealthPotion("Health Potion", "Heals 50", 50);

        assertTrue(room.getItems().isEmpty());

        room.addItem(potion);
        assertEquals(1, room.getItems().size());
        assertTrue(room.getItems().contains(potion));

        room.removeItem(potion);
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    @DisplayName("addItem should ignore null items")
    void testAddNullItem() {
        room.addItem(null);
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    @DisplayName("addItem should not add items if items are collected")
    void testAddItemWhenCollected() {
        HealthPotion potion = new HealthPotion("Health Potion", "Heals 50", 50);
        room.setItemsCollected(true);

        room.addItem(potion);
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    @DisplayName("Pillar management should work correctly")
    void testPillarManagement() {
        assertNull(room.getPillar());
        assertFalse(room.hasPillar());

        Pillar pillar = new Pillar(PillarType.ABSTRACTION);
        room.setPillar(pillar);

        assertEquals(pillar, room.getPillar());
        assertTrue(room.hasPillar());
        assertEquals(RoomType.PILLAR, room.getRoomType());

        room.removePillar();
        assertNull(room.getPillar());
        assertFalse(room.hasPillar());
    }

    @Test
    @DisplayName("Trap management should work correctly")
    void testTrapManagement() {
        assertNull(room.getTrap());
        assertFalse(room.hasTrap());

        Trap trap = new Trap("Test Trap", "A test trap", 10);
        room.setTrap(trap);

        assertEquals(trap, room.getTrap());
        assertTrue(room.hasTrap());
        assertEquals(RoomType.TRAP, room.getRoomType());
    }

    @Test
    @DisplayName("Door management should work correctly")
    void testDoorManagement() {
        // Test all doors initially false
        assertFalse(room.hasNorthDoor());
        assertFalse(room.hasEastDoor());
        assertFalse(room.hasSouthDoor());
        assertFalse(room.hasWestDoor());

        // Test setting doors
        room.setNorthDoor(true);
        assertTrue(room.hasNorthDoor());

        room.setEastDoor(true);
        assertTrue(room.hasEastDoor());

        room.setSouthDoor(true);
        assertTrue(room.hasSouthDoor());

        room.setWestDoor(true);
        assertTrue(room.hasWestDoor());

        // Test unsetting doors
        room.setNorthDoor(false);
        assertFalse(room.hasNorthDoor());
    }

    @Test
    @DisplayName("Chest management should work correctly")
    void testChestManagement() {
        assertFalse(room.hasChest());
        assertFalse(room.getChestOpened());
        assertTrue(room.getChest().isEmpty());

        List<Item> chestItems = new ArrayList<>();
        chestItems.add(new HealthPotion("Potion", "Heals", 50));

        room.setChest(chestItems);

        assertTrue(room.hasChest());
        assertEquals(RoomType.TREASURE, room.getRoomType());
        assertEquals(1, room.getChest().size());
    }

    @Test
    @DisplayName("setChest should handle null chest")
    void testSetNullChest() {
        room.setChest(null);
        assertFalse(room.hasChest());
        assertTrue(room.getChest().isEmpty());
    }

    @Test
    @DisplayName("openChest should work with sufficient gold")
    void testOpenChestWithSufficientGold() {
        // Set up chest
        List<Item> chestItems = new ArrayList<>();
        chestItems.add(new HealthPotion("Potion", "Heals", 50));
        room.setChest(chestItems);

        // Give hero enough gold
        testHero.addGold(10);

        assertFalse(room.isChestOpened());
        room.openChest(testHero);

        assertTrue(room.isChestOpened());
        assertEquals(5, testHero.getGold()); // Should have spent 5 gold
        assertTrue(room.getChest().isEmpty()); // Items should be removed
    }

    @Test
    @DisplayName("openChest should fail with insufficient gold")
    void testOpenChestWithInsufficientGold() {
        // Set up chest
        List<Item> chestItems = new ArrayList<>();
        chestItems.add(new HealthPotion("Potion", "Heals", 50));
        room.setChest(chestItems);

        // Hero has no gold
        assertEquals(0, testHero.getGold());

        room.openChest(testHero);

        assertFalse(room.isChestOpened());
        assertEquals(1, room.getChest().size()); // Items should remain
    }

    @Test
    @DisplayName("openChest should not work on already opened chest")
    void testOpenAlreadyOpenedChest() {
        // Set up and open chest
        List<Item> chestItems = new ArrayList<>();
        chestItems.add(new HealthPotion("Potion", "Heals", 50));
        room.setChest(chestItems);
        testHero.addGold(10);
        room.openChest(testHero);

        // Try to open again
        int goldAfterFirstOpen = testHero.getGold();
        room.openChest(testHero);

        assertEquals(goldAfterFirstOpen, testHero.getGold()); // No additional gold spent
    }

    @Test
    @DisplayName("openChest should not work on non-treasure room")
    void testOpenChestOnNonTreasureRoom() {
        testHero.addGold(10);
        int initialGold = testHero.getGold();

        room.openChest(testHero);

        assertEquals(initialGold, testHero.getGold()); // No gold should be spent
        assertFalse(room.isChestOpened());
    }

    @Test
    @DisplayName("clearItems should remove all items")
    void testClearItems() {
        room.addItem(new HealthPotion("Potion1", "Heals", 50));
        room.addItem(new HealthPotion("Potion2", "Heals", 50));

        assertEquals(2, room.getItems().size());

        room.clearItems();

        assertTrue(room.getItems().isEmpty());
    }

    @Test
    @DisplayName("getDungeonElements should return all elements")
    void testGetDungeonElements() {
        // Add various elements
        Pillar pillar = new Pillar(PillarType.ABSTRACTION);
        Trap trap = new Trap("Test Trap", "A test trap", 10);
        HealthPotion potion = new HealthPotion("Potion", "Heals", 50);

        room.setPillar(pillar);
        room.setTrap(trap);
        room.addItem(potion);

        List<DungeonElement> elements = room.getDungeonElements();

        assertEquals(3, elements.size());
        assertTrue(elements.contains(pillar));
        assertTrue(elements.contains(trap));
        assertTrue(elements.contains(potion));
    }

    @Test
    @DisplayName("getDescription should return appropriate description")
    void testGetDescription() {
        String description = room.getDescription();
        assertTrue(description.contains("Empty room"));

        // Test with chest
        List<Item> chestItems = new ArrayList<>();
        chestItems.add(new HealthPotion("Potion", "Heals", 50));
        room.setChest(chestItems);

        description = room.getDescription();
        assertTrue(description.contains("chest"));

        // Test with opened chest
        room.setChestOpened(true);
        description = room.getDescription();
        assertTrue(description.contains("empty chest"));

        // Test with pillar
        room = new Room(testPosition, RoomType.EMPTY);
        Pillar pillar = new Pillar(PillarType.ABSTRACTION);
        room.setPillar(pillar);

        description = room.getDescription();
        assertTrue(description.contains("Pillar"));
        assertTrue(description.contains("Abstraction"));
    }

    @Test
    @DisplayName("toString should return formatted string")
    void testToString() {
        String result = room.toString();

        assertTrue(result.contains("Room at"));
        assertTrue(result.contains(testPosition.toString()));
        assertTrue(result.contains("Type: Empty"));
        assertTrue(result.contains("Visited: false"));
        assertTrue(result.contains("Monsters: 0"));
        assertTrue(result.contains("Items: 0"));
    }

    @Test
    @DisplayName("equals should work correctly")
    void testEquals() {
        Room samePositionRoom = new Room(testPosition, RoomType.MONSTER);
        Room differentPositionRoom = new Room(new Point(1, 1), RoomType.EMPTY);

        assertEquals(room, samePositionRoom); // Same position
        assertNotEquals(room, differentPositionRoom); // Different position
        assertNotEquals(room, null);
        assertNotEquals(room, "not a room");
        assertEquals(room, room); // Same object
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void testHashCode() {
        Room samePositionRoom = new Room(testPosition, RoomType.MONSTER);
        Room differentPositionRoom = new Room(new Point(1, 1), RoomType.EMPTY);

        assertEquals(room.hashCode(), samePositionRoom.hashCode()); // Same position should have same hash
        assertNotEquals(room.hashCode(), differentPositionRoom.hashCode()); // Different position should have different hash

        // Hash code should be consistent
        int firstHash = room.hashCode();
        int secondHash = room.hashCode();
        assertEquals(firstHash, secondHash);
    }

    @Test
    @DisplayName("setChestOpened should change chest opened status")
    void testSetChestOpened() {
        assertFalse(room.getChestOpened());
        room.setChestOpened(true);
        assertTrue(room.getChestOpened());
        room.setChestOpened(false);
        assertFalse(room.getChestOpened());
    }

    @Test
    @DisplayName("setItemsCollected should change items collected status")
    void testSetItemsCollected() {
        room.setItemsCollected(false);
        room.setItemsCollected(true);
        // The effect is tested in testAddItemWhenCollected
    }

    @Test
    @DisplayName("Room should handle multiple monsters")
    void testMultipleMonsters() {
        Monster goblin = monsterFactory.getMonster(MonsterType.GOBLIN, testPosition);
        Monster orc = monsterFactory.getMonster(MonsterType.ORC, testPosition);

        room.addMonster(goblin);
        room.addMonster(orc);

        assertEquals(2, room.getMonsters().size());
        assertTrue(room.getMonsters().contains(goblin));
        assertTrue(room.getMonsters().contains(orc));

        room.removeMonster(goblin);
        assertEquals(1, room.getMonsters().size());
        assertFalse(room.getMonsters().contains(goblin));
        assertTrue(room.getMonsters().contains(orc));
    }

    @Test
    @DisplayName("Room should handle multiple items")
    void testMultipleItems() {
        HealthPotion potion1 = new HealthPotion("Health Potion 1", "Heals 50", 50);
        HealthPotion potion2 = new HealthPotion("Health Potion 2", "Heals 30", 30);

        room.addItem(potion1);
        room.addItem(potion2);

        assertEquals(2, room.getItems().size());
        assertTrue(room.getItems().contains(potion1));
        assertTrue(room.getItems().contains(potion2));

        room.removeItem(potion1);
        assertEquals(1, room.getItems().size());
        assertFalse(room.getItems().contains(potion1));
        assertTrue(room.getItems().contains(potion2));
    }

    @Test
    @DisplayName("Room should handle chest with multiple items")
    void testChestWithMultipleItems() {
        List<Item> chestItems = new ArrayList<>();
        chestItems.add(new HealthPotion("Potion 1", "Heals 50", 50));
        chestItems.add(new HealthPotion("Potion 2", "Heals 30", 30));

        room.setChest(chestItems);
        assertEquals(2, room.getChest().size());

        // Give hero enough gold and inventory space
        testHero.addGold(10);

        room.openChest(testHero);

        assertTrue(room.isChestOpened());
        assertTrue(room.getChest().isEmpty()); // All items should be collected
    }

    @Test
    @DisplayName("Room with activated pillar should show in description")
    void testDescriptionWithActivatedPillar() {
        Pillar pillar = new Pillar(PillarType.ENCAPSULATION);
        pillar.interact(testHero); // This should activate the pillar
        room.setPillar(pillar);

        String description = room.getDescription();
        assertTrue(description.contains("already been collected"));
    }

    @Test
    @DisplayName("Room with sprung trap should show in description")
    void testDescriptionWithSprungTrap() {
        Trap trap = new Trap("Spike Trap", "Sharp spikes", 10);
        room.setTrap(trap);

        // Spring the trap
        room.enter(testHero);

        String description = room.getDescription();
        assertTrue(description.contains("spike trap"));
    }

    @Test
    @DisplayName("Room with items should show in description")
    void testDescriptionWithItems() {
        room.addItem(new HealthPotion("Potion", "Heals", 50));

        String description = room.getDescription();
        assertTrue(description.contains("items on the ground"));
        assertTrue(description.contains("Press 'E'"));
    }

    @Test
    @DisplayName("getMonsters should return unmodifiable list")
    void testGetMonstersUnmodifiable() {
        room.addMonster(testMonster);
        List<Monster> monsters = room.getMonsters();

        assertThrows(UnsupportedOperationException.class, () ->
                monsters.add(monsterFactory.getMonster(MonsterType.ORC, testPosition)));
        assertThrows(UnsupportedOperationException.class, () ->
                monsters.remove(testMonster));
    }

    @Test
    @DisplayName("getItems should return unmodifiable list")
    void testGetItemsUnmodifiable() {
        HealthPotion potion = new HealthPotion("Potion", "Heals", 50);
        room.addItem(potion);
        List<Item> items = room.getItems();

        assertThrows(UnsupportedOperationException.class, () ->
                items.add(new HealthPotion("Another", "Another potion", 30)));
        assertThrows(UnsupportedOperationException.class, () ->
                items.remove(potion));
    }

    @Test
    @DisplayName("getDungeonElements should return unmodifiable list")
    void testGetDungeonElementsUnmodifiable() {
        room.addItem(new HealthPotion("Potion", "Heals", 50));
        List<DungeonElement> elements = room.getDungeonElements();

        assertThrows(UnsupportedOperationException.class, () ->
                elements.add(new HealthPotion("Another", "Another potion", 30)));
    }

    @Test
    @DisplayName("Room should handle all room types correctly")
    void testAllRoomTypes() {
        for (RoomType roomType : RoomType.values()) {
            Room testRoom = new Room(new Point(0, 0), roomType);
            assertEquals(roomType, testRoom.getRoomType());

            String description = testRoom.getDescription();
            assertTrue(description.contains(roomType.getDisplayName()));
        }
    }

    @Test
    @DisplayName("Room state should persist correctly")
    void testRoomStatePersistence() {
        // Set up room with various states
        room.setVisited(true);
        room.setChestOpened(true);
        room.setItemsCollected(true);
        room.setNorthDoor(true);
        room.setEastDoor(true);

        Pillar pillar = new Pillar(PillarType.INHERITANCE);
        room.setPillar(pillar);

        Trap trap = new Trap("Test Trap", "Test", 5);
        room.setTrap(trap);

        room.addMonster(testMonster);
        room.addItem(new HealthPotion("Potion", "Heals", 50));

        // Verify all states are maintained
        assertTrue(room.isVisited());
        assertTrue(room.isChestOpened());
        assertTrue(room.hasNorthDoor());
        assertTrue(room.hasEastDoor());
        assertFalse(room.hasSouthDoor());
        assertFalse(room.hasWestDoor());
        assertEquals(pillar, room.getPillar());
        assertEquals(trap, room.getTrap());
        assertEquals(1, room.getMonsters().size());
        assertEquals(0, room.getItems().size()); // Should be 0 because itemsCollected is true
    }
}
