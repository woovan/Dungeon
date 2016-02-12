package com.woovan.game;

import com.woovan.game.enums.Direction;
import com.woovan.game.enums.Tile;

public class Dungeon extends Map {
	
	public static final int MAX_TILES_NUM = 10000;
	 
	public static final int MIN_ROOM_WIDTH = 4;
	public static final int MIN_ROOM_HEIGHT = 4;
	public static final int MAX_ROOM_WIDTH = 8;
	public static final int MAX_ROOM_HEIGHT = 8;
	
	public static final int MIN_CORRIDOR_LEN = 3;
	public static final int MAX_CORRIDOR_LEN = 6;
	
	public static final int DEFAULT_FEATURE_NUM = 1000;
	 
	public static final int MAX_TRY_TIMES = 1000;
	 
	public static final int DEFAULT_CREATE_ROOM_CHANCE = 70;
	 
	
	

	public Dungeon(int seed, int xSize, int ySize) {
		super(seed, xSize, ySize);
	}
	
	public void build() {
		fill(Tile.Unused);
	}
	
	boolean makeRoom(int x, int y, int xLength, int yLength) {
		return true;
	}
	
	boolean makeCorridor(int x, int y, int length, Direction direction) {
		check(x, y);
		int len = randomInt(2, length);
		
		switch (direction) {
		case UP:
			
			break;

		default:
			break;
		}
		return true;
	}
	

}
