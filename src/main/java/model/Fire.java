package model;

import util.Position;

import java.util.List;

public class Fire extends AbstractEntity{
    Position position;
    public Fire(Position position){
        super(position);
    }
    @Override
    public void update(BoardContext context) {
        if (context.stepNumber() % 2 == 0) {
            List<Position> myNeighbours = context.getNeighbors(position);
            for (Position neighborsPos : myNeighbours) {
                context.createFire(neighborsPos);
            }

        }
    }

    @Override
    public ModelElement getType() {
        return ModelElement.FIRE;
    }

}
