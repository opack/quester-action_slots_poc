package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester2.puzzle.PuzzleSwitchInputProcessor.SwitchListener;

/**
 * G�re la repr�sentation logique du puzzle
 */
public class Puzzle implements SwitchListener {
	public interface PuzzleChangeListener {
		/**
		 * Appel�e lorsque 2 attributs ont �t� �chang�s.
		 */
		void onAttributesSwitched(int firstX, int firstY, int secondX, int secondY);
	}
	
	private int width;
	private int height;
	private PuzzleAttributes[][] puzzle;
	
	private PuzzleChangeListener listener;
	
	public Puzzle(int width, int height) {
		this.width = width;
		this.height = height;
		
		// Cr�ation du puzzle
		puzzle = new PuzzleAttributes[width][height];
		initPuzzle();
	}

	public void setListener(PuzzleChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * G�n�re un puzzle de d�part al�atoire
	 */
	private void initPuzzle() {
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
			}
		}
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

	@Override
	public void onPuzzleSwitch(int firstX, int firstY, int secondX, int secondY) {
		// Inverse les 2 �l�ments aux positions indiqu�es
		if (!switchAttributes(firstX, firstY, secondX, secondY)) {
			return;
		}
		
		// Recherche des �ventuelles combinaisons
		boolean firstCreateAlignment = findAlignments(firstX, firstY);
		boolean secondCreateAlignment = findAlignments(secondX, secondY);
		
		// Si aucune combinaison n'a �t� trouv�e, on replace les items � leur position initiale
		if (!firstCreateAlignment && !secondCreateAlignment) {
			switchAttributes(firstX, firstY, secondX, secondY);
		}
	}

	/**
	 * Inverse les attributs aux positions indiqu�es
	 */
	private boolean switchAttributes(int firstX, int firstY, int secondX, int secondY) {
		if (!isValidPos(firstX, firstY) || !isValidPos(secondX, secondY)) {
			return false;
		}
		// Inverse les 2 �l�ments aux positions indiqu�es
		PuzzleAttributes tmp = puzzle[firstX][firstY];
		puzzle[firstX][firstY] = puzzle[secondX][secondY];
		puzzle[secondX][secondY] = tmp;
		
		// Pr�vient le listener afin de faire une jolie inversion
		listener.onAttributesSwitched(firstX, firstY, secondX, secondY);
		return true;
	}

	/**
	 * V�rifie la pr�sence d'alignements comprenant la position indiqu�e
	 */
	private boolean findAlignments(int x, int y) {
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
		if (hAlignedPos.isEmpty() && vAlignedPos.isEmpty()) {
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
		effect.perform();
		
		// Suppression des �l�ments align�s
		// ...
		
		// Chute des �l�ments sup�rieurs
		// ...
		
		// Ajout de nouveaux �l�ments
		// ...
		
		return true;
	}
}
