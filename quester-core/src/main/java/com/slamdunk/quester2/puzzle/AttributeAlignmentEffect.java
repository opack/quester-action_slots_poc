package com.slamdunk.quester2.puzzle;

import java.util.Map;

import com.slamdunk.quester.model.points.Point;

/**
 * Ajoute une quantité d'attribut force aux actions courantes
 */
public class AttributeAlignmentEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleStage puzzle, Map<Point, PuzzleAttributes> alignment) {
		PuzzleAttributes attribute = null;
		Point pos;
		for (Map.Entry<Point, PuzzleAttributes> align : alignment.entrySet()) {
			// Suppression des éléments alignés
			pos = align.getKey();
			puzzle.removeAttribute(pos.getX(), pos.getY());
			
			// Pour chaque attribut, s'il est simple on ajoute des points,
			// s'il est super on va déclencher un autre effet.
			attribute = align.getValue();
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD " + attribute);
			
			// Fait tomber les attributs
			puzzle.fall();
		}
		
		// Si on avait plus de 3 éléments alignés, on ajoute un bonus.
		if (alignment.size() == 4) {
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS4 " + attribute);
		} else if (alignment.size() == 5) {
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + attribute);
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + attribute);
		}
	}
}
