package com.slamdunk.quester.logic.controlers;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.ai.CrossPathAction;
import com.slamdunk.quester.logic.ai.EnterCastleAction;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.utils.Assets;

public class PlayerControler extends CharacterControler {
	public PlayerControler(CharacterData data, PlayerActor body) {
		super(data, body, new PlayerAI());
		setShowDestination(true);
	}
	
	@Override
	public boolean canAcceptDrop(Payload payload) {
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean crossPath(PathToAreaControler path) {
		// On se d�place sur le chemin
		if (!prepareMoveOver(path.getActor().getWorldX(), path.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le une fois que le d�placement est fini
		ai.addAction(new CrossPathAction(this, path));
		return true;
	}
	
	public boolean enterCastle(CastleControler castle) {
		// On se d�place sur le ch�teau
		if (!prepareMoveOver(castle.getActor().getWorldX(), castle.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le donjon une fois que le d�placement est fini
		ai.addAction(new EnterCastleAction(this, castle));
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
}
