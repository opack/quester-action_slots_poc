package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class CheckEnemyDetection extends AbstractAIAction {

	@Override
	public void act() {
		// V�rifie si on est entr� dans la zone de perception d'un ennemi
		CharacterControler character = ai.controler;
		boolean isInEnemySight = false;
		for (CharacterControler curCharacter : GameControler.instance.getCharacters()) {
			if (curCharacter == character) {
				continue;
			}
			if (curCharacter.isInSight(character)) {
				// Si le personnage entre dans la zone d'un autre personnage, alors il est activ�.
				curCharacter.setEnabled(true);
				// Si cet autre perso est hostile, alors on devra arr�ter le d�placement;
				if (curCharacter.isHostile()) {
					isInEnemySight = true;
				}
			}
		}
		
		if (isInEnemySight) {
			// Si le joueur a �t� d�tect�, alors il ne peut plus se d�placer � ce tour
			character.getData().movesLeft = 0;
			GameControler.instance.updateHUD();
			
			ai.clearActions();
			ai.setNextActions(new PlaySoundAction(Assets.bumpSound));
		} else {
			ai.nextAction();
		}
	}

	@Override
	public QuesterActions getAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
