package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.input.GestureDetector;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.camera.MouseScrollZoomProcessor;
import com.slamdunk.quester.display.camera.TouchGestureListener;
import com.slamdunk.quester.display.hud.HUDRenderer;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.MapRenderer;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

/**
 * Repr�sente un �cran de jeu. Un �cran contient plusieurs zones de carte et en affiche
 * une avec un mapRenderer. Un HUD peut �galement �tre affich�. Il est �galement charg�
 * de g�rer les interactions avec l'utilisateur et la musique.
 */
public class GameScreen implements Screen {
	private static final FPSLogger fpsLogger = new FPSLogger();
	/**
	 * Toutes les zones de la carte. L'une d'entre elles sera affich�e comme zone courante.
	 */
	private final MapArea[][] areas;
	/**
	 * Position de la zone courante sur la carte g�n�rale.
	 */
	private final Point currentRoom;
	/**
	 * Musique � jouer sur cet �cran
	 */
	private String backgroundMusic;
	/**
	 * Gestionnaire des entr�es utilisateur. Ce multiplexer g�re les touches et les
	 * scrolls de souris.
	 */
	protected final InputMultiplexer inputMultiplexer;
	/**
	 * Charg� de l'affichage de la carte
	 */
	private MapRenderer mapRenderer;
	/**
	 * Charg� de l'affichage du HUD.
	 */
	private HUDRenderer hudRenderer;
	/**
	 * L'acteur actuellement utilis� pour repr�senter le joueur sur la carte.
	 */
	private PlayerActor player;
	/**
	 * Astuce permettant de centrer la cam�ra sur le joueur au premier affichage de
	 * la carte.
	 */
	private boolean isFirstDisplay;
	
	public GameScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		Point entrance = builder.getEntranceRoom();
		currentRoom = new Point(entrance.getX(), entrance.getY());
		
		// Cr�ation des renderers
		mapRenderer = new MapRenderer(builder.getAreaWidth(), builder.getAreaHeight(), worldCellWidth, worldCellHeight);
		hudRenderer = new HUDRenderer();
		
		// DBG Affichage du donjon en texte
		builder.printMap();
		
		// Cr�ation du gestionnaire d'input
 		inputMultiplexer = new InputMultiplexer();
 		inputMultiplexer.addProcessor(hudRenderer);
 		inputMultiplexer.addProcessor(new GestureDetector(new TouchGestureListener(mapRenderer)));
 		inputMultiplexer.addProcessor(new MouseScrollZoomProcessor(mapRenderer));
 		enableInputListeners(true);
		
		// DBG Rustine pour r�ussir � centrer sur le joueur lors de l'affichage
        // de la toute premi�re pi�ce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute premi�re fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}
	
	/**
	 * Centre la cam�ra sur le milieu de la carte
	 */
	public void centerCamera() {
		ActorMap map = mapRenderer.getMap();
		WorldElementActor firstActor = map.getTopElementAt(0, 0);
		mapRenderer.getCamera().position.set(
			firstActor.getX() + map.getMapWidth() * map.getCellWidth() / 2, 
			firstActor.getY() + map.getMapHeight() * map.getCellHeight() / 2, 
			0);
	}

	/**
	 * Centre la cam�ra sur le joueur
	 * @param element
	 */
	public void centerCameraOn(WorldElementActor actor) {
		// On n'utilise pas actor.centerCameraOnSelf() car on veut
		// un d�placement imm�diat de la cam�ra et non pas progressif.
		mapRenderer.getCamera().position.set(
			actor.getX() + actor.getWidth() / 2, 
			actor.getY() + actor.getHeight() / 2, 
			0);
	}
	
	/**
	 * Cr�e le HUD
	 */
	public void initHud(int miniMapWidth, int miniMapHeight) {
		hudRenderer.init(mapRenderer.getStage());
		if (miniMapWidth > 0 && miniMapHeight > 0) {
			hudRenderer.setMiniMap(areas, miniMapWidth, miniMapHeight);
		}
	}

	/**
	 * Cr�e une repr�sentation physique (WorldActor) du joueur.
	 * @param hp
	 * @param att
	 */
	public void createPlayer(Point position) {
		player = new PlayerActor();
		player.setControler(GameControler.instance.getPlayer());
		player.setPositionInWorld(position.getX(), position.getY());
		
		GameControler.instance.getPlayer().setPathfinder(mapRenderer.getMap().getPathfinder());
	}
	
	/**
	 * Affiche la pi�ce de donjon aux coordonn�es indiqu�es, en placant
	 * le h�ro � l'entr�e de la pi�ce aux coordonn�es indiqu�es.
	 */
	public void displayWorld(DisplayData display) {
		Assets.playMusic(backgroundMusic);
		
		// La salle actuellement affich�e a chang�
		// Certains �l�ments (portes et chemins) ont besoin de conna�tre la position
		// de la salle courante. Il faut donc mettre � jour currentRoom avant de cr�er
		// les �l�ments.
        currentRoom.setXY(display.regionX, display.regionY);
		MapArea area = areas[display.regionX][display.regionY];
		mapRenderer.buildMap(area, currentRoom);

	 	// Placement du joueur puis cr�ation des autres personnages
	 	player.setPositionInWorld(display.playerX, display.playerY);
	 	CharacterControler playerControler = player.getControler();
	 	mapRenderer.getMap().addCharacter(playerControler);
        mapRenderer.createCharacters(area);
        
        // Mise � jour du pad et de la minimap
        hudRenderer.update(display.regionX, display.regionY);
        
        // Centrage de la cam�ra sur le joueur
//        centerCameraOn(player);
        centerCamera();
        // Zoom pour afficher toute la carte
        mapRenderer.getCamera().zoom = mapRenderer.getMap().getMapWidth() * mapRenderer.getMap().getCellWidth() / screenWidth;
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		hudRenderer.dispose();
	}

	public void enableInputListeners(boolean enable) {
		if (enable) {
			Gdx.input.setInputProcessor(inputMultiplexer);
		}
	}

	/**
	 * Retourne la zone du monde aux coordonn�es indiqu�es
	 */
	public MapArea getArea(Point currentArea) {
		return areas[currentArea.getX()][currentArea.getY()];
	}

	public String getBackgroundMusic() {
		return backgroundMusic;
	}

	/**
	 * Retourne la zone du monde courante
	 */
	public MapArea getCurrentArea() {
		return areas[currentRoom.getX()][currentRoom.getY()];
	}

	public ActorMap getMap() {
		return mapRenderer.getMap();
	}
	
	public MapRenderer getMapRenderer() {
		return mapRenderer;
	}
	
	public PlayerActor getPlayerActor() {
		return player;
	}
	
	@Override
	public void hide() {
		// DBG L'�cran n'est plus affich�. Il faut avoir sauvegard� avant !
		//mapRenderer.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			//centerCameraOn(player);
			centerCamera();
		}
		
		// Efface l'�cran
		//Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Mise � jour du menu contextuel, qui doit faire une belle animation pour appara�tre
//DBG		if (ContextMenuControler.openedMenu != null) {
//			ContextMenuControler.openedMenu.act(delta);
//		}
		
		// Le WorldElement dont le tour est en cours joue
		GameControler.instance.getCurrentCharacter().act(delta);
		
        // Dessine la sc�ne et le hud
        mapRenderer.render();
        hudRenderer.render(delta);
        
        fpsLogger.log();
	}
	
	@Override
	public void resize(int width, int height) {
		mapRenderer.getStage().setViewport(screenWidth, screenHeight, true);
	}
	

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	public void setBackgroundMusic(String backgroundMusic) {
		this.backgroundMusic = backgroundMusic;
	}
	
	@Override
	public void show() {
		// R�activation des listeners
		enableInputListeners(true);
		
		// Centrage de la cam�ra sur le joueur
		// DBG Normalement le centerCameraOn() devrait �tre
		// suffisant pour centrer la cam�ra sur le joueur quand
		// on revient sur la carte du monde. Ca ne marche
		// malheureusement pas et on doit recourir encore
		// une fois � l'astuce du isFirstDisplay :(
		//DBGcenterCameraOn(player);
		centerCamera();
		isFirstDisplay = true;
		
		// Lancement de la musique
		Assets.playMusic(backgroundMusic);
	}

	/**
	 * Affiche un message � l'utilisateur
	 * @param message
	 */
	public void showMessage(String message) {
		MessageBox msg = MessageBoxFactory.createSimpleMessage(message, hudRenderer);
		msg.show();
	}

	/**
	 * Met � jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(int currentAreaX, int currentAreaY) {
		hudRenderer.update(currentAreaX, currentAreaY);
	}

	/**
	 * Met � jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(Point currentArea) {
		updateHUD(currentArea.getX(), currentArea.getY());
	}

	public HUDRenderer getHUDRenderer() {
		return hudRenderer;
	}
}
