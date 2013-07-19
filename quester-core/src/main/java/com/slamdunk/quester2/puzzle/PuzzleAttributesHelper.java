package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.DoubleEntryArray;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AlignmentData;

public class PuzzleAttributesHelper {
	/**
	 * Liste les attributs de base
	 */
	public static final List<PuzzleAttributes> BASE_ATTRIBUTES;
	/**
	 * Tableau à double entrée indiquant l'attribut Super pour un PuzzleAttributes 
	 * et une orientation (true = horizontal) donnés.
	 */
	public static final DoubleEntryArray<PuzzleAttributes, AttributeOrientation, PuzzleAttributes> SUPER_ATTRIBUTES;
	/**
	 * Tableau à double entrée indiquant si un couple de PuzzleAttributes est matchable
	 */
	private static final DoubleEntryArray<PuzzleAttributes, PuzzleAttributes, Boolean> MATCHABLES;
	/**
	 * Table associant une recette d'attributs à un effet d'alignement
	 */
	private static final Map<String, PuzzleMatchEffect> ALIGNMENT_EFFECTS;
	
	static {
		// Création du tableau d'attributs de base et super
		BASE_ATTRIBUTES = new ArrayList<PuzzleAttributes>();
		
		// Création de la matrice d'items matchables
		MATCHABLES = new DoubleEntryArray<PuzzleAttributes, PuzzleAttributes, Boolean>();
		SUPER_ATTRIBUTES = new DoubleEntryArray<PuzzleAttributes, AttributeOrientation, PuzzleAttributes>();
		ALIGNMENT_EFFECTS = new HashMap<String, PuzzleMatchEffect>();
		AttributeAlignmentEffect attributeAlignmentEffect = new AttributeAlignmentEffect();
		for (PuzzleAttributes attribute : PuzzleAttributes.values()) {
			// Rien à faire avec EMPTY
			if (attribute == PuzzleAttributes.EMPTY) {
				continue;
			}
			
			// Ajout du match d'un attribut vers le même attribut.
			addMatchables(attribute, attribute);
			
			if (attribute.getType() == AttributeTypes.BASE) {
				// Ajout de l'attribut aux attributs de base
				BASE_ATTRIBUTES.add(attribute);
				// Création des recette provoquant un simple alignement d'attributs
				addAlignmentEffect(attribute, attributeAlignmentEffect);
			}
			// Ajout du match d'un super vers son attribut de base
			// Remplissage de la matrice indiquant quel super est créé à partir d'un attribut de base
			else if (attribute.getType() == AttributeTypes.SUPER) {
				addMatchables(attribute, attribute.getBaseAttribute());
				SUPER_ATTRIBUTES.put(attribute.getBaseAttribute(), attribute.getOrientation(), attribute);
			}
		}
		
		// Création des recettes pour l'échange entre eux de 2 attributs super
		// ...
		// Création des recettes pour l'échange d'un hyper avec n'importe quoi
		// ...
	}
	
	public static boolean areMatchable(PuzzleAttributes attribute1, PuzzleAttributes attribute2) {
		Boolean matchable = MATCHABLES.get(attribute1, attribute2);
		return matchable != null && matchable.booleanValue();
	}
	
	private static void addAlignmentEffect(PuzzleAttributes attribute, AttributeAlignmentEffect alignmentEffect) {
		Map<PuzzleAttributes, Integer> recipe = new HashMap<PuzzleAttributes, Integer>();
		for (int nbAttributesInRecipe = 3; nbAttributesInRecipe <= 5; nbAttributesInRecipe++) {
			recipe.clear();
			recipe.put(attribute, nbAttributesInRecipe);
			ALIGNMENT_EFFECTS.put(PuzzleMatchEffect.buildRecipe(recipe), alignmentEffect);
		}
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
		int random = MathUtils.random(BASE_ATTRIBUTES.size() - 1);
		return BASE_ATTRIBUTES.get(random);
	}

	public static PuzzleAttributes getSuper(AlignmentData alignment) {
		PuzzleAttributes attribute = alignment.attributes.get(alignment.alignSourceAttributeIndex);
		return SUPER_ATTRIBUTES.get(attribute, alignment.orientation);
	}
}
