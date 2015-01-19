package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.weapon.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
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
import com.badlogic.gdx.utils.TimeUtils;

public class BulletWorld implements Disposable {
	public static final int GRAVITY_START = 1000;
	
	protected final Array<BulletInstance> instances = new Array<BulletInstance>();
	protected final Array<BulletInstance> instancesToExpire = new Array<BulletInstance>();
	private final ObjectMap<String, btTypedConstraint> constraints = new ObjectMap<String, btTypedConstraint>();
	public final btCollisionConfiguration collisionConfiguration;
	public final btCollisionDispatcher dispatcher;
	public final btBroadphaseInterface broadphase;
	public final btConstraintSolver solver;
	public final btDiscreteDynamicsWorld world;
	public final Vector3 gravity;
	public int maxSubSteps = 5;
	public float fixedTimeStep = 1f / 60f;
	private Camera camera = null;
	private ClosestRayResultCallback rayTestCallback;
	private Vector3 tmpV3 = new Vector3();
	private Vector3 tmpV32 = new Vector3();

	public BulletWorld (final Vector3 gravity) {
		collisionConfiguration = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfiguration);
		broadphase = new btDbvtBroadphase();
		solver = new btSequentialImpulseConstraintSolver();
		world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		world.setGravity(gravity);
		this.gravity = gravity;
		rayTestCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
	}

	public BulletWorld () {
		this(new Vector3(0, -GRAVITY_START, 0));
	}
	
	public BulletRayResult getRayResult(Vector3 from, Vector3 to) {
		rayTestCallback.setCollisionObject(null);
		rayTestCallback.setClosestHitFraction(1f);
		rayTestCallback.setRayFromWorld(from);
		rayTestCallback.setRayToWorld(to);
		
		world.rayTest(from, to, rayTestCallback);
		if (rayTestCallback.hasHit()) {
			rayTestCallback.getHitPointWorld(tmpV3);
			rayTestCallback.getHitNormalWorld(tmpV32);
			
			boolean map = false;
			GameObject go = null;
			btRigidBody body = (btRigidBody) (rayTestCallback.getCollisionObject());
			
			if( body != null ) {
				go = BulletCollector.get(body.getUserValue());
				if( go == null ) map = true;
			}
			
			return new BulletRayResult()
				.setStartRay(from)
				.setEndRay(tmpV3)
				.setNormalRay(tmpV32)
				.setMap(map)
				.setObject(go);
		}
		
		return null;
	}
	
	public BulletInstance add (final BulletInstance instance) {
		if( camera != null ) {
			instance.setCamera(camera);
		}
		
		instances.add(instance);
		world.addRigidBody(instance.body);
		
		if( instance.getExpire() > TimeUtils.millis() ) {
			instancesToExpire.add(instance);
		}
		
		return instance;
	}
	
	public BulletInstance add (final GameObject go) {
		return add(go.getInstance());
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public void attachWeaponToBabo(Babo babo, Weapon weapon) {
		weapon.getInstance().body.setCenterOfMassTransform(new Matrix4());
		weapon.getInstance().body.setAngularFactor(new Vector3(0,1,0));
		weapon.getInstance().body.clearForces();
	    weapon.translate(babo.getInstance().body.getCenterOfMassPosition());
		
	    // Respectivement pour le 2 le vecteur 1 est le point sur le body 1
	    btPoint2PointConstraint constraint = new btPoint2PointConstraint(
	        babo.getInstance().body,
	        weapon.getInstance().body,
			new Vector3(0,0,0),
			new Vector3(0,0,0));
	    
	    constraints.put(Integer.toString(weapon.getId()), constraint);
	    
		// Le deuxieme argument desactive les collisions entre babo et l'arme
		world.addConstraint(constraint, true);
	}
	
	public void detachWeaponToBabo(Babo babo, Weapon weapon) {
		String id = Integer.toString(weapon.getId());
		btTypedConstraint constraintToRemove = constraints.get(id, null);

		if( constraintToRemove != null ) {
			weapon.getInstance().body.setAngularFactor(new Vector3(1,1,1));
			world.removeConstraint(constraintToRemove);
			constraints.remove(id);
			constraintToRemove.dispose();
		}
	}
	
	/*
	 * Supprime une instance
	*/
	public void remove (BulletInstance instance) {
		world.removeRigidBody(instance.body);
		instance.dispose();
		instances.removeValue(instance, true);
	}

	public void render (final ModelBatch modelBatch, final Environment environment) {
		/*for( int i = 0; i < instances.size; i++) {
			if( instances.get(i).userData != null && instances.get(i).userData.equals("map") ) {
				
			}
			else {
				modelBatch.render(instances.get(i), environment);
			}
		}*/
		modelBatch.render(instances, environment);
	}

	public void update () {
		for( int i = 0; i < instancesToExpire.size; i++) {
			if( instancesToExpire.get(i).getExpire() < TimeUtils.millis() ) {
				remove(instancesToExpire.get(i));
				instancesToExpire.removeIndex(i);
			}
		}
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

		world.dispose();
		if (solver != null) solver.dispose();
		if (broadphase != null) broadphase.dispose();
		if (dispatcher != null) dispatcher.dispose();
		if (collisionConfiguration != null) collisionConfiguration.dispose();
	}
}

