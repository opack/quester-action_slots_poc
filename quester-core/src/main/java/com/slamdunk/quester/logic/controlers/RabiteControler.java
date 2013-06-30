package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.ai.RabiteAI;
import com.slamdunk.quester.model.data.CharacterData;

public class RabiteControler extends CharacterControler {

	public RabiteControler(CharacterData data, RabiteActor body) {
		super(data, body, new RabiteAI());
	}

	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		return action == QuesterActions.ATTACK;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
}
