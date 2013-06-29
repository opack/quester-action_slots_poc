package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.FogActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.model.data.WorldElementData;

public class FogControler extends WorldElementControler {

	public FogControler(WorldElementData data, FogActor body) {
		super(data, body);
	}

	public void unveil() {
		ActorMap map = GameControler.instance.getScreen().getMap();
		// Retire le brouillard
		map.removeElement(actor);
    	// Dévoile ce qui se cachait dessous
		WorldElementActor beneath = map.getTopElementAt(actor.getWorldX(), actor.getWorldY());
		beneath.getControler().setEnabled(true);
		beneath.setVisible(true);
	}
}
