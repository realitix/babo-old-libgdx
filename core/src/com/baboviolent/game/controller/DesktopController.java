package com.baboviolent.game.controller;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

public class DesktopController extends BaseController {
	
	public DesktopController(Babo player) {
		super(player);
    }
	
    @Override
    public boolean keyDown(int keycode) {
    	/**
    	 * La caméra est toujours pointé vers l'axe Z donc c'est facile'
    	 */ 
        /*switch(keycode) {
            case Keys.LEFT:
                player.getInstance().transform.rotate(0, 1, 0, 5f);
			    break;
			case Keys.RIGHT:
                player.getInstance().transform.rotate(0, 1, 0, -5f);
			    break;
        }
        
        Vector3 playerDirection = new Vector3()
            .set(-1,0,0)
            .rot(player.getInstance().transform)
            .nor();*/
            
        Vector3 force = player.getForce();
        if( keycode == Keys.UP )
        	force.z = 1;
       	if( keycode == Keys.DOWN )
        	force.z = -1;
        if( keycode == Keys.LEFT )
        	force.x = -1;
        if( keycode == Keys.RIGHT )
        	force.x = 1;

		player.setForce(force.nor().scl(BaboViolentGame.BABO_SPEED));
		return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
    	Vector3 force = player.getForce();
    	
    	if( keycode == Keys.UP || keycode == Keys.DOWN )
    		force.z = 0;
    	if( keycode == Keys.LEFT || keycode == Keys.RIGHT )
    		force.x = 0;
    	
    	player.setForce(force.nor().scl(BaboViolentGame.BABO_SPEED));
    	return true;
    }
}