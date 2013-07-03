package com.slamdunk.quester.display.hud.actionslots;

import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public interface DropReceiver {
	/**
	 * Indique si ce receiver serait pr�t � accepter cette action
	 */
	boolean canAcceptDrop(QuesterActions action);
	
	/**
	 * Appel�e lorsqu'un drop est en train de passer au-dessus
	 * de la cible
	 */
	void onDropHoverEnter(QuesterActions action);
	
	/**
	 * Appel�e lorsqu'un drop quitte la surface de la cible
	 */
	void onDropHoverLeave(QuesterActions action);

	/**
	 * M�thode appel�e lorsqu'un chargement est l�ch� sur ce receiver
	 */
	void receiveDrop(QuesterActions action);
}