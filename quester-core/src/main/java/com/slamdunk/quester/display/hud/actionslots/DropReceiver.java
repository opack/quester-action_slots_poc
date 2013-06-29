package com.slamdunk.quester.display.hud.actionslots;

import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public interface DropReceiver {
	/**
	 * Indique si ce receiver serait pr�t � accepter cette action
	 */
	boolean canAcceptDrop(QuesterActions action);

	/**
	 * M�thode appel�e lorsqu'un chargement est l�ch� sur ce receiver
	 */
	void receiveDrop(ActionSlotControler dropped);
}