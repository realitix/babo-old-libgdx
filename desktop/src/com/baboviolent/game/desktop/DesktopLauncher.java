package com.baboviolent.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.baboviolent.game.BaboViolentGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "BaboViolent";
		config.width = 1000;
		config.height = 600;
		//config.fullscreen = true;
		
		new LwjglApplication(new BaboViolentGame(), config);
	}
}
