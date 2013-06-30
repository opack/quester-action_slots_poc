package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.hud.actionslots.DropReceiver;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.WorldElementData;

public class WorldElementControler implements Comparable<WorldElementControler>, DropReceiver {
	private static int WORLD_ELEMENTS_COUNT = 0;
	
	protected WorldElementActor actor;
	
	protected WorldElementData data;
	protected boolean enabled;
	
	private final int id;
	
	public WorldElementControler(WorldElementData data) {
		id = WORLD_ELEMENTS_COUNT++;
		data.playRank = id;
		setData(data);
		enabled = true;
	}
	
	public WorldElementControler(WorldElementData data, WorldElementActor actor) {
		this(data);
		setActor(actor);
	}
	
	/**
	 * Méthode appelée lorsque le Stage décide qu'il faut faire agir les acteurs
	 * @param delta 
	 */
	public void act(float delta) {
		if (actor != null) {
			actor.act(delta);
		}
	}

	@Override
	public boolean canAcceptDrop(QuesterActions action) {
		return false;
	}
	
	@Override
	public int compareTo(WorldElementControler o) {
		return data.playRank - o.data.playRank;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorldElementControler) {
			return id == ((WorldElementControler)obj).id;
		}
		return false;
	}

	public WorldElementActor getActor() {
		return actor;
	}

	public WorldElementData getData() {
		return data;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void onDropHoverEnter(QuesterActions action) {
		Image targetImage = actor.getImage();
		if (targetImage != null) {
			if (canAcceptDrop(action)) {
				targetImage.setColor(Color.GREEN);
			} else {
				targetImage.setColor(Color.RED);
			}
		}
	}
	
	@Override
	public void onDropHoverLeave(QuesterActions action) {
		Image targetImage = actor.getImage();
		if (targetImage != null) {
			targetImage.setColor(Color.WHITE);
		}
	}
	
	@Override
	public void receiveDrop(ActionSlotControler dropped) {
		System.out.println("WorldElementControler.receiveDrop() : " + dropped.getData().action);
	}
	
	public void setActor(WorldElementActor actor) {
		this.actor = actor;
	}

	public void setData(WorldElementData data) {
		this.data = data;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
