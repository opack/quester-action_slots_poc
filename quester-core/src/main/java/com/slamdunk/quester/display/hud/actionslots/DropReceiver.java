package com.slamdunk.quester.display.hud.actionslots;

import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public interface DropReceiver {
	/**
	 * Indique si ce receiver serait prêt à accepter cette action
	 */
	boolean canAcceptDrop(QuesterActions action);

	/**
	 * Méthode appelée lorsqu'un chargement est lâché sur ce receiver
	 */
	void receiveDrop(ActionSlotControler dropped);
}