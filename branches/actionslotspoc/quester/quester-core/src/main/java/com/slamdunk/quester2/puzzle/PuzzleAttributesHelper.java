package com.slamdunk.quester2.puzzle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slamdunk.quester.model.data.DoubleEntryArray;

public class PuzzleAttributesHelper {
	public static final PuzzleAttributes[] BASE_ATTRIBUTES = new PuzzleAttributes[]{
		PuzzleAttributes.CONSTITUTION,
		PuzzleAttributes.DEXTERITY,
		PuzzleAttributes.FOCUS,
		PuzzleAttributes.LUCK,
		PuzzleAttributes.STRENGTH,
		PuzzleAttributes.WILL
	};
	
	/**
	 * Tableau à double entrée indiquant si un couple de PuzzleAttributes est matchable
	 */
	private static final DoubleEntryArray<PuzzleAttributes, Boolean> MATCHABLES;
	/**
	 * Table associant une recette d'attributs à un effet d'alignement
	 */
	private static final Map<String, PuzzleMatchEffect> ALIGNMENT_EFFECTS;
	
	static {
		// Création de la matrice d'items matchables
		MATCHABLES = new DoubleEntryArray<PuzzleAttributes, Boolean>();
		addMatchables(PuzzleAttributes.CONSTITUTION, PuzzleAttributes.CONSTITUTION);
		addMatchables(PuzzleAttributes.DEXTERITY, PuzzleAttributes.DEXTERITY);
		addMatchables(PuzzleAttributes.FOCUS, PuzzleAttributes.FOCUS);
		addMatchables(PuzzleAttributes.LUCK, PuzzleAttributes.LUCK);
		addMatchables(PuzzleAttributes.STRENGTH, PuzzleAttributes.STRENGTH);
		addMatchables(PuzzleAttributes.WILL, PuzzleAttributes.WILL);
		
		// Création de la table des recettes d'alignement
		ALIGNMENT_EFFECTS = new HashMap<String, PuzzleMatchEffect>();
		ALIGNMENT_EFFECTS.put(
			PuzzleMatchEffect.buildRecipe(PuzzleAttributes.STRENGTH, PuzzleAttributes.STRENGTH, PuzzleAttributes.STRENGTH),
			new StrengthAlignmentEffect());
	}
	
	public static boolean areMatchable(PuzzleAttributes element1, PuzzleAttributes element2) {
		Boolean matchable = MATCHABLES.get(element1, element2);
		return matchable != null && matchable.booleanValue();
	}
	
	private static void addMatchables(PuzzleAttributes element1, PuzzleAttributes element2) {
		MATCHABLES.put(element1, element2, Boolean.TRUE);
		MATCHABLES.put(element2, element1, Boolean.TRUE);
	}
	
	public static PuzzleMatchEffect getMatchEffect(List<PuzzleAttributes> elements) {
		String recipe = PuzzleMatchEffect.buildRecipe(elements);
		return ALIGNMENT_EFFECTS.get(recipe);
	}
}
