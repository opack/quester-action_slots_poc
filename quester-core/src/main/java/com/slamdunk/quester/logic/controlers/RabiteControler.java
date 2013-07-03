package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.ai.RabiteAI;
import com.slamdunk.quester.model.data.CharacterData;

public class RabiteControler extends CharacterControler {

	public RabiteControler(CharacterData data, RabiteActor body) {
		super(data, body, new RabiteAI());
		setDetectionArea(NEIGHBORS_ALL);
	}

	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		PlayerControler player = GameControler.instance.getPlayer();
		final boolean isInRange = ActorMap.distance(
			player.actor.getWorldX(), player.actor.getWorldY(), 
			actor.getWorldX(), actor.getWorldY()) == 1;
		return action == QuesterActions.ATTACK
			&& isInRange;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
}
