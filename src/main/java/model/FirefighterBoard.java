package model;

import util.Position;
import util.TargetStrategy;

import java.util.*;


public class FirefighterBoard implements Board<List<ModelElement>> {
  private final int columnCount;
  private final int rowCount;
  private final int initialFireCount;
  private final int initialFirefighterCount;
  private final TargetStrategy targetStrategy = new TargetStrategy();
  private List<Position> firefighterPositions;
  private Set<Position> firePositions;
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
    firefighterPositions = new ArrayList<>();
    firePositions = new HashSet<>();
    for (int index = 0; index < initialFireCount; index++)
      firePositions.add(randomPosition());
    for (int index = 0; index < initialFirefighterCount; index++)
      firefighterPositions.add(randomPosition());
  }

  private Position randomPosition() {
    return new Position(randomGenerator.nextInt(rowCount), randomGenerator.nextInt(columnCount));
  }

  @Override
  public List<ModelElement> getState(Position position) {
    List<ModelElement> result = new ArrayList<>();
    for (Position firefighterPosition : firefighterPositions)
      if (firefighterPosition.equals(position))
        result.add(ModelElement.FIREFIGHTER);
    if (firePositions.contains(position))
      result.add(ModelElement.FIRE);
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
    List<Position> modifiedPositions = updateFirefighters();
    modifiedPositions.addAll(updateFires());
    step++;
    return modifiedPositions;
  }

  private List<Position> updateFires() {
    List<Position> modifiedPositions = new ArrayList<>();
    if (step % 2 == 0) {
      List<Position> newFirePositions = new ArrayList<>();
      for (Position fire : firePositions) {
        newFirePositions.addAll(neighbors.get(fire));
      }
      firePositions.addAll(newFirePositions);
      modifiedPositions.addAll(newFirePositions);
    }
    return modifiedPositions;

  }

  @Override
  public int stepNumber() {
    return step;
  }

  private List<Position> updateFirefighters() {
    List<Position> modifiedPosition = new ArrayList<>();
    List<Position> firefighterNewPositions = new ArrayList<>();
    for (Position firefighterPosition : firefighterPositions) {
      Position newFirefighterPosition =
              targetStrategy.neighborClosestToFire(firefighterPosition,
                      firePositions, neighbors);
      firefighterNewPositions.add(newFirefighterPosition);
      extinguish(newFirefighterPosition);
      modifiedPosition.add(firefighterPosition);
      modifiedPosition.add(newFirefighterPosition);
      List<Position> neighborFirePositions = neighbors.get(newFirefighterPosition).stream()
              .filter(firePositions::contains).toList();
      for (Position firePosition : neighborFirePositions)
        extinguish(firePosition);
      modifiedPosition.addAll(neighborFirePositions);
    }
    firefighterPositions = firefighterNewPositions;
    return modifiedPosition;
  }

  @Override
  public void reset() {
    step = 0;
    initializeElements();
  }

  private void extinguish(Position position) {
    firePositions.remove(position);
  }


  @Override
  public void setState(List<ModelElement> state, Position position) {
    firePositions.remove(position);
    for (; ; ) {
      if (!firefighterPositions.remove(position)) break;
    }
    for (ModelElement element : state) {
      switch (element) {
        case FIRE -> firePositions.add(position);
        case FIREFIGHTER -> firefighterPositions.add(position);
      }
    }
  }
}