package Model.character;

import java.util.ArrayList;
import Model.element.Item;
import Model.util.MonsterType;
import Model.util.Point;

/**
 * Represents an enemy in the game.
 */
public class Monster extends Character {
    // Properties
    private boolean isElite;
    private String name;
    private MonsterType type;
    private ArrayList<Item> rewards;

    /**
     * Constructor for Monster.
     * @param name The monster's name
     * @param type The monster's type
     * @param isElite Whether this is an elite monster
     * @param health Initial health points
     * @param position Starting position
     */
    public Monster(String name, MonsterType type, boolean isElite, int health, Point position) {
        super(health, position);
        this.name = name;
        this.type = type;
        this.isElite = isElite;
        this.rewards = new ArrayList<>();
    }

    /**
     * Attack implementation for Monster.
     * @param target The character to attack
     * @return The damage dealt
     */
    @Override
    public int attack(Character target) {
        // Base damage calculation
        int baseDamage = 5;

        // Elite monsters deal more damage
        if (isElite) {
            baseDamage *= 2;
        }

        // Additional logic could be added based on monster type
        return baseDamage;
    }

    /**
     * Get rewards when monster is defeated.
     * @return List of items as rewards
     */
    public ArrayList<Item> getRewardOnDefeat() {
        return rewards;
    }

    /**
     * Add a reward item to this monster's drop list.
     * @param item The item to add as reward
     */
    public void addReward(Item item) {
        rewards.add(item);
    }

    // Getters and setters
    public boolean isElite() {
        return isElite;
    }

    public void setElite(boolean isElite) {
        this.isElite = isElite;
    }

    public String getName() {
        return name;
    }

    public MonsterType getType() {
        return type;
    }
}