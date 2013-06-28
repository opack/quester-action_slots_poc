package com.slamdunk.quester.display.map;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;
import static com.slamdunk.quester.model.data.WorldElementData.PATH_MARKER_DATA;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.actors.CastleActor;
import com.slamdunk.quester.display.actors.ClipActor;
import com.slamdunk.quester.display.actors.EntranceDoorActor;
import com.slamdunk.quester.display.actors.ExitDoorActor;
import com.slamdunk.quester.display.actors.GroundActor;
import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.logic.controlers.DungeonDoorControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PathToAreaControler;
import com.slamdunk.quester.logic.controlers.RabiteControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapLevels;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

/**
 * Afin d'afficher une MapArea, le MapRenderer cr�e des WorldElementActor. Ils sont
 * ensuite manipul�s via une ActorMap, qui aide � les disposer correctement en couches
 * et en quadrillage.
 */
public class MapRenderer {
	protected final OrthographicCamera camera;
	protected final ActorMap map;
	private List<Point> overlayPath;
	
	protected final Stage stage;
	
	public MapRenderer(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
        map = new ActorMap(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
        
        // Cr�e une couche de fond
        map.addLayer(MapLevels.GROUND);
        
        // Cr�e une couche avec les objets
        map.addLayer(MapLevels.OBJECTS);
        
        // Cr�e une couche avec les personnages
        map.addLayer(MapLevels.CHARACTERS);
        
        // Cr�e une couche de brouillard
        map.addLayer(MapLevels.FOG);
        
        // Cr�e une couche avec diverses informations
        map.addLayer(MapLevels.OVERLAY);
        
        // Cr�ation de la cam�ra
 		camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		
 		// Cr�ation du Stage
 		stage = new Stage();
 		stage.setCamera(camera);
 		stage.addActor(map);
 		
 		// Cr�ation de la liste qui contiendra les WorldActor utilis�s pour l'affichage du chemin du joueur
		overlayPath = new ArrayList<Point>();
	}
	
	public void buildMap(MapArea area, Point currentRoom) {
		MapLayer backgroundLayer = map.getLayer(MapLevels.GROUND);
        MapLayer objectsLayer = map.getLayer(MapLevels.OBJECTS);
        MapLayer fogLayer = map.getLayer(MapLevels.FOG);
        
		// Nettoyage de la pi�ce actuelle
		map.clearMap();
        
        // Cr�ation du fond, des objets et du brouillard
	 	for (int col=0; col < area.getWidth(); col++) {
   		 	for (int row=0; row < area.getHeight(); row++) {
   		 		createActor(col, row, area.getGroundAt(col, row), backgroundLayer);
   		 		createActor(col, row, area.getObjectAt(col, row), objectsLayer);
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

	private void createActor(int col, int row, WorldElementData data, MapLayer layer) {
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
		 	case PATH_MARKER:
		 		controler = new WorldElementControler(
					data, 
					new GroundActor(Assets.pathMarker));
				break;
	 		case GRASS:
	 			controler = new WorldElementControler(
					data, 
					new GroundActor(Assets.grass));
				break;
	 		case GROUND:
	 			controler = new WorldElementControler(
					data, 
					new GroundActor(Assets.ground));
				break;
			case PATH_TO_REGION:
				controler = createPathToArea((PathData)data);
				break;
			case RABITE:
				RabiteControler rabite = new RabiteControler(
					(CharacterData)data, 
					new RabiteActor());
				rabite.addListener(GameControler.instance);
        		rabite.getData().name = "Rabite" + rabite.getId();
        		rabite.setPathfinder(map.getPathfinder());
        		map.addCharacter(rabite);
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
				return;
		}
		WorldElementActor actor = controler.getActor();
		actor.setControler(controler);
		actor.setPositionInWorld(col, row);
		
		layer.setCell(new LayerCell(String.valueOf(controler.getId()), col, row, actor));
		// Si cet �l�ment est solide et que la cellule �tait marqu�e comme walkable, elle ne l'est plus
		if (data.isSolid && map.isWalkable(col, row)) {
			map.setWalkable(col, row, false);
		}
	}

	public void createCharacters(MapArea area) {
		MapLayer charactersLayer = map.getLayer(MapLevels.CHARACTERS);
		
		// Cr�ation des personnages
        for (CharacterData character : area.getCharacters()) {
        	// Recherche d'une position al�atoire disponible
        	int col = -1;
        	int row = -1;
        	do {
	        	col = MathUtils.random(area.getWidth() - 1);
	        	row = MathUtils.random(area.getHeight() - 1);
        	} while (!map.isEmpty(ActorMap.LAYERS_OBSTACLES, col, row));
        	
        	// Cr�ation et placement de l'acteur
        	createActor(col, row, character, charactersLayer);
        }
	}
	
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

	public void render() {
		stage.draw();
	}

	public void showPath(List<Point> path) {
		MapLayer overlayLayer = map.getLayer(MapLevels.OVERLAY);
		for (Point pos : path) {
			createActor(pos.getX(), pos.getY(), PATH_MARKER_DATA, overlayLayer);
	 		overlayPath.add(pos);
		}
	}
}
