package com.slamdunk.quester.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.slamdunk.quester2.Quester2;

public class QuesterDesktop2 {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Quester";
		config.useGL20 = true;
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new Quester2(), config);
	}
}
