package com.slamdunk.quester.display.map;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.actors.CastleActor;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.ClipActor;
import com.slamdunk.quester.display.actors.EntranceDoorActor;
import com.slamdunk.quester.display.actors.ExitDoorActor;
import com.slamdunk.quester.display.actors.GroundActor;
import com.slamdunk.quester.display.actors.PathMarkerActor;
import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.logic.controlers.DungeonDoorControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.GroundControler;
import com.slamdunk.quester.logic.controlers.PathMarkerControler;
import com.slamdunk.quester.logic.controlers.PathToAreaControler;
import com.slamdunk.quester.logic.controlers.RabiteControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.data.PathMarkerData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapElements;
import com.slamdunk.quester.model.map.MapLevels;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

/**
 * Afin d'afficher une MapArea, le MapRenderer cr�e des WorldElementActor. Ils sont
 * ensuite manipul�s via une ActorMap, qui aide � les disposer correctement en couches
 * et en quadrillage.
 */
public class MapRenderer implements CharacterListener {
	protected final OrthographicCamera camera;
	protected ActorMap map;
	private List<Point> overlayPath;
	private MapArea renderedArea;
	
	protected final Stage stage;
	private final int cellHeight;
	private final int cellWidth;
	
	public MapRenderer(int worldCellWidth, int worldCellHeight) {
		this.cellWidth = worldCellWidth;
		this.cellHeight = worldCellHeight;
		
        // Cr�ation de la cam�ra
 		camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		
 		// Cr�ation du Stage
 		stage = new Stage();
 		stage.setCamera(camera);
 		
 		// Cr�ation de la liste qui contiendra les WorldActor utilis�s pour l'affichage du chemin du joueur
		overlayPath = new ArrayList<Point>();
	}
	
	public void addCharacter(CharacterActor character) {
		map.addCharacter(character);
		// Ajout du renderer en tant que listener : lorsque le personnage, on
		// veut mettre � jour l'area
		character.getControler().addListener(this);
	}

	public void buildMap(MapArea area, Point currentRoom) {
		if (map != null) {
			// Suppression de la map du Stage s'il en existe une et que les dimensions sont diff�rentes
			stage.getRoot().removeActor(map);
		}
		// Cr�ation d'une map aux dimensions souhait�es
		map = new ActorMap(area.getWidth(), area.getHeight(), cellWidth, cellHeight);
        
        // Cr�e une couche de fond
		MapLayer backgroundLayer = map.addLayer(MapLevels.GROUND);
        
        // Cr�e une couche avec les objets
		MapLayer objectsLayer = map.addLayer(MapLevels.OBJECTS);
        
        // Cr�e une couche avec les personnages
		MapLayer charactersLayer = map.addLayer(MapLevels.CHARACTERS);
        
        // Cr�e une couche de brouillard
		MapLayer fogLayer = map.addLayer(MapLevels.FOG);
        
        // Cr�e une couche avec diverses informations
        map.addLayer(MapLevels.OVERLAY);
        
        // Ajout de la map au Stage
        stage.addActor(map);
		
        // Stocke la map logique actuellement affich�e
		this.renderedArea = area;
        
        // Cr�ation des �l�ments de la carte
	 	for (int col = 0; col < area.getWidth(); col++) {
   		 	for (int row = 0; row < area.getHeight(); row++) {
   		 		createActor(col, row, area.getGroundAt(col, row), backgroundLayer);
   		 		createActor(col, row, area.getObjectAt(col, row), objectsLayer);
   	        	createActor(col, row, area.getCharacterAt(col, row), charactersLayer);
   		 		createActor(col, row, area.getFogAt(col, row), fogLayer);
   		 	}
        }
	}

	public void clearOverlay() {
		MapLayer overlayLayer = map.getLayer(MapLevels.OVERLAY);
		overlayLayer.clearLayer();
	}

	public void clearPath() {
		if (!overlayPath.isEmpty()) {
			MapLayer overlayLayer = map.getLayer(MapLevels.OVERLAY);
			for (Point pos : overlayPath) {
				overlayLayer.removeCell(pos.getX(), pos.getY());
			}
		}
	}
	
	private WorldElementActor createActor(int col, int row, WorldElementData data, MapLayer layer) {
		if (data == null) {
			System.out.println("MapRenderer.createActor() Pas de data � " + col + " " + row);
			return null;
		}
		boolean isCharacter = false;
		WorldElementControler controler = null;
		switch (data.element) {
		 	case CASTLE:
		 		controler = new CastleControler(
		 			(CastleData)data, 
		 			new CastleActor(Assets.castle));		 		
				break;
			case COMMON_DOOR:
				controler = new DungeonDoorControler(
					(PathData)data, 
					new PathToAreaActor(Assets.commonDoor));
				break;
			case DUNGEON_ENTRANCE_DOOR:
				controler = new DungeonDoorControler(
					(PathData)data, 
					new EntranceDoorActor());
				break;
		 	case DUNGEON_EXIT_DOOR:
		 		controler = new DungeonDoorControler(
					(PathData)data, 
					new ExitDoorActor());
				break;
		 	case FOG:
		 		controler = new GroundControler(
					data, 
					new GroundActor(Assets.fog));
				break;
	 		case GRASS:
	 			controler = new GroundControler(
					data, 
					new GroundActor(Assets.grass));
				break;
	 		case GROUND:
	 			controler = new GroundControler(
					data, 
					new GroundActor(Assets.ground));
				break;
	 		case PATH_MARKER:
	 			PathMarkerActor actor = null;
	 			PathMarkerData pathMarkerData = (PathMarkerData)data;
	 			if (pathMarkerData.isReachable) {
		 			if (pathMarkerData.isLastMarker) {
		 				actor = new PathMarkerActor(Assets.menu_move);
		 			} else {
		 				actor = new PathMarkerActor(Assets.pathMarker);
		 			}
	 			} else {
	 				if (pathMarkerData.isLastMarker) {
		 				actor = new PathMarkerActor(Assets.menu_move_disabled);
		 			} else {
		 				actor = new PathMarkerActor(Assets.pathMarkerDisabled);
		 			}
	 			}
	 			Quester.getInstance().getHUDRenderer().getActionSlots().addPathMarker(actor);
		 		controler = new PathMarkerControler(
					data, 
					actor);
				break;
			case PATH_TO_REGION:
				controler = createPathToArea((PathData)data);
				break;
			case PLAYER:
				controler = GameControler.instance.getPlayer();
				CharacterActor playerActor = new PlayerActor();
				controler.setActor(playerActor);
				isCharacter = true;
//DBG				controler.getAI().init();
				break;
			case RABITE:
				RabiteActor rabiteActor = new RabiteActor();
				RabiteControler rabite = new RabiteControler(
					(CharacterData)data, 
					rabiteActor);
				rabite.addListener(GameControler.instance);
        		rabite.getData().name = "Rabite" + rabite.getId();
        		// Tant qu'il n'est pas d�couvert, le rabite est invisible et inactif
        		//rabite.setEnabled(false);
        		isCharacter = true;
        		controler = rabite;
        		break;
			case ROCK:
				controler = new WorldElementControler(
					data, 
					new WorldElementActor(Assets.rock));
				break;
	 		case VILLAGE:
	 			controler = new WorldElementControler(
					data, 
					new WorldElementActor(Assets.village));
				break;
			case WALL:
				controler = new WorldElementControler(
					data, 
					new WorldElementActor(Assets.wall));
				break;
			case EMPTY:
			default:
				// Case vide ou avec une valeur inconnue: rien � faire :)
				return null;
		}
		WorldElementActor actor = controler.getActor();
		actor.setControler(controler);
		actor.setPositionInWorld(col, row);
		
		if (isCharacter) {
			addCharacter((CharacterActor)actor);
		}
		
		layer.setCell(new LayerCell(String.valueOf(controler.getId()), col, row, actor));
		// Si cet �l�ment est solide et que la cellule �tait marqu�e comme walkable, elle ne l'est plus
		if (data.isSolid && map.isWalkable(col, row)) {
			map.setWalkable(col, row, false);
		}
		return actor;
	}

	public WorldElementActor createActor(int col, int row, WorldElementData data, MapLevels level) {
		MapLayer layer = map.getLayer(level);
		return createActor(col, row, data, layer);
	}

//DBG	public void createCharacters(MapArea area) {
//		MapLayer charactersLayer = map.getLayer(MapLevels.CHARACTERS);
//		
//		// Cr�ation des personnages
//        for (CharacterData character : area.getCharacters()) {
//        	// Recherche d'une position al�atoire disponible
//        	int col = -1;
//        	int row = -1;
//        	do {
//	        	col = MathUtils.random(area.getWidth() - 1);
//	        	row = MathUtils.random(area.getHeight() - 1);
//        	} while (!map.isEmpty(ActorMap.LAYERS_OBSTACLES, col, row));
//        	
//        	// Cr�ation et placement de l'acteur
//        	createActor(col, row, character, charactersLayer);
//        }
//	}
	
	private PathToAreaControler createPathToArea(PathData data) {
		PathToAreaActor actor = null;
		switch (data.border) {
		case TOP:
			actor = new PathToAreaActor(Assets.pathUp);
			break;
		case BOTTOM:
			actor = new PathToAreaActor(Assets.pathDown);
			break;
		case LEFT:
			actor = new PathToAreaActor(Assets.pathLeft);
			break;
		case RIGHT:
			actor = new PathToAreaActor(Assets.pathRight);
			break;
		}
 		
 		return new PathToAreaControler(
			(PathData)data, 
			actor);
	}
	
	//	 TODO Cr�er une m�thode createVisualEffect qui cr�e un ClipActor destin� � contenir
//	 un effet sp�cial, � le jouer et � dispara�tre.
//	 Cette m�thode servira pour la mort des personnages, les coups re�us, les sorts...
//	 Le code sera similaire � celui r�alis� dans CharacterControler.die().
//	 Les effets sp�ciaux seront r�pertori�s dans une table et conserv�s dans un cache
//	 pour �viter de les charger plusieurs fois. Plusieurs ClipActor pourront se servir
//	 du m�me Clip car la position du clip est mise � jour dans ClipActor au moment du dessin.
	public void createVisualEffect(String name, WorldElementActor target) {
		// R�cup�re le clip correspondant � cet effet visuel
		Clip clip = Assets.getVisualEffectClip(name);
		
		// Cr�ation d'un ClipActor pour pouvoir afficher le clip � l'�cran.
		// Le ClipActor est positionn� au m�me endroit que l'Actor qui va dispara�tre
		final ClipActor effect = new ClipActor();
		effect.clip = clip;
		if (target != null) {
			effect.setPosition(target.getX(), target.getY());
			effect.setSize(target.getWidth(), target.getHeight());
		}
		
		// Ajout du ClipActor � la couche d'overlay, pour que l'affichage reste coh�rent
		final MapLayer overlay = map.getLayer(MapLevels.OVERLAY);		
		overlay.addActor(effect);
		
		// Placement du clip au milieu de la zone de dessin
		if (target != null) {
			clip.drawArea.width = target.getWidth();
			clip.drawArea.height = target.getHeight();
		} else {
			clip.drawArea.width = map.getCellWidth();
			clip.drawArea.height = map.getCellHeight();
		}
		clip.alignX = 0.5f;
		clip.alignY = 0.5f;
		
		// A la fin du clip, on supprime l'acteur
		clip.setLastKeyFrameRunnable(new Runnable(){
			@Override
			public void run() {
				// Une fois l'animation achev�e, on retire cet acteur
				overlay.removeActor(effect);
			}
		});
	}

	public void dispose () {
		stage.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public WorldElementControler getControlerAt(int x, int y, MapLevels layerName) {
		MapLayer layer = map.getLayer(layerName);
		LayerCell cell = layer.getCell(x, y);
		if (cell == null) {
			return null;
		}
		return ((WorldElementActor)cell.getActor()).getControler();
	}
	
	public ActorMap getMap() {
		return map;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public int getCellHeight() {
		return cellHeight;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void highlightDetectionArea(int baseX, int baseY, int[][] detectionArea, Color color) {
		WorldElementActor actor;
		Image image;
		for (int[] detectPos : detectionArea) {
			actor = map.getTopElementAt(baseX + detectPos[0], baseY + detectPos[1], MapLevels.GROUND);
			image = actor.getImage();
			if (image != null) {
				image.setColor(color);
			}
		}
	}

	@Override
	public void onActionPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCharacterDeath(CharacterControler character) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCharacterMoved(CharacterControler character, int oldCol, int oldRow) {
		// Mise � jour des donn�es de l'ActorMap
		WorldElementActor actor = character.getActor();
		final int newCol = actor.getWorldX();
		final int newRow = actor.getWorldY();
		map.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
		
		// Suppression du brouillard de guerre si c'est le joueur qui a boug�
		if (character.getData().element == MapElements.PLAYER) {
			MapLayer fog = map.getLayer(MapLevels.FOG);
			// Suppression du brouillard sur la ligne au-dessus du joueur
			removeFog(newCol - 1, newRow + 1, fog);
			removeFog(newCol, newRow + 1, fog);
			removeFog(newCol + 1, newRow + 1, fog);
			// Suppression du brouillard sur la m�me ligne que le joueur
			removeFog(newCol - 1, newRow, fog);
			removeFog(newCol, newRow, fog);
			removeFog(newCol + 1, newRow, fog);
			// Suppression du brouillard sur la ligne au-dessous du joueur
			removeFog(newCol - 1, newRow - 1, fog);
			removeFog(newCol, newRow - 1, fog);
			removeFog(newCol + 1, newRow - 1, fog);
		}
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}

	private void removeFog(int col, int row, MapLayer fog) {
		fog.removeCell(col, row);
		renderedArea.setFogAt(col, row, null);
	}

	public void render() {
		stage.draw();
	}

	public void showPath(List<Point> path) {
		if (path == null || path.isEmpty()) {
			return;
		}
		MapLayer overlayLayer = map.getLayer(MapLevels.OVERLAY);
		final int walkDistance = GameControler.instance.getPlayer().getData().walkDistance;
		final int posCount = path.size();
		for (int curPos = 0; curPos < posCount; curPos++) {
			PathMarkerData data = new PathMarkerData();
			data.isLastMarker = curPos == posCount - 1;
			data.isReachable = curPos < walkDistance;
			
			Point pos = path.get(curPos);
	 		overlayPath.add(pos);
	 		
	 		createActor(pos.getX(), pos.getY(), data, overlayLayer);
		}
	}
}
