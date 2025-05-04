package Model.element;

import Model.character.Hero;

/**
 * Abstract base class for items that can be found and used in the dungeon.
 * This class implements the DungeonElement interface to allow interaction
 * with heroes.
 */
public abstract class Item extends DungeonElement {
    // More properties can be added
    private String name;
    private String description;

    /**
     * Constructor for Item.
     *
     * @param name The name of the item
     * @param description The description of the item
     */
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Use the item on a hero. This method is called when a hero
     * decides to use this item from their inventory.
     *
     * @param hero The hero that is using the item
     */
    public abstract void use(Hero hero);

    /**
     * Implementation of the interact method from DungeonElement.
     * This defines what happens when a hero encounters this item in a room.
     * By default, the hero will attempt to pick up the item.
     *
     * @param hero The hero that is interacting with this item
     */
    @Override
    public void interact(Hero hero) {
        boolean pickedUp = hero.pickupItem(this);
        // might want to add additional logic here for when
        // the item can't be picked up
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * String representation of the item.
     *
     * @return A string containing the item's name and description
     */
    @Override
    public String toString() {
        return name + ": " + description;
    }
}