package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.GroundActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;

public class GroundControler extends WorldElementControler {
	private static boolean isDestinationSet;
	
	public GroundControler(WorldElementData data) {
		super(data);
	}
	
	public GroundControler(WorldElementData data, GroundActor actor) {
		super(data, actor);
	}
	
	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		return action == QuesterActions.MOVE;
	}

	public void movePlayer() {
		PlayerControler player = GameControler.instance.getPlayer();
		CharacterData data = player.getData();
		if (player.updatePath(actor.getWorldX(), actor.getWorldY(), false)	// Il existe un chemin
		&& (data.isFreeMove	// Le déplacement est gratuit
				|| (data.movesLeft >= 1	// Le déplacement n'est pas gratuit mais il reste assez de moves...
					&& player.getPath().size() == 1 ) )) { // ...et le chemin n'est long que d'1 case
			GameControler.instance.getPlayer().prepareMoveTo(actor.getWorldX(), actor.getWorldY());
		}
		
//		PlayerControler player = GameControler.instance.getPlayer();
//		if (isDestinationSet) {
//			// Suppression d'un éventuel autre chemin affiché à l'écran par un autre GroundControler
//			resetDestination();
//		}
//		// La destination n'est pas encore définie. On propose un chemin au joueur.
//		else if (player.updatePath(actor.getWorldX(), actor.getWorldY(), false)) {
//			// Affichage du chmin proposé
//			GameControler.instance.getScreen().getMapRenderer().showPath(player.getPath());
//			isDestinationSet = true;
//		}
	}

	@Override
	public void onDropHoverEnter(QuesterActions action) {
		if (canAcceptDrop(action)) {
			PlayerControler player = GameControler.instance.getPlayer();
			player.updatePath(actor.getWorldX(), actor.getWorldY(), false);
			GameControler.instance.getScreen().getMapRenderer().clearPath();
			GameControler.instance.getScreen().getMapRenderer().showPath(player.getPath());
		}
	}

	public static void resetDestination() {
		GameControler.instance.getScreen().getMapRenderer().clearPath();
		isDestinationSet = false;
	}

}
