package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.NONE;

/**
 * Attend la fin des actions en cours sur l'Actor puis passe à 
 * l'action suivante.
 */
public class WaitCompletionAction extends AbstractAIAction {
	public void act() {
		if (ai.controler.getActor().getCurrentAction() == NONE) {
			// L'attente est finie, on exécute l'action suivante
			ai.nextAction();
		}
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.WAIT_COMPLETION;
	}
}
