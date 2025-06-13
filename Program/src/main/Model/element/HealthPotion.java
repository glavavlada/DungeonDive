package main.Model.element;

import main.Model.character.Hero;

/**
 * Represents a health potion that restores health to a hero.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class HealthPotion extends Item {
    private final int myHealingAmount;

    /**
     * Constructor for HealthPotion.
     *
     * @param theName The name of the potion.
     * @param theDescription The description of the potion.
     * @param theHealingAmount The amount of health this potion restores.
     */
    public HealthPotion(final String theName, final String theDescription, final int theHealingAmount) {
        super(theName, theDescription); // Call the constructor of the parent Item class
        this.myHealingAmount = theHealingAmount;
    }

    /**
     * Uses the health potion on the hero, restoring health.
     *
     * @param theHero The hero using the potion.
     */
    @Override
    public void use(final Hero theHero) {
        if (theHero != null && theHero.isAlive()) {
            int currentHealth = theHero.getHealth();
            int maxHealth = theHero.getMaxHealth();
            int newHealth;

            // Handle potential integer overflow/underflow
            if (myHealingAmount > 0 && currentHealth > Integer.MAX_VALUE - myHealingAmount) {
                // Positive overflow - set to max health
                newHealth = maxHealth;
            } else if (myHealingAmount < 0 && currentHealth < Integer.MIN_VALUE - myHealingAmount) {
                // Negative underflow - set to 0
                newHealth = 0;
            } else {
                // Safe to add
                newHealth = currentHealth + myHealingAmount;
                // Clamp between 0 and max health
                newHealth = Math.max(0, Math.min(maxHealth, newHealth));
            }

            theHero.setHealth(newHealth);
            System.out.println(theHero.getName() + " used " + getName() + " and restored " + this.myHealingAmount + " health.");
        }
    }

    /**
     * Gets the amount of health this potion restores.
     *
     * @return The healing amount.
     */
    public int getHealingAmount() {
        return myHealingAmount;
    }

}
