package com.baboviolent.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.baboviolent.game.BaboViolentGame;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class DesktopLauncher {
	public static void main (String[] arg) {
		
		
		//TexturePacker.process("D:\\Users\\T0143947\\repo\\babo\\design\\textures\\game\\to_pack", "D:\\Users\\T0143947\\repo\\babo\\design\\textures\\game\\to_pack\\atlas", "game");
		//TexturePacker.process("/home/realitix/git/baboviolent/design/textures/game/to_pack", "/home/realitix/git/baboviolent/design/textures/game/to_pack/atlas", "game");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "BaboViolent";
		config.width = 800;
		config.height = 600;
		config.useGL30 = false;
		/*config.width = 1920;
		config.height = 1080;
		config.fullscreen = true;*/
		
		new LwjglApplication(new BaboViolentGame(), config);
	}
}
