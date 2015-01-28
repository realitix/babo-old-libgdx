package com.baboviolent.game.ai;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.ai.fsm.AiBaboState;
import com.baboviolent.game.ai.pfa.tiled.flat.BaboPathGenerator;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.PoolParticle;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AiBabo extends Babo implements Steerable<Vector3> {

	private SteeringBehavior<Vector3> steeringBehavior;
	private static final SteeringAcceleration<Vector3> steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());
	private StateMachine<AiBabo> stateMachine;
	private final Array<Babo> babos;
	private final BaboPathGenerator pathGenerator;
	
	public AiBabo(
			String username, 
			String skin, 
			final BaboEffectSystem effectSystem, 
			final BulletWorld world, 
			final Array<Babo> babos,
			final BaboPathGenerator pathGenerator) 
	{
		super(username, skin, effectSystem, world);
		this.babos = babos;
		this.pathGenerator = pathGenerator;
		stateMachine = new DefaultStateMachine<AiBabo>(this, AiBaboState.THINK);
	}
	
	public void seekClosestBabo() {
		Babo b = getClosestBabo();
		if( b != null ) {
			rollTo(b.getPosition());
		}
	}
	
	public boolean reachedDestination() {
		FollowPath<Vector3, LinePathParam> fp = (FollowPath<Vector3, LinePathParam>) steeringBehavior;
		Vector3 end = fp.getPath().getEndPoint();
		float t = fp.getArrivalTolerance();
		if( this.getPosition().dst2(end) < t*t) {
			return true;
		}
		
		return false;
	}
	
	public Array<Babo> getBabos() {
		return babos;
	}
	
	public Babo getClosestBabo() {
		float minDistance = 9999999;
		Babo closest = null;
		for(int i = 0; i < babos.size; i++) {
			if( babos.get(i) != this ) {
				float distance = babos.get(i).getPosition().sub(this.getPosition()).len2();
				if( distance < minDistance ) {
					minDistance = distance;
					closest = babos.get(i);
				}
			}
		}
		return closest;
	}
	
	private void rollTo(Vector3 position) {
		Array<Vector3> waypoints = pathGenerator.getPath(this.getPosition(), position);
        if( waypoints.size >= 2 ) {
			LinePath<Vector3> linePath = new LinePath<Vector3>(waypoints, true);
	        FollowPath<Vector3, LinePathParam> sb = new FollowPath<Vector3, LinePathParam>(this, linePath)
				.setTimeToTarget(1f)
				.setArrivalTolerance(1f)
				.setDecelerationRadius(0)
				.setPredictionTime(0)
				.setPathOffset(10);
	        
			this.setSteeringBehavior(sb);
        }
	}
	
	@Override
	public void update() {
		super.update();
		updateAi();
	}
	
	private void updateAi () {
		if (steeringBehavior != null) {
			steeringBehavior.calculateSteering(steeringOutput);
			steeringOutput.linear.y = 0;
			steeringOutput.linear.x = -steeringOutput.linear.x;
			applySteering(steeringOutput);			
		}
		
		stateMachine.update();
	}
	
	private void applySteering (SteeringAcceleration<Vector3> steering) {
		this.setDirection(steering.linear);
	}
	
	public StateMachine<AiBabo> getStateMachine () {
		return stateMachine;
	}
	
	public SteeringBehavior<Vector3> getSteeringBehavior () {
		return steeringBehavior;
	}
	
	public AiBabo setSteeringBehavior (SteeringBehavior<Vector3> steeringBehavior) {
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
		tmpV3.set(this.body.getLinearVelocity()).y = 0;
		
		return tmpV3;
	}

	@Override
	public float getAngularVelocity() {
		return this.body.getAngularVelocity().y;
	}

	@Override
	public float getBoundingRadius() {
		return BaboViolentGame.BABO_DIAMETER/2;
	}
	
	@Override
	public Vector3 getPosition() {
		return super.getPosition();
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
