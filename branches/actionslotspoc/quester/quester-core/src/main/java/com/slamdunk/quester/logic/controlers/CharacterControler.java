package com.slamdunk.quester.logic.controlers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.AIAction;
import com.slamdunk.quester.logic.ai.AttackAction;
import com.slamdunk.quester.logic.ai.CheckEnemyDetection;
import com.slamdunk.quester.logic.ai.EndTurnAction;
import com.slamdunk.quester.logic.ai.HealAction;
import com.slamdunk.quester.logic.ai.MoveAction;
import com.slamdunk.quester.logic.ai.MoveNearAction;
import com.slamdunk.quester.logic.ai.ProtectAction;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.ai.ThinkAction;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.points.Point;

public class CharacterControler extends WorldElementControler implements Damageable {

	/**
	 * Les 8 voisins autour
	 */
	public static int[][] NEIGHBORS_ALL = new int[][]{
		new int[]{-1, -1},
		new int[]{0, -1},
		new int[]{+1, -1},
		new int[]{-1, 0},
		new int[]{+1, 0},
		new int[]{-1, +1},
		new int[]{0, +1},
		new int[]{+1, +1},
	};
	
	/**
	 * Voisins en "+" : au-dessus, en-dessous, à gauche et à droite.
	 */
	public static int[][] NEIGHBORS_PLUS = new int[][]{
		new int[]{0, -1},
		new int[]{-1, 0},
		new int[]{+1, 0},
		new int[]{0, +1},
	};
	
	/**
	 * Objet choissant les actions à effectuer
	 */
	protected AI ai;
	
	protected CharacterData characterData;
	
	/**
	 * Réduction de dégâts que le joueur a joué
	 */
	private int damageReduction;
	
	/**
	 * Zone de détection
	 */
	private int[][] detectionArea;
	
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
	
	public CharacterControler(CharacterData data, CharacterActor body, AI ai) {
		super(data, body);
		listeners = new ArrayList<CharacterListener>();
		
		this.ai = ai;
		this.ai.setControler(this);
	}
	
	@Override
	public void act(float delta) {
		// Si le personnage est inactif, sa prochaine action est de finir son tour
		if (!isEnabled()) {
			ai.setNextAction(new EndTurnAction());
		}
		// Exécution de la prochaine action
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

	@Override
	public CharacterActor getActor() {
		return (CharacterActor)super.getActor();
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

	public int[][] getDetectionArea() {
		return detectionArea;
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

	public Sound getStepSound() {
		return null;
	}
	
	@Override
	public void heal(int amount) {
		setHealth(characterData.health + amount);
	}
	
	@Override
	public boolean isDead() {
		return characterData.health <= 0;
	}
	
	public boolean isHostile() {
		return false;
	}
	
	/**
	 * Renvoie true si le CharacterControler passé en paramètre est dans la zone de perception
	 * de ce perso. C'est notamment ici qu'on pourra dire que si le CharacterControler a un sort
	 * d'invisibilité, alors il ne sera pas visible.
	 */
	public boolean isInSight(CharacterControler character) {
		if (detectionArea != null) {
			final int myX = actor.getWorldX();
			final int myY = actor.getWorldY();
			final int hisX = character.actor.getWorldX();
			final int hisY = character.actor.getWorldY();
			for (int[] neighbor : detectionArea) {
				if (myX + neighbor[0] == hisX
				&& myY + neighbor[1] == hisY) {
					return true;
				}
			}
		}
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
		// Calcule le chemin qu'il faut emprunter
		updatePath(x, y, ignoreArrivalWalkability);
		
		// S'il n'y a pas de chemin, on ne fait rien
		if (path == null) {
			return false;
		}
		
		// Comme on veut se déplacer "près" de la position, on retire le dernier point
		if (stopNear) {
			path.remove(path.size() - 1);
		}
		
		prepareMoveAlongPath();
		return true;
	}

	public void prepareMoveAlongPath() {
		for (Point pos : path) {
			ai.addAction(new MoveAction(pos.getX(), pos.getY()));
			ai.addAction(new CheckEnemyDetection());
			if (!characterData.isFreeMove) {
				characterData.movesLeft--;
			}
		}
	}

	public boolean prepareMoveNear(WorldElementActor target) {
		// Calcule le chemin qu'il faut emprunter
		updatePath(target.getWorldX(), target.getWorldY(), true);
		
		// S'il n'y a pas de chemin, on ne fait rien
		if (path == null) {
			return false;
		}
		
		ai.addAction(new MoveNearAction(target));
		ai.addAction(new CheckEnemyDetection());
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
		ai.clearActions();
		ai.addAction(new ThinkAction());
	}
	
	public void protect(int damageReduction) {
		this.damageReduction = damageReduction;
	}
	
	@Override
	public void receiveDamage(int damage) {
		damage = Math.max(0, damage - damageReduction);
		setHealth(characterData.health - damage);
	}
	
	@Override
	public void receiveDrop(QuesterActions action) {
		PlayerControler player = GameControler.instance.getPlayer();
		switch (action) {
		case ATTACK:
			player.ai.clearActions();
			player.ai.addAction(new AttackAction(this));
		break;
		case END_TURN:
			player.ai.clearActions();
			player.ai.addAction(new EndTurnAction());
		break;
		case HEAL:
			player.ai.clearActions();
			player.ai.addAction(new HealAction(player, 3));
		break;
		case PROTECT:
			player.ai.clearActions();
			player.ai.addAction(new ProtectAction(player, 3));
		break;
		}
	}

	public void removeListener(CharacterListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void setData(WorldElementData data) {
		super.setData(data);
		characterData = (CharacterData)data;
	}
	
	public void setDetectionArea(int[][] detectionArea) {
		this.detectionArea = detectionArea;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		boolean prevEnableState = this.enabled;
		super.setEnabled(enabled);
		// Quand un personnage est activé, on réinitialise son IA
		if (prevEnableState != enabled && enabled) {
			ai.clearActions();
		}
		// Quand un personnage passe à l'état inactif, on modifie l'action courante
		// de son Actor en conséquence
		if (!enabled) {
			actor.setCurrentAction(QuesterActions.NONE, 0);
		}
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
	
	/**
	 * Retourne true si la valeur a changé
	 */
	public boolean setPlaying(boolean isPlaying) {
		boolean statusChanged = this.isPlaying != isPlaying;
		this.isPlaying = isPlaying;
		
		// Quand c'est à son tour de jouer, le joueur perd la protection qu'il avait jouée
		// à son tour
		if (statusChanged && isPlaying) {
			damageReduction = 0;
		}
		return statusChanged;
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

	public boolean updatePath(int x, int y, boolean ignoreArrivalWalkability) {
		path = GameControler.instance.getScreen().getMap().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y,
				ignoreArrivalWalkability);
		return path != null && !path.isEmpty();
	}
}
