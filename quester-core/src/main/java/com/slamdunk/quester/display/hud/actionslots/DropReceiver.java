package com.slamdunk.quester.display.hud.actionslots;

import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public interface DropReceiver {
	/**
	 * Indique si ce receiver serait prêt à accepter cette action
	 */
	boolean canAcceptDrop(QuesterActions action);
	
	/**
	 * Appelée lorsqu'un drop est en train de passer au-dessus
	 * de la cible
	 */
	void onDropHoverEnter(QuesterActions action);
	
	/**
	 * Appelée lorsqu'un drop quitte la surface de la cible
	 */
	void onDropHoverLeave(QuesterActions action);

	/**
	 * Méthode appelée lorsqu'un chargement est lâché sur ce receiver
	 */
	void receiveDrop(QuesterActions action);
}