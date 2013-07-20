package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AttributeData;

/**
 * Ajoute une quantit� d'attribut force aux actions courantes
 */
public class CrossMatchEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleMatchData matchData) {
		// D�clenchement des attributs de base
		for (AttributeData data : matchData.getAttributes()) {
			eat(data.position, data.attribute, true);
		}
		
		// Chute des attributs restants
		int count = matchData.count();
		if (count > 0) {
			puzzle.fall();
		}
		
		// Ajout d'un petit bonus
		System.out.println("DBG CrossMatchEffect.perform() ADD BONUS CROSS " + matchData.getSource().attribute);

		// Cr�ation d'un super attribut
		PuzzleAttributes superAttribute = PuzzleAttributesHelper.getSuperAttribute(matchData.getSource().attribute, matchData.getOrientation());
		if (superAttribute != null) {
			Point sourcePos = matchData.getSource().position;
			puzzle.createAttribute(sourcePos.getX(), sourcePos.getY(), superAttribute);
		}
	}
}
