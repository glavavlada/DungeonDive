package main.Model.util;

/**
 * Enum representing the different types of Pillars of OO.
 */
public enum PillarType {
    ABSTRACTION("Abstraction", "Grants enhanced understanding of complex systems, increasing critical hit chance."),
    ENCAPSULATION("Encapsulation", "Shields the hero, increasing defense or damage reduction."),
    INHERITANCE("Inheritance", "Allows the hero to adapt, perhaps increasing all stats slightly or granting a new ability."),
    POLYMORPHISM("Polymorphism", "Grants versatility, perhaps allowing the hero to mimic an enemy's last special attack or adapt their damage type.");

    private final String myDisplayName;
    private final String myDescription;

    PillarType(String displayName, String description) {
        this.myDisplayName = displayName;
        this.myDescription = description;
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
