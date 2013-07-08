package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.data.WorldElementData.GROUND_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.WALL_DATA;
import static com.slamdunk.quester.model.map.Borders.BOTTOM;
import static com.slamdunk.quester.model.map.Borders.RIGHT;
import static com.slamdunk.quester.model.map.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.model.map.MapElements.RABITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.decorators.AreaDecorator;
import com.slamdunk.quester.model.map.decorators.RandomWallDecorator;
import com.slamdunk.quester.model.points.Point;

public class DungeonBuilder extends MapBuilder {
	private int difficulty;
	private Point exitRoom;
	
	public DungeonBuilder(int dungeonWidth, int dungeonHeight, int difficulty) {
		super(dungeonWidth, dungeonHeight, COMMON_DOOR);
		this.difficulty = difficulty;
	}
	
//	@Override
//	protected boolean validateDungeon() {
//		if (exitRoom == null) {
//			System.out.println("DungeonBuilder.validateDungeon() La validation du donjon ne sera pas faite car aucune sortie n'est définie.");
//			return true;
//		}
//		AStar pathfinder = createPathfinder();
//		System.out.println("DungeonBuilder.validateDungeon() " + pathfinder);
//		List<UnmutablePoint> path = pathfinder.findPath(
//			entranceArea.getX() * 2, entranceArea.getY() * 2,
//			exitRoom.getX() * 2, exitRoom.getY() * 2);
//		if (path == null) {
//			System.err.println("Impossible d'atteindre la sortie située à " + exitRoom + " depuis l'entrée située à " + entranceArea);
//			return false;
//		} else {
//			System.out.println("Chemin vers la sortie :" + path);
//			return true;
//		}
//	}

	/**
	 * Crée une porte principale (ENTRANCE_DOOR ou EXIT_DOOR) sur le mur du donjon
	 * indiqué par walls. Ce mur indique si la porte est sur le côté haut, bas,
	 * gauche ou droit du donjon. Un choix aléatoire de la pièce sur ce côté
	 * du donjon sera fait pour déterminer où sera cette porte.
	 * Une fois le mur choisi, il est retiré de la liste des murs possible walls,
	 * pour éviter de mettre la porte d'entrée et de sortie sur le même côté du
	 * donjon.
	 * Si une position interdite est spécifiée, alors la position choisie ne pourra
	 * pas être la même.
	 */
	private Point createMainDoor(List<Borders> walls, MapElements door, Point forbiddenPosition) {
		Borders choosenWall = walls.remove(MathUtils.random(walls.size() - 1));
		int choosenRoomX = 0;
		int choosenRoomY = 0;
		int forbiddenX = -1;
		int forbiddenY = -1;
		if (forbiddenPosition != null) {
			forbiddenX = forbiddenPosition.getX();
			forbiddenY = forbiddenPosition.getY();
		}
		boolean isMonoRoomDungeon = mapWidth == 1 && mapHeight == 1;
		// On choisit une salle au hasard 
		do {
			switch (choosenWall) {
				case TOP:
					choosenRoomX = MathUtils.random(mapWidth - 1);
					choosenRoomY = mapHeight - 1;
					break;
				case BOTTOM:
					choosenRoomX = MathUtils.random(mapWidth - 1);
					choosenRoomY = 0;
					break;
				case LEFT:
					choosenRoomX = 0;
					choosenRoomY = MathUtils.random(mapHeight - 1);
					break;
				case RIGHT:
					choosenRoomX = mapWidth - 1;
					choosenRoomY = MathUtils.random(mapHeight - 1);
					break;
			}
		// On continue tant qu'on tombe sur la position interdite,
		// sauf s'il n'y a qu'une seule pièce dans le donjon
		} while (!isMonoRoomDungeon
				&& forbiddenX == choosenRoomX
				&& forbiddenY == choosenRoomY);
		// Le PathData correspondant ne pointe vers aucune autre région
		PathData path = new PathData(door, choosenWall, -1, -1, -1, -1);
		// Le joueur ne peut pas ressortir par l'entrée
		if (door == DUNGEON_ENTRANCE_DOOR) {
			path.isCrossable = false;
		}
		areas[choosenRoomX][choosenRoomY].addPath(choosenWall, path);
		return pointManager.getPoint(choosenRoomX, choosenRoomY);
	}
	
	/**
	 * Crée un pathfinder permettant de trouver un chemin parmi les zones
	 * du donjon, en fonction des portes créées.
	 */
	protected AStar createPathfinder() {
		// On valide enfin que l'on peut atteindre la sortie depuis l'entrée.
		// Pour ce faire, on crée une carte virtuelle représentant le donjon.
		// Chaque pièce du donjon est une case du pathfinder, ainsi que 
		// chaque jonction possible entre les pièces.
		AStar pathfinder = new AStar(mapWidth * 2 - 1, mapHeight * 2 - 1, false);
		// Par défaut, toutes ces cases sont walkable. Nous allons à présent
		// indiquer que les seules cases walkable sont les pièces de donjon
		// et les cases qui représentent une jonction entre deux pièces où
		// une porte existe bel et bien.
		// Petite optimisation : on parcours le donjon du haut vers le bas, de
		// la gauche vers la droite. Seules les portes sur les murs droit et bas
		// seront donc prises en compte. En effet, une porte vers le haut aura
		// déjà été indiquée par une pièce précédente comme étant une porte vers
		// le bas. Idem pour les portes vers la gauche.
		int colInPathfinder;
		int rowInPathfinder;
		MapArea room;
		WorldElementData pathData = new WorldElementData(getPathType());
		for (int row = mapHeight - 1; row >= 0; row--) {
			for (int col = 0; col < mapWidth; col++) {
				colInPathfinder = col * 2;
				rowInPathfinder = row * 2;
				room = areas[col][row];
				
				// Chaque zone peut être parcourue.
				pathfinder.setWalkable(colInPathfinder, rowInPathfinder, true);
				
				// Chaque porte vers la droite ou le bas est walkable
				if (room.getPaths(BOTTOM).contains(pathData)) {
					// Porte vers le bas, donc la case du pathfinder correspondant
					// à cette jonction est en bas.
					pathfinder.setWalkable(colInPathfinder, rowInPathfinder - 1, true);
				}
				if (room.getPaths(RIGHT).contains(pathData)) {
					// Porte vers la droite, donc la case du pathfinder correspondant
					// à cette jonction est à droite.
					pathfinder.setWalkable(colInPathfinder + 1, rowInPathfinder, true);
				}
			}
		}
		return pathfinder;
	}

	@Override
	protected void fillArea(MapArea area) {
		// Dans le donjon, la mort d'un personnage est définitive
		area.setPermKillCharacters(true);
		
		// Création de la structure de la zone
		int width = area.getWidth();
		int height = area.getHeight();
//		AStar pathfinder = new AStar(width, height);
		for (int col=0; col < width; col++) {
   		 	for (int row=0; row < height; row++) {
   		 		// On dessine du sol partout
   		 		area.setGroundAt(col, row, GROUND_DATA);
   		 		
   		 		// Des murs sur le pourtour de la pièce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1
   		 		) {
   		 			area.setObjectAt(col, row, WALL_DATA);
//   		 			pathfinder.setWalkable(col, row, false);
   		 		} else {
   		 			// Du brouillard dans la pièce
   		 			//DBGarea.setFogAt(col, row, FOG_DATA);
   		 		}
   		 	}
        }
		
		// Création de murs DANS la pièce
		createWalls(area);
		
		// On s'assure à présent que toutes les portes sont accessibles depuis les autres portes.
		// Le cas échéant, on fait des trous dans les murs pour assurer un passage.
		// TODO
		
		// Ajout des personnages
		// TODO : Améliorer la gestion de la difficulté
		int nbRobots = 0;//DBGMathUtils.random(1, (int)(difficulty * 1.5) + 1);
		Point randomPos = new Point(-1, -1);
		for (int count = 0; count < nbRobots; count++) {
			// Recherche d'une position aléatoire disponible
			getRandomFreePos(randomPos, area);			
			CharacterData data = new CharacterData (
				RABITE,
				MathUtils.random(difficulty + 10, difficulty * 2 + 10),
				MathUtils.random(difficulty + 1, (int)((difficulty + 1) * 1.5)));
			data.speed = 4;
			area.setCharacterAt(randomPos.getX(), randomPos.getY(), data);
		}
	}
	
	private void createWalls(MapArea area) {
		// Choix d'un ou plusieurs décorateurs de zone
		List<AreaDecorator> decorators = new ArrayList<AreaDecorator>();
		int nbDecoratorsToCreate = MathUtils.random(3);
		for (int curDecorator = 0; curDecorator < nbDecoratorsToCreate; curDecorator++) {
			// DBG Pas très aléatoire comme choix ;)
			decorators.add(new RandomWallDecorator());
		}
		
		// Exécution des décorateurs
		for (AreaDecorator decorator : decorators) {
			decorator.decorate(area);
		}
	}

	private void getRandomFreePos(Point posToFill, MapArea area) {
	  	int col = -1;
	  	int row = -1;
	  	do {
        	col = MathUtils.random(area.getWidth() - 1);
        	row = MathUtils.random(area.getHeight() - 1);
	  	} while (!area.isEmpty(MapLevels.OBJECTS, col, row) || !area.isEmpty(MapLevels.CHARACTERS, col, row));
	  	posToFill.setXY(col, row);
	}

	/**
	 * Choisit aléatoirement une pièce pour y placer l'entrée et la sortie du donjon.
	 * L'algorithme fait en sorte de ne pas mettre les deux sur le même côté du donjon.
	 */
	@Override
	public void placeMainEntrances() {
		List<Borders> walls = new ArrayList<Borders>(Arrays.asList(Borders.values()));
		// Choix d'une pièce d'entrée
		entranceArea = createMainDoor(walls, DUNGEON_ENTRANCE_DOOR, null);
		// La pièce d'entrée est marquée comme étant accessible depuis l'entrée (logique ^^)
		linkArea(entranceArea);
		// Recherche de la position de départ dans la salle de départ. La boucle est
		// faite pour ne parcourir que les bords de la pièce car on sait que la porte
		// est placée sur un mur.
		MapArea room = areas[entranceArea.getX()][entranceArea.getY()];
		entrancePositionSearch: {
			for (int col = 0; col < room.getWidth(); col ++) {
				for (int row = 0; row < room.getHeight(); row ++) {
					if (room.getObjectAt(col, row).element == DUNGEON_ENTRANCE_DOOR) {
						entrancePosition = new Point(col, row);
						break entrancePositionSearch;
					}
				}
			}
		}
		
		
		// Choix d'une pièce de sortie
		exitRoom = createMainDoor(walls, DUNGEON_EXIT_DOOR, entranceArea);
		
		mainEntrancesPlaced = true;
	}
	
	@Override
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
					} else if (pos.equals(exitRoom)) {
						sb.append("B");
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
			// Passe n°2 pour dessiner les chemins vers le bas
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
}
