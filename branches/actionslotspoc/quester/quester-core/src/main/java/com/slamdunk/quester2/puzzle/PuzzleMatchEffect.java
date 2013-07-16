package com.slamdunk.quester2.puzzle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
		Map<PuzzleAttributes, Integer> counts = new HashMap<PuzzleAttributes, Integer>();
		Integer curCount;
		for (PuzzleAttributes element : elements) {
			curCount = counts.get(element);
			if (curCount == null) {
				counts.put(element, 1);
			} else {
				counts.put(element, curCount + 1);
			}
		}
		return buildRecipe(counts);
	}

	protected static String buildRecipe(Map<PuzzleAttributes, Integer> elements) {
		// Cr�e le code
		Integer curCount;
		StringBuilder sb = new StringBuilder();
		for (PuzzleAttributes element : ELEMENTS_ORDER_IN_CODE) {
			curCount = elements.get(element);
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
	public abstract void perform(List<PuzzleAttributes> attributes);
}
