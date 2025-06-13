package main.Model.util;

/**
 * Enum representing the different types of Pillars of OO.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public enum PillarType {
    ABSTRACTION("Abstraction", "Grants enhanced understanding of complex systems, increasing critical hit chance."),
    ENCAPSULATION("Encapsulation", "Shields the hero, increasing max health."),
    INHERITANCE("Inheritance", "Hero learns old techniques, lowering mana needed for special attacks by 1."),
    POLYMORPHISM("Polymorphism", "Grants the power of other forms, increasing attack damage.");

    private final String myDisplayName;
    private final String myDescription;

    PillarType(final String theDisplayName, final String theDescription) {
        this.myDisplayName = theDisplayName;
        this.myDescription = theDescription;
    }

    public String getDisplayName() {
        return myDisplayName;
    }

    public String getDescription() {
        return myDescription;
    }

    @Override
    public String toString() {
        return myDisplayName;
    }
}
