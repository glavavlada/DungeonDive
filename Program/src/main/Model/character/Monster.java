package main.Model.character;

import java.util.ArrayList;
import java.util.Random;

import main.Model.element.Item;
import main.Model.util.MonsterType;

/**
 * Represents an enemy in the game.
 */
public class Monster extends Character {
    private boolean myIsElite;
    private final MonsterType myMonsterType;
    private final int myGoldReward;
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
        myGoldReward = theMonsterBuilder.myGoldReward;
        this.myRewards = new ArrayList<>();
    }

    /**
     * Attack implementation for Monster.
     * @param theTarget The character to attack
     * @return The damage dealt
     */
    @Override
    public int attack(final Character theTarget) {
        Random rand = new Random();
        int damage = 0;

        // 20 Percent chance of special with 25% more damage.
        if (rand.nextDouble(1) < .2) {
            damage = (int) (getBaseAttackDamage() + getBaseAttackDamage() * 0.25);
            System.out.println(getName() + " used " + getSpecialAttackName());
        } else {
            damage = getBaseAttackDamage();
        }

        // Elite monsters deal more damage
        if (myIsElite) {
            damage *= 1.5; // Example: 50% increase
        }


        if (rand.nextDouble(1) < getCritChance()) {
            damage *= getCritMultiplier();
            System.out.println("Monster Crit landed");
        }

        System.out.println(getName() + " attacks " + theTarget.getClass().getSimpleName() + " for " + damage + " damage!");
        theTarget.takeDamage(damage); // Apply the damage
        return damage;
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

    public int getGoldReward() {
        return myGoldReward;
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
        private int myGoldReward;

        // Do setType for hero and monster types
        public MonsterBuilder setMonsterType(final MonsterType theMonsterType) {
            myMonsterType = theMonsterType;
            return self();
        }

        public MonsterBuilder setIsElite(final boolean theIsElite) {
            myIsElite = theIsElite;
            return self();
        }

        public MonsterBuilder setGoldReward(final int theGoldReward) {
            myGoldReward = theGoldReward;
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