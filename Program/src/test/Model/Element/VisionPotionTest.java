package test.Model.Element;

import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.dungeon.Dungeon;
import main.Model.element.VisionPotion;
import main.Model.util.HeroType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Vision potions.
 *
 * @author Jacob Hilliker
 * @version 6/12/2025
 */
public class VisionPotionTest {

    Dungeon myDungeon;
    Hero myHero;
    VisionPotion myPotion;

    @BeforeEach
    void setUp() {
        myDungeon = new Dungeon(10, 10, "Normal", true);
        HeroFactory factory = new HeroFactory();
        myHero = factory.getHero("name", HeroType.THIEF, new Point(3, 3));
        myPotion = new VisionPotion("Vision Potion", "Reveals Tiles", myDungeon);
    }

    @Test
    void VisionPotion_constructor() {
        VisionPotion potion = new VisionPotion("Name", "Hello World", myDungeon);
        assertEquals(potion.getName(), "Name");
        assertEquals(potion.getDescription(), "Hello World");
        assertEquals(potion, new VisionPotion("Name", "Hello World", myDungeon));
    }

    @Test
    void VisionPotion_nullDungeon() {
        myDungeon = null;
        assertThrows(IllegalArgumentException.class, () -> {
            VisionPotion potion = new VisionPotion("Name", "Hello World", myDungeon);
        });
    }

    @Test
    void VisionPotion_nullName() {
        myDungeon = null;
        assertThrows(IllegalArgumentException.class, () -> {
            VisionPotion potion = new VisionPotion(null, "Hello World", myDungeon);
        });
    }

    @Test
    void VisionPotion_nullDescription() {
        myDungeon = null;
        assertThrows(IllegalArgumentException.class, () -> {
            VisionPotion potion = new VisionPotion("Name", null, myDungeon);
        });
    }

    @Test
    void VisionPotion_emptyName() {
        myDungeon = null;
        assertThrows(IllegalArgumentException.class, () -> {
            VisionPotion potion = new VisionPotion("", "Hello World", myDungeon);
        });
    }

    @Test
    void VisionPotion_emptyDescription() {
        myDungeon = null;
        assertThrows(IllegalArgumentException.class, () -> {
            VisionPotion potion = new VisionPotion("Name", "", myDungeon);
        });
    }

    @Test
    void use_tilesVisited() {
        assertFalse(myDungeon.getRoom(2, 2).isVisited());
        assertFalse(myDungeon.getRoom(2, 3).isVisited());
        assertFalse(myDungeon.getRoom(2, 4).isVisited());
        assertFalse(myDungeon.getRoom(3, 2).isVisited());
        assertFalse(myDungeon.getRoom(3, 4).isVisited());
        assertFalse(myDungeon.getRoom(4, 2).isVisited());
        assertFalse(myDungeon.getRoom(4, 3).isVisited());
        assertFalse(myDungeon.getRoom(4, 4).isVisited());
        myHero.pickupItem(myPotion);
        myHero.useItem(myPotion);
        assertTrue(myDungeon.getRoom(2, 2).isVisited());
        assertTrue(myDungeon.getRoom(2, 3).isVisited());
        assertTrue(myDungeon.getRoom(2, 4).isVisited());
        assertTrue(myDungeon.getRoom(3, 2).isVisited());
        assertTrue(myDungeon.getRoom(3, 4).isVisited());
        assertTrue(myDungeon.getRoom(4, 2).isVisited());
        assertTrue(myDungeon.getRoom(4, 3).isVisited());
        assertTrue(myDungeon.getRoom(4, 4).isVisited());
    }

    @Test
    void use_nullTest() {
        Hero nullHero = null;
        assertThrows(NullPointerException.class, () -> {
            myPotion.use(nullHero);
        });
    }

    @Test
    void use_heroNotAlive() {
        myHero.setHealth(0);
        assertThrows(IllegalArgumentException.class, () -> {
            myPotion.use(myHero);
        });
    }

}
