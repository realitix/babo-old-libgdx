package com.baboviolent.game.camera;

import com.baboviolent.game.gameobject.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * A Camera that tends to chase an object in a natural way
 * 
 * Basically it consists of three parts:
 * 
 * - The object: a world transformation matrix (translation and rotation) of the object to follow, you could use your
 * render matrix for this.
 * 
 * - The target position: the location and offset (explained below) to look at relative to the object to follow.
 * so if you want to look slight above or ahead the object just set the target,
 * to look directly at the object leave them zero.
 * 
 * - The desired position: the desired location and offset (explained below) of the camera relative to the object to follow.
 * so if you want your camera always behind or above your object just the desired location and offset.
 * 
 * For the target position and desired position there's a location and offset. Both are relative to the object.
 * The difference is that the location is rotated with the object, while the offset is not. So i.e. to keep the cam
 * always above the object even if the object is up side down, you set the offset. To keep the cam always facing the back
 * of the object, you set the location.
 * 
 * There are three operating modes. 
 * - The first being a tail camera, set the rotationSpeed to a negative value for this.
 * In this mode the cam is always in line of the desired position and the object. As if it was attached fixed at the object.
 * 
 * - The second mode is the follow camera, set the rotationSpeed to zero for this. In this mode the cam will not move to
 * the desired location, it just maintains distance. As if it was attached with a rope to the object.
 * 
 * - The third mode is the interpolated of these two, set the rotationSpeed to a positive value for this this.
 * In this mode the cam will follow to maintain distance and rotate to get to the desired position.
 * 
 * TODO: The acceleration (and speed) are to be futher implemented to give a more smooth follow of the object. 
 * @author Xoppa
 */
public class ChaseCamera2 extends PerspectiveCamera {
	/** enable or disable chasing */
	public boolean chasing = true;
	/** The object (zero based transformation) to follow */
	public Matrix4 transform;
	/** The location of target to look at relative to the object (i.e. use to always look slight in front of the object when rotated) */
	public final Vector3 targetLocation = new Vector3();
	/** The offset to location for the target to look at (i.e. use to always look slight above the object, regardless rotation) */ 
	public final Vector3 targetOffset = new Vector3();
	/** The desired location relative to the object (i.e. use this to always place the cam behind the object when rotated) */ 
	public final Vector3 desiredLocation = new Vector3();
	/** The offset for the camera to the location (i.e. use this to always place the cam above the object regardless rotation) */ 
	public final Vector3 desiredOffset = new Vector3(0, 1000, -300);
	/** The absolute bounds (world coordinates) the camera cannot get out of */
	public final BoundingBox bounds = new BoundingBox();
	/** The bounds for the camera to respect relative to the location, regardless rotation */
	public final BoundingBox offsetBounds = new BoundingBox();
	/** The maximum acceleration (units per square second) the camera can move */
	public float acceleration = 0.01f;
	/** The maximum speed (in degrees per second) to rotate around the object.
	 * 0 = no rotation, just chase (as if the cam is connected to the object with a rope)
	 * <1 = maximum rotation, don't interpolate (as if the cam is fixed to the object)
	 * */
	public float rotationSpeed = 0;
	/** The squared minimum distance required to rotate around the object (must be greater than zero)  */
	public float rotationOffsetSq = 0.1f * 0.1f;
	/** Read this to get the current speed and direction (units per second) */
	public final Vector3 speed = new Vector3();
	/** Read this to get the current absolute speed (units per second) */
	public float absoluteSpeed = 0;
	
	public ChaseCamera2() {
		super();
	}

	public ChaseCamera2(float fieldOfView, float viewportWidth, float viewportHeight) {
		super(fieldOfView, viewportWidth, viewportHeight);
		far = 10000;
		near = 10;
	}
	
	public void chase(GameObject go) {
	    transform = go.getInstance().transform;
	    Vector3 goPos = new Vector3();
	    transform.getTranslation(goPos);
	    position.set(goPos.x, goPos.y + desiredOffset.y, goPos.z + desiredOffset.z);
	    //System.out.println("Position de la caméra: "+position.toString());
	    //lookAt(new Vector3(position.x, 0, position.z));
	    //position.set(-100, 400, -100);
	}
	
	private final static Vector3 current = new Vector3();
	private final static Vector3 desired = new Vector3();
	private final static Vector3 target = new Vector3();
	private final static Vector3 rotationAxis = new Vector3();
	private final static Matrix4 rotationMatrix = new Matrix4();
	private Vector3 tmp = new Vector3();

	public void update(final float delta, final boolean updateFrustum) {
		if (chasing && transform != null) {
			Vector3 goPos = new Vector3();
		    transform.getTranslation(goPos);
		    lookAt(goPos);
		    position.set(goPos.x, goPos.y + desiredOffset.y, goPos.z + desiredOffset.z);
		}
		super.update(updateFrustum);		
	}

	@Override
	public void update() {
		update(true);
	}
	
	@Override
	public void update(final boolean updateFrustum) {
		update(Gdx.graphics.getDeltaTime(), updateFrustum);
	}
	
	public void update(final float delta) {
		update(delta, true);
	}
}

