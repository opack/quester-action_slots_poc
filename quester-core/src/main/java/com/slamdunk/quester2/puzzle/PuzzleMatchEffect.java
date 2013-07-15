package com.slamdunk.quester2.puzzle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Représente un effet d'alignement
 */
public abstract class PuzzleMatchEffect {
	/**
	 * Ordre d'apparition du nombre d'éléments requis dans le code
	 */
	private static final PuzzleAttributes[] ELEMENTS_ORDER_IN_CODE = {
		PuzzleAttributes.CONSTITUTION,
		PuzzleAttributes.DEXTERITY,
		PuzzleAttributes.FOCUS,
		PuzzleAttributes.LUCK,
		PuzzleAttributes.STRENGTH,
		PuzzleAttributes.WILL
	};
	
	protected static String buildRecipe(PuzzleAttributes... elements) {
		return buildRecipe(Arrays.asList(elements));
	}
	
	protected static String buildRecipe(Iterable<PuzzleAttributes> elements) {
		// Compte les éléments par type
		Map<PuzzleAttributes, Integer> count = new HashMap<PuzzleAttributes, Integer>();
		Integer curCount;
		for (PuzzleAttributes element : elements) {
			curCount = count.get(element);
			if (curCount == null) {
				count.put(element, 1);
			} else {
				count.put(element, curCount + 1);
			}
		}
		
		// Crée le code
		StringBuilder sb = new StringBuilder();
		for (PuzzleAttributes element : ELEMENTS_ORDER_IN_CODE) {
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
