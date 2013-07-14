package com.slamdunk.quester;

import static com.slamdunk.quester.model.data.WorldElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.hud.HUDRenderer;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.DungeonScreen;
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
	private HUDRenderer hudRenderer;
	private WorldScreen worldMapScreen;
	private DungeonScreen dungeonScreen;
	
	private static Quester instance;
	
	@Override
	public void create () {
		instance = this;
		
		// Chargement de la taille de l'écran
		screenWidth = Config.asInt("screen.width", 480);
		screenHeight = Config.asInt("screen.height", 800);
		
		// Chargement des assets
		Assets.load();
		
		// Création d'un joueur
		GameControler.instance.createPlayerControler(150, 3);
		
		// Création du HUD
		hudRenderer = new HUDRenderer();
		
		// Arrivée sur la carte du monde
		enterWorldMap();
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
		if (worldMapScreen != null) {
			worldMapScreen.dispose();
		}
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		hudRenderer.dispose();
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
	
	private void disposeDungeonScreen() {
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
			dungeonScreen = null;
		}
	}
	
	public void enterWorldMap() {
		// Suppression du donjon éventuel
		disposeDungeonScreen();
		
		// Si le monde n'est pas encore créé, on le crée
		if (worldMapScreen == null) {
			// Création de la carte
			Point areaSize = new Point(Config.asInt("world.areaWidth", 11), Config.asInt("world.areaHeight", 11));
			MapBuilder builder = new WorldBuilder(Config.asInt("world.width", 11), Config.asInt("world.height", 11));
			builder.createAreas(
				areaSize, areaSize,
				GRASS_DATA);
			builder.placeMainEntrances();
			worldMapScreen = new WorldScreen(hudRenderer, builder, Config.asInt("map.cellWidth", 96), Config.asInt("map.cellHeight", 96));
			GameControler.instance.setScreen(worldMapScreen);
			
			// Choix de la musique de fond
			worldMapScreen.setBackgroundMusic(Assets.worldmapMusics[MathUtils.random(Assets.worldmapMusics.length - 1)]);
			
			// Création de l'acteur représentant le joueur
//DBG			Point entrancePosition = builder.getEntrancePosition();
//			worldMapScreen.createPlayer(entrancePosition);
			
			// Le joueur est créé : création du hud
			worldMapScreen.initHud(Config.asInt("minimap.width", (int)(screenWidth * 0.8)), Config.asInt("minimap.height", (int)(screenWidth * 0.8)));
			
			// Affichage de la carte
	        Point entranceRoom = builder.getEntranceRoom();
	        DisplayData data = new DisplayData();
	        data.regionX = entranceRoom.getX();
	        data.regionY = entranceRoom.getY();
	        data.playerX = builder.getEntrancePosition().getX();
	        data.playerY = builder.getEntrancePosition().getY();
	        GameControler.instance.displayWorld(data);
		} else {
			// Affichage de la carte
			GameControler.instance.setScreen(worldMapScreen);
			worldMapScreen.initHud(Config.asInt("minimap.width", (int)(screenWidth * 0.8)), Config.asInt("minimap.height", (int)(screenWidth * 0.8)));
			GameControler.instance.setCurrentArea(worldMapScreen.getCurrentArea().getX(), worldMapScreen.getCurrentArea().getY());
			GameControler.instance.setCharacters(worldMapScreen.getMap().getCharacters());
			worldMapScreen.checkFreeMove();
			GameControler.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
			updateHUD(GameControler.instance.getCurrentArea());
		}
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			Point roomMinSize, Point roomMaxSize,
			int difficulty) {
		// Si un donjon existe déjà, on le supprime
		disposeDungeonScreen();
		
		// Construction de la carte
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight, difficulty);
		builder.createAreas(roomMinSize, roomMaxSize, GROUND_DATA);
		builder.placeMainEntrances();
		dungeonScreen = new DungeonScreen(hudRenderer, builder, 96, 96);
		GameControler.instance.setScreen(dungeonScreen);
		
		// Choix de la musique de fond
		dungeonScreen.setBackgroundMusic(Assets.dungeonMusics[MathUtils.random(Assets.dungeonMusics.length - 1)]);
		
		// Crée l'acteur représentant le joueur
//DBG		Point entrancePosition = builder.getEntrancePosition();
//		dungeonScreen.createPlayer(entrancePosition);
		
		// Le joueur est créé : création du hud
		dungeonScreen.initHud(Config.asInt("minimap.width", (int)(screenWidth * 0.8)), Config.asInt("minimap.height", (int)(screenWidth * 0.8)));
		
		// Affichage de la carte
        Point entranceRoom = builder.getEntranceRoom();
        DisplayData data = new DisplayData();
        data.regionX = entranceRoom.getX();
        data.regionY = entranceRoom.getY();
        data.playerX = builder.getEntrancePosition().getX();
        data.playerY = builder.getEntrancePosition().getY();
        GameControler.instance.displayWorld(data);
        
        updateHUD(data.regionX, data.regionY);
        
		setScreen(dungeonScreen);
	}
	
	@Override
	public void render() {
		super.render();
		hudRenderer.render(Gdx.graphics.getDeltaTime());
	}

	/**
	 * Affiche un message à l'utilisateur
	 * @param message
	 */
	public void showMessage(String message) {
		MessageBox msg = MessageBoxFactory.createSimpleMessage(message, hudRenderer);
		msg.show();
	}

	/**
	 * Met à jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(int currentAreaX, int currentAreaY) {
		hudRenderer.update(currentAreaX, currentAreaY);
	}

	/**
	 * Met à jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(Point currentArea) {
		updateHUD(currentArea.getX(), currentArea.getY());
	}

	public HUDRenderer getHUDRenderer() {
		return hudRenderer;
	}
}
