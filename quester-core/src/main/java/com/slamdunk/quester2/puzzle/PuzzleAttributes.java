package com.slamdunk.quester2.puzzle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.utils.Assets;

/**
 * Liste les différents attributs disponibles dans le puzzle, ainsi que
 * leurs propriétés
 */
public enum PuzzleAttributes {
	// Attributs de base
	STRENGTH (Assets.attribute_strength),
	CONSTITUTION (Assets.attribute_constitution),
	DEXTERITY (Assets.attribute_dexterity),
	FOCUS (Assets.attribute_focus),
	WILL (Assets.attribute_will),
	LUCK (Assets.attribute_luck);
	
	private TextureRegion texture;
	private PuzzleAttributes(TextureRegion texture) {
		this.texture = texture;
	}
	public TextureRegion getTexture() {
		return texture;
	}
}
