package com.slamdunk.quester.model.map;

public enum Borders {
	BOTTOM, LEFT, RIGHT, TOP;

	public Borders getOpposingBorder() {
		switch (this) {
		case BOTTOM:
			return TOP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case TOP:
			return BOTTOM;
		}
		return null;
	}
};