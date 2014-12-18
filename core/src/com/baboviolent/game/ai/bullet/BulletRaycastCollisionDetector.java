package com.baboviolent.game.ai.bullet;

import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;

/** A 3D {@link RaycastCollisionDetector} to be used with bullet physics. It reports the closest collision which is not the
 * supplied "me" collision object.
 */
public class BulletRaycastCollisionDetector implements RaycastCollisionDetector<Vector3> {

	btCollisionWorld world;
	ClosestRayResultCallback callback;

	public BulletRaycastCollisionDetector (btCollisionWorld world, btCollisionObject me) {
		this.world = world;
		this.callback = new ClosestNotMeRayResultCallback(me);
	}

	@Override
	public boolean collides (Ray<Vector3> ray) {
		return findCollision(null, ray);
	}

	@Override
	public boolean findCollision (Collision<Vector3> outputCollision, Ray<Vector3> inputRay) {
		// reset because we reuse the callback
		/*callback.setCollisionObject(null);
		
		world.rayTest(inputRay.start, inputRay.end, callback);
		
		if (outputCollision != null && callback.hasHit() ) {
			callback.getHitPointWorld(outputCollision.point);
			callback.getHitNormalWorld(outputCollision.normal);
			System.out.println("hit : " + callback.hasHit() + " start="+inputRay.start+" end="+inputRay.end);
			System.out.println("point : " + outputCollision.point + " normal="+outputCollision.normal);
		}
		
		return callback.hasHit();*/
		
		ClosestRayResultCallback callback = new ClosestRayResultCallback(inputRay.start, inputRay.end);
		world.rayTest(inputRay.start, inputRay.end, callback);
		if(callback.hasHit() && outputCollision != null) {
			callback.getHitPointWorld(outputCollision.point);
			callback.getHitNormalWorld(outputCollision.normal);
			System.out.println("hit : " + callback.hasHit() + " start="+inputRay.start+" end="+inputRay.end);
			System.out.println("point : " + outputCollision.point + " normal="+outputCollision.normal);
		}
		
		return callback.hasHit();
	}
}
