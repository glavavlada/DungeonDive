package main.Model.character;

import main.Model.util.Point;
import main.Model.util.Direction;

public abstract class Character {
    private int myHealth;
    private Point myPosition;

    public Character(final int theHealth, final Point thePosition) {
        myHealth = theHealth;
        myPosition = thePosition;
    }

    public abstract int attack(final Character theTarget);

    public void takeDamage(final int theDamage) {
        myHealth = Math.max(0, myHealth - theDamage);
    }

    public boolean isAlive() {
        return myHealth > 0;
    }

    public void move(final Direction theDirection) {
        switch (theDirection) {
            case NORTH:
                myPosition = new Point(myPosition.getX(), myPosition.getY() - 1);
                break;
            case SOUTH:
                myPosition = new Point(myPosition.getX(), myPosition.getY() + 1);
                break;
            case EAST:
                myPosition = new Point(myPosition.getX() + 1, myPosition.getY());
                break;
            case WEST:
                myPosition = new Point(myPosition.getX() - 1, myPosition.getY());
                break;
        }
    }

    public int getHealth() {
        return myHealth;
    }

    public Point getPosition() {
        return myPosition;
    }

    public void setHealth(final int theHealth) {
        myHealth = theHealth;
    }

    public void setPosition(final Point thePosition) {
        myPosition = thePosition;
    }
}
