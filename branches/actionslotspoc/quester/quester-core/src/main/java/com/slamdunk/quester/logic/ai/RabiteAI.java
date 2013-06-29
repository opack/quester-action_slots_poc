package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.GameControler;

/**
 * Le Rabite attaque un tour sur deux
 */
public class RabiteAI extends AI {
	
	@Override
	public void init() {
		super.init();
		// Tour d'action 1 : ne rien faire
		addAction(new EndTurnAction(getControler()));
		
		// Tour d'action 2 : attaquer
		addAction(new AttackAction(controler, GameControler.instance.getPlayer()));
		addAction(new EndTurnAction(getControler()));
	}
}
