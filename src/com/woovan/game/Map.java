package com.woovan.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.woovan.game.enums.Direction;
import com.woovan.game.enums.Tile;

public class Map {
	
	private int seed;
	
	private Random random;

	protected int xSize;
	
	protected int ySize;
	
	protected Tile[][] cells;
	
	public Map(int seed, int xSize, int ySize) {
		super();
		this.seed = seed;
		this.random = new Random(seed);
		this.xSize = xSize;
		this.ySize = ySize;
		this.cells = new Tile[xSize][ySize];
		random.nextInt();
	}
	
	public Map(int xSize, int ySize) {
		this(new Random().nextInt(10000), xSize, ySize);
	}
	
	class Cell {
		private int x;
		private int y;
		private Tile tile;
		private Cell(int x, int y, Tile tile) {
			this.x = x;
			this.y = y;
			this.tile = tile;
		}
		public Tile getTile() {
			return tile;
		}
		void setTile(Tile tile) {
			this.tile = tile;
			cells[x][y] = tile;
		}
		Cell get(Direction direction) {
			assert(direction != null);
			switch (direction) {
			case UP:
				return getCell(x, y + 1);
			case UP_RIGHT:
				return getCell(x + 1, y + 1);
			case RIGHT:
				return getCell(x + 1, y);
			case DOWN_RIGHT:
				return getCell(x + 1, y - 1);
			case DOWN:
				return getCell(x, y - 1);
			case DOWN_LEFT:
				return getCell(x - 1, y - 1);
			case LEFT:
				return getCell(x - 1, y);
			case UP_LEFT:
				return getCell(x - 1, y + 1);
			default:
				return null;
			}
		}
		public String toString() {
			return x + "," + y + " = " + tile.name();
		}
	}
	
	Cell getCell(int x, int y) {
		try {
			return new Cell(x, y, cells[x][y]);
		} catch (Exception e) {
		}
		return null;
	}
	
    Tile getTile(int x, int y) {
        check(x, y);
        return cells[x][y];
    }
	
    void setTile(int x, int y, Tile tile) {
    	check(x, y);
        cells[x][y] = tile;
	}
    
    void setTile(Cell cell) {
    	setTile(cell.x, cell.y, cell.tile);
	}
    
    void setTiles(int xStart, int yStart, int xEnd, int yEnd, Tile tile) {
    	check(xStart, yStart, xEnd, yEnd);
        
        for (int y = yStart; y <= yEnd; y++) {
			for (int x = xStart; x <= xEnd; x++) {
				setTile(x, y, tile);
			}
		}
    }
 
    void fill(Tile tile) {
    	setTiles(0, 0, xSize - 1, ySize - 1, tile);
    }
    
    void fillEdge(Tile tile) {
    	for (int x = 0; x < xSize; x++) {
			setTile(x, 0, tile);
			setTile(x, ySize - 1, tile);
		}
    	for (int y = 0; y < ySize; y++) {
			setTile(0, y, tile);
			setTile(xSize -1, y, tile);
		}
    }
    
    void randomFill(float rate, Tile tile1, Tile tile2) {
    	for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if(random.nextFloat() <= rate) {
					setTile(x, y, tile1);
				} else {
					setTile(x, y, tile2);
				}
			}
		}
    }
    
	boolean isXInBounds(int x) {
        return x >= 0 && x < xSize;
    }
    
    boolean isYInBounds(int y) {
        return y >= 0 && y < ySize;
    }
    
    void check(int x, int y) {
    	assert(isXInBounds(x));
        assert(isYInBounds(y));
    }
    
    void check(int xStart, int yStart, int xEnd, int yEnd) {
    	assert(xStart <= xEnd);
        assert(yStart <= yEnd);
        
        assert(isXInBounds(xStart) && isXInBounds(xEnd));
        assert(isYInBounds(yStart) && isYInBounds(yEnd));
    }
    
    boolean isAreaUnused(int xStart, int yStart, int xEnd, int yEnd) {
    	check(xStart, yStart, xEnd, yEnd);
    	
        for (int y = yStart; y <= yEnd; y++) {
        	for (int x = xStart; x <= xEnd; x++) {
				if(getTile(x, y) != Tile.Unused) {
					return false;
				}
			}
		}
        return true;
    }
    
    boolean isCorner(int x, int y) {
    	return (x == 0 || x == xSize - 1) && (y == 0 || y == ySize - 1);
    }
    
    boolean isEdge(int x, int y) {
    	return (x == 0 || x == xSize - 1 || y == 0 || y == ySize - 1) && !isCorner(x, y);
    }
    
    List<Cell> getCellsAround(int x, int y, int distance, boolean self) {
    	check(x, y);
    	assert(distance > 0);
    	
    	List<Cell> list = new ArrayList<Cell>();
    	for (int i = -distance; i <= distance; i++) {
    		for (int j = -distance; j <= distance; j++) {
    			//if(self || (Math.abs(i) == distance || Math.abs(j) == distance)) {
    			if(self || Math.abs(i) > 0 || Math.abs(j) > 0) {
    				int xi = x + i;
    				int yj = y + j;
    				if(isXInBounds(xi) && isYInBounds(yj)) {
    					list.add(getCell(xi, yj));
    				}
    			}
    		}
		}
    	return list;
    }
    
    List<Cell> getCellsAround2(int x, int y, int distance, boolean self) {
    	check(x, y);
    	assert(distance > 0);
    	
    	List<Cell> list = new ArrayList<Cell>();
    	for (int i = -distance; i <= distance; i++) {
    		for (int j = -distance; j <= distance; j++) {
    			if(Math.abs(i) == distance && Math.abs(j) == distance) {
    				continue;
    			}
    			if(self || Math.abs(i) > 0 || Math.abs(j) > 0) {
    				int xi = x + i;
    				int yj = y + j;
    				if(isXInBounds(xi) && isYInBounds(yj)) {
    					list.add(getCell(xi, yj));
    				}
    			}
    		}
		}
    	return list;
    }
    
    int getCellsCountAround(int x, int y, int distance, Tile tile, boolean self) {
    	check(x, y);
    	assert(distance > 0);
    	
    	int count = 0;
    	for (int i = -distance; i <= distance; i++) {
    		for (int j = -distance; j <= distance; j++) {
    			if(self || Math.abs(i) > 0 || Math.abs(j) > 0) {
    				int xi = x + i;
    				int yj = y + j;
    				if(isXInBounds(xi) && isYInBounds(yj)) {
    					if(cells[xi][yj] == tile) {
        					count ++;
        				}
    				}
    			}
    		}
		}
    	return count;
    }
    
    int getCellsCountAround2(int x, int y, int distance, Tile tile, boolean self) {
    	check(x, y);
    	assert(distance > 0);
    	
    	int count = 0;
    	for (int i = -distance; i <= distance; i++) {
    		for (int j = -distance; j <= distance; j++) {
    			if(Math.abs(i) == distance && Math.abs(j) == distance) {
    				continue;
    			}
    			if(self || Math.abs(i) > 0 || Math.abs(j) > 0) {
					int xi = x + i;
					int yj = y + j;
					if(isXInBounds(xi) && isYInBounds(yj)) {
						if(cells[xi][yj] == tile) {
	    					count ++;
	    				}
					}
    			}
    		}
		}
    	return count;
    }
    
    Cell randomEdgeCell() {
    	int edge = randomInt(4);
    	switch (edge) {
		case 0:
			return getCell(randomInt(1, xSize -2), ySize - 1);
		case 1:
			return getCell(randomInt(1, xSize -2), 0);
		case 2:
			return getCell(0, randomInt(1, ySize -2));
		case 3:
			return getCell(xSize - 1, randomInt(1, ySize -2));
		default:
			return null;
		}
    }
    
    public void print() {
        for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				switch (cells[x][y]) {
				case Unused:
					System.out.print(" ");
					break;
				case Wall:
					System.out.print("#");
                    break;
                case Floor:
                	System.out.print(".");
                    break;
                case Corridor:
                	System.out.print("_");
                    break;
                case Door:
                	System.out.print("+");
                    break;
                case UpStairs:
                	System.out.print("@");
                    break;
                case DownStairs:
                	System.out.print("^");
                    break;
				default:
					break;
				}
			}
			System.out.println();
		}
    }
    
    protected int randomInt(int n) {
		return random.nextInt(n);
	}
	
    protected int randomInt(int min, int max) {
		assert(min <= max);
		if(min == max) return min;
		int n = random.nextInt(max - min + 1);
		return min + n;
	}

	public int getSeed() {
		return seed;
	}
	
}
