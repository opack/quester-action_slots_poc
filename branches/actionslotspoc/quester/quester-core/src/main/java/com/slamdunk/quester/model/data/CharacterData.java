package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.MapElements;

public class CharacterData extends ObstacleData {
	/**
	 * Points d'attaque
	 */
	public int attack;
	/**
	 * Points de vie
	 */
	public int health;
	/**
	 * Nom
	 */
	public String name;
	/**
	 * Ordre de jeu
	 */
	public int playRank;
	/**
	 * Vitesse (en nombre de cases par seconde) � laquelle se d�place le personnage
	 */
	public float speed;
	/**
	 * Distance � laquelle l'arme peut attaquer
	 */
	public int weaponRange;
	/**
	 * Distance que le joueur peut parcourir au maximum
	 */
	public int walkDistance;
	/**
	 * Nombre de d�placements restants
	 */
	public int movesLeft;
	/**
	 * Indique si les d�placement d�pendent de movesLeft
	 */
	public boolean isFreeMove;
	
	public CharacterData(MapElements element, int hp, int attack) {
		super(element);
		// Nom par d�faut
		name = element.name();
		// HP et attaque
		this.health = hp;
		this.attack = attack;
		// Vitesse par d�faut : 1s/case
		speed = 1;
		// Port�e par d�faut : 1 case
		weaponRange = 1;
		// Ordre de jeu par d�faut : 1er
		playRank = 0;
		// Par d�faut, le personnage ne peut pas se d�placer
		walkDistance = 0;
		movesLeft = 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CharacterData)) {
			return false;
		}
		CharacterData characterData = (CharacterData)obj;
		return super.equals(characterData)
			&& characterData.health == health
			&& characterData.attack == attack;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal() ^ health ^ attack;
	}
}
