package com.slamdunk.quester.logic.ai;

import java.util.List;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

/**
 * D�place le contr�leur vers les coordonn�es sp�cifi�es.
 */
public class MoveNearAction extends AbstractAIAction {
	private WorldElementActor destination;
	
	public MoveNearAction(WorldElementActor destination) {
		this.destination = destination;
	}

	public void act() {
		CharacterControler character = ai.controler;
		
		// Met � jour le chemin menant pr�s de la destination
		character.updatePath(
			destination.getWorldX(), destination.getWorldY(),
			true);
		final List<Point> walkPath = character.getPath();
		if (walkPath == null) {
			// Pas de chemin ? Fin de l'action
			ai.nextAction();
			return;
		}
		
		// On souhaite s'arr�ter � c�t�
		walkPath.remove(walkPath.size() - 1);
		
		// D�placement vers la prochaine position
		if (!walkPath.isEmpty()) {
			// Fait un bruit de pas
			Assets.playSound(character.getStepSound());
			
			// D�place le personnage
			Point pos = walkPath.remove(0);
			int oldX = character.getActor().getWorldX();
			int oldY = character.getActor().getWorldY();
			character.getActor().moveTo(pos.getX(), pos.getY(), 1 / character.getData().speed);
			
			// Avertit les listeners que le personnage bouge
			for (CharacterListener listener : character.getListeners()) {
				listener.onCharacterMoved(character, oldX, oldY);
			}
		}
		
		// Si c'�tait la derni�re position, alors on peut finir cette action
		if (walkPath.isEmpty()) {
			ai.nextAction();
		}
		
		// On attend la fin du mouvement puis on termine le tour.
		ai.setNextActions(new WaitCompletionAction(), new EndTurnAction());
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
