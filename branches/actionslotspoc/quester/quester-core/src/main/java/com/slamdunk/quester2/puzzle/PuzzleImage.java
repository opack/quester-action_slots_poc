package com.slamdunk.quester2.puzzle;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
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
	
	@Override
	public void addAction(Action action) {
		// Cr�ation d'une s�quence action pour pouvoir ajouter d'�ventuelles autres actions par la suite
		if (getActions().size == 0) {
			super.addAction(Actions.sequence(action));
		} else {
			// Si on arrive ici, c'est qu'on veut ajouter une action mais qu'il y en a d�j� d'autres en cours.
			// La seule action est forc�ment une SequenceAction (on l'a cr��e juste au-dessus), donc on peut
			// ajouter celle-ci � la suite. Le but est de faire en sorte que les actions ne soient plus
			// ex�cut�es en parall�le mais les unes � la suite des autres.
			SequenceAction sequence = (SequenceAction)getActions().get(0);
			sequence.addAction(action);
		}
	}
}
