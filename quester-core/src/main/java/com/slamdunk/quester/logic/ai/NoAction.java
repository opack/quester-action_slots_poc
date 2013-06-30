package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;

public class NoAction extends AbstractAIAction {
	@Override
	public void act() {
		CharacterControler character = ai.controler;
		character.prepareThink();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.NONE;
	}

}
