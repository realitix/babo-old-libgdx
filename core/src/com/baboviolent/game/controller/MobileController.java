package com.baboviolent.game.controller;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Controleur pour les mobiles, c'est un touchpad
 */ 
public class MobileController extends BaseController {
	
	private Stage stage;
	private Touchpad left;
	private Touchpad right;
	private Vector3 direction;
	
	public MobileController(final BaseMode mode) {
		super(mode);
		direction = new Vector3();
		ScreenViewport v = new ScreenViewport();
		stage = new Stage(v);
		int width = v.getScreenWidth();
		int height = v.getScreenHeight();
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		int size = width/5;
		left = new Touchpad(size/5, skin);
		left.setBounds(width/10, height/10, size, size);
		
		right = new Touchpad(size/5, skin);
		right.setBounds(width - width/10 - size, height/10, size, size);
		
		stage.addActor(left);
		stage.addActor(right);
    }
	
	@Override
	public void update() {
		stage.act(Gdx.graphics.getDeltaTime());        
        stage.draw();
        
        // LEFT
        if( left.isTouched() && (left.getKnobPercentX() != 0 || left.getKnobPercentY() != 0)) {
        	direction.set(left.getKnobPercentX(), 0, left.getKnobPercentY());
        	mode.onSetPlayerDirection(direction.nor().scl(BaboViolentGame.BABO_SPEED));
        }
        
        // RIGHT
        if( right.isTouched() && (right.getKnobPercentX() != 0 || right.getKnobPercentY() != 0)) {
        	
        }
	}
	
	public Stage getStage() {
		return stage;
	}
}