package model;

import util.Position;
import util.TargetStrategy;

import java.util.*;


public class FirefighterBoard implements Board<List<ModelElement>>,BoardContext{
  private final int columnCount;
  private final int rowCount;
  private final int initialFireCount;
  private final int initialFirefighterCount;
  private List<FireFighter>  firefighters;
  private Set<Fire> fires;
  private Set<Position> firesToCreate;
  private Set<Position> firesToExtinguish;
  private Map<Position, List<Position>> neighbors = new HashMap();
  private final Position[][] positions;
  private int step = 0;
  private final Random randomGenerator = new Random();

  public FirefighterBoard(int columnCount, int rowCount, int initialFireCount, int initialFirefighterCount) {
    this.columnCount = columnCount;
    this.rowCount = rowCount;
    this.positions = new Position[rowCount][columnCount];
    for (int column = 0; column < columnCount; column++)
      for (int row = 0; row < rowCount; row++)
        positions[row][column] = new Position(row, column);
    for (int column = 0; column < columnCount; column++)
      for (int row = 0; row < rowCount; row++) {
        List<Position> list = new ArrayList<>();
        if (row > 0) list.add(positions[row - 1][column]);
        if (column > 0) list.add(positions[row][column - 1]);
        if (row < rowCount - 1) list.add(positions[row + 1][column]);
        if (column < columnCount - 1) list.add(positions[row][column + 1]);
        neighbors.put(positions[row][column], list);
      }
    this.initialFireCount = initialFireCount;
    this.initialFirefighterCount = initialFirefighterCount;
    initializeElements();
  }

  public void initializeElements() {
    firefighters = new ArrayList<>();
    fires = new HashSet<>();
    for (int index = 0; index < initialFireCount; index++)
      fires.add(new Fire(randomPosition()));
    for (int index = 0; index < initialFirefighterCount; index++)
      firefighters.add(new FireFighter (randomPosition()));
  }

  private Position randomPosition() {
    return new Position(randomGenerator.nextInt(rowCount), randomGenerator.nextInt(columnCount));
  }

  @Override
  public List<ModelElement> getState(Position position) {
    List<ModelElement> result = new ArrayList<>();
    for (FireFighter fireFighter : firefighters) {
        if (fireFighter.getPosition().equals(position)) {
            result.add(ModelElement.FIREFIGHTER);
            break;
        }
    }
    for (Fire fire : fires) {
        if (fire.getPosition().equals(position)) {
            result.add(ModelElement.FIRE);
            break;
        }
    }
    return result;
  }

  @Override
  public int rowCount() {
    return rowCount;
  }

  @Override
  public int columnCount() {
    return columnCount;
  }

  public List<Position> updateToNextGeneration() {
    List<Position> modifiedPositions = new ArrayList<>();
    firesToCreate = new HashSet<>();
    firesToExtinguish = new HashSet<>();
    for (FireFighter fireFighter : firefighters) {
        Position oldPosition = fireFighter.getPosition();
        fireFighter.update(this);
        modifiedPositions.add(oldPosition);
        modifiedPositions.add(fireFighter.getPosition());
    }

    for(Fire fire : new HashSet<>(fires)){
        fire.update(this);
    }

    fires.removeIf(fire -> firesToExtinguish.contains(fire.getPosition()));
    modifiedPositions.addAll(firesToExtinguish);
    Set<Position> currentFirePostion =getFirePositions();
    for( Position position : firesToCreate) {
        if (!currentFirePostion.contains(position)) {
            fires.add(new Fire(position));
            modifiedPositions.add(position);
        }
    }
    step++;
    return new ArrayList<>(modifiedPositions);
  }

  @Override
  public int stepNumber() {
    return step;
  }

  @Override
  public void reset() {
    step = 0;
    initializeElements();
  }

  public void extinguish(Position position) {
    firesToExtinguish.add(position);
  }

  public void createFire(Position position) {
      firesToCreate.add(position);
  }

  @Override
  public void setState(List<ModelElement> state, Position position) {
    fires.removeIf(fire -> fire.getPosition().equals(position));
    firefighters.removeIf(fireFighter ->  fireFighter.getPosition().equals(position));
    for (ModelElement element : state) {
      switch (element) {
        case FIRE -> fires.add(new Fire(position));
        case FIREFIGHTER -> firefighters.add(new FireFighter(position));
      }
    }
  }
    public Map<Position, List<Position>> getNeighborsMap() {
        return this.neighbors;
    }

    @Override
    public List<Position> getNeighbors(Position p) {
        return this.neighbors.get(p);
    }

    @Override
    public Set<Position> getFirePositions() {
        Set<Position> positions = new HashSet<>();
        for (Fire fire : fires) {
            positions.add(fire.getPosition());
        }
        return positions;
    }

}