package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.GroundActor;
import com.slamdunk.quester.model.data.WorldElementData;

public class GroundControler extends WorldElementControler {
	private static boolean isDestinationSet;
	
	public GroundControler(WorldElementData data) {
		super(data);
	}
	
	public GroundControler(WorldElementData data, GroundActor actor) {
		super(data, actor);
	}

	public void movePlayer() {
		PlayerControler player = GameControler.instance.getPlayer();
		if (isDestinationSet) {
			// Suppression d'un éventuel autre chemin affiché à l'écran par un autre GroundControler
			resetDestination();
		}
		// La destination n'est pas encore définie. On propose un chemin au joueur.
		else if (player.updatePath(actor.getWorldX(), actor.getWorldY(), false)) {
			// Affichage du chmin proposé
			GameControler.instance.getScreen().getMapRenderer().showPath(player.getPath());
			isDestinationSet = true;
		}
	}

	public static void resetDestination() {
		GameControler.instance.getScreen().getMapRenderer().clearPath();
		isDestinationSet = false;
	}

}
