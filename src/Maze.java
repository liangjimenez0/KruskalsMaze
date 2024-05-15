import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

import java.util.LinkedList;
import java.util.Queue;

import java.util.Stack;

// Utility class
class Utils {

  // Finds the representative of a given cell
  public Cell findRepresentative(HashMap<Cell, Cell> representatives, Cell cell) {

    if (representatives.get(cell).equals(cell)) {
      return cell;
    }
    else {
      return findRepresentative(representatives, representatives.get(cell));
    }

  }

  // Determines if ever single cell has the same representative
  public boolean oneTree(HashMap<Cell, Cell> representatives, ArrayList<ArrayList<Cell>> cells) {

    for (ArrayList<Cell> listOfCell : cells) {
      for (Cell c : listOfCell) {
        if (!this.findRepresentative(representatives, cells.get(0).get(0))
            .equals(this.findRepresentative(representatives, c))) {
          return false;
        }
      }
    }
    return true;
  }

}

// Represents a cell
class Cell {
  // the x position of the cell
  int x;
  // the y position of the cell
  int y;
  // the color of the cell
  Color color;
  // determines whether the cell has been looked at or not
  boolean processed;
  // determines if the cell is in the final path needed
  // to solve the maze
  boolean path;
  // a list of a cells neighbor cells
  ArrayList<Cell> neighborCells;

  // starting constructor for the cell
  Cell(int x, int y) {
    this.x = x;
    this.y = y;

    this.color = Color.BLACK;
    this.processed = false;
    this.path = false;

    this.neighborCells = new ArrayList<Cell>();
  }

  @Override
  // determines if this cell is the same as the given cell
  public boolean equals(Object c) {
    if (!(c instanceof Cell)) {
      return false;
    }

    Cell cell = (Cell) c;

    return this.x == cell.x && this.y == cell.y;
  }

  // custom hash code for a cell
  public int hashCode() {
    return this.x * 10000 + this.y;
  }

  // EFFECT: changes the color of a given cell based on its location
  void changeColor(int width, int height) {

    if (this.path) {
      color = Color.BLUE;
    }
    else if (this.processed) {
      color = Color.CYAN.darker().darker();
    }
    else if (this.x == 0 && this.y == 0) {
      color = Color.GREEN;
    }
    else if (this.x == width - 1 && this.y == height - 1) {
      color = Color.MAGENTA;
    }
    else {
      color = Color.GRAY.brighter();
    }
  }

  // draws a cell
  public WorldImage drawCell() {
    return new RectangleImage(30, 30, OutlineMode.SOLID, this.color);
  }

  // adds a cell's neighbor to its list of neighboring cells
  public void connectTo(Cell neighbor) {
    this.neighborCells.add(neighbor);
  }

  // removes a given cell from the list
  public void removeNeighbor(Cell neighbor) {
    this.neighborCells.remove(neighbor);
  }

  // changes the path value of the cell to true
  public void changePath() {
    this.path = true;
  }

  // changes the processed value of the cell to true
  public void changeProcessed() {
    this.processed = true;
  }

}

// represents an edge
class Edge {
  // a cell to the left of the edge
  Cell from;
  // a cell to the right of the edge
  Cell to;
  // a randomized edge weight
  int weight;

  // the starting constructor of an edge
  Edge(Cell from, Cell to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

}

// A comparator used to compare edge weights
class SortEdgesComparator implements Comparator<Edge> {
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }
}

// represents a breadth first search
class BreadthFirstSearch {
  // represents a hash map to keep track of how you
  // reach the end cell
  HashMap<Cell, Cell> cameFromEdge;
  // represents a queue
  Queue<Cell> worklist;
  // represents the cell in the bottom right
  Cell target;
  // determines if the search is finished or not
  boolean finished;
  // represents the cell in the top left
  Cell firstCell;

  // starting constructor for BFS
  BreadthFirstSearch(ArrayList<ArrayList<Cell>> cells, int w, int h) {
    this.worklist = new LinkedList<Cell>();
    this.worklist.add(cells.get(0).get(0));
    this.target = cells.get(h - 1).get(w - 1);
    this.firstCell = cells.get(0).get(0);
    this.cameFromEdge = new HashMap<Cell, Cell>();
    this.finished = false;

  }

  // EFFECT: goes through the worklist to find
  // the target cell
  public boolean search() {
    if (!worklist.isEmpty() && !this.finished) {
      Cell next = this.worklist.remove();

      next.processed = true;

      if (next.equals(this.target)) {
        this.finished = true;
        this.reconstruct(cameFromEdge, next);
      }
      else {
        for (Cell n : next.neighborCells) {
          if (!n.processed) {
            n.changeProcessed();
            worklist.add(n);
            cameFromEdge.put(n, next);
          }

        }
      }
    }

    return this.finished;
  }

  // EFFECT: determines if a cell is a part of
  // the final path
  public void reconstruct(HashMap<Cell, Cell> hash, Cell cell) {

    this.firstCell.changePath();

    while (!cell.equals(this.firstCell)) {
      cell.changePath();
      cell = hash.get(cell);
    }

  }

}

class DepthFirstSearch {
  // represents a hash map to keep track of how you
  // reach the end cell
  HashMap<Cell, Cell> cameFromEdge;
  // represents a stack
  Stack<Cell> worklist;
  // represents the cell in the bottom right
  Cell target;
  // determines if the search is finished or not
  boolean finished;
  // represents the cell in the top left
  Cell firstCell;

  // starting constructor
  DepthFirstSearch(ArrayList<ArrayList<Cell>> cells, int w, int h) {
    this.worklist = new Stack<Cell>();
    this.worklist.add(cells.get(0).get(0));
    this.target = cells.get(h - 1).get(w - 1);
    this.firstCell = cells.get(0).get(0);
    this.cameFromEdge = new HashMap<Cell, Cell>();
    this.finished = false;

  }

  // EFFECT: goes through the worklist to find
  // the target cell
  public boolean search() {
    if (!worklist.isEmpty() && !this.finished) {
      Cell next = this.worklist.pop();

      next.processed = true;

      if (next.equals(this.target)) {
        this.finished = true;
        this.reconstruct(cameFromEdge, next);
      }
      else {
        for (Cell n : next.neighborCells) {
          if (!n.processed) {
            n.changeProcessed();
            worklist.add(n);
            cameFromEdge.put(n, next);
          }

        }
      }

    }

    return this.finished;
  }

  // EFFECT: determines if a cell is a part of
  // the final path
  public void reconstruct(HashMap<Cell, Cell> hash, Cell cell) {

    this.firstCell.changePath();

    while (!cell.equals(this.firstCell)) {
      cell.changePath();
      cell = hash.get(cell);
    }

  }

}

// represents the maze game
class MazeGame extends World {
  // represents all of the cells based on the width and height
  ArrayList<ArrayList<Cell>> cells;
  // represents all of the edges, connected to the list of cells
  ArrayList<Edge> edges;
  // represents a random
  Random rand;
  // represents a given width
  int width;
  // represents a given height
  int height;
  // represents whether or not you want to do a bfs
  boolean bfs;
  // represents whether or not you want to do a dfs
  boolean dfs;
  // represents a breadth first search
  BreadthFirstSearch b;
  // represents a depth first search
  DepthFirstSearch d;

  int bfsSteps;
  int dfsSteps;

  ArrayList<Cell> visited;

  // initializes the beginning constructors of the game
  MazeGame(int width, int height) {
    this.width = width;
    this.height = height;
    this.rand = new Random();
    this.bfs = false;
    this.dfs = false;

    this.listOfCells();
    this.listOfEdges();
    this.sortEdges();
    this.makeMaze();
    this.connectNeighbors();
    this.linkEdges();

    this.b = new BreadthFirstSearch(this.cells, this.width, this.height);
    this.d = new DepthFirstSearch(this.cells, this.width, this.height);

    this.visited = new ArrayList<Cell>();
  }

  // constructor used for testing
  MazeGame(int width, int height, Random rand) {
    this.width = width;
    this.height = height;
    this.rand = rand;

    this.listOfCells();
    this.listOfEdges();
    this.sortEdges();
    this.makeMaze();
    this.connectNeighbors();
    this.linkEdges();

    this.b = new BreadthFirstSearch(this.cells, this.width, this.height);
    this.d = new DepthFirstSearch(this.cells, this.width, this.height);
  }

  // EFFECT: Generates a list of cells
  public void listOfCells() {
    this.cells = new ArrayList<ArrayList<Cell>>();

    for (int i = 0; i < height; i++) {
      ArrayList<Cell> empty = new ArrayList<Cell>();
      for (int j = 0; j < width; j++) {
        empty.add(new Cell(j, i));
      }
      this.cells.add(empty);
    }
  }

  // EFFECT: Generates a list of edges with random edge weights
  public void listOfEdges() {
    this.edges = new ArrayList<Edge>();

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (i > 0) {
          this.edges.add(new Edge(this.cells.get(i - 1).get(j), this.cells.get(i).get(j),
              Math.abs(rand.nextInt())));
        }

        if (j > 0) {
          this.edges.add(new Edge(this.cells.get(i).get(j - 1), this.cells.get(i).get(j),
              Math.abs(rand.nextInt())));
        }
      }
    }
  }

  // EFFECT: sorts the edges by edge weight from lowest to highest
  public void sortEdges() {
    this.edges.sort(new SortEdgesComparator());
  }

  // EFFECT: uses kruskal's algorithm to create a minimum spanning tree
  public void makeMaze() {
    Utils utils = new Utils();
    HashMap<Cell, Cell> representatives = new HashMap<Cell, Cell>();
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    ArrayList<Edge> worklist = new ArrayList<Edge>(this.edges);

    // initializes the HashMap
    for (ArrayList<Cell> listOfC : this.cells) {
      for (Cell c : listOfC) {
        representatives.put(c, c);
      }
    }

    while (!utils.oneTree(representatives, this.cells)) {
      Edge cheapest = worklist.remove(0);
      Cell repOfFromCell = utils.findRepresentative(representatives, cheapest.from);
      Cell repOfToCell = utils.findRepresentative(representatives, cheapest.to);

      if (!repOfFromCell.equals(repOfToCell)) {

        edgesInTree.add(cheapest);

        // changes the representative of a cell
        representatives.put(repOfFromCell, repOfToCell);
      }
    }

    this.edges.removeAll(edgesInTree);
  }

  // EFFECTS: connects the cells so that they are linked
  public void connectNeighbors() {

    for (int j = 0; j < this.height; j = j + 1) {
      for (int i = 0; i < this.width - 1; i = i + 1) {
        cells.get(j).get(i).connectTo(cells.get(j).get(i + 1));
      }
    }
    for (int y = 0; y < this.height; y = y + 1) {
      for (int i = 1; i < this.width; i = i + 1) {
        cells.get(y).get(i).connectTo(cells.get(y).get(i - 1));
      }
    }
    for (int y = 1; y < this.height; y = y + 1) {
      for (int i = 0; i < this.width; i = i + 1) {
        cells.get(y).get(i).connectTo(cells.get(y - 1).get(i));
      }
    }
    for (int y = 0; y < this.height - 1; y = y + 1) {
      for (int i = 0; i < this.width; i = i + 1) {
        cells.get(y).get(i).connectTo(cells.get(y + 1).get(i));
      }
    }

  }

  // EFFECT: removes the a cell's neighbor if they share an edge
  public void linkEdges() {
    for (Edge e : this.edges) {
      e.from.removeNeighbor(e.to);
      e.to.removeNeighbor(e.from);
    }
  }

  // draws the scene for the maze game
  public WorldScene makeScene() {
    WorldScene background = new WorldScene(width * 40, height * 40);

    // draws the cells onto the background
    for (ArrayList<Cell> listOfCell : this.cells) {
      for (Cell c : listOfCell) {

        // changes the cell color based on its position
        c.changeColor(width, height);

        background.placeImageXY(c.drawCell(), (c.x * 30) + 15 + (width * 5),
            (c.y * 30) + 15 + (height * 5));

      }
    }

    // draws the edges onto the background
    for (Edge e : this.edges) {
      if (e.to.x == e.from.x) {
        background.placeImageXY(
            new RectangleImage(30, 3, OutlineMode.SOLID, Color.black.brighter()),
            (e.to.x * 30) + 15 + (width * 5), ((e.to.y + e.from.y) * 30 / 2) + 15 + (height * 5));
      }
      else {
        background.placeImageXY(
            new RectangleImage(3, 30, OutlineMode.SOLID, Color.black.brighter()),
            ((e.to.x + e.from.x) * 30 / 2) + 15 + (width * 5), (e.to.y * 30) + 15 + (height * 5));
      }
    }

    background.placeImageXY(new TextImage("MAZE", 35, Color.BLACK),
        ((width * 30) + (width * 5 * 2) + 15) / 2, height * 30 / 8);

    background.placeImageXY(
        new TextImage("Key: 'b' = BFS, 'd' = DFS, 'r' = Reset, 'R' = Refresh", 12, Color.BLACK),
        ((width * 50) + (width * 5 * 2) + 15) / 2, height * 30 / 8);

    background.placeImageXY(new TextImage("DFS steps: " + this.dfsSteps, 15, Color.black),
        width * 30 / 3, (height * 30) + (height * 5) + 15);
    background.placeImageXY(new TextImage("BFS steps: " + this.bfsSteps, 15, Color.black),
        width * 30, height * 30 + (height * 5) + 15);

    return background;
  }

  // EFFECT: changes the world state each tick depending
  // on which search the user decides
  public void onTick() {

    if (bfs && !this.b.search()) {
      this.b.search();
      this.bfsSteps++;
    }

    if (dfs && !this.d.search()) {
      this.d.search();
      this.dfsSteps++;
    }

    for (ArrayList<Cell> cell : this.cells) {
      for (Cell c : cell) {

        if (c.processed) {
          visited.add(c);
        }
      }
    }

  }

  // EFFECT: determines how user wants to solve the maze and changes
  // values based on that
  public void onKeyEvent(String key) {

    if (key.equals("b")) {
      this.bfs = true;
      this.dfs = false;
    }

    if (key.equals("d")) {
      this.bfs = false;
      this.dfs = true;
    }

    if (key.equals("r")) {
      this.rand = new Random();
      this.bfs = false;
      this.dfs = false;
      this.bfsSteps = 0;
      this.dfsSteps = 0;
      this.visited = new ArrayList<Cell>();

      this.listOfCells();
      this.listOfEdges();
      this.sortEdges();
      this.makeMaze();
      this.connectNeighbors();
      this.linkEdges();

      this.b = new BreadthFirstSearch(this.cells, this.width, this.height);
      this.d = new DepthFirstSearch(this.cells, this.width, this.height);
    }

    if (key.equals("R")) {
      this.rand = new Random();
      this.bfs = false;
      this.dfs = false;
      this.bfsSteps = 0;
      this.dfsSteps = 0;
      this.visited = new ArrayList<Cell>();

      for (ArrayList<Cell> cell : this.cells) {
        for (Cell c : cell) {
          c.processed = false;
          c.path = false;
        }
      }

      this.b = new BreadthFirstSearch(this.cells, this.width, this.height);
      this.d = new DepthFirstSearch(this.cells, this.width, this.height);
    }

    if (key.equals("u")) {
      for (ArrayList<Cell> cell : this.cells) {
        for (Cell c : cell) {
          if (c.processed) {
            c.processed = false;
          }
        }
      }
    }

    if (key.equals("v")) {
      for (ArrayList<Cell> cell : this.cells) {
        for (Cell c : cell) {
          if (visited.contains(c)) {
            c.processed = true;
          }
        }
      }
    }

  }

}

// represents examples of the maze game
class ExamplesMaze {

  Random random;
  MazeGame maze;

  Utils utils;
  HashMap<Cell, Cell> hashmap;
  HashMap<Cell, Cell> hashmapNotOneTree;

  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  Cell cell5;
  Cell cell6;
  Cell cell7;
  Cell cell8;
  Cell cell9;

  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;
  Edge e5;

  Random test1;
  Random test2;
  Random test3;

  ArrayList<Cell> loCells;
  ArrayList<ArrayList<Cell>> loloCells;

  ArrayList<Cell> list;
  ArrayList<Cell> list1;
  BreadthFirstSearch bfs;
  DepthFirstSearch dfs;

  // used to initialize the data
  void initData() {
    this.random = new Random(1);
    this.maze = new MazeGame(20, 20, random);

    this.utils = new Utils();
    this.hashmap = new HashMap<Cell, Cell>();
    this.hashmapNotOneTree = new HashMap<Cell, Cell>();

    this.cell1 = new Cell(1, 2);
    this.cell2 = new Cell(2, 3);
    this.cell3 = new Cell(3, 4);
    this.cell4 = new Cell(5, 6);
    this.cell5 = new Cell(7, 8);
    this.cell6 = new Cell(9, 10);
    this.cell7 = new Cell(1, 2);
    this.cell8 = new Cell(2, 3);
    this.cell9 = new Cell(1, 1);

    this.e1 = new Edge(this.cell1, this.cell2, 10);
    this.e2 = new Edge(this.cell2, this.cell3, 15);
    this.e3 = new Edge(this.cell3, this.cell4, 25);
    this.e4 = new Edge(this.cell4, this.cell5, 27);
    this.e5 = new Edge(this.cell5, this.cell6, 34);

    this.test1 = new Random(2);
    this.test2 = new Random(3);
    this.test3 = new Random(4);

    this.loCells = new ArrayList<Cell>();
    this.loloCells = new ArrayList<ArrayList<Cell>>();

    this.loCells.add(cell1);
    this.loCells.add(cell2);
    this.loCells.add(cell3);
    this.loCells.add(cell4);
    this.loCells.add(cell5);

    this.loloCells.add(loCells);

    this.list = new ArrayList<Cell>();
    this.list1 = new ArrayList<Cell>();

    this.dfs = new DepthFirstSearch(this.loloCells, 1, 1);
    this.bfs = new BreadthFirstSearch(this.loloCells, 1, 1);
  }

  // to test the Maze game
  void testMaze(Tester t) {
    int width = 20;
    int height = 20;

    MazeGame starterWorld = new MazeGame(width, height);
    starterWorld.bigBang(width * 40, height * 40, 0.01);
  }

  // to test the method findRepresentatives
  void testFindRepresentatives(Tester t) {
    this.initData();

    this.hashmap.put(cell1, cell5);
    this.hashmap.put(cell2, cell1);
    this.hashmap.put(cell3, cell5);
    this.hashmap.put(cell4, cell5);
    this.hashmap.put(cell5, cell5);

    t.checkExpect(utils.findRepresentative(hashmap, this.cell1), this.cell5);
    t.checkExpect(utils.findRepresentative(hashmap, this.cell2), this.cell5);
    t.checkExpect(utils.findRepresentative(hashmap, this.cell3), this.cell5);
    t.checkExpect(utils.findRepresentative(hashmap, this.cell4), this.cell5);
    t.checkExpect(utils.findRepresentative(hashmap, this.cell5), this.cell5);
  }

  // to test the method oneTree
  void testOneTree(Tester t) {
    this.initData();

    this.hashmap.put(cell1, cell5);
    this.hashmap.put(cell2, cell1);
    this.hashmap.put(cell3, cell5);
    this.hashmap.put(cell4, cell5);
    this.hashmap.put(cell5, cell5);

    this.hashmapNotOneTree.put(cell1, cell5);
    this.hashmapNotOneTree.put(cell2, cell1);
    this.hashmapNotOneTree.put(cell3, cell3);
    this.hashmapNotOneTree.put(cell4, cell5);
    this.hashmapNotOneTree.put(cell5, cell5);

    t.checkExpect(utils.oneTree(hashmapNotOneTree, this.loloCells), false);
    t.checkExpect(utils.oneTree(hashmap, this.loloCells), true);

  }

  // to test the method equals
  void testEquals(Tester t) {
    this.initData();

    t.checkExpect(this.cell1.equals(this.cell2), false);
    t.checkExpect(this.cell1.equals(this.cell7), true);
    t.checkExpect(this.cell2.equals(this.cell3), false);
    t.checkExpect(this.cell2.equals(this.cell8), true);
    t.checkExpect(this.cell5.equals(this.cell2), false);
    t.checkExpect(this.cell4.equals(this.cell4), true);
  }

  // to test the method hashCode
  void testHashCode(Tester t) {
    this.initData();

    t.checkExpect(this.cell1.hashCode(), 10002);
    t.checkExpect(this.cell2.hashCode(), 20003);
    t.checkExpect(this.cell3.hashCode(), 30004);
    t.checkExpect(this.cell4.hashCode(), 50006);
    t.checkExpect(this.cell5.hashCode(), 70008);
  }

  // to test the method changeColor
  void testChangeColor(Tester t) {
    this.initData();

    this.cell9.changeColor(9, 0);
    this.cell2.changeColor(3, 4);
    this.cell3.changeColor(8, 9);
    this.cell4.changeColor(6, 7);
    this.cell5.changeColor(-1, 0);

    t.checkExpect(this.cell9.color, new Color(182, 182, 182));
    t.checkExpect(this.cell2.color, Color.magenta);
    t.checkExpect(this.cell3.color, Color.gray.brighter());
    t.checkExpect(this.cell4.color, Color.magenta);
    t.checkExpect(this.cell5.color, Color.gray.brighter());
  }

  // to test the method compare
  void testCompare(Tester t) {
    this.initData();

    Comparator<Edge> comp = new SortEdgesComparator();

    t.checkExpect(comp.compare(e1, e2), -5);
    t.checkExpect(comp.compare(e2, e2), 0);
    t.checkExpect(comp.compare(e3, e2), 10);
    t.checkExpect(comp.compare(e4, e1), 17);
    t.checkExpect(comp.compare(e5, e3), 9);
  }

  // to test the method drawCell
  void testDrawCell(Tester t) {
    this.initData();

    t.checkExpect(this.cell1.drawCell(),
        new RectangleImage(30, 30, OutlineMode.SOLID, Color.black));
    t.checkExpect(this.cell2.drawCell(),
        new RectangleImage(30, 30, OutlineMode.SOLID, Color.black));
    t.checkExpect(this.cell3.drawCell(),
        new RectangleImage(30, 30, OutlineMode.SOLID, Color.black));

    this.cell9.changeColor(1, 0);
    this.cell2.changeColor(0, 0);

    t.checkExpect(this.cell9.drawCell(),
        new RectangleImage(30, 30, OutlineMode.SOLID, new Color(182, 182, 182)));
  }

  // to test the method listOfCells
  void testListOfCells(Tester t) {
    this.initData();

    this.maze.listOfCells();

    t.checkExpect(this.maze.cells.size(), 20);
    t.checkExpect(this.maze.cells.get(0).size(), 20);

    for (int i = 1; i < 20; i++) {
      for (int j = 1; j < 20; j++) {
        t.checkExpect(this.maze.cells.get(i).get(j), new Cell(j, i));
      }
    }
  }

  // to test the method listOfEdges
  void testListOfEdges(Tester t) {
    this.initData();

    this.maze.listOfEdges();

    t.checkExpect(this.maze.edges.size(), 760);
    t.checkExpect(this.maze.edges.get(0).from, this.maze.cells.get(0).get(0));
    t.checkExpect(this.maze.edges.get(0).to, this.maze.cells.get(0).get(1));
    t.checkExpect(this.maze.edges.get(1).from, this.maze.cells.get(0).get(1));
    t.checkExpect(this.maze.edges.get(1).to, this.maze.cells.get(0).get(2));
  }

  // to test the method sortEdges
  void testSortEdges(Tester t) {
    this.initData();

    this.maze.listOfEdges();
    this.maze.sortEdges();

    for (int i = 0; i < this.maze.edges.size() - 1; i++) {
      t.checkExpect(this.maze.edges.get(i).weight <= this.maze.edges.get(i + 1).weight, true);
    }

  }

  // to test the method makeMaze
  void testMakeMaze(Tester t) {
    this.initData();

    this.maze.listOfEdges();
    this.maze.sortEdges();
    this.maze.makeMaze();

    t.checkExpect(this.maze.edges.size() < 760, true);
    t.checkExpect(this.maze.edges.size(), 361);

    for (int i = 0; i < this.maze.edges.size() - 1; i++) {
      t.checkExpect(this.maze.edges.get(i).weight <= this.maze.edges.get(i + 1).weight, true);
    }
  }

  // to test the method makeScene
  void testMakeScene(Tester t) {
    this.initData();

    this.maze.makeScene();

    WorldScene background = new WorldScene(this.maze.width * 40, this.maze.height * 40);

    for (ArrayList<Cell> listOfCell : this.maze.cells) {
      for (Cell c : listOfCell) {

        c.changeColor(maze.width, maze.height);

        background.placeImageXY(c.drawCell(), (c.x * 30) + 15 + (maze.width * 5),
            (c.y * 30) + 15 + (maze.height * 5));

      }
    }

    for (Edge e : this.maze.edges) {
      if (e.to.x == e.from.x) {
        background.placeImageXY(
            new RectangleImage(30, 3, OutlineMode.SOLID, Color.black.brighter()),
            (e.to.x * 30) + 15 + (maze.width * 5),
            ((e.to.y + e.from.y) * 30 / 2) + 15 + (maze.height * 5));
      }
      else {
        background.placeImageXY(
            new RectangleImage(3, 30, OutlineMode.SOLID, Color.black.brighter()),
            ((e.to.x + e.from.x) * 30 / 2) + 15 + (maze.width * 5),
            (e.to.y * 30) + 15 + (maze.height * 5));
      }
    }

    background.placeImageXY(new TextImage("MAZE", 35, Color.BLACK),
        ((maze.width * 30) + (maze.width * 5 * 2) + 15) / 2, maze.height * 30 / 8);

    background.placeImageXY(
        new TextImage("Key: 'b' = BFS, 'd' = DFS, 'r' = Reset, 'R' = Refresh", 12, Color.BLACK),
        ((maze.width * 50) + (maze.width * 5 * 2) + 15) / 2, maze.height * 30 / 8);

    background.placeImageXY(new TextImage("DFS steps: " + 0, 15, Color.black), maze.width * 30 / 3,
        (maze.height * 30) + (maze.height * 5) + 15);
    background.placeImageXY(new TextImage("BFS steps: " + 0, 15, Color.black), maze.width * 30,
        maze.height * 30 + (maze.height * 5) + 15);

    t.checkExpect(this.maze.makeScene(), background);
  }

  // to test the method onTick
  void testOnTick(Tester t) {
    this.initData();
    this.maze.listOfCells();
    this.maze.listOfEdges();
    this.maze.onTick();
    t.checkExpect(this.maze.bfs, false);
    t.checkExpect(this.maze.height, 20);
  }

  // to test the method onKeyPressed
  void testOnKeyPressed(Tester t) {
    this.initData();

    this.maze.onKeyEvent("b");
    t.checkExpect(this.maze.bfs, true);
    t.checkExpect(this.maze.dfs, false);

    this.maze.onKeyEvent("d");
    t.checkExpect(this.maze.bfs, false);
    t.checkExpect(this.maze.dfs, true);

    this.maze.onKeyEvent("r");
    t.checkExpect(this.maze.bfs, false);
    t.checkExpect(this.maze.dfs, false);
    t.checkExpect(this.maze.bfsSteps, 0);
    t.checkExpect(this.maze.dfsSteps, 0);

    this.maze.onKeyEvent("R");

    t.checkExpect(this.maze.bfs, false);
    t.checkExpect(this.maze.dfs, false);
    t.checkExpect(this.maze.bfsSteps, 0);
    t.checkExpect(this.maze.dfsSteps, 0);

    for (ArrayList<Cell> cell : this.maze.cells) {
      for (Cell c : cell) {
        t.checkExpect(c.processed, false);
        t.checkExpect(c.path, false);
      }
    }

    this.maze.onKeyEvent("u");

    for (ArrayList<Cell> cell : this.maze.cells) {
      for (Cell c : cell) {
        t.checkExpect(c.processed, false);
      }
    }

    this.maze.onKeyEvent("v");

    for (Cell c : this.maze.visited) {
      t.checkExpect(c.processed, true);
    }
  }

  // to test the method linkEdges
  void testLinkEdges(Tester t) {
    this.initData();

    this.cell1.connectTo(cell2);
    this.cell2.connectTo(cell4);
    this.cell3.connectTo(cell4);

    this.list.add(new Cell(5, 6));
    this.list1.add(new Cell(2, 3));
    this.list1.add(new Cell(5, 6));

    this.maze.linkEdges();
    t.checkExpect(this.cell4.neighborCells, new ArrayList<Cell>());
    t.checkExpect(this.cell3.neighborCells, this.list);
    t.checkExpect(this.cell2.neighborCells, this.list);
  }

  // to test the method connectNeighbors
  void testConnectNeighbors(Tester t) {
    this.initData();
    this.maze.listOfCells();
    this.maze.listOfEdges();
    this.maze.connectNeighbors();
  }

  // to test the method search in the BFS class
  void testSearchBFS(Tester t) {
    this.initData();

    this.loloCells.add(loCells);
    this.loloCells.add(list);
    this.loloCells.add(list1);

    this.list.add(cell1);
    this.list.add(cell3);
    this.list.add(cell7);

    t.checkExpect(this.bfs.search(), true);
  }

  // to test the method reconstruct in the BFS class
  void testReconstructBFS(Tester t) {
    this.initData();

    this.bfs.reconstruct(hashmap, cell1);

    t.checkExpect(this.hashmap.size(), 0);
    t.checkExpect(this.cell1.processed, false);
  }

  // to test the method search in the DFS class
  void testSearchDFS(Tester t) {
    this.initData();

    this.loloCells.add(loCells);
    this.loloCells.add(list);
    this.loloCells.add(list1);

    this.list.add(cell1);
    this.list.add(cell3);
    this.list.add(cell7);

    t.checkExpect(this.dfs.search(), true);
  }

  // to test the method reconstruct in the DFS class
  void testReconstructDFS(Tester t) {
    this.initData();

    this.dfs.reconstruct(hashmap, cell1);

    t.checkExpect(this.hashmap.size(), 0);
    t.checkExpect(this.cell1.processed, false);

  }

  // to test the method connectTo
  void testConnectTo(Tester t) {
    this.initData();

    this.cell1.connectTo(cell2);
    this.cell2.connectTo(cell4);
    this.cell3.connectTo(cell4);

    this.list.add(new Cell(5, 6));
    this.list1.add(new Cell(2, 3));
    this.list1.add(new Cell(5, 6));

    t.checkExpect(this.cell4.neighborCells, new ArrayList<Cell>());
    t.checkExpect(this.cell3.neighborCells, this.list);
    t.checkExpect(this.cell2.neighborCells, this.list);
  }

  // to test the method removeNeighbor
  void testRemoveNeighbor(Tester t) {
    this.initData();

    this.cell1.connectTo(cell2);
    this.cell3.connectTo(cell2);
    this.cell3.connectTo(cell4);
    this.cell2.connectTo(cell2);
    this.cell2.connectTo(cell1);
    this.cell2.connectTo(cell4);
    this.cell2.connectTo(cell3);

    t.checkExpect(this.cell1.neighborCells.size(), 1);
    this.cell1.removeNeighbor(cell2);
    t.checkExpect(this.cell1.neighborCells.size(), 0);
    t.checkExpect(this.cell3.neighborCells.size(), 2);
    this.cell3.removeNeighbor(cell4);
    t.checkExpect(this.cell3.neighborCells.size(), 1);
    t.checkExpect(this.cell2.neighborCells.size(), 4);
    this.cell2.removeNeighbor(cell2);
    t.checkExpect(this.cell2.neighborCells.size(), 3);
  }

  // to test the method changePath
  void testChangePath(Tester t) {
    this.initData();
    this.cell5.changePath();

    t.checkExpect(this.cell5.path, true);
    t.checkExpect(this.cell7.path, false);
    this.cell7.changePath();
    t.checkExpect(this.cell7.path, true);
    t.checkExpect(this.cell9.path, false);
    this.cell1.changePath();
    t.checkExpect(this.cell1.path, true);
  }

  // to test the method changeProcessed
  void testChangeProcessed(Tester t) {
    this.initData();

    this.cell4.changeProcessed();

    t.checkExpect(this.cell4.processed, true);
    t.checkExpect(this.cell7.processed, false);
    this.cell7.changeProcessed();
    t.checkExpect(this.cell7.processed, true);
    this.cell1.changeProcessed();
    t.checkExpect(this.cell1.processed, true);
    t.checkExpect(this.cell9.processed, false);
  }

}
