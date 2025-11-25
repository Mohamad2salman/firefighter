package model;

import util.Position;

public abstract class AbstractEntity {
    Position position;
    public AbstractEntity(Position position) {
        this.position = position;
    }
    public Position getPosition() {
        return position;
    }
    public abstract void update(BoardContext context);
    public abstract ModelElement getType();
}
