package com.baboviolent.game.ai;

import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.particle.PoolParticle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

public class BaboAi extends Babo implements Steerable<Vector3> {

	private SteeringBehavior<Vector3> steeringBehavior;
	private static final SteeringAcceleration<Vector3> steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());
	
	public BaboAi(String username, String skin, final ObjectMap<String, PoolParticle> particules, final BulletWorld world) {
		super(username, skin, particules, world);
	}
	
	public SteeringBehavior<Vector3> getSteeringBehavior () {
		return steeringBehavior;
	}
	
	@Override
	public void update() {
		super.update();
		updateAi();
	}
	
	private void updateAi () {
		if (steeringBehavior != null) {
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);

			/*
			 * Here you might want to add a motor control layer filtering steering accelerations.
			 * 
			 * For instance, a car in a driving game has physical constraints on its movement: it cannot turn while stationary; the
			 * faster it moves, the slower it can turn (without going into a skid); it can brake much more quickly than it can
			 * accelerate; and it only moves in the direction it is facing (ignoring power slides).
			 */
			
			// Apply steering acceleration
			applySteering(steeringOutput);
		}
	}
	
	private void applySteering (SteeringAcceleration<Vector3> steering) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		boolean anyAccelerations = false;
		
		// Update position and linear velocity
		if (!steeringOutput.linear.isZero()) {
			this.setDirection(steeringOutput.linear);
			//body.applyCentralForce(steeringOutput.linear.scl(deltaTime));
			//anyAccelerations = true;
		}

		// Update orientation and angular velocity
		if (steeringOutput.angular != 0) {
			tmpV2.set(1, 0);
			tmpV2.rotate(steeringOutput.angular);
			this.setTarget(tmpV3.set(tmpV2.x, 0, tmpV2.y));
			//body.applyTorque(tmpVector3.set(0, steeringOutput.angular * deltaTime, 0));
		}
	}

	public BaboAi setSteeringBehavior (SteeringBehavior<Vector3> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
		return this;
	}

	@Override
	public float getMaxLinearSpeed() {
		return 10;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		
	}

	@Override
	public float getMaxLinearAcceleration() {
		return 10;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
	}

	@Override
	public float getMaxAngularSpeed() {
		return 10;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
	}

	@Override
	public float getMaxAngularAcceleration() {
		return 10;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
	}

	@Override
	public float getOrientation() {
		tmpV3.set(target).sub(this.getPosition());
		return tmpV2.set(tmpV3.x, tmpV3.z).angle();
	}

	@Override
	public Vector3 getLinearVelocity() {
		return this.body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return this.body.getAngularVelocity().y;
	}

	@Override
	public float getBoundingRadius() {
		return 1;
	}

	@Override
	public boolean isTagged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTagged(boolean tagged) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3 newVector() {
		return new Vector3();
	}

	@Override
	public float vectorToAngle(Vector3 vector) {
		return (float)Math.atan2(-vector.z, vector.x);
	}

	@Override
	public Vector3 angleToVector(Vector3 outVector, float angle) {
		outVector.z = -(float)Math.sin(angle);
		outVector.y = 0;
		outVector.x = (float)Math.cos(angle);
		return outVector;
	}
}
