package com.slamdunk.quester.logic.controlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.AIAction;
import com.slamdunk.quester.logic.ai.AttackAction;
import com.slamdunk.quester.logic.ai.EndTurnAction;
import com.slamdunk.quester.logic.ai.HealAction;
import com.slamdunk.quester.logic.ai.MoveAction;
import com.slamdunk.quester.logic.ai.MoveNearAction;
import com.slamdunk.quester.logic.ai.ProtectAction;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.points.Point;

public class CharacterControler extends WorldElementControler implements Damageable {

	/**
	 * Objet choissant les actions à effectuer
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
	 * Objets intéressés par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * Chemin que va suivre le personnage
	 */
	private List<Point> path;
	
	/**
	 * Objet à utiliser pour trouver un chemin entre 2 points.
	 */
	private AStar pathfinder;
	
	/**
	 * Réduction de dégâts que le joueur a joué
	 */
	private int damageReduction;
	
	public CharacterControler(CharacterData data, CharacterActor body, AI ai) {
		super(data, body);
		listeners = new ArrayList<CharacterListener>();
		
		if (ai == null) {
			this.ai = new AI();
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
		// Impossible d'aller à l'emplacement :
		// Si aucun chemin n'existe
		return litPath != null && !litPath.isEmpty();
	}

	private void die() {
		// Récupération du clip de mort de cet acteur
		GameControler.instance.getScreen().getMapRenderer().createVisualEffect("explosion-death", actor);
		
		// On prévient les listeners que le Character meurt
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
	
	@Override
	public void heal(int amount) {
		setHealth(characterData.health + amount);
	}
	
	/**
	 * Retourne true si other est sur une case voisine
	 * @param actor
	 * @param target
	 * @return
	 */
	public boolean isAround(WorldElementActor other) {
		// A côté s'ils sont sur le même X et avec 1 seule case d'écart en Y...
		return actor.getWorldX() == other.getWorldX() && Math.abs(actor.getWorldY() - other.getWorldY()) == 1
		// ... ou sur le même Y et avec une seule case d'écart en X
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
	 * Déplace le personnage jusqu'à ce qu'il soit autour des coordonnées indiquées,
	 * en placant à chaque fois une torche.
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
		
		// Comme on veut se déplacer "près" de la position, on retire le dernier point
		if (stopNear) {
			walkPath.remove(walkPath.size() - 1);
		}
		
		// Pour aller jusqu'à ce point, on doit prendre chaque position et s'assurer qu'elle
		// est éclairée puis s'y déplacer
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
	 * Déplace le personnage jusqu'à ce qu'il atteigne les coordonnées indiquées.
	 */
	public boolean prepareMoveTo(int x, int y) {
		return prepareMove(x, y, false, false);
	}

	/**
	 * Annule toutes les actions en cours et prépare le think()
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
		damage = Math.max(0, damage - damageReduction);
		setHealth(characterData.health - damage);
	}
	
	@Override
	public void receiveDrop(ActionSlotControler dropped) {
		PlayerControler player = GameControler.instance.getPlayer();
		switch (dropped.getData().action) {
		case ATTACK:
			player.ai.clearActions();
			player.ai.addAction(new AttackAction(player, this));
			player.ai.addAction(new EndTurnAction(player));
		break;
		case END_TURN:
			player.ai.clearActions();
			player.ai.addAction(new EndTurnAction(player));
		break;
		case HEAL:
			player.ai.clearActions();
			player.ai.addAction(new HealAction(player, 3));
			player.ai.addAction(new EndTurnAction(player));
		break;
		case PROTECT:
			player.ai.clearActions();
			player.ai.addAction(new ProtectAction(player, 3));
			player.ai.addAction(new EndTurnAction(player));
		break;
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
		// Quand c'est à son tour de jouer, le joueur perd la protection qu'il avait jouée
		// à son tour
		if (isPlaying) {
			damageReduction = 0;
		}
	}

	public void setShowDestination(boolean isShowDestination) {
		this.isShowDestination = isShowDestination;
	}
	
	/**
	 * Arrête le déplacement en cours
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

	public void protect(int damageReduction) {
		this.damageReduction = damageReduction;
	}
}
