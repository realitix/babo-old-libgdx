package com.baboviolent.game.controller;

import com.baboviolent.game.mode.BaseMode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class BaboController {
	private BaseController controller;
	
	public BaboController(final BaseMode mode) {
		switch(Gdx.app.getType()) {
			case Android:
			case iOS:
				controller = new MobileController(mode);
				Gdx.input.setInputProcessor(((MobileController)controller).getStage());
				break;
			case Desktop:
			case WebGL:
				controller = new DesktopController(mode);
				Gdx.input.setInputProcessor((DesktopController)controller);
				break;
			default:
		}
	}
	
	public void update() {
		controller.update();
	}
	
	public Vector3 getTarget(Camera camera) {
		return controller.getTarget(camera);
	}
}