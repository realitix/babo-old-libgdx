package com.baboviolent.game.gameobject.weapon;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.Ammo;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Weapon extends GameObject {
	protected final BulletWorld world;
	protected Ammo ammo;
	protected float impulse; // Puissance de la balle
	protected float frequency; // Temps en millisecond entre deux tirs
	protected long lastShoot;
	
	public Weapon(final BulletWorld world) {
		super();
		this.world = world;
	}
	
	public void shoot(Vector3 target) {
	}
}