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
	EMPTY (Assets.attribute_empty, AttributeTypes.EMPTY, AlignmentOrientation.SELF),
	
	// Attributs de base
	STRENGTH (Assets.attribute_strength, AttributeTypes.BASE, AlignmentOrientation.SELF),
	CONSTITUTION (Assets.attribute_constitution, AttributeTypes.BASE, AlignmentOrientation.SELF),
	DEXTERITY (Assets.attribute_dexterity, AttributeTypes.BASE, AlignmentOrientation.SELF),
	FOCUS (Assets.attribute_focus, AttributeTypes.BASE, AlignmentOrientation.SELF),
	WILL (Assets.attribute_will, AttributeTypes.BASE, AlignmentOrientation.SELF),
	LUCK (Assets.attribute_luck, AttributeTypes.BASE, AlignmentOrientation.SELF),
	
	// Attributs super
	SUPER_STRENGTH_H (Assets.attribute_strength_superh, AttributeTypes.SUPER, AlignmentOrientation.HORIZONTAL, STRENGTH),
	SUPER_CONSTITUTION_H (Assets.attribute_constitution_superh, AttributeTypes.SUPER, AlignmentOrientation.HORIZONTAL, CONSTITUTION),
	SUPER_DEXTERITY_H (Assets.attribute_dexterity_superh, AttributeTypes.SUPER, AlignmentOrientation.HORIZONTAL, DEXTERITY),
	SUPER_FOCUS_H (Assets.attribute_focus_superh, AttributeTypes.SUPER, AlignmentOrientation.HORIZONTAL, FOCUS),
	SUPER_WILL_H (Assets.attribute_will_superh, AttributeTypes.SUPER, AlignmentOrientation.HORIZONTAL, WILL),
	SUPER_LUCK_H (Assets.attribute_luck_superh, AttributeTypes.SUPER, AlignmentOrientation.HORIZONTAL, LUCK),
	
	SUPER_STRENGTH_V (Assets.attribute_strength_superv, AttributeTypes.SUPER, AlignmentOrientation.VERTICAL, STRENGTH),
	SUPER_CONSTITUTION_V (Assets.attribute_constitution_superv, AttributeTypes.SUPER, AlignmentOrientation.VERTICAL, CONSTITUTION),
	SUPER_DEXTERITY_V (Assets.attribute_dexterity_superv, AttributeTypes.SUPER, AlignmentOrientation.VERTICAL, DEXTERITY),
	SUPER_FOCUS_V (Assets.attribute_focus_superv, AttributeTypes.SUPER, AlignmentOrientation.VERTICAL, FOCUS),
	SUPER_WILL_V (Assets.attribute_will_superv, AttributeTypes.SUPER, AlignmentOrientation.VERTICAL, WILL),
	SUPER_LUCK_V (Assets.attribute_luck_superv, AttributeTypes.SUPER, AlignmentOrientation.VERTICAL, LUCK),
	
	// Attribut hyper
	HYPER (Assets.attribute_hyper, AttributeTypes.HYPER, AlignmentOrientation.WHOLE);
	
	private Drawable texture;
	private AttributeTypes type;
	private PuzzleAttributes baseAttribute;
	private AlignmentOrientation orientation;
	
	private PuzzleAttributes(TextureRegion texture, AttributeTypes type, AlignmentOrientation orientation) {
		this(texture, type, orientation, null);
	}
	private PuzzleAttributes(TextureRegion texture, AttributeTypes type, AlignmentOrientation orientation, PuzzleAttributes baseAttribute) {
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
	public AlignmentOrientation getOrientation() {
		return orientation;
	}
}
