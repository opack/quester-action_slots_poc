package com.slamdunk.quester2.puzzle;

import java.util.List;

import com.slamdunk.quester.model.points.Point;

/**
 * Ajoute une quantit� d'attribut force aux actions courantes
 */
public class AttributeAlignmentEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleStage puzzle, List<Point> positions) {
		PuzzleAttributes attribute = null;
		for (Point pos : positions) {
			// Suppression des �l�ments align�s
			attribute = puzzle.removeAttribute(pos.getX(), pos.getY());
			
			// Pour chaque attribut, s'il est simple on ajoute des points,
			// s'il est super on va d�clencher un autre effet.
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD " + attribute);
		}
		
		// Si on avait plus de 3 �l�ments align�s, on ajoute un bonus.
		if (positions.size() == 4) {
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS4 " + attribute);
		} else if (positions.size() == 5) {
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + attribute);
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + attribute);
		}
	}
}
