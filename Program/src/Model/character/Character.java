package Model.character;

import Model.util.Direction;
import Model.util.Point;

public abstract class Character {
    private int health;
    private Point position;

    public Character(int health, Point position) {
        this.health = health;
        this.position = position;
    }

    public abstract int attack(Character target);

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void move(Direction direction) {
        switch (direction) {
            case NORTH:
                position = new Point(position.getX(), position.getY() - 1);
                break;
            case SOUTH:
                position = new Point(position.getX(), position.getY() + 1);
                break;
            case EAST:
                position = new Point(position.getX() + 1, position.getY());
                break;
            case WEST:
                position = new Point(position.getX() - 1, position.getY());
                break;
        }
    }

    public int getHealth() {
        return health;
    }

    public Point getPosition() {
        return position;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
