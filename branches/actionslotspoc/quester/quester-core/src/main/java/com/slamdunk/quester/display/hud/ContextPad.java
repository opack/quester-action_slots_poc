package com.slamdunk.quester.display.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class ContextPad extends Table {
	private static Button createButton(TextureRegion texture, ClickListener listener) {
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(texture);
		style.down = new TextureRegionDrawable(texture);
		style.pressedOffsetY = 1f;
		Button button = new Button(style);
		button.addListener(listener);
		return button;
	}
	
	public ContextPad(int buttonSize) {
		// Cr�ation des boutons
//DBG		Button centerCamera = createButton(Assets.center, new ClickListener(){
//			@Override
//			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
//				GameControler.instance.getPlayer().getActor().centerCameraOnSelf();
//			};
//		});
		Button stopMoveButton = createButton(Assets.cross, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getPlayer().stopMove();
			};
		});
		
		// Ajout � la table
		//DBGadd(centerCamera).size(buttonSize, buttonSize);
		add(stopMoveButton).size(buttonSize, buttonSize);		
		pack();
	}
}
