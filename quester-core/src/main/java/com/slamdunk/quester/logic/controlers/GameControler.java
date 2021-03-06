package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import java.util.Collections;
import java.util.List;

import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.points.Point;

public class GameControler implements CharacterListener {
	
	public static final GameControler instance = new GameControler();

	private List<CharacterControler> characters;
	private int curCharacterPlaying;
	
	private Point currentArea;

	private boolean hasMoreEnemies;
	
	private PlayerControler player;
	
	private GameScreen screen;
	
	private GameControler() {
		currentArea = new Point(-1, -1);
	}

	/**
	 * Cr�e le contr�leur du joueur, qui sera utilis� dans chaque �cran de jeu
	 */
	public void createPlayerControler(int hp, int att) {
		CharacterData data = new CharacterData(PLAYER, hp, att);
		data.speed = 4;
		data.walkDistance = 3;
		
		player = new PlayerControler(data, null);
		player.addListener(this);
	}
	
	public void displayWorld(DisplayData data) {
		// Modification de la zone courante et affichage de la carte
		currentArea.setXY(data.regionX, data.regionY);
		screen.displayWorld(data);
		
		if (characters != null) {
			setCharacters(characters);
		}
	}
	
	/**
	 * Ach�ve le tour du joueur courant et d�marre le tour du joueur suivant.
	 */
	public void endCurrentPlayerTurn() {
		// C'est au prochain joueur de jouer. Le tour du joueur courant s'ach�ve
		characters.get(curCharacterPlaying).setPlaying(false);
		
		// Au tour du prochain de jouer !
		curCharacterPlaying++;
		
		// Quand tout le monde a jou� son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que �a ait chang�.
        if (curCharacterPlaying >= characters.size()) {
        	initCharacterOrder();
        }
		
        // On active le prochain joueur
        characters.get(curCharacterPlaying).setPlaying(true);
	}

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}
	
	public Point getCurrentArea() {
		return currentArea;
	}
	
	public List<CharacterControler> getCharacters() {
		return characters;
	}

	public CharacterControler getCurrentCharacter() {
		return characters.get(curCharacterPlaying);
	}

	public PlayerControler getPlayer() {
		return player;
	}

	/**
	 * Retourne la carte associ�e � ce monde
	 * @return
	 */
	public GameScreen getScreen() {
		return screen;
	}

	public boolean hasMoreEnemies() {
		return hasMoreEnemies;
	}
	
	public void initCharacterOrder() {
    	Collections.sort(characters);
    	curCharacterPlaying = 0;
	}

	public void nextPlayer() {
		endCurrentPlayerTurn();
		updateHUD();
	}

	@Override
	public void onActionPointsChanged(int oldValue, int newValue) {
		Quester.getInstance().updateHUD(currentArea);
	}
	
	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		Quester.getInstance().updateHUD(currentArea);
	}

	@Override
	public void onCharacterDeath(CharacterControler character) {
		// On recherche l'indice du personnage � supprimer dans la liste
		int index = characters.indexOf(character);
		// Si le perso supprim� devait jouer apr�s le joueur actuel (index > curCharacterPlaying),
		// alors l'ordre de jeu n'est pas impact�.
		// Si le perso supprim� devait jouer avant (index < curCharacterPlaying), alors l'ordre de
		// jeu est impact� car les indices changent. Si on ne fait rien, un joueur risque de passer
		// son tour.
		// Si le perso supprim� est le joueur actuel (index = curCharacterPlaying), alors le
		// raisonnement est le m�me
		if (index <= curCharacterPlaying) {
			curCharacterPlaying --;
		}
		
		// Suppression du character dans la liste et de la pi�ce
		WorldElementData deadCharacterData = character.getData();
		WorldElementActor removedActor = screen.getMap().removeElement(character.getActor());
		screen.getMap().getStage().getActors().removeValue(removedActor, true);
		// Met � jour le pathfinder. Si l'�l�ment �tait solide,
		// alors sa disparition rend l'emplacement walkable.
		if (deadCharacterData.isSolid && removedActor != null) {
			screen.getMap().setWalkable(removedActor.getWorldX(), removedActor.getWorldY(), true);
		}
		characters.remove(character);
		MapArea area = screen.getCurrentArea();
		if (area.isPermKillCharacters()) {
			area.removeCharacter(deadCharacterData);
		}
		
		// Si c'est le joueur qui est mort, le jeu s'ach�ve
		if (deadCharacterData.element == PLAYER) {
			Quester.getInstance().showMessage("Bouh ! T'es mort !");
		}
		
		// D�termine s'il reste des ennemis.
		updateHasMoreEnemies();
	}
	

	@Override
	public void onCharacterMoved(CharacterControler character, int oldX, int oldY) {
		Quester.getInstance().updateHUD(currentArea);
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}
	
	public void setCharacters(List<CharacterControler> characters) {
		this.characters = characters;
		if (characters != null) {
			updateHasMoreEnemies();
			
			// Initialise l'IA de tous les personnages
			for (CharacterControler character : characters) {
				character.ai.init();
			}
		
			// D�bute le jeu avec le premier joueur
			initCharacterOrder();
			Quester.getInstance().updateHUD(currentArea);
			characters.get(curCharacterPlaying).setPlaying(true);
		}
	}

	public void setCurrentArea(int x, int y) {
		currentArea.setXY(x, y);
	}

	public void setScreen(GameScreen screen) {
		this.screen = screen;
	}
	
	public void updateHasMoreEnemies() {
		hasMoreEnemies = false;
		if (characters != null) {
			for (CharacterControler character : characters) {
				if (character.isHostile()) {
					hasMoreEnemies = true;
					break;
				}
			}
		}
	}

	/**
	 * Mise � jour du pad et de la minimap
	 */
	public void updateHUD() {
		Quester.getInstance().updateHUD(currentArea);
	}
}
