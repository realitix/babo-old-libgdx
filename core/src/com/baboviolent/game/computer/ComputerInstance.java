package com.baboviolent.game.computer;


public class ComputerInstance extends BulletInstance implements Steerable<Vector3> {
	private float maxLinearSpeed;
	private float maxLinearAcceleration;
	private float maxAngularSpeed;
	private float maxAngularAcceleration;
	private float boundingRadius;
	private boolean tagged;
	private boolean independentFacing = true;
	private SteeringBehavior<Vector3> steeringBehavior;
	private static final SteeringAcceleration<Vector3> steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());

	private static final Quaternion tmpQuaternion = new Quaternion();
	private static final Matrix4 tmpMatrix4 = new Matrix4();
	private static final Vector3 tmpVector3 = new Vector3();
	private static final Vector3 ANGULAR_LOCK = new Vector3(0, 1, 0);

	
	public ComputerInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, constructionInfo);
	}
	
	public ComputerInstance (Model model, btRigidBody body) {
		super(model, body);
	}
	
	private void initSteeringBehavior() {
		final LookWhereYouAreGoing<Vector3> lookWhereYouAreGoingSB = new LookWhereYouAreGoing<Vector3>(character) //
			.setAlignTolerance(.005f) //
			.setDecelerationRadius(MathUtils.PI2 * 3f / 4f) //
			.setTimeToTarget(.02f);
			
		Arrive<Vector3> arriveSB = new Arrive<Vector3>(character, target) //
			.setTimeToTarget(0.01f) //
			.setArrivalTolerance(0.0002f) //
			.setDecelerationRadius(3);
		
		BlendedSteering<Vector3> blendedSteering = new BlendedSteering<Vector3>(character) //
			.setLimiter(NullLimiter.NEUTRAL_LIMITER) //
			.add(arriveSB, 1f) //
			.add(lookWhereYouAreGoingSB, 1f);
		
		this.steeringBehavior = blendedSteering;
	}

	public SteeringBehavior<Vector3> getSteeringBehavior () {
		return steeringBehavior;
	}

	public void setSteeringBehavior (SteeringBehavior<Vector3> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
	}

	public void update (float deltaTime) {
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
			applySteering(steeringOutput, deltaTime);
		}
	}

	private void applySteering (SteeringAcceleration<Vector3> steering, float deltaTime) {
		boolean anyAccelerations = false;

		// Update position and linear velocity
		if (!steeringOutput.linear.isZero()) {
			body.applyCentralForce(steeringOutput.linear.scl(deltaTime));
			anyAccelerations = true;
		}

		// Update orientation and angular velocity
		if (isIndependentFacing()) {
			if (steeringOutput.angular != 0) {
				body.applyTorque(tmpVector3.set(0, steeringOutput.angular * deltaTime, 0));
				anyAccelerations = true;
			}
		}
		else {
			// If we haven't got any velocity, then we can do nothing.
			Vector3 linVel = getLinearVelocity();
			if (!linVel.isZero(MathUtils.FLOAT_ROUNDING_ERROR)) {
				// 
				// TODO: Commented out!!!
				// Looks like the code below creates troubles in combination with the applyCentralForce above
				// Maybe we should be more consistent by only applying forces or setting velocities.
				//
//				float newOrientation = vectorToAngle(linVel);
//				Vector3 angVel = body.getAngularVelocity();
//				angVel.y = (newOrientation - oldOrientation) % MathUtils.PI2;
//				if (angVel.y > MathUtils.PI) angVel.y -= MathUtils.PI2;
//				angVel.y /= deltaTime;
//				body.setAngularVelocity(angVel);
//				anyAccelerations = true;
//				oldOrientation = newOrientation;
			}
		}
		if (anyAccelerations) {
			body.activate();

			// TODO:
			// Looks like truncating speeds here after applying forces doesn't work as expected.
			// We should likely cap speeds form inside an InternalTickCallback, see
			// http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Simulation_Tick_Callbacks

			// Cap the linear speed
			Vector3 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			float maxLinearSpeed = getMaxLinearSpeed();
			if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
			}

			// Cap the angular speed
			Vector3 angVelocity = body.getAngularVelocity();
			if (angVelocity.y > getMaxAngularSpeed()) {
				angVelocity.y = getMaxAngularSpeed();
				body.setAngularVelocity(angVelocity);
			}
		}
	}

	public boolean isIndependentFacing () {
		return independentFacing;
	}

	@Override
	public float getOrientation () {
		transform.getRotation(tmpQuaternion, true);
		return tmpQuaternion.getYawRad();
	}

	@Override
	public Vector3 getLinearVelocity () {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity () {
		Vector3 angularVelocity = body.getAngularVelocity();
		return angularVelocity.y;
	}

	@Override
	public float getBoundingRadius () {
		// TODO: this should be calculated via the actual btShape
		return 1;
	}

	@Override
	public boolean isTagged () {
		return tagged;
	}

	@Override
	public void setTagged (boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public Vector3 newVector () {
		return new Vector3();
	}

	@Override
	public float vectorToAngle (Vector3 vector) {
//		return (float)Math.atan2(vector.z, vector.x);
		return (float)Math.atan2(-vector.z, vector.x);
	}

	@Override
	public Vector3 angleToVector (Vector3 outVector, float angle) {
//		outVector.set(MathUtils.cos(angle), 0f, MathUtils.sin(angle));
		outVector.z = -(float)Math.sin(angle);
		outVector.y = 0;
		outVector.x = (float)Math.cos(angle);
		return outVector;
	}

	@Override
	public Vector3 getPosition () {
		body.getMotionState().getWorldTransform(tmpMatrix4);
		return tmpMatrix4.getTranslation(tmpVector3);
	}

	@Override
	public float getMaxLinearSpeed () {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed (float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration () {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration (float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed () {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed (float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration () {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration (float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

}
