package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.Borders;
import com.slamdunk.quester.model.map.MapElements;

public class PathData extends ObstacleData {
	public Borders border;
	public boolean isCrossable;
	public int areaX;
	public int areaY;
	public int arrivalCol;
	public int arrivalRow;
	
	public PathData(MapElements pathType, Borders border, int areaX, int areaY, int arrivalCol, int arrivalRow) {
		super(pathType);
		this.border = border;
		this.areaX = areaX;
		this.areaY = areaY;
		this.arrivalCol = arrivalCol;
		this.arrivalRow = arrivalRow;
		isCrossable = true;
	}
}
