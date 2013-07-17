package com.slamdunk.quester2.puzzle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
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
		// Ajout du match d'un attribut vers le même attribut
		for (PuzzleAttributes attribute : PuzzleAttributes.values()) {
			addMatchables(attribute, attribute);
		}
		
		// Création de la table des recettes d'alignement
		ALIGNMENT_EFFECTS = new HashMap<String, PuzzleMatchEffect>();
		AttributeAlignmentEffect attributeAlignmentEffect = new AttributeAlignmentEffect();
		for (PuzzleAttributes attribute : PuzzleAttributes.values()) {
			addAlignmentEffect(attribute, attributeAlignmentEffect);
		}
	}
	
	public static boolean areMatchable(PuzzleAttributes attribute1, PuzzleAttributes attribute2) {
		Boolean matchable = MATCHABLES.get(attribute1, attribute2);
		return matchable != null && matchable.booleanValue();
	}
	
	private static void addAlignmentEffect(PuzzleAttributes attribute, AttributeAlignmentEffect alignmentEffect) {
		// Ajout des combinaisons de 3 items
		Map<PuzzleAttributes, Integer> recipe = new HashMap<PuzzleAttributes, Integer>();
		recipe.put(attribute, 3);
		ALIGNMENT_EFFECTS.put(PuzzleMatchEffect.buildRecipe(recipe), alignmentEffect);
		
		// Ajout des combinaisons de 4 items
		recipe.clear();
		recipe.put(attribute, 4);
		ALIGNMENT_EFFECTS.put(PuzzleMatchEffect.buildRecipe(recipe), alignmentEffect);
		
		// Ajout des combinaisons de 5 items
		recipe.clear();
		recipe.put(attribute, 5);
		ALIGNMENT_EFFECTS.put(PuzzleMatchEffect.buildRecipe(recipe), alignmentEffect);
	}

	private static void addMatchables(PuzzleAttributes attribute1, PuzzleAttributes attribute2) {
		MATCHABLES.put(attribute1, attribute2, Boolean.TRUE);
		MATCHABLES.put(attribute2, attribute1, Boolean.TRUE);
	}
	
	public static PuzzleMatchEffect getMatchEffect(List<PuzzleAttributes> attributes) {
		// Récupération de l'effet correspondant à cette combinaison d'attributs
		String recipe = PuzzleMatchEffect.buildRecipe(attributes);
		PuzzleMatchEffect effect = ALIGNMENT_EFFECTS.get(recipe);
		return effect;
	}

	public static PuzzleAttributes getRandomBaseAttribute() {
		return BASE_ATTRIBUTES[MathUtils.random(BASE_ATTRIBUTES.length - 1)];
	}
}
