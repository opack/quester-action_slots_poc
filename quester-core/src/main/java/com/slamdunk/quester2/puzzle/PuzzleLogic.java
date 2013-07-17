package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.model.points.Point;

/**
 * G�re la repr�sentation logique du puzzle
 */
public class PuzzleLogic {
	private class AlignmentData {
		List<Point> positions;
		List<PuzzleAttributes> attributes;
		
		public AlignmentData() {
			positions = new ArrayList<Point>();
			attributes = new ArrayList<PuzzleAttributes>();
		}
		
		public void clear() {
			positions.clear();
			attributes.clear();
		}

		public int size() {
			return positions.size();
		}
	}
	
	private int width;
	private int height;
	private PuzzleImage[][] puzzleImages;
	private PuzzleStage puzzleStage;
	
	private AlignmentData hAlignData;
	private AlignmentData vAlignData;
	
	public PuzzleLogic(PuzzleStage stage) {
		this.width = stage.getPuzzleWidth();
		this.height = stage.getPuzzleHeight();
		
		hAlignData = new AlignmentData();
		vAlignData = new AlignmentData();
		
		// Cr�ation du puzzle
		this.puzzleStage = stage;
		puzzleImages = stage.getPuzzleImages();
	}

	/**
	 * G�n�re un attribut en s'assurant qu'il ne va pas
	 * provoquer l'alignement de 3 attributs.
	 */
	public PuzzleAttributes initAttribute(int x, int y) {
		PuzzleAttributes attribute;
		do {
			// Choix d'un attribut al�atoire
			attribute = PuzzleAttributesHelper.getRandomBaseAttribute();;
		}
		// Si les 2 attributs � gauche ou en haut sont identiques, on en choisit un autre
		while (isSameOnLeftOrTop(attribute, x, y, 2));
		return attribute;
	}

	/**
	 * Test si l'attribut indiqu� se trouve �galement aux 2 positions voisines
	 * dans la ligne � gauche ou en haut de la position indiqu�e.
	 */
	private boolean isSameOnLeftOrTop(PuzzleAttributes attribute, int x, int y, int depth) {
		// Teste vers la gauche
		int countLeft = 0;
		for (int cur = x - 1; cur >= x - depth; cur--) {
			if (isValidPos(cur, y) && puzzleImages[cur][y].getAttribute() == attribute) {
				countLeft ++;
			} else {
				break;
			}
		}
		if (countLeft == depth) {
			return true;
		}
		// Teste vers le haut
		int countTop = 0;
		for (int cur = y + 1; cur <= y + depth; cur++) {
			if (isValidPos(x, cur) && puzzleImages[x][cur].getAttribute() == attribute) {
				countTop ++;
			} else {
				break;
			}
		}
		if (countTop == depth) {
			return true;
		}
		// Au moins 1 des 2 � gauche et au-dessus n'est pas du m�me type
		return false;
	}

	/**
	 * Retourne true si la position existe dans le puzzle
	 */
	public boolean isValidPos(int x, int y) {
		return x > -1 && x < width
		&& y > -1 && y < height;
	}
	
	public PuzzleAttributes get(int x, int y) {
		if (!isValidPos(x, y)) {
			return null;
		}
		return puzzleImages[x][y].getAttribute();
	}

	/**
	 * Retourne true si le switch a �t� autoris� et effectu�.
	 */
	public boolean switchAttributes(int firstX, int firstY, int secondX, int secondY) {
		if (!isValidPos(firstX, firstY) || !isValidPos(secondX, secondY)) {
			return false;
		}
		
		// Recherche des �ventuelles combinaisons
		boolean isFirstAligned = resolveAlignments(firstX, firstY);
		boolean isSecondAligned = resolveAlignments(secondX, secondY);

		if (!isFirstAligned && !isSecondAligned) {
			return false;
		}
		return true;
	}
	
	public void updatePuzzle() {
//		do {
			// Chute des �l�ments sup�rieurs
			makeAttributesFall();
			
			// Ajout de nouveaux �l�ments
			// ...
			
			// Recherche d'�ventuelles combinaisons
			// pour chaque �l�ment ajout� ou d�plac�
			// ...
		
		// Recommence tant qu'il y a des alignements
//		} while (alignmentFound);
	}

	/**
	 * Fait chuter les attributs, en traitant les attributs depuis le bas du tableau
	 * vers le haut
	 */
	private void makeAttributesFall() {
		boolean emptyRemains = false;
		do {
			emptyRemains = false;
//		boolean emptyFound = false;
			for (int y = 0; y < height; y ++) {
				for (int x = 0; x < width; x ++) {
					if (puzzleImages[x][y].getAttribute() == PuzzleAttributes.UNKNOWN) {
						// S'il y a un emplacement au-dessus, on switch
						if (y + 1 < height) {
							// Le switch n'a lieu que si l'attribut sup�rieur est connu
							if (puzzleImages[x][y + 1].getAttribute() != PuzzleAttributes.UNKNOWN) {
								puzzleStage.switchAttributes(x, y, x, y + 1);
							}
							emptyRemains = true;
						}					
						// Sinon, on est sur la derni�re ligne : on ajoute un nouvel attribut
						else {
							puzzleStage.createAttribute(x, y, PuzzleAttributesHelper.getRandomBaseAttribute());
						}
//						emptyFound = true;
					}
				}
				// On ne fait descendre qu'une ligne � la fois
//				if (emptyFound) {
//					break;
//				}
			}
		} while (emptyRemains);
	}

	/**
	 * V�rifie la pr�sence d'alignements comprenant la position indiqu�e
	 */
	private boolean resolveAlignments(int x, int y) {
		if (!isValidPos(x, y)) {
			return false;
		}
		
		// Recherche des alignements
		hAlignData.clear();
		vAlignData.clear();
		if (!match(x, y, hAlignData, vAlignData)) {
			return false;
		}
		
		// On r�soud l'alignement en privil�giant le plus long.
		int hCount = hAlignData.size();
		int vCount = vAlignData.size();
		if (hCount == vCount) {
			// Formation en coin
			// ...
			return false;
		} else if (hCount > vCount) {
			// Simple ligne horizontale
			return resolveLineAlignment(hAlignData);
		} else {
			// Simple ligne verticale
			return resolveLineAlignment(vAlignData);
		}
	}
	
	/**
	 * Recherche si l'attribut � la position indiqu�e participe �
	 * un alignement.
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean match(int x, int y, AlignmentData hAlignData, AlignmentData vAlignData) {
		PuzzleAttributes element = puzzleImages[x][y].getAttribute();
		
		// V�rifie si l'item participe � un alignement horizontal
		for (int curCol = x; curCol > -1; curCol--) {
			if (!match(curCol, y, element, hAlignData)) {
				break;
			}
		}
		// On commence � col+1 pour ne pas compter 2 fois la cellule � col;row
		for (int curCol = x + 1; curCol < width; curCol++) {
			if (!match(curCol, y, element, hAlignData)) {
				break;
			}
		}
		
		// V�rifie si l'item participe � un alignement vertical
		for (int curRow = y; curRow > -1; curRow--) {
			if (!match(x, curRow, element, vAlignData)) {
				break;
			}
		}
		// On commence � row+1 pour ne pas compter 2 fois la cellule � col;row
		for (int curRow = y + 1; curRow < height; curRow++) {
			if (!match(x, curRow, element, vAlignData)) {
				break;
			}
		}
		return hAlignData.size() >= 3 || vAlignData.size() >= 3;
	}
	
	/**
	 * Recherche si l'attribut � la position indiqu�e est matchable avec l'�l�ment
	 * sp�cifi�.
	 */
	private boolean match(int x, int y, PuzzleAttributes element, AlignmentData alignData) {
		PuzzleAttributes neighborElement = puzzleImages[x][y].getAttribute();
		if (PuzzleAttributesHelper.areMatchable(neighborElement, element)) {
			alignData.positions.add(new Point(x, y));
			alignData.attributes.add(neighborElement);
			return true;
		}
		return false;
	}
	
	/**
	 * Effectue l'effet li�e � la combinaison d'�l�ments indiqu�e
	 */
	private boolean resolveLineAlignment(AlignmentData alignData) {
		// D�clenchement de l'effet ad�quat
		PuzzleMatchEffect effect = PuzzleAttributesHelper.getMatchEffect(alignData.attributes);
		if (effect == null) {
			return false;
		}
		effect.perform(puzzleStage, alignData.positions);
		
		return true;
	}
}
