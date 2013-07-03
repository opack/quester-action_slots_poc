package com.slamdunk.quester.logic.controlers;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.ai.AttackAction;
import com.slamdunk.quester.logic.ai.CrossPathAction;
import com.slamdunk.quester.logic.ai.EndTurnAction;
import com.slamdunk.quester.logic.ai.EnterCastleAction;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.utils.Assets;

public class PlayerControler extends CharacterControler {
	public PlayerControler(CharacterData data, PlayerActor body) {
		super(data, body, new PlayerAI());
		setShowDestination(true);
	}
	
	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		return action == QuesterActions.CHEST
		|| action == QuesterActions.END_TURN
		|| action == QuesterActions.HEAL
		|| action == QuesterActions.PROTECT
		|| action == QuesterActions.TECHSPE;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean crossPath(PathToAreaControler pathControler) {
		if (!updatePath(pathControler.getActor().getWorldX(), pathControler.getActor().getWorldY(), true)
		|| (!characterData.isFreeMove && getPath().size() > characterData.movesLeft)) {
			return false;
		}
		
		// On se déplace sur le chemin
		if (!prepareMoveOver(pathControler.getActor().getWorldX(), pathControler.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le une fois que le déplacement est fini
		ai.addAction(new CrossPathAction(pathControler));
		return true;
	}
	
	public boolean enterCastle(CastleControler castle) {
		// On se déplace sur le château
		if (!prepareMoveOver(castle.actor.getWorldX(), castle.actor.getWorldY())) {
			return false;
		}
		
		// On entre dans le donjon une fois que le déplacement est fini
		ai.addAction(new EnterCastleAction(castle));
		return true;		
	}
		
	@Override
	public Sound getAttackSound() {
		return Assets.swordSounds[MathUtils.random(Assets.swordSounds.length - 1)];
	}

	@Override
	public Sound getStepSound() {
		return Assets.stepsSound;
	}
	
	@Override
	public void receiveDrop(ActionSlotControler dropped) {
		switch (dropped.getData().action) {
		case TECHSPE:
			ai.clearActions();
			for (CharacterControler character : GameControler.instance.getCharacters()) {
				// Attaque tous les personnages hostiles, sans tenir compte de la portée de l'arme
				if (character.isHostile()) {
					ai.addAction(new AttackAction(character));
				}
			}
		break;
		default:
			super.receiveDrop(dropped);
		}
	}
	
	@Override
	public boolean setPlaying(boolean isPlaying) {
		boolean statusChanged = super.setPlaying(isPlaying);
		// Remplissage des actions en début de tour, et réinitialisation du compteur
		// de pas
		if (statusChanged && isPlaying) {
			Quester.getInstance().getHUDRenderer().refillActionSlots();
			characterData.movesLeft = characterData.walkDistance;
		}
		return statusChanged;
	}

	public void prepareEndTurn() {
		ai.clearActions();
		ai.addAction(new EndTurnAction());
	}
}
