package com.baboviolent.game.controller;

import com.baboviolent.game.mode.BaseMode;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class BaseController {
    protected final BaseMode mode;
    protected Vector3 target;
    
    public BaseController(final BaseMode mode) {
        this.mode = mode;
        target = new Vector3();
    }
    
    public void update() {
    }
    
	public Vector3 getTarget(Camera camera) {
		return target;
	}
}