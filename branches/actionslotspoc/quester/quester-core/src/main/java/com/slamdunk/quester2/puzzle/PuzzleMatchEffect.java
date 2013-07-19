package com.slamdunk.quester2.puzzle;

import com.slamdunk.quester.model.points.Point;

/**
 * Représente un effet d'alignement
 */
public abstract class PuzzleMatchEffect {
	protected PuzzleStage puzzle;
	protected int puzzleWidth;
	protected int puzzleHeight;
	
	public void setPuzzle(PuzzleStage puzzle) {
		this.puzzle = puzzle;
		puzzleWidth = puzzle.getPuzzleWidth();
		puzzleHeight = puzzle.getPuzzleHeight();
	}
	
	/**
	 * Consomme un attribut
	 * @param alignment
	 * @param cur
	 */
	protected void eat(Point pos, PuzzleAttributes attribute, boolean performBonusEffect) {
		// Suppression des éléments alignés
		puzzle.removeAttribute(pos.getX(), pos.getY());
		
		// Pour chaque attribut, s'il est simple on ajoute des points,
		// s'il est super on va déclencher un autre effet.
		System.out.println("DBG AttributeAlignmentEffect.perform() ADD " + attribute);
		if (performBonusEffect) {
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
			case HYPER:
				performHyper();
				break;
			default:
				// BASE ou EMPTY : pas d'effet bonus
				break;
			}
		}
	}

	/**
	 * Effectue l'effet hyper avec l'attribut source
	 */
	private void performHyper() {
		// TODO ...
	}

	/**
	 * Supprime et comptabilise tous les attributs de la ligne
	 * @param y
	 */
	private void performSuperH(int row) {
		PuzzleAttributes attribute;
		boolean doBonus;
		for (int x = 0; x < puzzleWidth; x++) {
			attribute = puzzle.getAttribute(x, row);
			// Si on tombe sur un SuperH, on ne l'appelle pas car on le fait déjà
			doBonus = attribute.getType() != AttributeTypes.SUPER || attribute.getOrientation() != AlignmentOrientation.HORIZONTAL;
			eat(new Point(x, row), attribute, doBonus);
		}
	}
	
	/**
	 * Supprime et comptabilise tous les attributs de la ligne
	 * @param y
	 */
	private void performSuperV(int col) {
		PuzzleAttributes attribute;
		boolean doBonus;
		for (int y = 0; y < puzzleHeight; y++) {
			attribute = puzzle.getAttribute(col, y);
			// Si on tombe sur un SuperV, on ne l'appelle pas car on le fait déjà
			doBonus = attribute.getType() != AttributeTypes.SUPER || attribute.getOrientation() != AlignmentOrientation.VERTICAL;
			eat(new Point(col, y), attribute, doBonus);
		}
	}
	
	public void perform(PuzzleStage puzzle, PuzzleMatchData matchData) {
		setPuzzle(puzzle);
		perform(matchData);
	}
	
	/**
	 * Effectue des actions (ajout de points d'attributs aux actions, suppression des
	 * éléments d'une ligne, création d'un attribut super...) en fonction de l'effet
	 * et des attributs alignés dont les positions sont indiquées.
	 */
	public abstract void perform(PuzzleMatchData matchData);
}
