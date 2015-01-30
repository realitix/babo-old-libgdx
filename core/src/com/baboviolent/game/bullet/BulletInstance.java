package com.baboviolent.game.bullet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class BulletInstance extends ModelInstance implements Disposable {
	public static final int DISTANCE_FRUSTRUM = 5000000;
	public BulletInstance.MotionState motionState;
	public btRigidBody body;
	private long expire; // Moment de l'expiration en milliseconde
	private Camera camera = null;
	private Vector3 tmp = new Vector3();
	private float radius;
	private Vector3 center;
	
	public BulletInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model);
		init(constructionInfo);
	}
	
	public BulletInstance (Model model, btRigidBody body) {
		super(model);
		motionState = new BulletInstance.MotionState(this.transform);
		this.body = body;
		this.body.setMotionState(motionState);
		init();
	}
	
    public BulletInstance (Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, node);
		init(constructionInfo);
	}
    
    public void init(btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
    	motionState = new BulletInstance.MotionState(this.transform);
		body = new btRigidBody(constructionInfo);
		body.setMotionState(motionState);
		constructionInfo.dispose();
		init();
    }
    
    public void init() {
    	BoundingBox bounds = new BoundingBox();
    	center = new Vector3();
    	Vector3 dimensions = new Vector3();
    	calculateBoundingBox(bounds);
    	bounds.getCenter(center);
    	bounds.getDimensions(dimensions);
    	radius = dimensions.len() / 2f;
    }
    
    public BulletInstance setCamera(Camera camera) {
    	this.camera = camera;
    	return this;
    }
    
    public BulletInstance setRadius(float radius) {
    	this.radius = radius;
    	return this;
    }
    
    public BulletInstance setExpire(long e) {
        expire = e;
        return this;
    }
    
    public long getExpire() {
        return expire;
    }
    
    /**
     * On surcharge afin de n'afficher que les objets visibles a la camera (optimisation)
     */
    @Override
    protected void getRenderables (Node node, Array<Renderable> renderables, Pool<Renderable> pool) {
    	if( camera == null ) {
    		super.getRenderables(node, renderables, pool);
    	}
    	else {
    		// Si c'est la map, on calcul la distance a chaque node
    		if( this.userData != null && this.userData.equals("map") ) tmp.set(node.translation);
    		else this.transform.getTranslation(tmp).add(center);
    		
    		if (camera.frustum.sphereInFrustum(tmp, radius) && node.parts.size > 0) {
        		for (NodePart nodePart : node.parts) {
        			if (nodePart.enabled) renderables.add(getRenderable(pool.obtain(), node, nodePart));
        		}
        	}
        	
        	for (Node child : node.children) {
        		getRenderables(child, renderables, pool);
        	}
    	}
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
}