package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleLogic.AlignmentData;

/**
 * Ajoute une quantité d'attribut force aux actions courantes
 */
public class AttributeAlignmentEffect extends PuzzleMatchEffect {
	private int puzzleWidth;
	private int puzzleHeight;
	private PuzzleStage puzzle;

	@Override
	public void perform(PuzzleStage puzzle, AlignmentData alignment) {
		this.puzzle = puzzle; 
		puzzleWidth = puzzle.getPuzzleWidth();
		puzzleHeight = puzzle.getPuzzleHeight();
		
		// Déclenchement des attributs de base
		for (int cur = 0; cur < alignment.size(); cur ++) {
			eatAttribute(alignment.positions.get(cur), alignment.attributes.get(cur));
		}
		
		// Chute des attributs restants
		int count = alignment.size();
		if (count > 0) {
			puzzle.fall();
		}
		
		// Création des attributs super
		if (count == 4) {
			// Ajout d'un petit bonus
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS4 " + alignment.attributes.get(alignment.alignSourceAttributeIndex));
			
			// Création d'un super attribut
			PuzzleAttributes superAttribute = PuzzleAttributesHelper.getSuper(alignment);
			Point sourcePos = alignment.positions.get(alignment.alignSourceAttributeIndex);
			puzzle.createAttribute(sourcePos.getX(), sourcePos.getY(), superAttribute);
		} else if (alignment.size() == 5) {
			// Ajout d'un petit bonus
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + alignment.attributes.get(alignment.alignSourceAttributeIndex));
			System.out.println("DBG AttributeAlignmentEffect.perform() ADD BONUS5 " + alignment.attributes.get(alignment.alignSourceAttributeIndex));
			
			// Création d'un hyper attribut
			// ...
		}
	}

	/**
	 * Consomme un attribut
	 * @param alignment
	 * @param cur
	 */
	private void eatAttribute(Point pos, PuzzleAttributes attribute) {
		// Suppression des éléments alignés
		puzzle.removeAttribute(pos.getX(), pos.getY());
		
		// Pour chaque attribut, s'il est simple on ajoute des points,
		// s'il est super on va déclencher un autre effet.
		System.out.println("DBG AttributeAlignmentEffect.perform() ADD " + attribute);
		switch (attribute.getType()) {
		case SUPER:
			switch (attribute.getOrientation()) {
			case HORIZONTAL:
				performSuperH(pos.getY());
				break;
			case VERTICAL:
				performSuperV(pos.getX());
				break;
			}
			break;
		}
	}

	/**
	 * Supprime et comptabilise tous les attributs de la ligne
	 * @param y
	 */
	private void performSuperH(int row) {
		for (int x = 0; x < puzzleWidth; x++) {
			eatAttribute(new Point(x, row), puzzle.getAttribute(x, row));
		}
	}
	
	/**
	 * Supprime et comptabilise tous les attributs de la ligne
	 * @param y
	 */
	private void performSuperV(int col) {
		for (int y = 0; y < puzzleHeight; y++) {
			eatAttribute(new Point(col, y), puzzle.getAttribute(col, y));
		}
	}
}
