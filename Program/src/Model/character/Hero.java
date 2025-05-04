package Model.character;

import java.util.ArrayList;
import Model.element.Item;
import Model.element.Pillar;
import Model.util.HeroType;
import Model.dungeon.Room;
import Model.util.Point;

/**
 * Represents the player character.
 */
public class Hero extends Character {
    // Properties
    private ArrayList<Item> inventory;
    private int pillarsActivated;
    private HeroType type;
    private String name;

    /**
     * Constructor for Hero.
     * @param name The hero's name
     * @param type The hero's type
     * @param initialHealth Starting health
     * @param startPosition Starting position
     */
    public Hero(String name, HeroType type, int initialHealth, Point startPosition) {
        super(initialHealth, startPosition);
        this.name = name;
        this.type = type;
        this.inventory = new ArrayList<>();
        this.pillarsActivated = 0;
    }

    /**
     * Attack implementation for Hero.
     * @param target The character to attack
     * @return The damage dealt
     */
    @Override
    public int attack(Character target) {
        // Implementation could vary based on hero type
        // This is a simple example
        int baseDamage = 10;
        // Additional damage based on hero type could be added
        return baseDamage;
    }

    /**
     * Use an item from inventory.
     * @param item The item to use
     */
    public void useItem(Item item) {
        if (inventory.contains(item)) {
            item.use(this);
            inventory.remove(item);
        }
    }

    /**
     * Pick up an item and add to inventory.
     * @param item The item to pick up
     * @return true if successful, false if inventory is full
     */
    public boolean pickupItem(Item item) {
        // Could add inventory limit check here
        inventory.add(item);
        return true;
    }

//    /**
//     * Activate a pillar when found.
//     * @param pillar The pillar to activate
//     */
//    public void activatePillar(Pillar pillar) {
//        pillar.activate(this);
//        pillarsActivated++;
//    }

//    /**
//     * Enter a room in the dungeon.
//     * @param room The room to enter
//     */
//    public void enterRoom(Room room) {
//        room.setVisited(true);
//        // Room could have elements that interact with hero
//    }

    // Getters and setters
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public int getPillarsActivated() {
        return pillarsActivated;
    }

    public void setPillarsActivated(int pillarsActivated) {
        this.pillarsActivated = pillarsActivated;
    }

    public HeroType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}