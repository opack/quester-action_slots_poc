package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.DoubleEntryArray;

public class PuzzleAttributesHelper {
	/**
	 * Liste les attributs de base
	 */
	public static final List<PuzzleAttributes> BASE_ATTRIBUTES;
	/**
	 * Tableau à double entrée indiquant l'attribut Super pour un PuzzleAttributes 
	 * et une orientation (true = horizontal) donnés.
	 */
	public static final DoubleEntryArray<PuzzleAttributes, AlignmentOrientation, PuzzleAttributes> SUPER_ATTRIBUTES;
	/**
	 * Tableau à double entrée indiquant si un couple de PuzzleAttributes est matchable
	 */
	private static final DoubleEntryArray<PuzzleAttributes, PuzzleAttributes, Boolean> MATCHABLES;
	
	static {
		// Création du tableau d'attributs de base et super
		BASE_ATTRIBUTES = new ArrayList<PuzzleAttributes>();
		
		// Création de la matrice d'items matchables
		MATCHABLES = new DoubleEntryArray<PuzzleAttributes, PuzzleAttributes, Boolean>();
		SUPER_ATTRIBUTES = new DoubleEntryArray<PuzzleAttributes, AlignmentOrientation, PuzzleAttributes>();
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
			}
			// Ajout du match d'un super vers son attribut de base
			// Remplissage de la matrice indiquant quel super est créé à partir d'un attribut de base
			else if (attribute.getType() == AttributeTypes.SUPER) {
				addMatchables(attribute, attribute.getBaseAttribute());
				SUPER_ATTRIBUTES.put(attribute.getBaseAttribute(), attribute.getOrientation(), attribute);
			}
		}
	}
	
	public static boolean areMatchable(PuzzleAttributes attribute1, PuzzleAttributes attribute2) {
		Boolean matchable = MATCHABLES.get(attribute1, attribute2);
		return matchable != null && matchable.booleanValue();
	}

	private static void addMatchables(PuzzleAttributes attribute1, PuzzleAttributes attribute2) {
		MATCHABLES.put(attribute1, attribute2, Boolean.TRUE);
		MATCHABLES.put(attribute2, attribute1, Boolean.TRUE);
	}

	public static PuzzleAttributes getRandomBaseAttribute() {
		int random = MathUtils.random(BASE_ATTRIBUTES.size() - 1);
		return BASE_ATTRIBUTES.get(random);
	}

	public static PuzzleAttributes getSuper(PuzzleAttributes attribute, AlignmentOrientation orientation) {
		return SUPER_ATTRIBUTES.get(attribute, orientation);
	}
}
