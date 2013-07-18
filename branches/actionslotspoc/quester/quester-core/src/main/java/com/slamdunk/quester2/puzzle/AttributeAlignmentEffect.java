package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AlignmentData;

/**
 * Ajoute une quantité d'attribut force aux actions courantes
 */
public class AttributeAlignmentEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleStage puzzle, AlignmentData alignment) {
		PuzzleAttributes attribute = null;
		Point pos;
		for (int cur = 0; cur < alignment.size(); cur ++) {
			// Suppression des éléments alignés
			pos = alignment.positions.get(cur);
			puzzle.removeAttribute(pos.getX(), pos.getY());
			
			// Pour chaque attribut, s'il est simple on ajoute des points,
			// s'il est super on va déclencher un autre effet.
			attribute = alignment.attributes.get(cur);
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD " + attribute);
		}
		
		int count = alignment.size();
		if (count > 0) {
			// Fait tomber les attributs
			puzzle.fall();
		}
		
		// Si on avait plus de 3 éléments alignés, on ajoute un bonus.
		if (count == 4) {
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS4 " + attribute);
			// Création d'un super attribut
			PuzzleAttributes superAttribute = PuzzleAttributesHelper.getSuper(alignment);
			Point sourcePos = alignment.positions.get(alignment.alignSourceAttributeIndex);
			puzzle.createAttribute(sourcePos.getX(), sourcePos.getY(), superAttribute);
		} else if (alignment.size() == 5) {
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + attribute);
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + attribute);
			// Création d'un hyper attribut
			// ...
		}
	}
}
