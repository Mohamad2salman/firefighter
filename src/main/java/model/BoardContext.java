package model;
import util.Position;
import util.TargetStrategy;
import java.util.*;

public interface BoardContext {
    public int stepNumber();
    List<Position> getNeighbors(Position p);
    public Map<Position, List<Position>> getNeighborsMap() ;
    Set<Position> getFirePositions();
    public void extinguish(Position position);
    public void  createFire(Position position);
    //public int rowCount();
    //public int columnCount();
    //public Position randomPosition();
    //boolean isOccupied(Position position);

}
