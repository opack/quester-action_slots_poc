package com.slamdunk.quester;

import static com.slamdunk.quester.model.data.WorldElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.display.screens.WorldScreen;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.DungeonBuilder;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.map.WorldBuilder;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;
public class Quester extends Game {
	/**
	 * Taille de l'affichage en pixels
	 */
	public static int screenWidth;
	public static int screenHeight;
	
	/**
	 * Ecrans du jeu
	 */
	private GameScreen worldMapScreen;
	private GameScreen dungeonScreen;
	
	private static Quester instance;
	
	@Override
	public void create () {
		instance = this;
		
		// Chargement de la taille de l'�cran
		screenWidth = Config.asInt("screen.width", 480);
		screenHeight = Config.asInt("screen.height", 800);
		
		// Chargement des assets
		Assets.load();
		
		// Cr�ation d'un joueur
		GameControler.instance.createPlayerControler(150, 3);
		
		// Arriv�e sur la carte du monde
		enterWorldMap();
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		if (worldMapScreen != null) {
			worldMapScreen.dispose();
		}
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		Assets.dispose();
	}

	public static Quester getInstance() {
		return instance;
	}

	public Screen getWorldMapScreen() {
		return worldMapScreen;
	}

	public Screen getDungeonScreen() {
		return dungeonScreen;
	}
	
	public void enterWorldMap() {
		// Si le monde n'est pas encore cr��, on le cr�e
		if (worldMapScreen == null) {
			// Cr�ation de la carte
			MapBuilder builder = new WorldBuilder(Config.asInt("world.width", 11), Config.asInt("world.height", 11));
			builder.createAreas(Config.asInt("world.areaWidth", 11), Config.asInt("world.areaHeight", 11), GRASS_DATA);
			builder.placeMainEntrances();
			worldMapScreen = new WorldScreen(builder, Config.asInt("map.cellWidth", 96), Config.asInt("map.cellHeight", 96));
			GameControler.instance.setScreen(worldMapScreen);
			
			// Choix de la musique de fond
			worldMapScreen.setBackgroundMusic(Assets.worldmapMusics[MathUtils.random(Assets.worldmapMusics.length - 1)]);
			
			// Cr�ation de l'acteur repr�sentant le joueur
			Point entrancePosition = builder.getEntrancePosition();
			worldMapScreen.createPlayer(entrancePosition);
			GameControler.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
			GameControler.instance.getPlayer().getAI().init();
			
			// Le joueur est cr�� : cr�ation du hud
			worldMapScreen.initHud(Config.asInt("minimap.width", (int)(screenWidth * 0.8)), Config.asInt("minimap.height", (int)(screenWidth * 0.8)));
			
			// Affichage de la carte
	        Point entranceRoom = builder.getEntranceRoom();
	        DisplayData data = new DisplayData();
	        data.regionX = entranceRoom.getX();
	        data.regionY = entranceRoom.getY();
	        data.playerX = entrancePosition.getX();
	        data.playerY = entrancePosition.getY();
	        GameControler.instance.displayWorld(data);
		}
		// Affichage de la carte
		GameControler.instance.setScreen(worldMapScreen);
		GameControler.instance.setCurrentArea(worldMapScreen.getCurrentArea().getX(), worldMapScreen.getCurrentArea().getY());
		GameControler.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
		worldMapScreen.updateHUD(GameControler.instance.getCurrentArea());
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight,
			int difficulty) {
		// Si un donjon existe d�j�, on le supprime
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		
		// Construction de la carte
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight, difficulty);
		builder.createAreas(roomWidth, roomHeight, GROUND_DATA);
		builder.placeMainEntrances();
		dungeonScreen = new GameScreen(builder, 96, 96);
		GameControler.instance.setScreen(dungeonScreen);
		
		// Choix de la musique de fond
		dungeonScreen.setBackgroundMusic(Assets.dungeonMusics[MathUtils.random(Assets.dungeonMusics.length - 1)]);
		
		// Cr�e l'acteur repr�sentant le joueur
		Point entrancePosition = builder.getEntrancePosition();
		dungeonScreen.createPlayer(entrancePosition);
		GameControler.instance.getPlayer().setActor(dungeonScreen.getPlayerActor());
		GameControler.instance.getPlayer().getAI().init();
		
		// Le joueur est cr�� : cr�ation du hud
		dungeonScreen.initHud(Config.asInt("minimap.width", (int)(screenWidth * 0.8)), Config.asInt("minimap.height", (int)(screenWidth * 0.8)));
		
		// Affichage de la carte
        Point entranceRoom = builder.getEntranceRoom();
        DisplayData data = new DisplayData();
        data.regionX = entranceRoom.getX();
        data.regionY = entranceRoom.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        GameControler.instance.displayWorld(data);
        
        dungeonScreen.updateHUD(data.regionX, data.regionY);
        
		setScreen(dungeonScreen);
	}
}
