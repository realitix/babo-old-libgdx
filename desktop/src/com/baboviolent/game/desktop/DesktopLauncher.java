package com.baboviolent.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.baboviolent.game.BaboViolentGame;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class DesktopLauncher {
	public static void main (String[] arg) {
		
		
		//TexturePacker.process("D:\\Users\\T0143947\\repo\\babo\\android\\assets\\data\\texture\\ground\\a_packer", "D:\\Users\\T0143947\\repo\\babo\\android\\assets\\data\\texture\\ground\\a_packer\\atlas", "ground");
		//TexturePacker.process("/home/realitix/git/baboviolent/design/images/joysticks/a_packer", "/home/realitix/git/baboviolent/design/images/joysticks/a_packer/atlas", "joysticks");
		
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
