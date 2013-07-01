package com.slamdunk.quester.display.screens;

import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.MapBuilder;

public class DungeonScreen extends GameScreen {

	public DungeonScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		super(builder, worldCellWidth, worldCellHeight);
	}

	@Override
	public void displayWorld(DisplayData display) {
		super.displayWorld(display);
		GameControler.instance.getPlayer().getData().isFreeMove = false;
	}
}
