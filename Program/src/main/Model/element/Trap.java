package main.Model.element;

import main.Model.character.Hero;
import java.util.Objects;

/**
 * Represents a trap in the dungeon that can affect a hero.
 */
public class Trap extends DungeonElement {
    private final String myName;
    private final String myDescription;
    private final int myDamageAmount;
    private boolean myIsSprung; // To ensure trap only triggers once, or can be reset

    /**
     * Constructor for Trap.
     *
     * @param theName        The name of the trap (e.g., "Spike Trap", "Poison Dart").
     * @param theDescription A description of the trap.
     * @param theDamageAmount The amount of damage the trap deals.
     */
    public Trap(final String theName, final String theDescription, final int theDamageAmount) {
        if (theName == null || theName.isEmpty()) {
            throw new IllegalArgumentException("Trap name cannot be null or empty.");
        }
        this.myName = theName;
        this.myDescription = theDescription == null ? "" : theDescription;
        this.myDamageAmount = Math.max(0, theDamageAmount); // Damage shouldn't be negative
        this.myIsSprung = false;
    }

    /**
     * Triggers the trap's effect on the hero.
     *
     * @param theHero The hero who triggered the trap.
     */
    public void trigger(final Hero theHero) {
        if (!myIsSprung && theHero != null && theHero.isAlive()) {
            System.out.println(theHero.getName() + " triggered a " + myName + "!");
            theHero.takeDamage(myDamageAmount);
            System.out.println(myName + " dealt " + myDamageAmount + " damage to " + theHero.getName() + ".");
            myIsSprung = true; // Trap has been sprung
        } else if (myIsSprung) {
            System.out.println("The " + myName + " has already been sprung.");
        }
    }

    public boolean isSprung() {
        return myIsSprung;
    }

    /**
     * Resets the trap so it can be triggered again (optional functionality).
     */
    public void reset() {
        this.myIsSprung = false;
        System.out.println(myName + " has been reset.");
    }

    public int getDamageAmount() {
        return myDamageAmount;
    }

    public String getName() {
        return myName;
    }

    public String getDescription() {
        return myDescription;
    }

    /**
     * Defines interaction with the trap. Typically, stepping on its tile triggers it.
     *
     * @param theHero The hero interacting with (triggering) the trap.
     */
    @Override
    public void interact(final Hero theHero) {
        trigger(theHero);
    }

    @Override
    public String toString() {
        return myName + " (" + myDescription + ")" + (myIsSprung ? " [Sprung]" : " [Armed]");
    }

    @Override
    public boolean equals(final Object theO) {
        if (this == theO) return true;
        if (theO == null || getClass() != theO.getClass()) return false;
        Trap trap = (Trap) theO;
        return myDamageAmount == trap.myDamageAmount &&
                myIsSprung == trap.myIsSprung &&
                Objects.equals(myName, trap.myName) &&
                Objects.equals(myDescription, trap.myDescription);
    }

    /**
     *gets damage amount this trap deals
     * @return damage amount
     */
    public int getDamage() {
        return getDamageAmount();
    }


    @Override
    public int hashCode() {
        return Objects.hash(myName, myDescription, myDamageAmount, myIsSprung);
    }
}
