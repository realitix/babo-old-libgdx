package com.baboviolent.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.baboviolent.game.BaboViolentGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "BaboViolent";
		config.width = 800;
		config.height = 600;
		/*config.width = 1920;
		config.height = 1080;
		config.fullscreen = true;*/
		
		new LwjglApplication(new BaboViolentGame(), config);
	}
}
