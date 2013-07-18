package com.slamdunk.quester2.puzzle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.DoubleEntryArray;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AlignmentData;

public class PuzzleAttributesHelper {
	public static final PuzzleAttributes[] BASE_ATTRIBUTES;
	public static final Map<PuzzleAttributes, PuzzleAttributes> SUPER_ATTRIBUTES_H;
	public static final Map<PuzzleAttributes, PuzzleAttributes> SUPER_ATTRIBUTES_V;
	
	/**
	 * Tableau à double entrée indiquant si un couple de PuzzleAttributes est matchable
	 */
	private static final DoubleEntryArray<PuzzleAttributes, Boolean> MATCHABLES;
	/**
	 * Table associant une recette d'attributs à un effet d'alignement
	 */
	private static final Map<String, PuzzleMatchEffect> ALIGNMENT_EFFECTS;
	
	static {
		// Création du tableau d'attributs de base
		BASE_ATTRIBUTES = new PuzzleAttributes[]{
			PuzzleAttributes.CONSTITUTION,
			PuzzleAttributes.DEXTERITY,
			PuzzleAttributes.FOCUS,
			PuzzleAttributes.LUCK,
			PuzzleAttributes.STRENGTH,
			PuzzleAttributes.WILL
		};
		
		// Création des tables d'attributs super
		SUPER_ATTRIBUTES_H = new HashMap<PuzzleAttributes, PuzzleAttributes>();
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.CONSTITUTION, PuzzleAttributes.SUPER_CONSTITUTION_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.SUPER_CONSTITUTION_H, PuzzleAttributes.SUPER_CONSTITUTION_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.DEXTERITY, PuzzleAttributes.SUPER_DEXTERITY_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.SUPER_DEXTERITY_H, PuzzleAttributes.SUPER_DEXTERITY_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.FOCUS, PuzzleAttributes.SUPER_FOCUS_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.SUPER_FOCUS_H, PuzzleAttributes.SUPER_FOCUS_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.LUCK, PuzzleAttributes.SUPER_LUCK_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.SUPER_LUCK_H, PuzzleAttributes.SUPER_LUCK_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.STRENGTH, PuzzleAttributes.SUPER_STRENGTH_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.SUPER_STRENGTH_H, PuzzleAttributes.SUPER_STRENGTH_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.WILL, PuzzleAttributes.SUPER_WILL_H);
		SUPER_ATTRIBUTES_H.put(PuzzleAttributes.SUPER_WILL_H, PuzzleAttributes.SUPER_WILL_H);
		SUPER_ATTRIBUTES_V = new HashMap<PuzzleAttributes, PuzzleAttributes>();
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.CONSTITUTION, PuzzleAttributes.SUPER_CONSTITUTION_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.SUPER_CONSTITUTION_V, PuzzleAttributes.SUPER_CONSTITUTION_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.DEXTERITY, PuzzleAttributes.SUPER_DEXTERITY_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.SUPER_DEXTERITY_V, PuzzleAttributes.SUPER_DEXTERITY_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.FOCUS, PuzzleAttributes.SUPER_FOCUS_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.SUPER_FOCUS_V, PuzzleAttributes.SUPER_FOCUS_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.LUCK, PuzzleAttributes.SUPER_LUCK_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.SUPER_LUCK_V, PuzzleAttributes.SUPER_LUCK_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.STRENGTH, PuzzleAttributes.SUPER_STRENGTH_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.SUPER_STRENGTH_V, PuzzleAttributes.SUPER_STRENGTH_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.WILL, PuzzleAttributes.SUPER_WILL_V);
		SUPER_ATTRIBUTES_V.put(PuzzleAttributes.SUPER_WILL_V, PuzzleAttributes.SUPER_WILL_V);
		
		// Création de la matrice d'items matchables
		// Ajout du match d'un attribut vers le même attribut. En revanche, EMPTY n'est pas matchable
		MATCHABLES = new DoubleEntryArray<PuzzleAttributes, Boolean>();
		for (PuzzleAttributes attribute : PuzzleAttributes.values()) {
			addMatchables(attribute, attribute);
		}
		MATCHABLES.put(PuzzleAttributes.EMPTY, PuzzleAttributes.EMPTY, Boolean.FALSE);
		
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
	
	public static PuzzleMatchEffect getMatchEffect(Collection<PuzzleAttributes> attributes) {
		// Récupération de l'effet correspondant à cette combinaison d'attributs
		String recipe = PuzzleMatchEffect.buildRecipe(attributes);
		PuzzleMatchEffect effect = ALIGNMENT_EFFECTS.get(recipe);
		return effect;
	}

	public static PuzzleAttributes getRandomBaseAttribute() {
		return BASE_ATTRIBUTES[MathUtils.random(BASE_ATTRIBUTES.length - 1)];
	}

	public static PuzzleAttributes getSuper(AlignmentData alignment) {
		PuzzleAttributes attribute = alignment.attributes.get(0);
		PuzzleAttributes superAttribute;
		if (alignment.isHorizontal) {
			superAttribute = SUPER_ATTRIBUTES_H.get(attribute);
		} else {
			superAttribute = SUPER_ATTRIBUTES_V.get(attribute);
		}
		return superAttribute;
	}
}
