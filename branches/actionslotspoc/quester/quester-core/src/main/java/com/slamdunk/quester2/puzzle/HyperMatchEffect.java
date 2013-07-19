package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AttributeData;

/**
 * Ajoute une quantité d'attribut force aux actions courantes
 */
public class HyperMatchEffect extends PuzzleMatchEffect {

	@Override
	public void perform(PuzzleMatchData matchData) {
		System.out.println("DBG HyperMatchEffect.perform()");
		
		// Déclenchement des attributs de base
		PuzzleAttributes baseAttribute = PuzzleAttributes.EMPTY;
		for (AttributeData data : matchData.getAttributes()) {
			eat(data.position, data.attribute, true);
			if (data.attribute != PuzzleAttributes.HYPER) {
				baseAttribute = PuzzleAttributesHelper.getBaseAttribute(data.attribute);
			}
		}
		
		// On mange tous les éléments de la grille du même type
		PuzzleAttributes attribute;
		for (int x = 0; x < puzzleWidth; x++) {
			for (int y = 0; y < puzzleHeight; y++) {
				attribute = puzzle.getAttribute(x, y);
				if (PuzzleAttributesHelper.getBaseAttribute(attribute) == baseAttribute) {
					eat(new Point(x, y), attribute, true);
				}
			}
		}
		
		// Chute des attributs restants
		int count = matchData.count();
		if (count > 0) {
			puzzle.fall();
		}
	}
}
