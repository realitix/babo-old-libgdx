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
	
	public static final int TARGET_DISTANCE = 1000;
	
	private Stage stage;
	private Touchpad left;
	private Touchpad right;
	private Vector3 direction;
	private Vector3 lastPosition;
	private Vector3 currentPosition;
	
	public MobileController(final BaseMode mode) {
		super(mode);
		direction = new Vector3();
		lastPosition = new Vector3();
		currentPosition = new Vector3();
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
        else {
        	direction.set(0, 0, 0);
        	mode.onSetPlayerDirection(direction);
        }
        
        // RIGHT
        if( right.isTouched() && (right.getKnobPercentX() != 0 || right.getKnobPercentY() != 0)) {
        	target.set(mode.getPlayer().getPosition());
        	target.add(-right.getKnobPercentX() * TARGET_DISTANCE, 0, right.getKnobPercentY() * TARGET_DISTANCE);
        	mode.onStartShoot();
        }
        else {
        	currentPosition.set(mode.getPlayer().getPosition()).sub(lastPosition);
        	target.add(currentPosition);
        	mode.onStopShoot();
        }
        
        lastPosition.set(mode.getPlayer().getPosition());
	}
	
	public Stage getStage() {
		return stage;
	}
}