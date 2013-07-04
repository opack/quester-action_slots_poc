package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.map.Borders.BOTTOM;
import static com.slamdunk.quester.model.map.Borders.LEFT;
import static com.slamdunk.quester.model.map.Borders.RIGHT;
import static com.slamdunk.quester.model.map.Borders.TOP;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.PointManager;

public abstract class MapBuilder {
	protected final MapArea[][] areas;
	protected boolean areasCreated;
	protected Point entranceArea;
	
	protected Point entrancePosition;
	private List<Point> linked;
	
	protected boolean mainEntrancesPlaced;
	protected final int mapHeight;
	
	protected final int mapWidth;
	
	private MapElements pathType;
	protected PointManager pointManager;
	private boolean[][] reachableFromEntrance;
	
	private List<Point> unlinked;
	
	/**
	 * @param pathType Type d'�l�ment repr�sentant un chemin entre deux zones
	 */
	public MapBuilder(int width, int height, MapElements pathType) {
		this.mapWidth = width;
		this.mapHeight = height;
		areas = new MapArea[width][height];
		reachableFromEntrance = new boolean[width][height];
		pointManager = new PointManager(width, height);
		
		this.pathType = pathType;
		
		// Pr�paration de la liste des salles d�j� li�es � l'entr�e
		linked = new ArrayList<Point>();
		// Pr�paration de la liste des salles � lier � l'entr�e
		unlinked = new ArrayList<Point>();
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				unlinked.add(pointManager.getPoint(col, row));
			}
		}
	}
	
	public MapArea[][] build() {
		if (!areasCreated || !mainEntrancesPlaced) {
			throw new IllegalStateException("areasCreated=" + areasCreated + ", mainEntrancesPlaced=" + mainEntrancesPlaced);
		}
		
		// Cr�ation des portes entre les zones.
		// Tant qu'il y a des salles qui ne sont pas accessibles depuis l'entr�e...
		while (!unlinked.isEmpty()) {
			// 1. Prendre au hasard une salle non-joignable depuis l'entr�e.
			Point unlinkedPos = unlinked.get(MathUtils.random(unlinked.size() - 1));
			
			// 2. Choisir une salle joignable au hasard
			Point linkedPos = linked.get(MathUtils.random(linked.size() - 1));
			
			// 3. Connecter cette salle (via un chemin al�atoire) � l'entr�e, ou �
			// une salle accessible depuis l'entr�e
			createRandomPath(unlinkedPos, linkedPos);
		}
		
		// DBG
		if (!validateDungeon()) {
			throw new IllegalStateException("La carte g�n�r�e n'est pas valide.");
		}
		return areas;
	}
	
	/**
	 * Cr�e les zones du donjon, sans portes mais avec du sol.
	 * Penser � passer le flag roomsCreated � true.
	 */
	public void createAreas(Point areaMinSize, Point areaMaxSize, WorldElementData defaultBackground) {
		for (int col = 0; col < mapWidth; col ++) {
			for (int row = 0; row < mapHeight; row ++) {
				// La taille de la zone correspond � la taille de la map,
				// car on n'affiche qu'une zone � chaque fois.
				MapArea room = new MapArea(
					col, row,
					MathUtils.random(areaMinSize.getX(), areaMaxSize.getX()), MathUtils.random(areaMinSize.getY(), areaMaxSize.getY()),
					defaultBackground);
				fillArea(room);
				areas[col][row] = room;
			}
		}
		areasCreated = true;
	}

	private void createHorizontalPath(MapArea leftArea, MapArea rightArea) {
		int nbPaths = getNbPathsBetweenAreas();
//DBG		int position;
		int posOnRightWall;
		int posOnLeftWall;
		for (int cur = 0; cur < nbPaths; cur ++) {
			// R�cup�ration d'une position pour placer la porte sur un mur vertical
//DBG			position = getPathPosition(LEFT);
			posOnRightWall = getPathPosition(leftArea, RIGHT);
			posOnLeftWall = getPathPosition(rightArea, LEFT);
			
			// On place un chemin sur le mur droit de la premi�re zone
			PathData pathToRight = new PathData(
				pathType, RIGHT,
				rightArea.getX(), rightArea.getY(),
				0, posOnLeftWall);
			leftArea.addPath(RIGHT, pathToRight, posOnRightWall);
			
			// On place un chemin sur le mur gauche de la seconde zone
			PathData pathToLeft = new PathData(
				pathType, LEFT, 
				leftArea.getX(), leftArea.getY(), 
				leftArea.getWidth() - 1, posOnRightWall);
			rightArea.addPath(LEFT, pathToLeft, posOnLeftWall);
		}
	}

	/**
	 * Cr�ation d'un chemin al�atoire allant d'une zone � l'autre.
	 * On s'arr�te cependant d�s qu'on atteint une zone rejoignant
	 * l'entr�e.
	 * @param unlinkedPos
	 * @param linkedPos
	 */
	private void createRandomPath(Point from, Point to) {
		int curX = from.getX();
		int curY = from.getY();
		int destinationX = to.getX();
		int destinationY = to.getY();
		final List<Borders> borders = new ArrayList<Borders>();

		// On s'arr�te si on arrive � la destination ou si on atteint une zone
		// qui permet de rejoindre l'entr�e
		while ((curX != destinationX || curY != destinationY)
		&& !reachableFromEntrance[curX][curY]) {
			// Bient�t, cette salle sera accessible depuis l'entr�e
			linkArea(curX, curY);
			
			borders.clear();
			// Si la sortie est plus en haut, on autorise un offset vers le haut
			if (destinationY > curY) {
				borders.add(TOP);
			}
			// Si la sortie est plus en bas, on autorise un offset vers le bas
			if (destinationY < curY) {
				borders.add(BOTTOM);
			}
			// Si la sortie est plus � gauche, on autorise un offset vers la gauche
			if (destinationX < curX) {
				borders.add(LEFT);
			}
			// Si la sortie est plus � droite, on autorise un offset vers la droite
			if (destinationX > curX) {
				borders.add(RIGHT);
			}
			// 1. Choix d'un mur
			// 2. Cr�ation du chemin entre les zones
			// 3. M�j du tableau des joignables : la zone destination sera connect�e � l'entr�e au final
			// 4. D�placement du curseur en direction de la destination
			Borders choosenBorder = borders.get(MathUtils.random(borders.size() - 1));
			switch (choosenBorder) {
				case TOP:
					createVerticalPath(areas[curX][curY + 1], areas[curX][curY]);
					curY++;
					break;
				case BOTTOM:
					createVerticalPath(areas[curX][curY], areas[curX][curY - 1]);
					curY--;
					break;
				case LEFT:
					createHorizontalPath(areas[curX - 1][curY], areas[curX][curY]);
					curX--;
					break;
				case RIGHT:
					createHorizontalPath(areas[curX][curY], areas[curX + 1][curY]);
					curX++;
					break;
			}
		}
	}

	private void createVerticalPath(MapArea topArea, MapArea bottomArea) {
		int nbPaths = getNbPathsBetweenAreas();
//DBG		int position;
		int posOnTopWall;
		int posOnBottomWall;
		for (int cur = 0; cur < nbPaths; cur ++) {
			// R�cup�ration d'une position pour placer une porte sur un mur horizontal
//DBG			position = getPathPosition(TOP);
			posOnTopWall = getPathPosition(bottomArea, TOP);
			posOnBottomWall = getPathPosition(topArea, BOTTOM);
			
			// On place un chemin au milieu du mur bas de la premi�re zone
			PathData pathToBottom = new PathData(
				pathType, BOTTOM,
				bottomArea.getX(), bottomArea.getY(),
				posOnTopWall, bottomArea.getHeight() - 1);
			topArea.addPath(BOTTOM, pathToBottom, posOnBottomWall);
			
			// On place un chemin au milieu du mur haut de la seconde zone
			PathData pathToTop = new PathData(
				pathType, TOP,
				topArea.getX(), topArea.getY(),
				posOnBottomWall, 0);
			bottomArea.addPath(TOP, pathToTop, posOnTopWall);
		}
	}
	
	/**
	 * Remplit une zone
	 */
	protected abstract void fillArea(MapArea room);

	public MapElements getPathType() {
		return pathType;
	}

	public Point getEntrancePosition() {
		return entrancePosition;
	}
	
	public Point getEntranceRoom() {
		return entranceArea;
	}

	public int getMapHeight() {
		return mapHeight;
	}
	
	public int getMapWidth() {
		return mapWidth;
	}

	/**
	 * Retourne le nombre de chemins � cr�er entre 2 zones. Une classe fille
	 * peut red�finir cette m�thode pour cr�er plusieurs chemins entre les zones.
	 * @return
	 */
	protected int getNbPathsBetweenAreas() {
		return 1;
	}

	protected int getPathPosition(MapArea area, Borders border) {
		int position = 0;
		switch (border) {
			// Les c�t�s horizontaux
			case TOP:
			case BOTTOM:
				// Choix d'un nombre entre 1 et taille -2 pour s'assurer qu'on ne
				// place pas un chemin dans un coin
				position = MathUtils.random(1, area.getWidth() - 2);
				break;
				
			// Les c�t�s verticaux
			case LEFT:
			case RIGHT:
				// Choix d'un nombre entre 1 et taille -2 pour s'assurer qu'on ne
				// place pas un chemin dans un coin
				position = MathUtils.random(1, area.getHeight() - 2);
				break;
		}
		return position;
	}
	
	/**
	 * Met � jour les diff�rents objets pour indiquer que la zone
	 * aux coordonn�es sp�cifi�es est accessible depuis l'entr�e
	 * @param curX
	 * @param curY
	 */
	protected void linkArea(int x, int y) {
		linkArea(pointManager.getPoint(x, y));
	}

	protected void linkArea(Point pos) {
		reachableFromEntrance[pos.getX()][pos.getY()] = true;
		unlinked.remove(pos);
		linked.add(pos);
	}

	/**
	 * Choisit l'entr�e (et �ventuellement la sortie) de la carte.
	 * Penser � passer le flag mainEntrancesPlaced � true.
	 */
	public abstract void placeMainEntrances();

	public void printMap() {
		StringBuilder sb = new StringBuilder();
		for (int row = mapHeight- 1; row >= 0; row --) {
			for (int col = 0; col < mapWidth; col ++) {
				Point pos = pointManager.getPoint(col, row);
				MapArea area = areas[col][row];
				if (area == null) {
					// Dessin d'une salle inaccessible
					sb.append("# ");
				} else {
					// Dessin d'une salle accessible
					if (pos.equals(entranceArea)) {
						sb.append("A");
					} else {
						sb.append("O");
					}
					
					// Y'a-t-il un chemin vers la droite ?
					if (!area.getPaths(RIGHT).isEmpty()) {
						sb.append("-");
					} else {
						sb.append(" ");
					}
				}
			}
			sb.append("\n");
			// Passe n�2 pour dessiner les chemins vers le bas
			for (int col = 0; col < mapWidth; col ++) {
				MapArea area = areas[col][row];
				if (area != null
				&& !area.getPaths(BOTTOM).isEmpty()) {
					sb.append("| ");
				} else {
					sb.append("  ");
				}
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}

	public void setLinkType(MapElements doorType) {
		this.pathType = doorType;
	}
	
	protected boolean validateDungeon() {
		// Par d�faut, le donjon est valide
		return true;
	}
}
