package com.baboviolent.game.bullet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;

public class BulletInstance extends ModelInstance implements Disposable {
	public BulletInstance.MotionState motionState;
	public btRigidBody body;
	
	/**
	 * Créateur d'un bulletinstance map'
	*/
	static public BulletInstance createMap (Model model) {
        btBvhTriangleMeshShape shape = new btBvhTriangleMeshShape(model.meshParts);
        btRigidBody.btRigidBodyConstructionInfo constructionInfo = 
            new btRigidBody.btRigidBodyConstructionInfo(0, null, shape);
            
        return new BulletInstance(model, constructionInfo);
	}
	
	public BulletInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model);
		init(constructionInfo);
	}
	
    public BulletInstance (Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, node);
		init(constructionInfo);
	}
    
    public void init(btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
    	motionState = new BulletInstance.MotionState(this.transform);
		body = new btRigidBody(constructionInfo);
		body.setMotionState(motionState);
    }

	@Override
	public void dispose () {
		if (motionState != null) motionState.dispose();
		if (body != null) body.dispose();
		motionState = null;
		body = null;
	}

	static class MotionState extends btMotionState {
		private final Matrix4 transform;

		public MotionState (final Matrix4 transform) {
			this.transform = transform;
		}

		/** For dynamic and static bodies this method is called by bullet once to get the initial state of the body. For kinematic
		 * bodies this method is called on every update, unless the body is deactivated. */
		@Override
		public void getWorldTransform (final Matrix4 worldTrans) {
			worldTrans.set(transform);
		}

		/** For dynamic bodies this method is called by bullet every update to inform about the new position and rotation. */
		@Override
		public void setWorldTransform (final Matrix4 worldTrans) {
			transform.set(worldTrans);
		}
	}
	
	static public class Constructor implements Disposable {
		public final Model model;
		public final String node;
		public final btCollisionShape shape;
		public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
		private static Vector3 localInertia = new Vector3();

		public Constructor (Model model, String node, btCollisionShape shape, float mass) {
			this.model = model;
			this.node = node;
			this.shape = shape;
			if (mass > 0f)
				shape.calculateLocalInertia(mass, localInertia);
			else
				localInertia.set(0, 0, 0);
			this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		}

		public BulletInstance construct () {
			return new BulletInstance(model, node, constructionInfo);
		}

		@Override
		public void dispose () {
			model.dispose();
			shape.dispose();
			constructionInfo.dispose();
		}
	}
}