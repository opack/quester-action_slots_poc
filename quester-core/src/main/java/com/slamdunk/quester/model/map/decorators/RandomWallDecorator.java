package com.slamdunk.quester.model.map.decorators;

import static com.slamdunk.quester.model.map.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.model.map.MapElements.PATH_TO_REGION;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.logic.controlers.Neighbors;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.Borders;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapElements;

public class RandomWallDecorator implements AreaDecorator {

	@Override
	public void decorate(MapArea area) {
		// Choix d'un côté au hasard
		Borders[] borders = Borders.values();
		Borders border = borders[MathUtils.random(borders.length - 1)];
		
		// Choix d'une taille de mur et d'une position au hasard
		int length = 0;
		int positionX = 0;
		int positionY = 0;
		int directionX = 0;
		int directionY = 0;
		switch (border) {
		case BOTTOM:
			length = MathUtils.random(1, area.getWidth() / 2);
			positionX = MathUtils.random(1, area.getWidth() - 2);
			positionY = 1;
			directionX = 0;
			directionY = +1;
			break;
		case TOP:
			length = MathUtils.random(1, area.getWidth() / 2);
			positionX = MathUtils.random(1, area.getWidth() - 2);
			positionY = area.getHeight() - 2;
			directionX = 0;
			directionY = -1;
			break;
		case LEFT:
			length = MathUtils.random(1, area.getWidth() / 2);
			positionX = 1;
			positionY = MathUtils.random(1, area.getWidth() - 2);
			directionX = +1;
			directionY = 0;
			break;
		case RIGHT:
			length = MathUtils.random(1, area.getHeight() / 2);
			positionX = area.getHeight() - 2;
			positionY = MathUtils.random(1, area.getHeight() - 2);
			directionX = -1;
			directionY = 0;
			break;
		}
		
		// Création du mur
		createWall(area, positionX, positionY, directionX, directionY, length);
		
		// Vérifie s'il y a une porte qui sera bloquée par ce mur.
		// Le cas échéant, supprime la case qui bloque.
		// ...
	}

	private void createWall(MapArea area, int positionX, int positionY, int offsetX, int offsetY, int length) {
		for (int curLength = 0; curLength < length; curLength++) {
			// Création du mur s'il n'est pas collé à une porte (ce qui indiquerait qu'il va la bloquer)
			boolean canCreateWall = true;
			for (int[] neighbor : Neighbors.NEIGHBORS_PLUS.values()) {
				WorldElementData data = area.getObjectAt(positionX + neighbor[0], positionY + neighbor[1]);
				if (data != null) {
					MapElements element = data.element;
					if (element == COMMON_DOOR
					|| element == DUNGEON_ENTRANCE_DOOR
					|| element == DUNGEON_EXIT_DOOR
					|| element == PATH_TO_REGION) {
						canCreateWall = false;
						break;
					}
				}
			}
			if (canCreateWall) {
				area.setObjectAt(positionX, positionY, WorldElementData.WALL_DATA);
			} else {
				System.out.printf("RandomWallDecorator.createWall() area %d;%d not putting a wall at %d;%d\n", area.getX(), area.getY(), positionX, positionY);
			}
			
			// Mise à jour de la position pour le prochain mur
			positionX += offsetX;
			positionY += offsetY;
		}
	}

}
