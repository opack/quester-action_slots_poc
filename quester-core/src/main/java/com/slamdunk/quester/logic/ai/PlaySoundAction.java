package com.slamdunk.quester.logic.ai;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.utils.Assets;

public class PlaySoundAction extends AbstractAIAction {
	private Sound sound;
	
	public PlaySoundAction(Sound sound) {
		this.sound = sound;
	}

	@Override
	public void act() {
		Assets.playSound(sound);
		ai.nextAction();
	}

	@Override
	public QuesterActions getAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
