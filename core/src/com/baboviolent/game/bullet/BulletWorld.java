package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
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
import com.badlogic.gdx.physics.bullet.dynamics.btPoint2PointConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btTypedConstraint;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PerformanceCounter;

public class BulletWorld implements Disposable {
	public static final int GRAVITY_START = 1000;
	private final ObjectMap<String, BulletInstance.Constructor> constructors = new ObjectMap<String, BulletInstance.Constructor>();
	protected final Array<BulletInstance> instances = new Array<BulletInstance>();
	private Array<btTypedConstraint> constraints = new Array<btTypedConstraint>();
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
		this(new Vector3(0, -GRAVITY_START, 0));
	}
	
	public void addConstructor (final String name, final BulletInstance.Constructor constructor) {
		constructors.put(name, constructor);
	}

	public BulletInstance.Constructor getConstructor (final String name) {
		return constructors.get(name);
	}

	public BulletInstance add (final String type, float x, float y, float z) {
		BulletInstance instance = constructors.get(type).construct();
		instance.transform.setToTranslation(x, y, z);
		add(instance);
		return instance;
	}
	
	public BulletInstance add (final BulletInstance instance) {
		instances.add(instance);
		world.addRigidBody(instance.body);
		return instance;
	}
	
	public BulletInstance add (final GameObject go) {
		instances.add(go.getInstance());
		world.addRigidBody(go.getInstance().body);
		return go.getInstance();
	}
	
	public void attachWeaponToBabo(Babo babo, GameObject weapon) {
		add(weapon);
	    // @TODO Comprendre cette partie!!
	    // Hypothese, Respectivement pour le 2 le vecteur 1 est le point sur le body 1
	    btPoint2PointConstraint constraint = new btPoint2PointConstraint(
	        babo.getInstance().body,
	        weapon.getInstance().body,
			new Vector3(0,0,0),
			new Vector3(0,0,0));
	    constraints.add(constraint);
		// Le deuxieme argument désactive les collisions entre babo et l'arme
		world.addConstraint(constraint, true);
		
		// On ajoute le weapon dans le babo
		babo.setWeapon(weapon);
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

