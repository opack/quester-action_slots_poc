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
	
	protected void eat(Point pos, PuzzleAttributes attribute, boolean performBonusEffect) {
		eat(pos.getX(), pos.getY(), attribute, performBonusEffect);
	}
	
	/**
	 * Consomme un attribut
	 * @param alignment
	 * @param cur
	 */
	protected void eat(int x, int y, PuzzleAttributes attribute, boolean performBonusEffect) {
		// Suppression de l'attribut "mangé"
		puzzle.removeAttribute(x, y);
		
		// Pour chaque attribut, s'il est simple on ajoute des points,
		// s'il est super on va déclencher un autre effet.
		System.out.printf("DBG AttributeAlignmentEffect.perform() ADD %d %d %s\n", x, y, attribute);
		if (performBonusEffect) {
			switch (attribute.getType()) {
			case SUPER:
				switch (attribute.getOrientation()) {
				case HORIZONTAL:
					performSuperHBonus(y);
					break;
				case VERTICAL:
					performSuperVBonus(x);
					break;
				case CROSS:
					performSuperXBonus(x, y);
					break;
				}
				break;
			default:
				// BASE ou EMPTY : pas d'effet bonus
				break;
			}
		}
	}

	/**
	 * Mange tous les attributs de la colonne et de la ligne
	 * @param x
	 * @param y
	 */
	public void performSuperXBonus(int x, int y) {
		performSuperHBonus(y);
		performSuperVBonus(x);
	}

	/**
	 * Mange les attributs dans la zone autour des Supers
	 */
	public void performDoubleSupersBonus(Point firstPos, Point secondPos) {
		int areaMinX = Math.min(firstPos.getX(), secondPos.getX());
		int areaMinY = Math.min(firstPos.getY(), secondPos.getY());
		int areaMaxX = Math.max(firstPos.getX(), secondPos.getX());
		int areaMaxY = Math.max(firstPos.getY(), secondPos.getY());
		for (int x = Math.max(0, areaMinX - 1); x <= areaMaxX + 1 && x < puzzleWidth; x ++) {
			for (int y = Math.max(0, areaMinY - 1); y <= areaMaxY + 1 && y < puzzleHeight; y ++) {
				eat(x, y, puzzle.getAttribute(x, y), true);
			}
		}
	}
	/**
	 * Mange tous les attributs targetAttribute de la grille
	 */
	public void performHyperBonus(int otherAttributeX, int otherAttributeY, PuzzleAttributes otherAttribute) {
		// Si l'autre attribut est un attribut de base, on mange tous ceux de ce
		// type sur l'écran
		if (otherAttribute.getType() == AttributeTypes.BASE) {
			PuzzleAttributes attribute;
			for (int x = 0; x < puzzleWidth; x++) {
				for (int y = 0; y < puzzleHeight; y++) {
					attribute = puzzle.getAttribute(x, y);
					if (PuzzleAttributesHelper.getBaseAttribute(attribute) == otherAttribute) {
						eat(new Point(x, y), attribute, true);
					}
				}
			}
		}
		// S'il s'agit d'un super, on déclenche un gros super
		else if (otherAttribute.getType() == AttributeTypes.SUPER) {
			switch (otherAttribute.getOrientation()) {
			case CROSS:
				performSuperHBonus(otherAttributeY - 1);
				performSuperHBonus(otherAttributeY + 1);
				performSuperVBonus(otherAttributeX - 1);
				performSuperVBonus(otherAttributeX + 1);
				break;
			case HORIZONTAL:
				performSuperHBonus(otherAttributeY - 1);
				performSuperHBonus(otherAttributeY + 1);
				break;
			case VERTICAL:
				performSuperVBonus(otherAttributeX - 1);
				performSuperVBonus(otherAttributeX + 1);
				break;
			}
		}
		// S'il s'agit d'un autre hyper, on mange tous les items de la grille
		else if (otherAttribute.getType() == AttributeTypes.HYPER) {
			PuzzleAttributes attribute;
			for (int x = 0; x < puzzleWidth; x++) {
				for (int y = 0; y < puzzleHeight; y++) {
					attribute = puzzle.getAttribute(x, y);
					eat(x, y, attribute, false);
				}
			}
		}
	}

	/**
	 * Mange tous les attributs de la ligne
	 * @param y
	 */
	public void performSuperHBonus(int row) {
		if (row < 0 || row >= puzzleHeight) {
			return;
		}
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
	 * Mange tous les attributs de la ligne
	 */
	public void performSuperVBonus(int col) {
		if (col < 0 || col >= puzzleWidth) {
			return;
		}
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
	protected abstract void perform(PuzzleMatchData matchData);
}
