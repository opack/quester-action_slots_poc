package com.slamdunk.quester2.puzzle;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PuzzleImage extends Image {
	private PuzzleAttributes attribute;
	private int puzzleX;
	private int puzzleY;
	
	public PuzzleImage(PuzzleAttributes attribute) {
		super(attribute.getTexture());
		this.attribute = attribute;
	}
	
	public PuzzleAttributes getAttribute() {
		return attribute;
	}

	public int getPuzzleX() {
		return puzzleX;
	}
	
	public void setPuzzleX(int puzzleX) {
		this.puzzleX = puzzleX;
	}
	
	public int getPuzzleY() {
		return puzzleY;
	}
	
	public void setPuzzleY(int puzzleY) {
		this.puzzleY = puzzleY;
	}
}
