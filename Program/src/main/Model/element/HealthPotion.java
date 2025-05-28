package main.Model.element;

import main.Model.character.Hero;

/**
 * Represents a health potion that restores health to a hero.
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
            theHero.setHealth(Math.min(theHero.getMaxHealth(), theHero.getHealth() + this.myHealingAmount)); // Assuming Hero has getMaxHealth()
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

    /**
     * When a hero interacts with a HealthPotion on the ground, they pick it up.
     * This overrides the default interact method from Item if specific behavior is needed,
     * but the default Item.interact() already calls hero.pickupItem(this), which is suitable.
     * If you wanted different behavior (e.g., auto-use if health is low), you could change it here.
     * For now, relying on the parent's interact method is fine.
     */
    // @Override
    // public void interact(Hero hero) {
    //     super.interact(hero); // This will call hero.pickupItem(this)
    // }
}
