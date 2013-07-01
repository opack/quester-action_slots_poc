package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class CheckEnemyDetection extends AbstractAIAction {

	@Override
	public void act() {
		// Vérifie si on est entré dans la zone de perception d'un ennemi
		CharacterControler character = ai.controler;
		boolean isInEnemySight = false;
		for (CharacterControler curCharacter : GameControler.instance.getCharacters()) {
			if (curCharacter == character) {
				continue;
			}
			if (curCharacter.isInSight(character)) {
				// Si le personnage entre dans la zone d'un autre personnage, alors il est activé.
				curCharacter.setEnabled(true);
				// Si cet autre perso est hostile, alors on devra arrêter le déplacement;
				if (curCharacter.isHostile()) {
					isInEnemySight = true;
				}
			}
		}
		
		if (isInEnemySight) {
			// Si le joueur a été détecté, alors il ne peut plus se déplacer à ce tour
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
