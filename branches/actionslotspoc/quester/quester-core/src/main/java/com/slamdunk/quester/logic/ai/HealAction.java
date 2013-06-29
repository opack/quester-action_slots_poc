package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.utils.Assets;

/**
 * Arrête le tour du personnage en cours et le prépare à penser
 * de nouveau.
 */
public class HealAction implements AIAction {
	private CharacterControler character;
	private int hpGained;
	
	public HealAction(CharacterControler character, int hpGained) {
		this.character = character;
		this.hpGained = hpGained;
	}
	
	public void act() {
		Assets.playSound(Assets.drinkSound);
		character.heal(hpGained);
		character.getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.END_TURN;
	}
}
