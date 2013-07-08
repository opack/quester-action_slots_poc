package com.slamdunk.quester.logic.controlers;

import java.util.HashMap;
import java.util.Map;

import com.slamdunk.quester.model.map.Borders;

public class Neighbors {
	/**
	 * Les 8 voisins autour
	 */
	public static int[][] NEIGHBORS_ALL = new int[][]{
		new int[]{-1, -1},
		new int[]{0, -1},
		new int[]{+1, -1},
		new int[]{-1, 0},
		new int[]{+1, 0},
		new int[]{-1, +1},
		new int[]{0, +1},
		new int[]{+1, +1},
	};
	
	/**
	 * Voisins en "+" : au-dessus, en-dessous, à gauche et à droite.
	 */
	public static Map<Borders, int[]> NEIGHBORS_PLUS;
	
	static {
		NEIGHBORS_PLUS = new HashMap<Borders, int[]>();
		NEIGHBORS_PLUS.put(Borders.TOP, new int[]{0, +1});
		NEIGHBORS_PLUS.put(Borders.BOTTOM, new int[]{0, -1});
		NEIGHBORS_PLUS.put(Borders.LEFT, new int[]{-1, 0});
		NEIGHBORS_PLUS.put(Borders.RIGHT, new int[]{+1, 0});
	}
}
