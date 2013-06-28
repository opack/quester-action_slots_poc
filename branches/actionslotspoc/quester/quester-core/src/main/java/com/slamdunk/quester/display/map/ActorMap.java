package com.slamdunk.quester.display.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.map.MapLevels;
import com.slamdunk.quester.model.points.Point;

/**
 * Carte du jeu compos�e d'Actors, dispos�s sur des MapLayer.
 * Cette classe g�re �galement le pathfinding et le raycasting.
 * Elle repr�sente le pendant "physique" d'une MapArea.
 */
public class ActorMap extends Group {
	/**
	 * Couches de la map pouvant contenir des obstacles
	 */
	public static MapLevels[] LAYERS_OBSTACLES;
	
	/**
	 * Retourne la distance en ligne droite entre les deux positions
	 * indiqu�es.
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return
	 */
	public static double distance(int fromX, int fromY, int toX, int toY) {
		return Math.hypot((double)(fromX - toX), (double)(fromY - toY));
	}
	private final float cellHeight;
	
	/**
	 * Taille physique d'une cellule sur l'�cran
	 */
	private final float cellWidth;
	protected final List<CharacterControler> characters;
	/**
	 * Contient chaque couche de la map
	 */
	private Map<MapLevels, MapLayer> layers;
	private final int mapHeight;

	/**
	 * Taille logique de la map
	 */
	private final int mapWidth;
	
	/**
	 * Map servant de support au pathfinding
	 */
	private AStar pathfinder;
	
	public ActorMap(int mapWidth, int mapHeight, float cellWidth, float cellHeight) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		
		layers = new HashMap<MapLevels, MapLayer>();
		
		pathfinder = new AStar(mapWidth, mapHeight);
		
		characters = new ArrayList<CharacterControler>();
		 
		 // Cr�e un tableau regroupant les couches pouvant contenir des obstacles, du plus haut au plus bas
		LAYERS_OBSTACLES = new MapLevels[]{MapLevels.CHARACTERS, MapLevels.OBJECTS};
	}

	public void addCharacter(CharacterControler character) {
		characters.add(character);
		MapLayer charactersLayer = getLayer(MapLevels.CHARACTERS);
        charactersLayer.setCell(new LayerCell(String.valueOf(character.getId()), character.getActor().getWorldX(), character.getActor().getWorldY(), character.getActor()));
	}

	public MapLayer addLayer(MapLevels level) {
		MapLayer layer = new MapLayer(mapWidth, mapHeight, cellWidth, cellHeight);
		layer.setLevel(level);
		
		layers.put(level, layer);
		addActor(layer);
		return layer;
	}

	/**
	 * Efface la carte en supprimant les donn�es qu'elle contient
	 * mais pas les diff�rentes couches (qui sont alors vides).
	 */
	
	public void clearMap() {
		// Nettoyage des couches
		for (MapLayer layer : layers.values()) {
			layer.clearLayer();
		}
		// RAZ du pathfinder
		pathfinder.reset();

		characters.clear();
	}

	/**
	 * Retourne true si la couche � ce niveau peut contenir des obstacles
	 */
	private boolean containsObstacles(MapLevels level) {
		for (MapLevels obstacleLayer : LAYERS_OBSTACLES) {
			if (level == obstacleLayer) {
				return true;
			}
		}
		return false;
	}
	
	public List<Point> findPath(int fromX, int fromY, int toX, int toY) {
		return pathfinder.findPath(fromX, fromY, toX, toY, true);
	}
	

	public List<Point> findPath(int fromX, int fromY, int toX, int toY, boolean ignoreArrivalLit) {
		return pathfinder.findPath(fromX, fromY, toX, toY, ignoreArrivalLit);
	}
	
	/**
	 * Hauteur d'une cellule de la carte
	 * @return
	 */
	public float getCellHeight() {
		return cellHeight;
	}
	
	/**
	 * Largeur d'une cellule de la carte
	 * @return
	 */
	public float getCellWidth() {
		return cellWidth;
	}

	/**
	 * Renvoit la liste des personnages de la carte
	 */
	
	public List<CharacterControler> getCharacters() {
		return characters;
	}
	
	/**
	 * Retourne une liste contenant l'ensemble des �l�ments � la position indiqu�e,
	 * depuis la couche la plus �lev�e vers la plus basse
	 * @param col
	 * @param row
	 * @return
	 */
	
	public List<WorldElementActor> getElementsAt(int col, int row) {
		final List<WorldElementActor> actors = new ArrayList<WorldElementActor>();
		LayerCell cell;
		for (MapLayer layer : getLayers()) {
			cell = layer.getCell(col, row);
			if (cell != null) {
				actors.add((WorldElementActor)cell.getActor());
			}
		}
		return actors;
	}

	public MapLayer getLayer(MapLevels level) {
		return layers.get(level);
	}
	
	public MapLayer getLayerContainingCell(String cellId) {
		for (MapLayer layer : layers.values()) {
			if (layer.getCell(cellId) != null) {
				return layer;
			}
		}
		return null;
	}
	
	public Collection<MapLayer> getLayers() {
		return layers.values();
	}

	/**
	 * Hauteur de la carte, en cellules.
	 * @return
	 */
	public int getMapHeight() {
		return mapHeight;
	}
	
	/**
	 * Largeur de la carte, en cellules.
	 * @return
	 */
	public int getMapWidth() {
		return mapWidth;
	}
	
	public AStar getPathfinder() {
		return pathfinder;		
	}
	
	/**
	 * Retourne l'�l�ment � la position indiqu�e sur la couche la plus �lev�e de la carte
	 * @param col
	 * @param row
	 * @return
	 */
	public WorldElementActor getTopElementAt(int x, int y) {
		LayerCell cell;
		for (MapLayer layer : layers.values()) {
			cell = layer.getCell(x, y);
			if (cell != null) {
				return (WorldElementActor)cell.getActor();
			}
		}
		return null;
	}

	/**
	 * Retourne l'�l�ment � la position indiqu�e en ne regardant que les couches dont le niveau
	 * est indiqu� dans le tableau layers
	 */
	public WorldElementActor getTopElementAt(int x, int y, MapLevels... layersLevels) {
		LayerCell cell;
		for (MapLevels level : layersLevels) {
			cell = layers.get(level).getCell(x, y);
			if (cell != null) {
				return (WorldElementActor)cell.getActor();
			}
		}
		return null;
	}

	public boolean isEmpty(int x, int y) {
		return isEmptyAbove(MapLevels.GROUND, x, y);
	}

	public boolean isEmpty(MapLevels[] levels, int x, int y) {
		for (MapLevels level : levels) {
			if (!layers.get(level).isEmpty(x, y)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * V�rifie que toutes les couches STRICTEMENT sup�rieures au niveau
	 * indiqu� n'ont aucune cellule aux coordonn�es indiqu�es.
	 * @param aboveLevel
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEmptyAbove(MapLevels aboveLevel, int x, int y) {
		final int minLevel = aboveLevel.ordinal();
		MapLevels[] levels = MapLevels.values();
		for (int level = levels.length - 1; level > minLevel; level--) {
			if (!layers.get(levels[level]).isEmpty(x, y)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * V�rifie que toutes les couches STRICTEMENT sup�rieures et
	 * inf�rieures aux niveaux indiqu�s n'ont aucune cellule aux
	 * coordonn�es indiqu�es.
	 * @param aboveLevel
	 * @param belowLevel
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEmptyBetween(MapLevels aboveLevel, MapLevels belowLevel, int x, int y) {
		final int maxLevel = belowLevel.ordinal();
		final int minLevel = aboveLevel.ordinal();
		MapLevels[] levels = MapLevels.values();
		for (int level = maxLevel - 1; level > minLevel; level--) {
			if (!layers.get(levels[level]).isEmpty(x, y)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValidPosition(int x, int y) {
		return x >= 0 && x < mapWidth
			&& y >= 0 && y < mapHeight;
	}

	public boolean isWalkable(int col, int row) {
		return pathfinder.isWalkable(col, row);
	}

	/**
	 * Indique si la cible mentionn�e peut �tre atteinte depuis le point de vue indiqu�.
	 * @param pointOfView
	 * @param target
	 * @param range
	 * @return
	 */
	public boolean isWithinRangeOf(WorldElementActor pointOfView, WorldElementActor target, int range) {
		MapLayer layer = getLayerContainingCell(String.valueOf(pointOfView.getControler().getId()));
		if (layer == null) {
			return false;
		}
		return layer.isInSight(
			pointOfView.getWorldX(), pointOfView.getWorldY(),
			target.getWorldX(), target.getWorldY(),
			range);
	}
	
	/**
	 * Supprime l'�l�ment indiqu� de la carte
	 * @param element
	 * @return 
	 */
	public WorldElementActor removeElement(WorldElementActor actor) {
		MapLayer layer = getLayerContainingCell(String.valueOf(actor.getControler().getId()));
		if (layer != null) {
			return removeElementAt(layer, actor.getWorldX(), actor.getWorldY());
		}
		return null;
	}

	public WorldElementActor removeElementAt(MapLayer layer, int x, int y) {
		if (layer != null) {
			LayerCell removed = layer.removeCell(x, y);
			if (removed != null) {
				WorldElementActor actor = (WorldElementActor)removed.getActor();
				return actor;
			}			
		}
		return null;
	}

	public boolean setCell(MapLevels layerId, LayerCell cell) {
		if (layerId == null) {
			return false;
		}
		MapLayer layer = layers.get(layerId);
		if (layer == null) {
			return false;
		}
		layer.setCell(cell);
		return true;
	}
	
	public void setWalkable(int col, int row, boolean isWalkable) {
		pathfinder.setWalkable(col, row, isWalkable);
	}
	
	/**
	 * Met � jour la carte et l'�l�ment indiqu� en prenant en compte l'ancienne et la nouvelle
	 * position indiqu�es.
	 * @param element
	 * @param oldCol
	 * @param oldRow
	 * @param newCol
	 * @param newRow
	 */
	public void updateMapPosition(WorldElementActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		WorldElementControler controler = actor.getControler();
		MapLayer layer = getLayerContainingCell(String.valueOf(controler.getId()));
		if (layer != null) {
			layer.moveCell(oldCol,  oldRow,  newCol, newRow, false);
			// Mise � jour du pathfinder si l'objet appartenait � une couche d'obstacles
			if (containsObstacles(layer.getLevel())) {
				// On part du principe qu'il n'y a qu'un seul objet solide)
				// par case. Du coup lorsqu'un objet est d�plac�, solide ou non,
				// son ancienne position est walkable.
				setWalkable(oldCol, oldRow, true);
				// La walkability de la nouvelle position d�pend de l'acteur.
				// Pour les lumi�res et ombre c'est identique : la cellule n'est pas traversable
				// si l'acteur est solide.
				boolean isWalkable = !controler.getData().isSolid;
				setWalkable(newCol, newRow, isWalkable);
			}
		}
	}
}
