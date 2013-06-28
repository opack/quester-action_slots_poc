package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;

/**
 * Arr�te le tour du personnage en cours et le pr�pare � penser
 * de nouveau.
 */
public class EndTurnAction implements AIAction {
	private CharacterControler character;
	
	public EndTurnAction(CharacterControler character) {
		this.character = character;
	}
	
	public void act() {
		GameControler.instance.nextPlayer();
		character.getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.END_TURN;
	}
}
