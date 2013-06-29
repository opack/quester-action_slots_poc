package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.utils.Assets;

/**
 * Arrête le tour du personnage en cours et le prépare à penser
 * de nouveau.
 */
public class ProtectAction implements AIAction {
	private CharacterControler character;
	private int damageReduction;
	
	public ProtectAction(CharacterControler character, int damageReduction) {
		this.character = character;
		this.damageReduction = damageReduction;
	}
	
	public void act() {
		character.protect(damageReduction);
		character.getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.END_TURN;
	}
}
