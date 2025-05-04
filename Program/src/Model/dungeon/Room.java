package Model.dungeon;

import Model.element.DungeonElement;

import java.util.ArrayList;

public class Room {
    private int x;
    private int y;
    private boolean north;
    private boolean south;
    private boolean east;
    private boolean west;
    private boolean visited;
    private ArrayList<DungeonElement> elements;

    public Room(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addElement(DungeonElement element) {

    }

    public boolean hasMonster(){
        return false;
    }

    public boolean hasPillar(){
        return false;
    }

    public boolean hasItem(){
        return false;
    }

    public boolean hasTrap(){
        return false;
    }
}
