package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

/**
 * Déplace le contrôleur vers les coordonnées spécifiées.
 */
public class MoveAction extends AbstractAIAction {
	private int destinationX;
	private int destinationY;
	
	public MoveAction(Point destination) {
		if (destination == null) {
			this.destinationX = -1;
			this.destinationY = -1;
		} else {
			this.destinationX = destination.getX();
			this.destinationY = destination.getY();
		}
	}

	public MoveAction(int destinationX, int destinationY) {
		this.destinationX = destinationX;
		this.destinationY = destinationY;
	}

	public void act() {
		CharacterControler character = ai.controler;
		
		// Fait un bruit de pas
		Assets.playSound(character.getStepSound());
		
		// Déplace le personnage
		int oldX = character.getActor().getWorldX();
		int oldY = character.getActor().getWorldY();
		character.getActor().moveTo(destinationX, destinationY, 1 / character.getData().speed);
		
		// Avertit les listeners que le personnage bouge
		for (CharacterListener listener : character.getListeners()) {
			listener.onCharacterMoved(character, oldX, oldY);
		}
		
		// Si on est entré dans la zone de perception d'un ennemi, le déplacement est interrompu.
		ai.nextAction();
		// On attend la fin du mouvement puis on termine le tour.
		ai.setNextActions(new WaitCompletionAction());
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
