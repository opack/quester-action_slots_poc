package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.SnapshotArray;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.camera.TouchGestureListener;
import com.slamdunk.quester.display.hud.HUDRenderer;
import com.slamdunk.quester.display.hud.actionslots.ActionSlots;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.MapRenderer;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.map.MapLevels;
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
	 * L'acteur actuellement utilis� pour repr�senter le joueur sur la carte.
	 */
	private PlayerActor player;
	/**
	 * Astuce permettant de centrer la cam�ra sur le joueur au premier affichage de
	 * la carte.
	 */
	private boolean isFirstDisplay;
	
	public GameScreen(HUDRenderer hudRenderer, MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		Point entrance = builder.getEntranceRoom();
		currentRoom = new Point(entrance.getX(), entrance.getY());
		
		// Cr�ation des renderers
		mapRenderer = new MapRenderer(builder.getAreaWidth(), builder.getAreaHeight(), worldCellWidth, worldCellHeight);
		
		// DBG Affichage du donjon en texte
		builder.printMap();
		
		// Cr�ation du gestionnaire d'input
 		inputMultiplexer = new InputMultiplexer();
 		inputMultiplexer.addProcessor(hudRenderer);
 		inputMultiplexer.addProcessor(new GestureDetector(new TouchGestureListener(mapRenderer)));
 		//DBGinputMultiplexer.addProcessor(new MouseScrollZoomProcessor(mapRenderer));
 		enableInputListeners(true);
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
	 * Cr�e le HUD
	 */
	public void initHud(int miniMapWidth, int miniMapHeight) {
		Quester.getInstance().getHUDRenderer().init(mapRenderer.getStage());
		if (miniMapWidth > 0 && miniMapHeight > 0) {
			Quester.getInstance().getHUDRenderer().setMiniMap(areas, miniMapWidth, miniMapHeight);
		}
	}

	/**
	 * V�rifie si le freemove doit �tre activ�, et l'active le cas �ch�ant.
	 */
	public void checkFreeMove() {
		boolean areHostileRemaining = false;
		for (CharacterControler curChar : mapRenderer.getMap().getCharacters()) {
			if (curChar.isHostile()) {
				areHostileRemaining = true;
				break;
			}
		}
		GameControler.instance.getPlayer().getData().isFreeMove = !areHostileRemaining;
		GameControler.instance.updateHUD();
	}
	
	/**
	 * Cr�e une repr�sentation physique (WorldActor) du joueur.
	 * @param hp
	 * @param att
	 */
	public void createPlayer(Point position) {
		PlayerControler playerControler = GameControler.instance.getPlayer();
		
		player = new PlayerActor();
		player.setControler(playerControler);
		player.setPositionInWorld(position.getX(), position.getY());
		
		playerControler.setActor(player);
		playerControler.getAI().init();
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
 		checkFreeMove();
        
        // Mise � jour du HUD
        ActionSlots actionSlots = Quester.getInstance().getHUDRenderer().getActionSlots();
        List<CharacterControler> characters = mapRenderer.getMap().getCharacters();
        for (CharacterControler character : characters) {
        	actionSlots.addTarget(character.getActor());
        }
        SnapshotArray<Actor> groundActors = GameControler.instance.getScreen().getMap().getLayer(MapLevels.GROUND).getChildren();
        for (Actor actor : groundActors.items) {
        	if (actor != null) {
        		actionSlots.addTarget((WorldElementActor)actor);
        	}
		}
        Quester.getInstance().getHUDRenderer().update(display.regionX, display.regionY);
        
        // Centrage de la cam�ra sur le joueur
//        centerCameraOn(player);
        centerCamera();
        // Zoom pour afficher toute la carte
        mapRenderer.getCamera().zoom = mapRenderer.getMap().getMapWidth() * mapRenderer.getMap().getCellWidth() / screenWidth;
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
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
		
		// Le WorldElement dont le tour est en cours joue
		GameControler.instance.getCurrentCharacter().act(delta);
		
        // Dessine la sc�ne et le hud
        mapRenderer.render();
        
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
		centerCamera();
		isFirstDisplay = true;
		
		// Lancement de la musique
		Assets.playMusic(backgroundMusic);
	}
}
