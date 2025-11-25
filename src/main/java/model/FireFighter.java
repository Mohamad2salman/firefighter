package model;

import util.Position;
import util.TargetStrategy;

import java.util.List;

public class FireFighter extends AbstractEntity{
    private TargetStrategy targetStrategy=new TargetStrategy();
    public FireFighter(Position position){
        super(position);
    }
    @Override
    public void update(BoardContext context) {
        Position newFirefighterPosition = targetStrategy.neighborClosestToFire(this.position,context.getFirePositions(), context.getNeighborsMap());
        if (!newFirefighterPosition .equals(this.position) && context.isOccupied(newFirefighterPosition )) {
        } else {
            this.position = newFirefighterPosition ;
        }
        context.extinguish(this.position);
        List<Position> neighbors=context.getNeighbors(this.position);
        for(Position neighborsPos:neighbors){
            if(context.getFirePositions().contains(neighborsPos)){
                context.extinguish(neighborsPos);
            }
        }
    }

    @Override
    public ModelElement getType() {
        return ModelElement.FIREFIGHTER;
    }


}
