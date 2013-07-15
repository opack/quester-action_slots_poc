package com.slamdunk.quester2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;
import com.slamdunk.quester2.hud.HUDRenderer2;
import com.slamdunk.quester2.puzzle.PuzzleScreen;
public class Quester2 extends Game {
	/**
	 * Taille de l'affichage en pixels
	 */
	public static int screenWidth;
	public static int screenHeight;
	
	private HUDRenderer2 hudRenderer;
	private PuzzleScreen puzzleScreen;
	
	@Override
	public void create () {
		// Chargement de la taille de l'écran
		screenWidth = Config.asInt("screen.width", 480);
		screenHeight = Config.asInt("screen.height", 800);
		
		// Chargement des assets
		Assets.load();
		
		// Création du HUD
		hudRenderer = new HUDRenderer2();
		
		// Arrivée sur la carte du monde
		puzzleScreen = new PuzzleScreen();
		setScreen(puzzleScreen);
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
		// Rechargement des assets
		Assets.load();
	}

	@Override
	public void dispose () {
		disposePuzzleScreen();
		hudRenderer.dispose();
		Assets.dispose();
	}

	private void disposePuzzleScreen() {
		if (puzzleScreen != null) {
			puzzleScreen.dispose();
			puzzleScreen = null;
		}
	}
	
	@Override
	public void render() {
		super.render();
		hudRenderer.render(Gdx.graphics.getDeltaTime());
	}
}
