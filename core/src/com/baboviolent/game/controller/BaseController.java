package com.baboviolent.game.controller;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.screen.GameScreen;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

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