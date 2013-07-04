package com.slamdunk.quester.model.data;

import static com.slamdunk.quester.model.map.MapElements.CASTLE;

import com.slamdunk.quester.model.points.Point;

public class CastleData extends ObstacleData {
	public int difficulty;
	public int dungeonHeight;
	public int dungeonWidth;
	public Point roomMinSize;
	public Point roomMaxSize;
	
	public CastleData(int dungeonWidth, int dungeonHeight, Point roomMinSize, Point roomMaxSize, int difficulty) {
		super(CASTLE);
		this.dungeonWidth = dungeonWidth;
		this.dungeonHeight = dungeonHeight;
		this.roomMinSize = roomMinSize;
		this.roomMaxSize = roomMaxSize;
		this.difficulty = difficulty;
	}
}
