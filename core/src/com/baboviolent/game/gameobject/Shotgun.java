package com.baboviolent.game.gameobject;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.loader.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Shotgun extends GameObject {
	
	public Shotgun() {
		super()
	    initPhysics();
        super.initInstance();
	}
	
	private void initPhysics() {
	    friction = 0;
        rollingFriction = 0;
        linearDamping = 0;
        angularDamping = 0;
        restitution = 0;
        mass = 1;
	}
}