package com.slamdunk.quester.logic.ai;

public interface AIAction {
	/**
	 * Effectue l'action
	 */
	void act();
	
	/**
	 * Retourne l'action associ�e
	 */
	QuesterActions getAction();
	
	/**
	 * Retourne l'AI contenant cette action
	 */
	AI getAI();
	
	/**
	 * D�finit l'AI contenant cette action
	 */
	void setAI(AI ai);
}
