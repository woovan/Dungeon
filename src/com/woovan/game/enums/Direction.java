package com.woovan.game.enums;

public enum Direction {
	
	UP(0),   	
	RIGHT(1),  
	DOWN(2),    
	LEFT(3),
    UP_LEFT(4),
    UP_RIGHT(5),
    DOWN_RIGHT(6),
    DOWN_LEFT(7)
    ;
	
	private int code;
	
	private Direction(int code) {
		this.code = code;
	}

	public static Direction get(int code) {
		for (Direction direction : values()) {
			if(direction.code == code) {
				return direction;
			}
		}
		return null;
	}
	
}
