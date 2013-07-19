package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AttributeData;

/**
 * Ajoute une quantité d'attribut force aux actions courantes
 */
public class SupersMatchEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleMatchData matchData) {
		// Déclenchement des attributs de base
		Point firstPos = null;
		Point secondPos = null;
		for (AttributeData data : matchData.getAttributes()) {
			eat(data.position, data.attribute, true);
			if (firstPos == null) {
				firstPos = data.position;
			} else if (secondPos == null) {
				secondPos = data.position;
			}
		}
		
		// Déclenchement du bonus
		performDoubleSupersBonus(firstPos, secondPos);
		
		// Chute des attributs restants
		int count = matchData.count();
		if (count > 0) {
			puzzle.fall();
		}
		
		// Ajout d'un petit bonus
		System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS CROSS " + matchData.getSource().attribute);
	}
}
