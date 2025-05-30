package main.Model.character;

import java.util.ArrayList;
import main.Model.element.Item;
import main.Model.util.MonsterType;

/**
 * Represents an enemy in the game.
 */
public class Monster extends Character {
    private boolean myIsElite;
    private final MonsterType myMonsterType;
    private ArrayList<Item> myRewards;


    /**
     * Constructor for Monster.
     *
     * @param theMonsterBuilder Builder for making Monster.
     */
    public Monster(final MonsterBuilder theMonsterBuilder) {
        // Previous parameters
        //final String theName, final MonsterType theType, final boolean theIsElite,
        //final int theHealth, final Point thePosition
        super(theMonsterBuilder);
        myMonsterType = theMonsterBuilder.myMonsterType;
        myIsElite = theMonsterBuilder.myIsElite;
        this.myRewards = new ArrayList<>();
    }

    /**
     * Attack implementation for Monster.
     * @param theTarget The character to attack
     * @return The damage dealt
     */
    @Override
    public int attack(final Character theTarget) {
        // Use base damage from MonsterType
        int baseDamage = this.myMonsterType.getBaseAttack(); // <-- Use MonsterType for base damage

        // Elite monsters deal more damage
        if (myIsElite) {
            baseDamage *= 1.5; // Example: 50% increase
        }

        System.out.println(getName() + " attacks " + theTarget.getClass().getSimpleName() + " for " + baseDamage + " damage!");
        theTarget.takeDamage(baseDamage); // Apply the damage
        return baseDamage;
    }


    /**
     * Get rewards when monster is defeated.
     * @return List of items as rewards
     */
    public ArrayList<Item> getRewardOnDefeat() {
        return myRewards;
    }

    /**
     * Add a reward item to this monster's drop list.
     * @param theItem The item to add as reward
     */
    public void addReward(final Item theItem) {
        myRewards.add(theItem);
    }

    // Getters and setters
    public boolean isElite() {
        return myIsElite;
    }

    public void setElite(final boolean theIsElite) {
        this.myIsElite = theIsElite;
    }

    public MonsterType getType() {
        return myMonsterType;
    }

    /**
     * Gets the current health percentage for display purposes
     * @return Health as a percentage (0.0 to 1.0)
     */
    public double getHealthPercentage() {
        // Use the stored myMaxHealth instead of type's base health
        return Math.max(0.0, Math.min(1.0, (double) getHealth() / getMaxHealth()));
    }

    /**
     * Gets formatted health display string
     * @return String in format "current/max"
     */
    public String getHealthDisplay() {
        return getHealth() + " / " + getMaxHealth();
    }

    public static class MonsterBuilder extends CharacterBuilder<MonsterBuilder, Monster> {

        private MonsterType myMonsterType;
        private Boolean myIsElite;

        // Do setType for hero and monster types
        public MonsterBuilder setMonsterType(final MonsterType theMonsterType) {
            myMonsterType = theMonsterType;
            return self();
        }

        public MonsterBuilder setIsElite(final boolean theIsElite) {
            myIsElite = theIsElite;
            return self();
        }

        protected MonsterBuilder self() {
            return this;
        }

        public Monster build() {
            return new Monster(this);
        }
    }
}