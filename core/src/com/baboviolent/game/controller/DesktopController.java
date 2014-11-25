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
        switch(keycode) {
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
            .nor();
        Vector3 force = new Vector3().set(0,0,0);
        
        switch(keycode) {
            case Keys.UP:
                force.add(playerDirection);
                break;
            case Keys.DOWN:
                force.add(-playerDirection.x, -playerDirection.y, -playerDirection.z);
                break;
        }
		force.scl(BaboViolentGame.BABO_SPEED);
		player.getInstance().body.applyCentralForce(force);
		
		return true;
    }
}