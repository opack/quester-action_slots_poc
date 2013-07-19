package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.model.points.Point;

/**
 * Gère la représentation logique du puzzle
 */
public class PuzzleLogic {
	public class AttributeData {
		public PuzzleAttributes attribute;
		public Point position;
		public AttributeData(Point position, PuzzleAttributes attribute) {
			if (position == null || attribute == null) {
				throw new IllegalStateException("Tried to create an AttributeData with no position or no attribute : pos=" + position + ", attribute=" + attribute);
			}
			this.attribute = attribute;
			this.position = position;
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AttributeData)) {
				return false;
			}
			AttributeData other = (AttributeData)obj;
			return other.attribute.equals(attribute)
				&& other.position.equals(position);
		}
		
		@Override
		public int hashCode() {
			return position.hashCode() ^ attribute.hashCode();
		}
	}
	public class AlignmentData {
		public List<AttributeData> attributes;
		/**
		 * Indique si l'alignement est horizontal ou vertical
		 */
		public AlignmentOrientation orientation;
		/**
		 * Indice de l'attribut à la source de l'alignement
		 */
		public int alignSourceAttributeIndex;
		
		public AlignmentData() {
			attributes = new ArrayList<AttributeData>();
		}
		
		public void clear() {
			attributes.clear();
			alignSourceAttributeIndex = -1;
		}
		
		public void add(Point position, PuzzleAttributes attribute) {
			attributes.add(new AttributeData(position, attribute));
		}
		
		public int size() {
			return attributes.size();
		}

		public void updateSourceIndex(int x, int y) {
			alignSourceAttributeIndex = -1;
			Point pos;
			final int count = attributes.size();
			for (int cur = 0; cur < count; cur ++) {
				pos = attributes.get(cur).position;
				if (pos.getX() == x && pos.getY() == y) {
					alignSourceAttributeIndex = cur;
					break;
				}
			}
		}
	}
	
	private int width;
	private int height;
	private PuzzleImage[][] puzzleImages;
	private PuzzleStage puzzleStage;
	
	private AlignmentData hAlignData;
	private AlignmentData vAlignData;
	private List<Point> lastFallen;
	
	public PuzzleLogic(PuzzleStage stage) {
		this.width = stage.getPuzzleWidth();
		this.height = stage.getPuzzleHeight();
		
		hAlignData = new AlignmentData();
		hAlignData.orientation = AlignmentOrientation.HORIZONTAL;
		vAlignData = new AlignmentData();
		vAlignData.orientation = AlignmentOrientation.VERTICAL;
		lastFallen = new ArrayList<Point>();
		
		// Création du puzzle
		this.puzzleStage = stage;
		puzzleImages = stage.getPuzzleImages();
	}

	/**
	 * Génère un attribut en s'assurant qu'il ne va pas
	 * provoquer l'alignement de 3 attributs.
	 */
	public PuzzleAttributes initAttribute(int x, int y) {
		PuzzleAttributes attribute;
		do {
			// Choix d'un attribut aléatoire
			attribute = PuzzleAttributesHelper.getRandomBaseAttribute();;
		}
		// Si les 2 attributs à gauche ou en haut sont identiques, on en choisit un autre
		while (isSameOnLeftOrTop(attribute, x, y, 2));
		return attribute;
	}

	/**
	 * Test si l'attribut indiqué se trouve également aux 2 positions voisines
	 * dans la ligne à gauche ou en haut de la position indiquée.
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
		// Au moins 1 des 2 à gauche et au-dessus n'est pas du même type
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
	 * Retourne true si le switch a été autorisé et effectué.
	 */
	public boolean switchAttributes(int firstX, int firstY, int secondX, int secondY) {
		if (!isValidPos(firstX, firstY) || !isValidPos(secondX, secondY)) {
			return false;
		}
		
		// Recherche des éventuelles combinaisons
		// Si le switch des 2 attributs déclenche un super effet, on le traite
		if (resolveSupers(firstX, firstY, secondX, secondY)) {
			return true;
		}
		
		// Si on n'a pas inversé un hyper avec autre chose, alors on regarde
		// si l'inversion a provoqué l'apparition de combinaisons
		boolean isFirstAligned = resolveAlignments(firstX, firstY);
		boolean isSecondAligned = resolveAlignments(secondX, secondY);

		if (!isFirstAligned && !isSecondAligned) {
			return false;
		}
		return true;
	}
	
	private boolean resolveSupers(int firstX, int firstY, int secondX, int secondY) {
		// Teste si on échange un hyper avec autre chose
		if (matchHyper(firstX, firstY, secondX, secondY)
		// Teste si on échange entre eux 2 supers
		|| matchSupers(firstX, firstY, secondX, secondY)) {
			return true;
		}
		return false;
	}

	/**
	 * Vérifie si un switch avec un hyper a été fait, et dans ce cas, déclenche l'effet
	 * adéquat.
	 */
	private boolean matchHyper(int firstX, int firstY, int secondX, int secondY) {
		// On s'assure qu'au moins un des deux attributs est un HYPER
		PuzzleAttributes firstAttribute = puzzleImages[firstX][firstY].getAttribute();
		PuzzleAttributes secondAttribute = puzzleImages[secondX][secondY].getAttribute();
		if (firstAttribute != PuzzleAttributes.HYPER && secondAttribute != PuzzleAttributes.HYPER) {
			return false;
		}
		
		// Déclenche l'effet adéquat
		PuzzleMatchData builder = new PuzzleMatchData();
		builder.setSource(new AttributeData(new Point(firstX, firstY), firstAttribute));
		builder.add(new AttributeData(new Point(secondX, secondY), secondAttribute));
		PuzzleMatchEffect effect = builder.buildMatchEffect();
		if (effect != null) {
			effect.perform(puzzleStage, builder);
			return true;
		}
		return false;
	}
	
	/**
	 * Vérifie si un switch entre 2 supers a été fait, et dans ce cas, déclenche l'effet
	 * adéquat.
	 */
	private boolean matchSupers(int firstX, int firstY, int secondX, int secondY) {
		// On s'assure qu'au moins un des deux attributs est un HYPER
		PuzzleAttributes firstAttribute = puzzleImages[firstX][firstY].getAttribute();
		PuzzleAttributes secondAttribute = puzzleImages[secondX][secondY].getAttribute();
		if (firstAttribute.getType() != AttributeTypes.SUPER || secondAttribute.getType() != AttributeTypes.SUPER) {
			return false;
		}
		
		// Déclenche l'effet adéquat
		PuzzleMatchData builder = new PuzzleMatchData();
		builder.setSource(new AttributeData(new Point(firstX, firstY), firstAttribute));
		builder.add(new AttributeData(new Point(secondX, secondY), secondAttribute));
		PuzzleMatchEffect effect = builder.buildMatchEffect();
		if (effect != null) {
			effect.perform(puzzleStage, builder);
			return true;
		}
		return false;
	}

	public void updatePuzzle() {
		if (!fall()) {
			// Si rien n'est tombé, alors on est dans une situation stable.
			// On va donc rechercher d'éventuelles combinaisons.
			match();
		}
	}
	
	public void match() {
		if (lastFallen.isEmpty()) {
			return;
		}
		for (Point fallen : lastFallen) {
			resolveAlignments(fallen.getX(), fallen.getY());
		}
		lastFallen.clear();
	}

	public boolean fall() {
		lastFallen.clear();
		int yEmpty;
		int yFall = 0;
		int nbOutOfGrid;
		
		for (int x = 0; x < width; x ++) {
			// Le prochain élément à éventuellement apparaître hors du tableau commencera juste
			// au-dessus du tableau : hauteur - 1 + 1 = hauteur
			nbOutOfGrid = 0;
			for (int y = 0; y < height; y ++) {
				if (puzzleImages[x][y].getAttribute() == PuzzleAttributes.EMPTY) {
					yEmpty = y;
					// Détermine l'emplacement du début de la chute en cherchant le prochain
					// attribut non vide, qui va donc chuter.
					if (nbOutOfGrid > 0) {
						// S'il y a au moins un attribut à créer hors de la grille, alors tous
						// les prochains seront forcément aussi à créer hors de de la grille
						yFall = height + nbOutOfGrid;
						nbOutOfGrid ++;
					} else {
						// Si on ne sait pas encore si des attributs peuvent chuter, on cherche
						// le prochain attribut non vide
						int curRow;
						for (curRow = yEmpty + 1; curRow < height; curRow ++) {
							if (puzzleImages[x][curRow].getAttribute() != PuzzleAttributes.EMPTY) {
								yFall = curRow;
								break;
							}
						}
						// Si on n'a pas trouvé d'élément non vide avant la fin du tableau, alors
						// un nouvel élément devra être créé hors du tableau, juste au-dessus du dernier
						// élément créé
						if (curRow == height) {
							yFall = height + nbOutOfGrid;
							nbOutOfGrid ++;
						}
					}
					
					// Création d'un PuzzleImage pour faire une belle animation.
					// A la fin de l'animation, l'attribut tombé sera affecté à l'image actuellement vide.
					puzzleStage.createFallAnimation(x, yFall, yEmpty);
					
					// Mémorise le point de chute de l'attribut pour tester ensuite s'il participe à
					// une combinaison une fois le tableau stabilisé.
					lastFallen.add(new Point(x, yEmpty));
				}
			}
		}
		return !lastFallen.isEmpty();
	}

	/**
	 * Vérifie la présence d'alignements comprenant la position indiquée
	 */
	private boolean resolveAlignments(int x, int y) {
		if (!isValidPos(x, y)) {
			return false;
		}
		
		// Recherche des alignements
		hAlignData.clear();
		vAlignData.clear();
		if (!match(x, y)) {
			return false;
		}
		hAlignData.updateSourceIndex(x, y);
		vAlignData.updateSourceIndex(x, y);
		
		// On résoud l'alignement en privilégiant le plus long.
		int hCount = hAlignData.size();
		int vCount = vAlignData.size();
		PuzzleMatchData builder = new PuzzleMatchData();
		builder.setSource(hAlignData.attributes.get(hAlignData.alignSourceAttributeIndex));
		if (hCount == vCount) {
			// Les deux lignes font la même taille
			builder.add(hAlignData.attributes);
			builder.add(vAlignData.attributes);
		} else if (hCount > vCount) {
			// La ligne horizontale est plus longue
			builder.add(hAlignData.attributes);
		} else {
			// La ligne verticale est plus longue
			builder.add(vAlignData.attributes);
		}
		PuzzleMatchEffect effect = builder.buildMatchEffect();
		if (effect == null) {
			return false;
		}
		effect.perform(puzzleStage, builder);
		return true;
	}
	
	/**
	 * Recherche si l'attribut à la position indiquée participe à
	 * un alignement.
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean match(int x, int y) {
		PuzzleAttributes element = puzzleImages[x][y].getAttribute();
		
		// Vérifie si l'item participe à un alignement horizontal
		for (int curCol = x; curCol > -1; curCol--) {
			if (!match(curCol, y, element, hAlignData)) {
				break;
			}
		}
		// On commence à col+1 pour ne pas compter 2 fois la cellule à col;row
		for (int curCol = x + 1; curCol < width; curCol++) {
			if (!match(curCol, y, element, hAlignData)) {
				break;
			}
		}
		
		// Vérifie si l'item participe à un alignement vertical
		for (int curRow = y; curRow > -1; curRow--) {
			if (!match(x, curRow, element, vAlignData)) {
				break;
			}
		}
		// On commence à row+1 pour ne pas compter 2 fois la cellule à col;row
		for (int curRow = y + 1; curRow < height; curRow++) {
			if (!match(x, curRow, element, vAlignData)) {
				break;
			}
		}
		return hAlignData.size() > 0 || vAlignData.size() > 0;
	}
	
	/**
	 * Recherche si l'attribut à la position indiquée est matchable avec l'élément
	 * spécifié.
	 */
	private boolean match(int x, int y, PuzzleAttributes element, AlignmentData alignData) {
		PuzzleAttributes neighborElement = puzzleImages[x][y].getAttribute();
		if (PuzzleAttributesHelper.areMatchable(neighborElement, element)) {
			alignData.add(new Point(x, y), neighborElement);
			return true;
		}
		return false;
	}
}
