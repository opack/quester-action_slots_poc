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
	 * et une orientation donnés.
	 */
	public static final DoubleEntryArray<PuzzleAttributes, AlignmentOrientation, PuzzleAttributes> SUPER_ATTRIBUTES;
	
	static {
		BASE_ATTRIBUTES = new ArrayList<PuzzleAttributes>();
		SUPER_ATTRIBUTES = new DoubleEntryArray<PuzzleAttributes, AlignmentOrientation, PuzzleAttributes>();
		for (PuzzleAttributes attribute : PuzzleAttributes.values()) {
			// Rien à faire avec EMPTY
			if (attribute == PuzzleAttributes.EMPTY) {
				continue;
			}
			
			if (attribute.getType() == AttributeTypes.BASE) {
				// Ajout de l'attribut aux attributs de base
				BASE_ATTRIBUTES.add(attribute);
			}
			// Ajout du match d'un super vers son attribut de base
			// Remplissage de la matrice indiquant quel super est créé à partir d'un attribut de base
			else if (attribute.getType() == AttributeTypes.SUPER) {
				SUPER_ATTRIBUTES.put(attribute.getBaseAttribute(), attribute.getOrientation(), attribute);
			}
		}
	}
	
	/**
	 * Vérifie que 2 attributs sont matchables. Si ces attributs sont des supers, on tente
	 * de matcher leur type de base.
	 */
	public static boolean areMatchable(PuzzleAttributes attribute1, PuzzleAttributes attribute2) {
		// EMPTY n'est jamais matchable
		if (attribute1 == PuzzleAttributes.EMPTY || attribute2 == PuzzleAttributes.EMPTY) {
			return false;
		}
		return getBaseAttribute(attribute1) == getBaseAttribute(attribute2);
	}

	public static PuzzleAttributes getRandomBaseAttribute() {
		int random = MathUtils.random(BASE_ATTRIBUTES.size() - 1);
		return BASE_ATTRIBUTES.get(random);
	}

	public static PuzzleAttributes getSuperAttribute(PuzzleAttributes attribute, AlignmentOrientation orientation) {
		PuzzleAttributes baseAttribute = getBaseAttribute(attribute);
		return SUPER_ATTRIBUTES.get(baseAttribute, orientation);
	}

	public static PuzzleAttributes getBaseAttribute(PuzzleAttributes attribute) {
		PuzzleAttributes baseAttribute = attribute.getBaseAttribute();
		if (baseAttribute == null) {
			return attribute;
		}
		return baseAttribute;
	}
}
