package com.slamdunk.quester2.puzzle;

import com.badlogic.gdx.Screen;
import com.slamdunk.quester.utils.Config;

public class PuzzleScreen implements Screen {
	private Puzzle puzzle;
	private PuzzleStage stage;
	
	public PuzzleScreen() {
		// Création du puzzle
		puzzle = new Puzzle(Config.asInt("puzzle.width", 9), Config.asInt("puzzle.height", 9));
		// Création du stage chargé de dessiner le puzzle et interagir avec l'utilisateur
		stage = new PuzzleStage(puzzle);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		stage.render(delta);
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

}
