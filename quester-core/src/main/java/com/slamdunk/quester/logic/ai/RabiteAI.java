package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;

/**
 * Le Rabite se déplace aléatoirement, et s'il est à côté du joueur il l'attaque.
 */
public class RabiteAI extends AI {
	@Override
	public void think() {
		// Si le joueur est à portée, on l'attaque.
		WorldElementActor rabiteActor = controler.getActor();
		int range = controler.getData().weaponRange;
		PlayerControler player = GameControler.instance.getPlayer();
		if (GameControler.instance.getScreen().getMap().isWithinRangeOf(rabiteActor, player.getActor(), range)) {
			addAction(new AttackAction(player));
		}
		// Sinon, on se déplace aléatoirement, ou on ne fait rien si aucun déplacement n'est possible
		else {
			addAction(new RandomMoveAction());
		}
		addAction(new EndTurnAction());
	}
}
