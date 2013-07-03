package com.slamdunk.quester.model.map;

import java.util.HashMap;
import java.util.Map;

public enum MapLevels {
	GROUND(0),
	OBJECTS(1),
	CHARACTERS(2),
	FOG(3),	
	OVERLAY(4);
	
	private static final Map<Integer, MapLevels> LEVELS;
	static {
		LEVELS = new HashMap<Integer, MapLevels>();
		for (MapLevels curLevel : values()) {
			LEVELS.put(curLevel.level, curLevel);
		}
	}
	
	public final int level;
	
	private MapLevels(int level) {
		this.level = level;
	}

	public static int count() {
		return LEVELS.size();
	}

	public static MapLevels getLevel(int level) {
		return LEVELS.get(level);
	}
	
}
