package com.slamdunk.quester.logic.controlers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.AIAction;
import com.slamdunk.quester.logic.ai.AttackAction;
import com.slamdunk.quester.logic.ai.CharacterAI;
import com.slamdunk.quester.logic.ai.EndTurnAction;
import com.slamdunk.quester.logic.ai.MoveAction;
import com.slamdunk.quester.logic.ai.MoveNearAction;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.points.Point;

public class CharacterControler extends WorldElementControler implements Damageable {

	/**
	 * Objet choissant les actions � effectuer
	 */
	protected AI ai;
	
	protected CharacterData characterData;
	
	/**
	 * Indique si ce Character est dans son tour de jeu
	 */
	private boolean isPlaying;
	
	/**
	 * Indique s'il faut afficher la destination et le chemin
	 * du personnage sur la carte
	 */
	private boolean isShowDestination;
	
	/**
	 * Objets int�ress�s par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * Chemin que va suivre le personnage
	 */
	private List<Point> path;
	
	/**
	 * Objet � utiliser pour trouver un chemin entre 2 points.
	 */
	private AStar pathfinder;
	
	public CharacterControler(CharacterData data, CharacterActor body, AI ai) {
		super(data, body);
		listeners = new ArrayList<CharacterListener>();
		
		if (ai == null) {
			this.ai = new CharacterAI();
		} else {
			this.ai = ai;
		}
		ai.setControler(this);
		ai.init();
	}
	
	@Override
	public void act(float delta) {
		AIAction action = ai.getNextAction();
		action.act();
		super.act(delta);
	}

	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}
	
	public boolean canAttack(WorldElementControler target) {
		// Impossible d'attaquer :
		// Si la cible n'est pas Damageable
		return (target instanceof Damageable)
		// Si la cible est morte
		&& !((Damageable)target).isDead();
	}

	public boolean canMoveTo(int x, int y) {
		final List<Point> litPath = GameControler.instance.getScreen().getMap().findPath(
			actor.getWorldX(), actor.getWorldY(), 
			x, y);
		// Impossible d'aller � l'emplacement :
		// Si aucun chemin n'existe
		return litPath != null && !litPath.isEmpty();
	}

	private void die() {
		// R�cup�ration du clip de mort de cet acteur
		GameControler.instance.getScreen().getMapRenderer().createVisualEffect("explosion-death", actor);
		
		// On pr�vient les listeners que le Character meurt
		for (CharacterListener listener : listeners) {
			listener.onCharacterDeath(this);
		}
	}
	
	public AI getAI() {
		return ai;
	}

	public Sound getAttackSound() {
		return null;
	}

	@Override
	public CharacterData getData() {
		return characterData;
	}
	
	@Override
	public int getHealth() {
		return characterData.health;
	}

	public List<CharacterListener> getListeners() {
		return listeners;
	}

	public List<Point> getPath() {
		return path;
	}
	
	public AStar getPathfinder() {
		return pathfinder;
	}
	
	public Sound getStepSound() {
		return null;
	}
	
	/**
	 * Retourne true si other est sur une case voisine
	 * @param actor
	 * @param target
	 * @return
	 */
	public boolean isAround(WorldElementActor other) {
		// A c�t� s'ils sont sur le m�me X et avec 1 seule case d'�cart en Y...
		return actor.getWorldX() == other.getWorldX() && Math.abs(actor.getWorldY() - other.getWorldY()) == 1
		// ... ou sur le m�me Y et avec une seule case d'�cart en X
		|| actor.getWorldY() == other.getWorldY() && Math.abs(actor.getWorldX() - other.getWorldX()) == 1;
	}
	
	@Override
	public boolean isDead() {
		return characterData.health <= 0;
	}
	
	public boolean isHostile() {
		return false;
	}

	public boolean isPlaying() {
		return isPlaying;
	}
	
	public boolean isShowDestination() {
		return isShowDestination;
	}
	
	/**
	 * Approche le personnage de la cible puis l'attaque.
	 */
	public boolean prepareAttack(WorldElementControler target) {
		// Approche de la cible
		if (!prepareMoveNear(target.getActor())) {
			return false;
		}
		
		// Attaque
		ai.addAction(new AttackAction(this, (Damageable)target));
		return true;
	}
	
	/**
	 * D�place le personnage jusqu'� ce qu'il soit autour des coordonn�es indiqu�es,
	 * en placant � chaque fois une torche.
	 */
	private boolean prepareMove(int x, int y, boolean stopNear, boolean ignoreArrivalWalkability) {
		if (pathfinder == null) {
			return false;
		}
		
		// Calcule le chemin qu'il faut emprunter
		final List<Point> walkPath = pathfinder.findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y,
				true);
		
		// S'il n'y a pas de chemin, on ne fait rien
		if (walkPath == null) {
			return false;
		}
		
		// Comme on veut se d�placer "pr�s" de la position, on retire le dernier point
		if (stopNear) {
			walkPath.remove(walkPath.size() - 1);
		}
		
		// Pour aller jusqu'� ce point, on doit prendre chaque position et s'assurer qu'elle
		// est �clair�e puis s'y d�placer
		for (Point pos : walkPath) {
			ai.addAction(new MoveAction(this, pos.getX(), pos.getY(), ignoreArrivalWalkability));
		}
		return true;
	}

	public boolean prepareMoveNear(WorldElementActor target) {
		// Calcule le chemin qu'il faut emprunter
		final List<Point> walkPath = pathfinder.findPath(
				actor.getWorldX(), actor.getWorldY(), 
				target.getWorldX(), target.getWorldY(),
				true);
		
		// S'il n'y a pas de chemin, on ne fait rien
		if (walkPath == null) {
			return false;
		}
		
		ai.addAction(new MoveNearAction(this, target));
		return true;
	}
	
	public boolean prepareMoveOver(int x, int y) {
		return prepareMove(x, y, false, true);
	}

	/**
	 * D�place le personnage jusqu'� ce qu'il atteigne les coordonn�es indiqu�es.
	 */
	public boolean prepareMoveTo(int x, int y) {
		return prepareMove(x, y, false, false);
	}

	/**
	 * Annule toutes les actions en cours et pr�pare le think()
	 */
	public void prepareThink() {
		path = null;
		if (isShowDestination) {
			GameControler.instance.getScreen().getMapRenderer().clearPath();
		}
		ai.init();
	}
	
	@Override
	public void receiveDamage(int damage) {
		// TODO Retirer la valeur d'armure �ventuellement
		characterData.health -= damage;
		
		// Si un d�placement �tait en cours, il est interrompu
		stopMove();
		
		if (isDead()) {
			die();
		}
	}
	
	@Override
	public void setData(WorldElementData data) {
		super.setData(data);
		characterData = (CharacterData)data;
	}
	
	@Override
	public void setHealth(int value) {
		int oldValue = characterData.health;
		characterData.health = value;
		for (CharacterListener listener : listeners) {
			listener.onHealthPointsChanged(oldValue, value);
		}
		if (isDead()) {
			die();
		}
	}
	
	public void setPathfinder(AStar pathfinder) {
		this.pathfinder = pathfinder;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
		
		// Si c'est au tour de ce personnage de jouer, alors
		// un nouveau tour d'attente a pass�.
		if (isPlaying) {
			characterData.waitTurns--;
			// S'il ne reste pluzs d'attente, on effectue la
			// prochaine action
			if (characterData.waitTurns <= 0) {
				characterData.waitTurns = characterData.actFrequency;
				this.isPlaying = true;
			} else {
				ai.setNextAction(new EndTurnAction(this));
				this.isPlaying = false;
			}
		}
	}

	public void setShowDestination(boolean isShowDestination) {
		this.isShowDestination = isShowDestination;
	}
	
	/**
	 * Arr�te le d�placement en cours
	 */
	public void stopMove() {
		prepareThink();
	}

	protected boolean updatePath(int x, int y) {
		path = GameControler.instance.getScreen().getMap().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		return path != null && !path.isEmpty();
	}
}
