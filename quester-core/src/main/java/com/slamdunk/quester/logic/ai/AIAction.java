package com.slamdunk.quester.logic.ai;

public interface AIAction {
	/**
	 * Effectue l'action
	 */
	void act();
	
	/**
	 * Retourne l'action associée
	 */
	QuesterActions getAction();
	
	/**
	 * Retourne l'AI contenant cette action
	 */
	AI getAI();
	
	/**
	 * Définit l'AI contenant cette action
	 */
	void setAI(AI ai);
}
