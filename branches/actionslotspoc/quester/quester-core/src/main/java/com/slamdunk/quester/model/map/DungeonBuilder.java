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
//			System.out.println("DungeonBuilder.validateDungeon() La validation du donjon ne sera pas faite car aucune sortie n'est d�finie.");
//			return true;
//		}
//		AStar pathfinder = createPathfinder();
//		System.out.println("DungeonBuilder.validateDungeon() " + pathfinder);
//		List<UnmutablePoint> path = pathfinder.findPath(
//			entranceArea.getX() * 2, entranceArea.getY() * 2,
//			exitRoom.getX() * 2, exitRoom.getY() * 2);
//		if (path == null) {
//			System.err.println("Impossible d'atteindre la sortie situ�e � " + exitRoom + " depuis l'entr�e situ�e � " + entranceArea);
//			return false;
//		} else {
//			System.out.println("Chemin vers la sortie :" + path);
//			return true;
//		}
//	}

	/**
	 * Cr�e une porte principale (ENTRANCE_DOOR ou EXIT_DOOR) sur le mur du donjon
	 * indiqu� par walls. Ce mur indique si la porte est sur le c�t� haut, bas,
	 * gauche ou droit du donjon. Un choix al�atoire de la pi�ce sur ce c�t�
	 * du donjon sera fait pour d�terminer o� sera cette porte.
	 * Une fois le mur choisi, il est retir� de la liste des murs possible walls,
	 * pour �viter de mettre la porte d'entr�e et de sortie sur le m�me c�t� du
	 * donjon.
	 * Si une position interdite est sp�cifi�e, alors la position choisie ne pourra
	 * pas �tre la m�me.
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
		// sauf s'il n'y a qu'une seule pi�ce dans le donjon
		} while (!isMonoRoomDungeon
				&& forbiddenX == choosenRoomX
				&& forbiddenY == choosenRoomY);
		// Le PathData correspondant ne pointe vers aucune autre r�gion
		PathData path = new PathData(door, choosenWall, -1, -1);
		// Le joueur ne peut pas ressortir par l'entr�e
		if (door == DUNGEON_ENTRANCE_DOOR) {
			path.isCrossable = false;
		}
		areas[choosenRoomX][choosenRoomY].addPath(choosenWall, path);
		return pointManager.getPoint(choosenRoomX, choosenRoomY);
	}
	
	/**
	 * Cr�e un pathfinder permettant de trouver un chemin parmi les zones
	 * du donjon, en fonction des portes cr��es.
	 */
	protected AStar createPathfinder() {
		// On valide enfin que l'on peut atteindre la sortie depuis l'entr�e.
		// Pour ce faire, on cr�e une carte virtuelle repr�sentant le donjon.
		// Chaque pi�ce du donjon est une case du pathfinder, ainsi que 
		// chaque jonction possible entre les pi�ces.
		AStar pathfinder = new AStar(mapWidth * 2 - 1, mapHeight * 2 - 1, false);
		// Par d�faut, toutes ces cases sont walkable. Nous allons � pr�sent
		// indiquer que les seules cases walkable sont les pi�ces de donjon
		// et les cases qui repr�sentent une jonction entre deux pi�ces o�
		// une porte existe bel et bien.
		// Petite optimisation : on parcours le donjon du haut vers le bas, de
		// la gauche vers la droite. Seules les portes sur les murs droit et bas
		// seront donc prises en compte. En effet, une porte vers le haut aura
		// d�j� �t� indiqu�e par une pi�ce pr�c�dente comme �tant une porte vers
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
				
				// Chaque zone peut �tre parcourue.
				pathfinder.setWalkable(colInPathfinder, rowInPathfinder, true);
				
				// Chaque porte vers la droite ou le bas est walkable
				if (room.getPaths(BOTTOM).contains(pathData)) {
					// Porte vers le bas, donc la case du pathfinder correspondant
					// � cette jonction est en bas.
					pathfinder.setWalkable(colInPathfinder, rowInPathfinder - 1, true);
				}
				if (room.getPaths(RIGHT).contains(pathData)) {
					// Porte vers la droite, donc la case du pathfinder correspondant
					// � cette jonction est � droite.
					pathfinder.setWalkable(colInPathfinder + 1, rowInPathfinder, true);
				}
			}
		}
		return pathfinder;
	}

	@Override
	protected void fillRoom(MapArea area) {
		// Dans le donjon, la mort d'un personnage est d�finitive
		area.setPermKillCharacters(true);
		
		// Cr�ation de la structure de la zone
		int width = area.getWidth();
		int height = area.getHeight();
//		AStar pathfinder = new AStar(width, height);
		for (int col=0; col < width; col++) {
   		 	for (int row=0; row < height; row++) {
   		 		// On dessine du sol partout
   		 		area.setGroundAt(col, row, GROUND_DATA);
   		 		
   		 		// Et des murs sur le pourtour de la pi�ce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1
   		 		) {
   		 			area.setObjectAt(col, row, WALL_DATA);
//   		 			pathfinder.setWalkable(col, row, false);
   		 		}
   		 	}
        }
		
//		// Cr�ation de murs DANS la pi�ce
//		for (int wallCount = 0; wallCount < 5; wallCount++) {
//			// Choisit un emplacement vide pour ajouter le mur
//   		 	int wallX = MathUtils.random(1, width - 2);
//   		 	int wallY = MathUtils.random(1, height - 2);
//   		 	if (area.getObjectAt(wallX, wallY).element != EMPTY) {
//   		 		continue;
//   		 	}
// 			
//   		 	// Place le mur
// 			area.setObjectAt(wallX, wallY, WALL_DATA);
// 			pathfinder.setWalkable(wallX, wallY, false);
//        }
//		// On s'assure que toutes les cases sont accessibles depuis les 4 portes qui pourraient exister
//		UnmutablePoint[] potentialDoors = new UnmutablePoint[]{
//			new UnmutablePoint(width / 2, height - 1),
//			new UnmutablePoint(width / 2, 0),
//			new UnmutablePoint(0, height / 2),
//			new UnmutablePoint(width - 1, height / 2),
//		};
//		List<UnmutablePoint> linked = new ArrayList<UnmutablePoint>();
//		List<UnmutablePoint> blocked = new ArrayList<UnmutablePoint>();
//		for (int col = 1; col < width - 1; col++) {
//			for (int row = 1; row < height - 1; row++) {
//				// Si la position est vide...
//				if (area.getObjectAt(col, row).element == EMPTY) {
//					UnmutablePoint pos = new UnmutablePoint(col, row);
//					// ... alors on regarde si on peut l'atteindre depuis les portes
//					for (UnmutablePoint door : potentialDoors) {
//						if (pathfinder.findPath(door.getX(), door.getY(), col, row) == null) {
//							// Cette case ne peut pas rejoindre au moins une des portes
//							blocked.add(pos);
//		   		 			break;
//						}
//					}
//				}
//			}
//		}

		// Ajout des personnages
		// TODO : Am�liorer la gestion de la difficult�
		int nbRobots = MathUtils.random(1, (int)(difficulty * 1.5) + 1);
		for (int count = 0; count < nbRobots; count++) {
			CharacterData data = new CharacterData (
				RABITE,
				MathUtils.random(difficulty + 10, difficulty * 2 + 10),
				MathUtils.random(difficulty + 1, (int)((difficulty + 1) * 1.5)));
			data.actFrequency = 2;
			data.speed = 4;
			area.addCharacter(data);
		}
	}
	
	/**
	 * Choisit al�atoirement une pi�ce pour y placer l'entr�e et la sortie du donjon.
	 * L'algorithme fait en sorte de ne pas mettre les deux sur le m�me c�t� du donjon.
	 */
	@Override
	public void placeMainEntrances() {
		List<Borders> walls = new ArrayList<Borders>(Arrays.asList(Borders.values()));
		// Choix d'une pi�ce d'entr�e
		entranceArea = createMainDoor(walls, DUNGEON_ENTRANCE_DOOR, null);
		// La pi�ce d'entr�e est marqu�e comme �tant accessible depuis l'entr�e (logique ^^)
		linkArea(entranceArea);
		// Recherche de la position de d�part dans la salle de d�part. La boucle est
		// faite pour ne parcourir que les bords de la pi�ce car on sait que la porte
		// est plac�e sur un mur.
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
		
		
		// Choix d'une pi�ce de sortie
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
}
