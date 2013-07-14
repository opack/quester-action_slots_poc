package com.slamdunk.quester.model.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.slamdunk.quester.model.map.MapElements;

/**
 * Représente un effet d'alignement
 */
public abstract class AlignmentEffect {
	/**
	 * Ordre d'apparition du nombre d'éléments requis dans le code
	 */
	private static final MapElements[] ELEMENTS_ORDER_IN_CODE = {
		MapElements.SWORD,
		MapElements.SHIELD,
		MapElements.STAR,
		MapElements.CHEST,
		MapElements.HEAL,
		MapElements.ENNEMY
	};
	
	protected static String buildRecipe(MapElements... elements) {
		return buildRecipe(Arrays.asList(elements));
	}
	
	protected static String buildRecipe(Iterable<MapElements> elements) {
		// Compte les éléments par type
		Map<MapElements, Integer> count = new HashMap<MapElements, Integer>();
		Integer curCount;
		for (MapElements element : elements) {
			curCount = count.get(element);
			if (curCount == null) {
				count.put(element, 1);
			} else {
				count.put(element, curCount + 1);
			}
		}
		
		// Crée le code
		StringBuilder sb = new StringBuilder();
		for (MapElements element : ELEMENTS_ORDER_IN_CODE) {
			curCount = count.get(element);
			if (curCount == null) {
				sb.append(0);
			} else {
				sb.append(curCount);
			}
		}
		
		return sb.toString();
	}

	/**
	 * Effectue des choses en fonction de l'effet
	 */
	public abstract void perform();
}
