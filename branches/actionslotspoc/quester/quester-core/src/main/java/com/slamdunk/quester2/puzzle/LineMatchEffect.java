package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AttributeData;

/**
 * Ajoute une quantit� d'attribut force aux actions courantes
 */
public class LineMatchEffect extends PuzzleMatchEffect {

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
		
		// Cr�ation des attributs super
		if (count == 4) {
			// Ajout d'un petit bonus
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUSx4 " + matchData.getSource().attribute);
			
			// Cr�ation d'un super attribut
			PuzzleAttributes baseAttribute = PuzzleAttributesHelper.getBaseAttribute(matchData.getSource().attribute);
			PuzzleAttributes superAttribute = PuzzleAttributesHelper.getSuper(baseAttribute, matchData.getOrientation());
			Point sourcePos = matchData.getSource().position;
			puzzle.createAttribute(sourcePos.getX(), sourcePos.getY(), superAttribute);
		} else if (count == 5) {
			// Ajout d'un petit bonus
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUSx5 " + matchData.getSource().attribute);
			
			// Cr�ation d'un hyper attribut
			Point sourcePos = matchData.getSource().position;
			puzzle.createAttribute(sourcePos.getX(), sourcePos.getY(), PuzzleAttributes.HYPER);
		}
	}
}
