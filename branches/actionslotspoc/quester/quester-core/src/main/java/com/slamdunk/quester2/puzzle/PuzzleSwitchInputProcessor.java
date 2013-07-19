package com.slamdunk.quester2.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Détecte le geste requérant l'inversion de deux attributs
 */
public class PuzzleSwitchInputProcessor extends InputAdapter {
	public interface SwitchListener {
		void onPuzzleSwitch(int firstX, int firstY, int secondX, int secondY);
	}
	
	private PuzzleImage firstSwitchedItem;
	private PuzzleImage secondSwitchedItem;
	private PuzzleStage puzzleStage;
	private Table puzzleTable;
	
	private Vector2 screenCoords;
	
	private List<SwitchListener> listeners;
	
	public PuzzleSwitchInputProcessor(PuzzleStage puzzleStage, Table puzzleTable) {
		this.puzzleStage = puzzleStage;
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
		// Si la grille est en mouvement, le joueur ne peut pas tenter un autre switch
		if (!puzzleStage.checkSteady()) {
			System.out.println("DBG PuzzleSwitchInputProcessor.touchDown() pas steady");
			return false;
		}
		
		Actor hit = getSwitchItem(screenX, screenY);
		
		if (hit instanceof PuzzleImage) {
			firstSwitchedItem = (PuzzleImage)hit;
		} else {
			// Si l'Actor sélectionné n'est pas un PuzzleImage, on annule
			reset();
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (firstSwitchedItem == null) {
			return true;
		}
		// Si la touche a eut lieu hors de la table, on annule
		Actor hit = getSwitchItem(screenX, screenY);
		if (hit == null) {
			reset();
		} else
		// Si on on a bien touché un PuzzleImage autre que le premier
		if (firstSwitchedItem != hit && hit instanceof PuzzleImage) {
			secondSwitchedItem = (PuzzleImage)hit;
			// On ne fait le switch que si les items sont côte à côte
			// Si le joueur déplace très vite son doigt sur l'écran, 
			// le hit peut en effet être un objet distant du premier.
			if (areNeighbors(firstSwitchedItem, secondSwitchedItem)) {
				notifySwitch();
			}
			reset();
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	/**
	 * Indique si les deux images sont côte à côte
	 */
	private boolean areNeighbors(PuzzleImage item1, PuzzleImage item2) {
		final double dx = item1.getPuzzleX() - item2.getPuzzleX();
		final double dy = item1.getPuzzleY() - item2.getPuzzleY();
		return Math.sqrt(dx * dx + dy * dy) == 1.0;
	}

	/**
	 * Informe les listeners que deux PuzzleImage ont été inversés
	 */
	private void notifySwitch() {
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
