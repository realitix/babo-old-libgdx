package com.baboviolent.game.gameobject.ammo;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Ammo extends GameObject {
	public Ammo() {
		super();
	}
	
	protected void init() {
	    super.initModel();
	    shape = Utils.convexHullShapeFromModel(model);
	}
	
	public BulletInstance getInstance() {
        btRigidBody body = initBody(shape);
        return new BulletInstance(model, body);
	}
}