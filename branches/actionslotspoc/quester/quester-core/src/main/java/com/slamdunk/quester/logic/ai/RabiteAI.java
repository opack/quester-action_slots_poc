package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.GameControler;

/**
 * Le Rabite attaque un tour sur deux
 */
public class RabiteAI extends AI {
	
	@Override
	public void init() {
		super.init();
		if (getControler().isEnabled()) {
			// Tour d'action 1 : ne rien faire
			addAction(new EndTurnAction());
			
			// Tour d'action 2 : se d�placer
			addAction(new RandomMoveAction());
			addAction(new EndTurnAction());
			
			// Tour d'action 3 : attaquer
			addAction(new AttackAction(GameControler.instance.getPlayer()));
			addAction(new EndTurnAction());
		} else {
			// Le Rabite n'a pas encore �t� d�couvert : on ne fait rien
			addAction(new EndTurnAction());
		}
	}
}
