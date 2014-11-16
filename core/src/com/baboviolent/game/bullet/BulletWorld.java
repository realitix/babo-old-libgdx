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
	
	private final ObjectMap<String, BulletConstructor> constructors = new ObjectMap<String, BulletConstructor>();
	protected final Array<BulletEntity> entities = new Array<BulletEntity>();
	private final Array<Model> models = new Array<Model>();	

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
	
	public void addConstructor (final String name, final BulletConstructor constructor) {
		constructors.put(name, constructor);
		if (constructor.model != null && !models.contains(constructor.model, true)) models.add(constructor.model);
	}

	public BulletConstructor getConstructor (final String name) {
		return constructors.get(name);
	}

	public BulletEntity add (final String type, float x, float y, float z) {
		final BulletEntity entity = constructors.get(type).construct(x, y, z);
		add(entity);
		return entity;
	}

	public BulletEntity add (final String type, final Matrix4 transform) {
		final BulletEntity entity = constructors.get(type).construct(transform);
		add(entity);
		return entity;
	}

	public void render (final ModelBatch batch, final Environment lights) {
		render(batch, lights, entities);
	}

	public void render (final ModelBatch batch, final Environment lights, final BulletEntity entity) {
		batch.render(entity.modelInstance, lights);
	}

	public void add (final BulletEntity entity) {
		entities.add(entity);
		world.addRigidBody((btRigidBody)entity.body);
		entity.body.setUserValue(entities.size - 1);
	}

	public void update () {
		world.stepSimulation(Gdx.graphics.getDeltaTime(), maxSubSteps, fixedTimeStep);
	}

	public void render (ModelBatch batch, Environment lights, Iterable<BulletEntity> entities) {
		for (final BulletEntity e : entities) {
			batch.render(e.modelInstance, lights);
		}
	}

	@Override
	public void dispose () {
		for (int i = 0; i < entities.size; i++) {
			btCollisionObject body = entities.get(i).body;
			if (body != null) {
					world.removeRigidBody((btRigidBody)body);
			}
		}

		for (int i = 0; i < entities.size; i++)
			entities.get(i).dispose();
		entities.clear();

		for (BulletConstructor constructor : constructors.values())
			constructor.dispose();
		constructors.clear();

		models.clear();

		world.dispose();
		if (solver != null) solver.dispose();
		if (broadphase != null) broadphase.dispose();
		if (dispatcher != null) dispatcher.dispose();
		if (collisionConfiguration != null) collisionConfiguration.dispose();
	}
}

