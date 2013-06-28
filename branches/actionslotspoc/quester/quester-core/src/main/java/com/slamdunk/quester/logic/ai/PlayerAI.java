package com.slamdunk.quester.logic.ai;


public class PlayerAI extends CharacterAI {
	
	@Override
	public void think() {
		// Ne rien faire ici revient � continuer � appeler think()
		// jusqu'� ce qu'une action ait �t� initi�e par le joueur.
		// Il y aura alors une autre action que THINK
		if (actions.size() > 1) {
			// Si une action a �t� choisie, on quitte la r�flexion
			// et on l'ex�cute
			nextAction();
		}
	}
}
