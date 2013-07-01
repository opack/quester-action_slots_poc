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
	 * Vitesse (en nombre de cases par seconde) à laquelle se déplace le personnage
	 */
	public float speed;
	/**
	 * Distance à laquelle l'arme peut attaquer
	 */
	public int weaponRange;
	/**
	 * Distance que le joueur peut parcourir au maximum
	 */
	public int walkDistance;
	/**
	 * Nombre de déplacements restants
	 */
	public int movesLeft;
	/**
	 * Indique si les déplacement dépendent de movesLeft
	 */
	public boolean isFreeMove;
	
	public CharacterData(MapElements element, int hp, int attack) {
		super(element);
		// Nom par défaut
		name = element.name();
		// HP et attaque
		this.health = hp;
		this.attack = attack;
		// Vitesse par défaut : 1s/case
		speed = 1;
		// Portée par défaut : 1 case
		weaponRange = 1;
		// Ordre de jeu par défaut : 1er
		playRank = 0;
		// Par défaut, le personnage ne peut pas se déplacer
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
