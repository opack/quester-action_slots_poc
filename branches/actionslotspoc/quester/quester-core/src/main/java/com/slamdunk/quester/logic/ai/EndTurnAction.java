package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.GameControler;

/**
 * Arr�te le tour du personnage en cours et le pr�pare � penser
 * de nouveau.
 */
public class EndTurnAction extends AbstractAIAction {
	
	public void act() {
		GameControler.instance.nextPlayer();
		ai.nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.END_TURN;
	}
}
