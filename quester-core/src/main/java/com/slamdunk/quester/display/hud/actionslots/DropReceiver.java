package com.slamdunk.quester.display.hud.actionslots;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public interface DropReceiver {
	/**
	 * Indique si ce receiver serait pr�t � accepter ce payload
	 */
	boolean canAcceptDrop(Payload payload);

	/**
	 * M�thode appel�e lorsqu'un chargement est l�ch� sur ce receiver
	 */
	void receiveDrop(ActionSlotControler dropped);
}