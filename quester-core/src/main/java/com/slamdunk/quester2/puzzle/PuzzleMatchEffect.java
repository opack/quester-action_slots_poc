package com.slamdunk.quester2.puzzle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Repr�sente un effet d'alignement
 */
public abstract class PuzzleMatchEffect {
	/**
	 * Ordre d'apparition du nombre d'�l�ments requis dans le code
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
		// Compte les �l�ments par type
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
		
		// Cr�e le code
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
