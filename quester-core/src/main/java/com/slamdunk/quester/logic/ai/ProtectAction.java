package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;

/**
 * Arr�te le tour du personnage en cours et le pr�pare � penser
 * de nouveau.
 */
public class ProtectAction extends AbstractAIAction {
	private CharacterControler character;
	private int damageReduction;
	
	public ProtectAction(CharacterControler character, int damageReduction) {
		this.character = character;
		this.damageReduction = damageReduction;
	}
	
	public void act() {
		character.protect(damageReduction);
		ai.nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.END_TURN;
	}
}
