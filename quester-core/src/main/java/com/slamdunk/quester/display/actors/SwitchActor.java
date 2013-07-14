package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.controlers.SwitchControler;

public class SwitchActor extends WorldElementActor {

	public SwitchActor(TextureRegion texture) {
		super(texture);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
	        }
	        
	        @Override
	        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	        	SwitchControler controler = (SwitchControler)getControler();
	        	controler.setSwitchElement();
	        }
		});
	}

}
