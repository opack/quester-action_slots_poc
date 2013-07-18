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
	EMPTY (Assets.attribute_empty),
	
	// Attributs de base
	STRENGTH (Assets.attribute_strength),
	CONSTITUTION (Assets.attribute_constitution),
	DEXTERITY (Assets.attribute_dexterity),
	FOCUS (Assets.attribute_focus),
	WILL (Assets.attribute_will),
	LUCK (Assets.attribute_luck),
	
	// Attributs super
	SUPER_STRENGTH_H (Assets.attribute_strength_superh),
	SUPER_CONSTITUTION_H (Assets.attribute_constitution_superh),
	SUPER_DEXTERITY_H (Assets.attribute_dexterity_superh),
	SUPER_FOCUS_H (Assets.attribute_focus_superh),
	SUPER_WILL_H (Assets.attribute_will_superh),
	SUPER_LUCK_H (Assets.attribute_luck_superh),
	
	SUPER_STRENGTH_V (Assets.attribute_strength_superv),
	SUPER_CONSTITUTION_V (Assets.attribute_constitution_superv),
	SUPER_DEXTERITY_V (Assets.attribute_dexterity_superv),
	SUPER_FOCUS_V (Assets.attribute_focus_superv),
	SUPER_WILL_V (Assets.attribute_will_superv),
	SUPER_LUCK_V (Assets.attribute_luck_superv);
	
	private Drawable texture;
	private PuzzleAttributes(TextureRegion texture) {
		this.texture = new TextureRegionDrawable(texture);
	}
	public Drawable getDrawable() {
		return texture;
	}
}
