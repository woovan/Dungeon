package com.woovan.game;

import java.util.Random;

import com.woovan.game.enums.Tile;

public class Cave extends Map {
	
	public static final float WALL_RATE = 0.40f;
	
	
	public Cave(int seed, int xSize, int ySize) {
		super(seed, xSize, ySize);
	}

	public void build() {
		randomFill(WALL_RATE, Tile.Wall, Tile.Floor);
		fillEdge(Tile.Wall);
		
		randomChange(4);
		findEntrance();
	}
	
	void randomChange(int n) {
		for (int i = 0; i < n; i++) {
			for (int y = 1; y < ySize - 1; y++) {
				for (int x = 1; x < xSize - 1; x++) {
					int w1count = getCellsCountAround(x, y, 1, Tile.Wall, true);
					int w2count = getCellsCountAround2(x, y, 2, Tile.Wall, true);
					if(w1count >= 5 || w2count <= 4) {
						setTile(x, y, Tile.Wall);
					} else {
						setTile(x, y, Tile.Floor);
					}
				}
			}
		}
	}
	
	Cell findEntrance() {
		int count = 0;
		Cell entrance = null;
		do {
			entrance = randomEdgeCell();
			count ++;
		} while (entrance.getTile() != Tile.Floor &&  count < 100);
		entrance.setTile(Tile.UpStairs);
		return entrance;
	}
	
	public static void main(String[] args) {
		int seed = new Random().nextInt(100);
		System.out.println("seed:" + seed);
		Cave maze = new Cave(seed, 30, 30);
		maze.build();
		maze.print();
	}
}
