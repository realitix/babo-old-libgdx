package com.baboviolent.game.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PerformanceCounter;

public class BulletWorld extends BulletEntity implements Disposable {
	
	private final ObjectMap<String, BulletInstance.Constructor> constructors = new ObjectMap<String, BulletInstance.Constructor>();
	protected final Array<BulletInstance> instances = new Array<BulletInstance>();
	public final btCollisionConfiguration collisionConfiguration;
	public final btCollisionDispatcher dispatcher;
	public final btBroadphaseInterface broadphase;
	public final btConstraintSolver solver;
	public final btDiscreteDynamicsWorld world;
	public final Vector3 gravity;
	public int maxSubSteps = 5;
	public float fixedTimeStep = 1f / 60f;

	public BulletWorld (final Vector3 gravity) {
		collisionConfiguration = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfiguration);
		broadphase = new btDbvtBroadphase();
		solver = new btSequentialImpulseConstraintSolver();
		world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		world.setGravity(gravity);
		this.gravity = gravity;
	}

	public BulletWorld () {
		this(new Vector3(0, -10, 0));
	}
	
	public void addConstructor (final String name, final BulletInstance.Constructor constructor) {
		constructors.put(name, constructor);
	}

	public BulletConstructor getConstructor (final String name) {
		return constructors.get(name);
	}

	public BulletEntity add (final String type, float x, float y, float z) {
		final BulletInstance instance = constructors.get(type).construct().setToTranslation(x, y, z);
		add(instance);
		return instance;
	}
	
	public void add (final BulletInstance instance) {
		instances.add(instance);
		world.addRigidBody(instance.body);
	}

	public void render (final ModelBatch modelBatch, final Environment environment) {
		modelBatch.render(instances, environment);
	}

	public void update () {
		world.stepSimulation(Gdx.graphics.getDeltaTime(), maxSubSteps, fixedTimeStep);
	}

	@Override
	public void dispose () {
		for (int i = 0; i < instances.size; i++) {
			btRigidBody body = instances.get(i).body;
			if (body != null) {
					world.removeRigidBody(body);
			}
		}

		for (int i = 0; i < instances.size; i++)
			instances.get(i).dispose();
		instances.clear();

		for (BulletInstance.Constructor constructor : constructors.values())
			constructor.dispose();
		constructors.clear();

		world.dispose();
		if (solver != null) solver.dispose();
		if (broadphase != null) broadphase.dispose();
		if (dispatcher != null) dispatcher.dispose();
		if (collisionConfiguration != null) collisionConfiguration.dispose();
	}
}

