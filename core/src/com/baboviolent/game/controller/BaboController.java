package com.baboviolent.game.controller;

import com.baboviolent.game.mode.BaseMode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class BaboController {
	private BaseController controller;
	private final Camera camera;
	
	public BaboController(final BaseMode mode, final Camera camera) {
		this.camera = camera;
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
				//Gdx.input.setCursorCatched(true);
				break;
			default:
		}
	}
	
	public void update() {
		controller.update();
	}
	
	public void render() {
		controller.render();
	}
	
	public Vector3 getTarget() {
		return controller.getTarget(camera);
	}
}
