package com.baboviolent.game.controller;

import com.baboviolent.game.controller.scene2d.BaboTouchpad;
import com.baboviolent.game.mode.BaseMode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Controleur pour les mobiles, c'est un touchpad
 */ 
public class MobileController extends BaseController {
	
	public static final int TARGET_DISTANCE = 6;
	
	private Stage stage;
	private Touchpad left;
	private Touchpad right;
	private Vector2 v2;
	private Vector3 direction;
	private Vector3 lastPosition;
	private Vector3 currentPosition;
	
	public MobileController(final BaseMode mode) {
		super(mode);
		direction = new Vector3();
		lastPosition = new Vector3();
		currentPosition = new Vector3();
		v2 = new Vector2();
		ScreenViewport v = new ScreenViewport();
		stage = new Stage(v);
		int width = v.getScreenWidth();
		int height = v.getScreenHeight();
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		int size = width/5;
		left = new BaboTouchpad(0, skin);
		left.setBounds(width/10, height/10, size, size);
		
		right = new BaboTouchpad(0, skin);
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
        	v2.set(left.getKnobPercentX(), left.getKnobPercentY());
        	v2.setAngle(roundAngle(v2.angle()));
        	direction.set(v2.x, 0, v2.y);
        	mode.onSetPlayerDirection(direction.nor());
        }
        else if( !direction.isZero() ) {
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
	
	/**
	 * Arrondi a 45 degre
	 * @param angle
	 * @return
	 */
	private float roundAngle(float angle) {
		if( angle > 337.5 || angle <= 22.5  ) angle = 0;
		else if( angle > 22.5 && angle <= 67.5  ) angle = 45;
		else if( angle > 67.5 && angle <= 112.5  ) angle = 90;
		else if( angle > 112.5 && angle <= 157.5  ) angle = 135;
		else if( angle > 157.5 && angle <= 202.5  ) angle = 180;
		else if( angle > 202.5 && angle <= 247.5  ) angle = 225;
		else if( angle > 247.5 && angle <= 292.5  ) angle = 270;
		else if( angle > 292.5 && angle <= 337.5  ) angle = 315;
		return angle;
	}
	
	public Stage getStage() {
		return stage;
	}
}