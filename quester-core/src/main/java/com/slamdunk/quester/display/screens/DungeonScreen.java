package com.slamdunk.quester.display.screens;

import com.slamdunk.quester.display.hud.HUDRenderer;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.MapBuilder;

public class DungeonScreen extends GameScreen implements CharacterListener {

	public DungeonScreen(HUDRenderer hudRenderer, MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		super(hudRenderer, builder, worldCellWidth, worldCellHeight);
	}

	@Override
	public void displayWorld(DisplayData display) {
		// Affiche le donjon
		super.displayWorld(display);
		// Enregistre ce Screen comme listener de chaque Character
		for (CharacterControler character : getMapRenderer().getMap().getCharacters()) {
			character.addListener(this);
		}
		// Place le joueur hors du mode freemove
		GameControler.instance.getPlayer().getData().isFreeMove = false;
	}
	

	@Override
	public void onActionPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterDeath(CharacterControler character) {
		// Si tous les ennemis sont morts, alors le joueur est en freemove !
		boolean areHostileRemaining = false;
		for (CharacterControler curChar : getMapRenderer().getMap().getCharacters()) {
			if (curChar.isHostile()) {
				areHostileRemaining = true;
				break;
			}
		}
		if (!areHostileRemaining) {
			GameControler.instance.getPlayer().getData().isFreeMove = true;
			GameControler.instance.updateHUD();
		}
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterMoved(CharacterControler character, int oldX, int oldY) {
		// TODO Auto-generated method stub
		
	}
}
