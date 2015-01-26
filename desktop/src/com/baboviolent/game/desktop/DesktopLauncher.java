package com.baboviolent.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.baboviolent.game.BaboViolentGame;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class DesktopLauncher {
	public static void main (String[] arg) {
		
		
		//TexturePacker.process("D:\\Users\\T0143947\\repo\\babo\\android\\assets\\data\\texture\\other\\blood", "D:\\Users\\T0143947\\repo\\babo\\android\\assets\\data\\texture\\other\\blood\\atlas", "blood");
		
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
