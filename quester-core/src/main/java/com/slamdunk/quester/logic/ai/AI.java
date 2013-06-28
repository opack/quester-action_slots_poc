package com.slamdunk.quester.logic.ai;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.logic.controlers.CharacterControler;

public class AI {
	/**
	 * Actions programm�es
	 */
	protected List<AIAction> actions;
	
	/**
	 * Lien vers le contr�leur
	 */
	protected CharacterControler controler;
	
	public AI() {
		actions = new ArrayList<AIAction>();
	}
	
	/**
	 * Ajoute une action a ex�cuter � la suite des actions d�j� programm�es
	 */
	public void addAction(AIAction action) {
		actions.add(action);
	}

	/**
	 * Supprime toutes les actions pr�vues
	 */
	public void clearActions() {
		actions.clear();
	}

	public CharacterControler getControler() {
		return controler;
	}

	/**
	 * Retourne la prochaine action � effectuer
	 */
	public AIAction getNextAction() {
		if (actions.isEmpty()) {
			init();
		}
		return actions.get(0);
	}

	/**
	 * Initialise l'IA
	 */
	public void init() {
		clearActions();
	}
	
	/**
	 * Active la prochaine action programm�e, ou NONE s'il n'y en a pas
	 */
	public void nextAction() {
		if (!actions.isEmpty()) {
			actions.remove(0);
		}
	}
	
	public void setControler(CharacterControler controler) {
		this.controler = controler;
	}
	
	/**
	 * D�finit la prochaine action � effectuer.
	 */
	public void setNextAction(AIAction action) {
		actions.add(0, action);
	}

	/**
	 * D�finit les actions suivantes (en conservant l'ordre) pour �tre ex�cut�es
	 * d�s le prochaine coup. 
	 * @param actionWaitCompletion
	 * @param actionEndTurn
	 */
	public void setNextActions(AIAction... nextActions) {
		// Les actions sont ins�r�es � l'envers car on les ins�re en t�te de liste.
		for (int cur = nextActions.length - 1; cur >= 0; cur --) {
			setNextAction(nextActions[cur]);
		}
	}

	/**
	 * D�termine la prochaine action � effectuer
	 */
	public void think() {
		// M�thode charg�e de d�cider ce que fera l'�l�ment lorsque ce
		// sera � son tour de jouer. Par d�faut, il ne fait rien et
		// termine son tour.
		nextAction();
	}
}
