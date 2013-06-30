package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.model.data.WorldElementData;

public class PathMarkerControler extends WorldElementControler {
	private boolean isValidationMarker;

	public PathMarkerControler(WorldElementData data, boolean isValidationMarker) {
		super(data);
		this.isValidationMarker = isValidationMarker;
		
	}
	public PathMarkerControler(WorldElementData data, WorldElementActor actor, boolean isValidationMarker) {
		super(data, actor);
		this.isValidationMarker = isValidationMarker;
	}
	
	public void moveAlongPath() {
		// Si le joueur clique sur le dernier marker, alors on valide le déplacement
		if (isValidationMarker) {
			GameControler.instance.getPlayer().prepareMoveAlongPath();
		}
		// Suppression du chemin affiché
		GroundControler.resetDestination();
	}
	

}
