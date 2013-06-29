package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.controlers.FogControler;
import com.slamdunk.quester.utils.Assets;

public class FogActor extends WorldElementActor {
	public FogActor() {
		super(Assets.fog);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	((FogControler)getControler()).unveil();
	        }
		});
	}
}
