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
    
    public BaseController(Babo player) {
        this.player = player;
    }
}