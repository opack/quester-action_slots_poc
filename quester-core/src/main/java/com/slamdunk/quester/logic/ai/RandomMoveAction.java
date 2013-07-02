package com.slamdunk.quester.logic.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.points.Point;

/**
 * Déplace le contrôleur vers les coordonnées spécifiées.
 */
public class RandomMoveAction extends AbstractAIAction {
	
	public void act() {
		final int x = ai.controler.getActor().getWorldX();
		final int y = ai.controler.getActor().getWorldY();
		final ActorMap map = GameControler.instance.getScreen().getMap();
		int neighborX;
		int neighborY;
		final List<Point> emptyNeighbors = new ArrayList<Point>();
		
		// Choix des voisins valides
		for (int[] neighbor : CharacterControler.NEIGHBORS_PLUS) {
			neighborX = x + neighbor[0];
			neighborY = y + neighbor[1];
			if (map.isEmpty(neighborX, neighborY)) {
				emptyNeighbors.add(map.getPointManager().getPoint(neighborX, neighborY));
			}
		}
		
		// Choix d'un de ces voisins au hasard
		Point destination = null;
		if (!emptyNeighbors.isEmpty()) {
			final int random = MathUtils.random(emptyNeighbors.size() - 1);
			destination = emptyNeighbors.get(random);
		}
		
		// Action consommée !
		ai.nextAction();
		if (destination != null) {
			ai.setNextActions(new MoveAction(destination));
		}
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
