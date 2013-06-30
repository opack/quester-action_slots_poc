package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
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
	
	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		return action == QuesterActions.MOVE;
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
	
	public void moveAlongPath() {
		// Si le joueur clique sur le dernier marker, alors on valide le déplacement
		if (isValidationMarker) {
			GameControler.instance.getPlayer().prepareMoveAlongPath();
		}
		// Suppression du chemin affiché
		GroundControler.resetDestination();
	}
	
	@Override
	public void receiveDrop(ActionSlotControler dropped) {
		if (dropped.getData().action == QuesterActions.MOVE) {
			moveAlongPath();
		}
	}
}
