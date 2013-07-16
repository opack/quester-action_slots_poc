package com.slamdunk.quester2.puzzle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.utils.Assets;

/**
 * Liste les différents attributs disponibles dans le puzzle, ainsi que
 * leurs propriétés
 */
public enum PuzzleAttributes {
	// Attribut inconnu
	UNKNOWN (Assets.attribute_unknown),
	
	// Attributs de base
	STRENGTH (Assets.attribute_strength),
	CONSTITUTION (Assets.attribute_constitution),
	DEXTERITY (Assets.attribute_dexterity),
	FOCUS (Assets.attribute_focus),
	WILL (Assets.attribute_will),
	LUCK (Assets.attribute_luck);
	
	private Drawable texture;
	private PuzzleAttributes(TextureRegion texture) {
		this.texture = new TextureRegionDrawable(texture);
	}
	public Drawable getDrawable() {
		return texture;
	}
}
