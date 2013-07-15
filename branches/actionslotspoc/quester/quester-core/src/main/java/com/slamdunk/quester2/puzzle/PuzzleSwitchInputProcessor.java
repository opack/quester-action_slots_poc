package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PuzzleSwitchInputProcessor extends InputAdapter {
	public interface SwitchListener {
		void onPuzzleSwitch(int firstX, int firstY, int secondX, int secondY);
	}
	
	private Actor firstSwitchedItem;
	private Actor secondSwitchedItem;
	private Table puzzleTable;
	
	private Vector2 screenCoords;
	
	private List<SwitchListener> listeners;
	
	public PuzzleSwitchInputProcessor(Table puzzleTable) {
		this.puzzleTable = puzzleTable;
		// Création du vecteur de travail
		screenCoords = new Vector2();
	}
	
	public void addListener(SwitchListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<SwitchListener>();
		}
		listeners.add(listener);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		firstSwitchedItem = getSwitchItem(screenX, screenY);
		
		// Si l'Actor sélectionné est la table, on annule
		if (firstSwitchedItem == puzzleTable) {
			reset();
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (firstSwitchedItem == null) {
			return true;
		}
		secondSwitchedItem = getSwitchItem(screenX, screenY);
		if (secondSwitchedItem == null) {
			reset();
		} else if (secondSwitchedItem != puzzleTable
				&& firstSwitchedItem != secondSwitchedItem) {
			performSwitch();
			reset();
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	private void performSwitch() {
		PuzzleImage firstImage = (PuzzleImage)firstSwitchedItem;
		PuzzleImage secondImage = (PuzzleImage)secondSwitchedItem;
		for (SwitchListener listener : listeners) {
			listener.onPuzzleSwitch(
				firstImage.getPuzzleX(), firstImage.getPuzzleY(), 
				secondImage.getPuzzleX(), secondImage.getPuzzleY());
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		reset();
		return super.touchUp(screenX, screenY, pointer, button);
	}

	private void reset() {
		firstSwitchedItem = null;
		secondSwitchedItem = null;
	}

	/**
	 * Retourne l'acteur de la puzzleTable aux coordonnées écran indiquées.
	 */
	private Actor getSwitchItem(int screenX, int screenY) {
		screenCoords.set(screenX, screenY);
		Vector2 tableCoords = puzzleTable.screenToLocalCoordinates(screenCoords);
		return puzzleTable.hit(tableCoords.x, tableCoords.y, false);
	}
}
