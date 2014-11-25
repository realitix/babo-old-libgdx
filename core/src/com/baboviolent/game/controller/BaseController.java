package com.baboviolent.game.controller;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

public class BaseController extends InputAdapter {
    protected Babo player;
    protected btKinematicCharacterController bulletController;
    protected btPairCachingGhostObject ghostObject;
    
    public BaseController(Babo player) {
        this.player = player;
        
        // Init bullet controller
        ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(player.getInstance().transform);
		btSphereShape ghostShape = new btSphereShape(BaboViolentGame.BABO_DIAMETER/2);
		ghostObject.setCollisionShape(player.getInstance().body.getCollisionShape());
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		bulletController = new btKinematicCharacterController(ghostObject, ghostShape, BaboViolentGame.SIZE_MAP_CELL);
    }
    
    public void update() {
        ghostObject.getWorldTransform(player.getInstance().transform);
    }
}