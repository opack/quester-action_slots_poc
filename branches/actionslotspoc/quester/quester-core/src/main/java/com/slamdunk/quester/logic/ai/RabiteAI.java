package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;

/**
 * Le Rabite se d�place al�atoirement, et s'il est � c�t� du joueur il l'attaque.
 * S'il a moins d'un tiers de sa vie, il s'�carte du joueur plut�t que d'attaquer.
 */
public class RabiteAI extends AI {
	/**
	 * Niveau de vie � partir duquel le rabite pr�f�re s'�chapper plut�t qu'attaquer
	 */
	private int panicThreshold;
	
	@Override
	public void init() {
		super.init();
		panicThreshold = controler.getData().health / 3;
	}
	
	@Override
	public void think() {
		// Si le Rabite a moins d'un tiers de vie, il bouge.
		if (controler.getData().health <= panicThreshold) {
			addAction(new RandomMoveAction());
		} else {
			// Si le joueur est � port�e, on l'attaque.
			WorldElementActor rabiteActor = controler.getActor();
			int range = controler.getData().weaponRange;
			PlayerControler player = GameControler.instance.getPlayer();
			if (GameControler.instance.getScreen().getMap().isWithinRangeOf(rabiteActor, player.getActor(), range)) {
				addAction(new AttackAction(player));
			}
			// Sinon, on se d�place al�atoirement, ou on ne fait rien si aucun d�placement n'est possible
			else {
				addAction(new RandomMoveAction());
			}
		}
		
		// Une fois l'action effectu�e, on finit le tour
		addAction(new EndTurnAction());
	}
}
