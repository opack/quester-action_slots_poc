package com.slamdunk.quester.logic.controlers;

public interface Damageable {

	/**
	 * Retourne le nombre de points de vie restants
	 */
	int getHealth();
	
	/**
	 * Gu�rit
	 */
	void heal(int amount);

	/**
	 * Raccourci vers getHP() == 0
	 */
	boolean isDead();
	
	/**
	 * Re�oit et g�re les d�g�ts.
	 * @param damage
	 */
	void receiveDamage(int damage);
	
	/**
	 * D�finit le nombre de points de vie restants
	 */
	void setHealth(int value);
}
