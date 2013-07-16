package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.points.Point;

/**
 * G�re la repr�sentation logique du puzzle
 */
public class Puzzle {
	public interface PuzzleChangeListener {
		/**
		 * Appel�e lorsque 2 attributs ont �t� �chang�s.
		 */
		void onAttributesSwitched(int firstX, int firstY, int secondX, int secondY);
		
		/**
		 * Appel�e lorsqu'un attribut est cr��.
		 */
		void onAttributeCreated(int x, int y, PuzzleAttributes attribute);
		
		/**
		 * Appel�e lorsqu'un attribut est supprim�
		 */
		void onAttributeRemoved(int x, int y);
	}
	
	private int width;
	private int height;
	private PuzzleAttributes[][] puzzle;
	
	private PuzzleChangeListener listener;
	
	private boolean isSet;
	
	public Puzzle(int width, int height) {
		this.width = width;
		this.height = height;
		
		// Cr�ation du puzzle
		puzzle = new PuzzleAttributes[width][height];
		isSet = false;
	}

	public void setListener(PuzzleChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * G�n�re un puzzle de d�part al�atoire
	 */
	public void init() {
		final PuzzleAttributes[] allAttributes = PuzzleAttributesHelper.BASE_ATTRIBUTES;
		final int randomMax = allAttributes.length - 1;
		PuzzleAttributes attribute;
		for (int y = height - 1; y > -1; y --) {
			for (int x = 0; x < width; x ++) {
				do {
					// Choix d'un attribut al�atoire
					attribute = allAttributes[MathUtils.random(randomMax)];
				}
				// Si les 2 attributs � gauche ou en haut sont identiques, on en choisit un autre
				while (isSameOnLeftOrTop(attribute, x, y, 2));
				
				// Affectation de cet attribut au puzzle
				puzzle[x][y] = attribute;
				
				// Notification du listener
				if (listener != null) {
					listener.onAttributeCreated(x, y, attribute);
				}
			}
		}
		isSet = true;
	}

	/**
	 * Test si l'attribut indiqu� se trouve �galement aux 2 positions voisines
	 * dans la ligne � gauche ou en haut de la position indiqu�e.
	 */
	private boolean isSameOnLeftOrTop(PuzzleAttributes attribute, int x, int y, int depth) {
		// Teste vers la gauche
		int countLeft = 0;
		for (int cur = x - 1; cur >= x - depth; cur--) {
			if (isValidPos(cur, y) && puzzle[cur][y] == attribute) {
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
			if (isValidPos(x, cur) && puzzle[x][cur] == attribute) {
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = height - 1; y > -1; y --) {
			for (int x = 0; x < width; x ++) {
				sb.append(puzzle[x][y].toString().charAt(0)).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public PuzzleAttributes get(int x, int y) {
		if (!isValidPos(x, y)) {
			return null;
		}
		return puzzle[x][y];
	}
	

	public PuzzleAttributes remove(int x, int y) {
		if (!isValidPos(x, y)) {
			return null;
		}
		PuzzleAttributes removed = puzzle[x][y];
		puzzle[x][y] = null;
		if (listener != null) {
			listener.onAttributeRemoved(x, y);
		}
		return removed;
	}

	/**
	 * Retourne true si le switch a �t� autoris� et effectu�.
	 */
	public boolean switchAttributes(int firstX, int firstY, int secondX, int secondY) {
		// Inverse les 2 �l�ments aux positions indiqu�es
		if (!performSwitch(firstX, firstY, secondX, secondY)) {
			return false;
		}
		
		// Recherche des �ventuelles combinaisons
		boolean isFirstAligned = resolveAlignments(firstX, firstY);
		boolean isSecondAligned = resolveAlignments(secondX, secondY);

		if (!isFirstAligned && !isSecondAligned) {
			// Si aucune combinaison n'a �t� trouv�e, on replace les items � leur position initiale
			performSwitch(firstX, firstY, secondX, secondY);
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
//		boolean emptyRemains = false;
//		do {
//			emptyRemains = false;
		boolean emptyFound = false;
			for (int y = 0; y < height; y ++) {
				for (int x = 0; x < width; x ++) {
					if (puzzle[x][y] == null) {
						// S'il y a un emplacement au-dessus, on switch
						if (y + 1 < height) {
							performSwitch(x, y, x, y + 1);
//							emptyRemains = true;
						}					
						// Sinon, on est sur la derni�re ligne : on ajoute un nouvel attribut
						else {
							puzzle[x][y] = PuzzleAttributesHelper.BASE_ATTRIBUTES[MathUtils.random(PuzzleAttributesHelper.BASE_ATTRIBUTES.length - 1)];
							if (listener != null) {
								listener.onAttributeCreated(x, y, puzzle[x][y]);
							}
						}
						emptyFound = true;
					}
				}
				// On ne fait descendre qu'une ligne � la fois
				if (emptyFound) {
					break;
				}
			}
//		} while (emptyRemains);
	}

	/**
	 * Inverse les attributs aux positions indiqu�es
	 */
	private boolean performSwitch(int firstX, int firstY, int secondX, int secondY) {
		if (!isValidPos(firstX, firstY) || !isValidPos(secondX, secondY)) {
			return false;
		}
		// Inverse les 2 �l�ments aux positions indiqu�es
		PuzzleAttributes tmp = puzzle[firstX][firstY];
		puzzle[firstX][firstY] = puzzle[secondX][secondY];
		puzzle[secondX][secondY] = tmp;
		
		// Pr�vient le listener afin de faire une jolie inversion
//		if (listener != null) {
//			System.out.printf("Puzzle.switchAttributes() %d %d %d %d\n",firstX, firstY, secondX, secondY);
//			listener.onAttributesSwitched(firstX, firstY, secondX, secondY);
//		}
		return true;
	}

	/**
	 * V�rifie la pr�sence d'alignements comprenant la position indiqu�e
	 */
	private boolean resolveAlignments(int x, int y) {
		if (!isValidPos(x, y)) {
			return false;
		}
		PuzzleAttributes element = puzzle[x][y];
		List<Point> hAlignedPos = new ArrayList<Point>();
		List<PuzzleAttributes> hAlignedElements = new ArrayList<PuzzleAttributes>();
		List<Point> vAlignedPos = new ArrayList<Point>();
		List<PuzzleAttributes> vAlignedElements = new ArrayList<PuzzleAttributes>();
		
		// V�rifie si l'item participe � un alignement horizontal
		for (int curCol = x; curCol > -1; curCol--) {
			if (!match(curCol, y, element, hAlignedPos, hAlignedElements)) {
				break;
			}
		}
		// On commence � col+1 pour ne pas compter 2 fois la cellule � col;row
		for (int curCol = x + 1; curCol < width; curCol++) {
			if (!match(curCol, y, element, hAlignedPos, hAlignedElements)) {
				break;
			}
		}
		
		// V�rifie si l'item participe � un alignement vertical
		for (int curRow = y; curRow > -1; curRow--) {
			if (!match(x, curRow, element, vAlignedPos, vAlignedElements)) {
				break;
			}
		}
		// On commence � row+1 pour ne pas compter 2 fois la cellule � col;row
		for (int curRow = y + 1; curRow < height; curRow++) {
			if (!match(x, curRow, element, vAlignedPos, vAlignedElements)) {
				break;
			}
		}
		
		// On r�soud l'alignement en privil�giant le plus long.
		if (hAlignedPos.size() > 1 && vAlignedPos.size() > 1) {
			return false;
		}
		int hCount = hAlignedPos.size();
		int vCount = vAlignedPos.size();
		if (hCount == vCount) {
			// Formation en coin
			// ...
			return false;
		} else if (hCount > vCount) {
			// Simple ligne horizontale
			return resolveLineAlignment(hAlignedPos, hAlignedElements);
		} else {
			// Simple ligne verticale
			return resolveLineAlignment(vAlignedPos, vAlignedElements);
		}
	}
	
	/**
	 * 
	 */
	private boolean match(int x, int y, PuzzleAttributes element, List<Point> alignedPosList, List<PuzzleAttributes> alignedElementsList) {
		PuzzleAttributes neighborElement = puzzle[x][y];
		if (PuzzleAttributesHelper.areMatchable(neighborElement, element)) {
			alignedPosList.add(new Point(x, y));
			alignedElementsList.add(neighborElement);
			return true;
		}
		return false;
	}
	
	/**
	 * Effectue l'effet li�e � la combinaison d'�l�ments indiqu�e
	 */
	private boolean resolveLineAlignment(List<Point> alignedPos, List<PuzzleAttributes> alignedElements) {
		// D�clenchement de l'effet ad�quat
		PuzzleMatchEffect effect = PuzzleAttributesHelper.getMatchEffect(alignedElements);
		if (effect == null) {
			return false;
		}
		effect.perform(this, alignedPos);
		
		return true;
	}

	/**
	 * Renvoit true si le puzzle a �t� cr�� (donc que la m�thode init() a �t�
	 * appel�e).
	 * @return
	 */
	public boolean isSet() {
		return isSet;
	}
}
