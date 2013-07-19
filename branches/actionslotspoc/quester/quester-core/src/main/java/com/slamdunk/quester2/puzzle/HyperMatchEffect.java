package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester2.puzzle.PuzzleLogic.AttributeData;

/**
 * Ajoute une quantit� d'attribut force aux actions courantes
 */
public class HyperMatchEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleMatchData matchData) {
		// D�clenchement des attributs de base
		AttributeData otherData = null;
		for (AttributeData data : matchData.getAttributes()) {
			eat(data.position, data.attribute, true);
			if (data.attribute != PuzzleAttributes.HYPER || otherData == null) {
				otherData = data;
			}
		}
		
		// D�clenchement de l'effet bonus
		performHyperBonus(otherData.position.getX(), otherData.position.getY(), otherData.attribute);
		
		// Chute des attributs restants
		int count = matchData.count();
		if (count > 0) {
			puzzle.fall();
		}
	}
}
