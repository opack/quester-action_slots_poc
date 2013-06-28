package com.slamdunk.quester.display.hud.minimap;

import static com.slamdunk.quester.model.map.MapElements.DUNGEON_EXIT_DOOR;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

public class DungeonMiniMap extends MiniMap {
	private final Drawable drawableExit;
	private Point exitRoom;
	
	public DungeonMiniMap(int mapWidth, int mapHeight) {
		super(mapWidth, mapHeight);
		exitRoom = new Point(-1, -1);
		drawableExit = new TextureRegionDrawable(Assets.areaExit);
	}
	
	public void init(int miniMapWidth, int miniMapHeight, MapArea[][] rooms) {
		// Initialisation standard
		super.init(miniMapWidth, miniMapHeight);
		
		// Recherche de la pi�ce de sortie
		for (int row = getMapHeight() - 1; row >= 0; row--) {
			for (int col = 0; col < getMapWidth(); col++) {
				// M�j des coordonn�es de la pi�ce de sortie
				if (rooms[col][row].containsPath(DUNGEON_EXIT_DOOR)) {
					exitRoom.setXY(col, row);
					return;
				}
			}
		}
	}


	/**
	 * Indique o� se trouve le joueur. Cette m�thode va
	 * mettre en �vidence cette pi�ce sur la mini-carte.
	 * @param x
	 * @param y
	 */
	public void setPlayerRoom(int x, int y) {
		// Si la salle n'a pas chang�, on ne fait rien
		if (currentPlayerRegion.getX() == x && currentPlayerRegion.getY() == y) {
			return;
		}
		
		// L'actuelle salle o� se trouve le joueur n'est plus la playerRoom. On met � jour l'image
		// si on avait d�j� connaissance de l'emplacement du joueur
		if (currentPlayerRegion.getX() != -1 && currentPlayerRegion.getY() != -1) {
			if (currentPlayerRegion.getX() == exitRoom.getX() && currentPlayerRegion.getY() == exitRoom.getY()) {
				// On vient de quitter la pi�ce de sortie
				areas[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableExit);
			} else {
				// On vient de quitter une pi�ce banale
				areas[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableVisited);
			}
		}
		
		// La nouvelle playerRoom est celle indiqu�e
		Point oldPlayerRegion = new Point(currentPlayerRegion);
		currentPlayerRegion.setXY(x, y);
		areas[x][y].setDrawable(drawableCurrent);
		
		// Mise � jour des chemins
		updatePaths(oldPlayerRegion, currentPlayerRegion);
	}
}
