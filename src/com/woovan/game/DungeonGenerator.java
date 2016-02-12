package com.woovan.game;

import java.util.Random;

import com.woovan.game.enums.Direction;
import com.woovan.game.enums.Tile;

public class DungeonGenerator {
	
	// 暂时支持的最大的地图块个数
	public static final int MAX_TILES_NUM = 10000;
	 
	// 房间的大小
	public static final int MAX_ROOM_WIDTH = 8;
	public static final int MAX_ROOM_HEIGHT = 8;
	public static final int MIN_ROOM_WIDTH = 4;
	public static final int MIN_ROOM_HEIGHT = 4;
	 
	// 房间和走廊的合计最大个数
	public static final int DEFAULT_FEATURE_NUM = 1000;
	 
	// 尝试生成房间和走廊的测试次数（即步长）
	public static final int MAX_TRY_TIMES = 1000;
	 
	// 默认创建房间的概率（100-该值则为创建走廊的概率）
	public static final int DEFAULT_CREATE_ROOM_CHANCE = 70;
	 
	// 走廊长度
	public static final int MIN_CORRIDOR_LEN = 3;
	public static final int MAX_CORRIDOR_LEN = 6;

	private int m_nSeed;     			// 随机数种子
	private int m_nXSize, m_nYSize;  	// 地图最大宽高 
	private int m_nMaxFeatures;  		// 房间和走廊的最大个数
	private int m_nChanceRoom;   		// 创建房间的概率【0,100】
	private int m_nChanceCorridor;   	// 创建走廊的概率【0,100】 该概率+创建房间的概率应当 = 100
	 
	    //typedef std.mt19937 RngT;
	public DungeonGenerator(int xSize, int ySize, int seed) {
		m_nSeed = seed;
		m_nXSize = xSize;
		m_nYSize = ySize;
		m_nMaxFeatures = DEFAULT_FEATURE_NUM;
		m_nChanceRoom = DEFAULT_CREATE_ROOM_CHANCE;
		m_nChanceCorridor = 100 - m_nChanceRoom;
	}
	 
	Map Generate() {
		assert(m_nMaxFeatures > 0 && m_nMaxFeatures <= DEFAULT_FEATURE_NUM);
		assert(m_nXSize > 3);
		assert(m_nYSize > 3);
		
		Random random = new Random(m_nSeed);
		// step1: 满地图填土
		Map map = new Map(m_nXSize, m_nYSize);
		map.fill(Tile.Unused);
		
		MakeDungeon(map, random);
		
		return map;
	}
	 
	private int getRandomInt(Random random, int min, int max) {
		if(min == max) return min;
		int rangeSize = max - min + 1;
		int ranNum = random.nextInt(rangeSize);
		return min + ranNum;
	}
	
	// 获取随机方向
	Direction getRandomDirection(Random random) {
		return Direction.get(random.nextInt(4) + 1);
	}
	 
	    // 创建走廊
    boolean MakeCorridor(Map map, Random random, int x, int y, int maxLength, Direction direction) {
        assert(x >= 0 && x < m_nXSize);
        assert(y >= 0 && y < m_nYSize);
 
        assert(maxLength > 0 && maxLength <= Math.max(m_nXSize, m_nYSize));
 
        // 设置走廊长度
        int length = getRandomInt(random, MIN_CORRIDOR_LEN, maxLength);
 
        int xStart = x;
        int yStart = y;
 
        int xEnd = x;
        int yEnd = y;
 
        switch (direction) {
		case UP:
			yStart = y - length;
			break;
		case RIGHT:
			xEnd = x + length;
			break;
		case DOWN:
			yEnd = y + length;
			break;
		case LEFT:
			xStart = x - length;
			break;
		default:
			break;
		}
 
        // 检查整个走廊是否在地图内
        if (!map.isXInBounds(xStart) || !map.isXInBounds(xEnd) || 
        		!map.isYInBounds(yStart) || !map.isYInBounds(yEnd))
            return false;
 
        // 检查走廊区域是否有被占用
        if (!map.isAreaUnused(xStart, yStart, xEnd, yEnd))
            return false;
 
        map.setTiles(xStart, yStart, xEnd, yEnd, Tile.Corridor);
        return true;
    }
	 
	    // 创造房间
	boolean MakeRoom(Map map, Random random, int x, int y, int xMaxLength, int yMaxLength, Direction direction) {
		assert( xMaxLength >= MIN_ROOM_WIDTH );
		assert( yMaxLength >= MIN_ROOM_HEIGHT );
 
        // 创建的房间最小是4 * 4，随机出房间大小
        int xLength = getRandomInt(random, MIN_ROOM_WIDTH, xMaxLength);
        int yLength = getRandomInt(random, MIN_ROOM_HEIGHT, yMaxLength);
 
        int xStart = x;
        int yStart = y;
        int xEnd = x;
        int yEnd = y;
        
        switch (direction) {
		case UP:
			yStart = y - yLength;
            xStart = x - xLength / 2;
            xEnd = x + (xLength + 1) / 2;
			break;
		case RIGHT:
			yStart = y - yLength / 2;
            yEnd = y + (yLength + 1) / 2;
            xEnd = x + xLength;
			break;
		case DOWN:
			yEnd = y + yLength;
            xStart = x - xLength / 2;
            xEnd = x + (xLength + 1) / 2;
			break;
		case LEFT:
			yStart = y - yLength / 2;
            yEnd = y + (yLength + 1) / 2;
            xStart = x - xLength;
            break;
		default:
			break;
		}
        // 要保证生成的房间一定四个点都在地图中
        if (!map.isXInBounds(xStart) || !map.isXInBounds(xEnd) ||
        		!map.isYInBounds(yStart) || !map.isYInBounds(yEnd))
            return false;
        // 要保证房间所占用土地未被其他地占用
        if (!map.isAreaUnused(xStart, yStart, xEnd, yEnd))
            return false;
        // 周围种墙
        map.setTiles(xStart, yStart, xEnd, yEnd, Tile.Wall);
        // 房间内铺地板
        map.setTiles(xStart + 1, yStart + 1, xEnd - 1, yEnd - 1, Tile.Floor);
        return true;
	}
	 
	 
	// 创建一个房间或者走廊
	boolean MakeRoomOrCorridor(Map map, Random random, int x, int y, int xmod, int ymod, Direction direction) {
		//  随机选择创建类型（房间或者走廊）
		int chance = getRandomInt(random, 0, 100);
	 
		if (chance <= m_nChanceRoom) {
			// 创建房间
			if (MakeRoom(map, random, x + xmod, y + ymod, MAX_ROOM_WIDTH, MAX_ROOM_HEIGHT, direction)) {
				// 当前位置设置门
				map.setTile(x, y, Tile.Door);
				// 删除门旁边的墙壁，改建为墙壁
				map.setTile(x + xmod, y + ymod, Tile.Floor);
				return true;
			}
			return false;
		} else {
			// 创建走廊
			if (MakeCorridor(map, random, x + xmod, y + ymod, MAX_CORRIDOR_LEN, direction)) {
				// 当前位置设置门
				map.setTile(x, y, Tile.Door);
				return true;
			}
			return false;
		}
	}
	 
	    // 对全地图进行随机处理生成房间和走廊
	boolean MakeRandomFeature(Map map, Random random) {
		for (int tries = 0; tries < MAX_TRY_TIMES; tries++) {
            // 获取一个有意义的地形格
            int x = getRandomInt(random, 1, m_nXSize - 2);
            int y = getRandomInt(random, 1, m_nYSize - 2);
 
            // 获取一个随机墙壁 或者 走廊
            if (map.getTile(x, y) != Tile.Wall && map.getTile(x, y) != Tile.Corridor)
                continue;
 
            // 保证该墙壁和走廊不临接门
            if (map.getCellsCountAround(x, y, 1, Tile.Door, false) > 0)
                continue;
 
            // 找个临接墙壁或者走廊的格子 创建新房间或者走廊
            if (map.getTile(x, y+1) == Tile.Floor || map.getTile(x, y+1) == Tile.Corridor)
            {
                if (MakeRoomOrCorridor(map, random, x, y, 0, -1, Direction.UP))
                    return true;
            }
            else if (map.getTile(x-1, y) == Tile.Floor || map.getTile(x-1, y) == Tile.Corridor)
            {
                if (MakeRoomOrCorridor(map, random, x, y, 1, 0, Direction.RIGHT))
                    return true;
            }
            else if (map.getTile(x, y-1) == Tile.Floor || map.getTile(x, y-1) == Tile.Corridor)
            {
                if (MakeRoomOrCorridor(map, random, x, y, 0, 1, Direction.DOWN))
                    return true;
            }
            else if (map.getTile(x+1, y) == Tile.Floor || map.getTile(x+1, y) == Tile.Corridor)
            {
                if (MakeRoomOrCorridor(map, random, x, y, -1, 0, Direction.LEFT))
                    return true;
            }
		}
		return false;
	}
	 
	// 随机制作出入口
	boolean MakeRandomStairs(Map map, Random random, Tile tile) {
		int tries = 0;
		int maxTries = MAX_TILES_NUM;
	 
        for ( ; tries < maxTries; tries ++) {
            // 随机获取一个非边缘的点
            int x = getRandomInt(random, 1, m_nXSize - 2);
            int y = getRandomInt(random, 1, m_nYSize - 2);
 
            // 如果周围没有地板并且没有走廊（通路）的话，直接放弃
            if (map.getCellsCountAround(x, y, 1, Tile.Floor, false) == 0 && map.getCellsCountAround(x, y, 1, Tile.Corridor, false) == 0)
                continue;
 
            // 周围不允许有门
            if (map.getCellsCountAround(x, y, 1, Tile.Door, false) > 0)
                continue;
 
            map.setTile(x, y, tile);
 
            return true;
        }
        return false;
	}
	 
	    // 随机生成地牢
	boolean MakeDungeon(Map map, Random random) {
		// step2 : 在正中间创建一个房间
		MakeRoom(map, random, m_nXSize / 2, m_nYSize / 2, MAX_ROOM_WIDTH, MAX_ROOM_HEIGHT, getRandomDirection(random));
	 
        for (int features = 1; features < m_nMaxFeatures; features++) {
            if (!MakeRandomFeature(map, random)) {
                System.out.println("生成地牢已满。（当前房间和走廊个数为： " + features + "）.");
                break;
            }
        }
 
        // 创建随机入口点
        if (!MakeRandomStairs(map, random, Tile.UpStairs))
            System.out.println("创建入口点失败！");
 
        // 创建随机出口点
        if (!MakeRandomStairs(map, random, Tile.DownStairs))
        	System.out.println("创建出口点失败！");
        return true;
	}
	 
	static void FlushReadme() {
		System.out.println("=================================");
		System.out.println(" < 表示入口 ");
		System.out.println(" > 表示出口 ");
		System.out.println(" _ 下划线表示地板 ");
		System.out.println(" # 表示墙壁 ");
		System.out.println(" . 点表示走廊 ");
		System.out.println(" + 表示门 ");
		System.out.println("纯黑表示啥都没有，是障碍");
		System.out.println("=================================");
	}
	
	public static void main(String[] args) {
		FlushReadme();
		 
	    DungeonGenerator pGenerator = new DungeonGenerator(100, 100, 100);
	    Map map = pGenerator.Generate();
	    map.print();
	 
	}
	 
}
