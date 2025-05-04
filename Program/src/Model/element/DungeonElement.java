package Model.element;

import Model.character.Hero;

/**
 * Abstract base class for all elements that can exist in a dungeon room.
 * This includes items, traps, pillars, and potentially other interactive objects.
 */
public abstract class DungeonElement {

    /**
     * Define how this element interacts with a hero when encountered.
     * Each subclass must implement this method to specify its unique behavior.
     *
     * @param hero The hero that is interacting with this element
     */
    public abstract void interact(Hero hero);
}