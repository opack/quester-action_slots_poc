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
	EMPTY (Assets.attribute_empty, AttributeTypes.EMPTY, AttributeOrientation.SELF),
	
	// Attributs de base
	STRENGTH (Assets.attribute_strength, AttributeTypes.BASE, AttributeOrientation.SELF),
	CONSTITUTION (Assets.attribute_constitution, AttributeTypes.BASE, AttributeOrientation.SELF),
	DEXTERITY (Assets.attribute_dexterity, AttributeTypes.BASE, AttributeOrientation.SELF),
	FOCUS (Assets.attribute_focus, AttributeTypes.BASE, AttributeOrientation.SELF),
	WILL (Assets.attribute_will, AttributeTypes.BASE, AttributeOrientation.SELF),
	LUCK (Assets.attribute_luck, AttributeTypes.BASE, AttributeOrientation.SELF),
	
	// Attributs super
	SUPER_STRENGTH_H (Assets.attribute_strength_superh, AttributeTypes.SUPER, AttributeOrientation.HORIZONTAL, STRENGTH),
	SUPER_CONSTITUTION_H (Assets.attribute_constitution_superh, AttributeTypes.SUPER, AttributeOrientation.HORIZONTAL, CONSTITUTION),
	SUPER_DEXTERITY_H (Assets.attribute_dexterity_superh, AttributeTypes.SUPER, AttributeOrientation.HORIZONTAL, DEXTERITY),
	SUPER_FOCUS_H (Assets.attribute_focus_superh, AttributeTypes.SUPER, AttributeOrientation.HORIZONTAL, FOCUS),
	SUPER_WILL_H (Assets.attribute_will_superh, AttributeTypes.SUPER, AttributeOrientation.HORIZONTAL, WILL),
	SUPER_LUCK_H (Assets.attribute_luck_superh, AttributeTypes.SUPER, AttributeOrientation.HORIZONTAL, LUCK),
	
	SUPER_STRENGTH_V (Assets.attribute_strength_superv, AttributeTypes.SUPER, AttributeOrientation.VERTICAL, STRENGTH),
	SUPER_CONSTITUTION_V (Assets.attribute_constitution_superv, AttributeTypes.SUPER, AttributeOrientation.VERTICAL, CONSTITUTION),
	SUPER_DEXTERITY_V (Assets.attribute_dexterity_superv, AttributeTypes.SUPER, AttributeOrientation.VERTICAL, DEXTERITY),
	SUPER_FOCUS_V (Assets.attribute_focus_superv, AttributeTypes.SUPER, AttributeOrientation.VERTICAL, FOCUS),
	SUPER_WILL_V (Assets.attribute_will_superv, AttributeTypes.SUPER, AttributeOrientation.VERTICAL, WILL),
	SUPER_LUCK_V (Assets.attribute_luck_superv, AttributeTypes.SUPER, AttributeOrientation.VERTICAL, LUCK);
	
	private Drawable texture;
	private AttributeTypes type;
	private PuzzleAttributes baseAttribute;
	private AttributeOrientation orientation;
	
	private PuzzleAttributes(TextureRegion texture, AttributeTypes type, AttributeOrientation orientation) {
		this(texture, type, orientation, null);
	}
	private PuzzleAttributes(TextureRegion texture, AttributeTypes type, AttributeOrientation orientation, PuzzleAttributes baseAttribute) {
		this.texture = new TextureRegionDrawable(texture);
		this.type = type;
		this.orientation = orientation;
		this.baseAttribute = baseAttribute;
	}
	public Drawable getDrawable() {
		return texture;
	}
	public AttributeTypes getType() {
		return type;
	}
	public PuzzleAttributes getBaseAttribute() {
		return baseAttribute;
	}
	public AttributeOrientation getOrientation() {
		return orientation;
	}
}
