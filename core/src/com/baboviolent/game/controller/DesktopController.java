package com.baboviolent.game.controller;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

/**
 * On utilise pas mousemoved car la target doit etre mis a jour meme quand la souris
 * ne bouge pas mais que babo bouge
 */ 
public class DesktopController extends BaseController {
	
	public DesktopController(final BaseMode mode) {
		super(mode);
    }
	
    @Override
    public boolean keyDown(int keycode) {
    	/**
    	 * La caméra est toujours pointé vers l'axe Z donc c'est facile'
    	 */ 
        Vector3 direction = mode.getPlayer().getDirection();
        if( keycode == Keys.UP )
        	direction.z = 1;
       	if( keycode == Keys.DOWN )
        	direction.z = -1;
        if( keycode == Keys.LEFT )
        	direction.x = -1;
        if( keycode == Keys.RIGHT )
        	direction.x = 1;
        
		mode.onSetPlayerDirection(direction.nor().scl(BaboViolentGame.BABO_SPEED));
		return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
    	if( keycode == Keys.ESCAPE )
    		Gdx.app.exit();
    	
    	Vector3 direction = mode.getPlayer().getDirection();
    	
    	if( keycode == Keys.UP && direction.z > 0 )
    		direction.z = 0;
    	if( keycode == Keys.DOWN && direction.z < 0 )
    		direction.z = 0;	
    	if( keycode == Keys.LEFT && direction.x < 0 )
    		direction.x = 0;
    	if( keycode == Keys.RIGHT && direction.x > 0 )
    		direction.x = 0;
    	
    	mode.onSetPlayerDirection(direction.nor().scl(BaboViolentGame.BABO_SPEED));
    	return true;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	mode.onMouseClicked(screenX, screenY);
    	return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    	mode.onMouseReleased(screenX, screenY);
    	return false;
    }
}