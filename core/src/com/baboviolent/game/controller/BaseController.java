package com.baboviolent.game.controller;

public class BaseController extends InputAdapter {
    protected Player player;
    protected btKinematicCharacterController bulletController;
    protected btPairCachingGhostObject ghostObject;
    
    public BaseController(Player player) {
        this.player = player;
        
        // Init bullet controller
        ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(player.getInstance().transform);
		ghostShape = new btCapsuleShape(2f, 2f);
		ghostObject.setCollisionShape(player.getInstance().body.getCollisionShape());
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		bulletController = new btKinematicCharacterController(ghostObject, ghostShape, BaboViolentGame.SIZE_CELL);
    }
    
    public update() {
        ghostObject.getWorldTransform(player.getInstance().transform);
    }
}