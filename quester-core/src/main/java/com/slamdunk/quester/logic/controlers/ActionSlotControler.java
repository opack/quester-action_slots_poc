package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.display.hud.actionslots.ActionSlotsHelper;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.ActionSlotData;

public class ActionSlotControler extends WorldElementControler {

	public ActionSlotControler(ActionSlotData data) {
		super(data);
	}
	
	@Override
	public ActionSlotData getData() {
		return (ActionSlotData)data;
	}
	
	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		return true;
	}
	
	@Override
	public void receiveDrop(QuesterActions action) {
		// Affectation du dropped à ce slot
		ActionSlotsHelper.setSlotData(action, (ActionSlotActor)actor);
	}
}
