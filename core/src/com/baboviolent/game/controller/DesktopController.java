package com.baboviolent.game.controller;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

public class DesktopController extends BaseController {
	
	public DesktopController(final GameScreen screen, Babo player) {
		super(screen, player);
    }
	
    @Override
    public boolean keyDown(int keycode) {
    	/**
    	 * La caméra est toujours pointé vers l'axe Z donc c'est facile'
    	 */ 
        Vector3 direction = player.getDirection();
        if( keycode == Keys.UP )
        	direction.z = 1;
       	if( keycode == Keys.DOWN )
        	direction.z = -1;
        if( keycode == Keys.LEFT )
        	direction.x = -1;
        if( keycode == Keys.RIGHT )
        	direction.x = 1;
        
		player.setDirection(direction.nor().scl(BaboViolentGame.BABO_SPEED));
		return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
    	if( keycode == Keys.ESCAPE )
    		Gdx.app.exit();
    	
    	Vector3 direction = player.getDirection();
    	
    	if( keycode == Keys.UP && direction.z > 0 )
    		direction.z = 0;
    	if( keycode == Keys.DOWN && direction.z < 0 )
    		direction.z = 0;	
    	if( keycode == Keys.LEFT && direction.x < 0 )
    		direction.x = 0;
    	if( keycode == Keys.RIGHT && direction.x > 0 )
    		direction.x = 0;
    	
    	player.setDirection(direction.nor().scl(BaboViolentGame.BABO_SPEED));
    	return true;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
    	screen.mouseMoved(screenX, screenY);
    	return false;
    }
}