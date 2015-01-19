package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.GameObject;
import com.badlogic.gdx.math.Vector3;

public class BulletRayResult {
	private GameObject object;
	private boolean map;
	private Vector3 startRay;
	private Vector3 endRay;
	private Vector3 normalRay;
	
	public BulletRayResult() {
		startRay = new Vector3();
		endRay = new Vector3();
	}
	
	public BulletRayResult setNormalRay(Vector3 normalRay) {
		this.normalRay = normalRay;
		return this;
	}
	
	public Vector3 getNormalRay() {
		return normalRay;
	}

	public GameObject getObject() {
		return object;
	}
	
	public BulletRayResult setObject(GameObject object) {
		this.object = object;
		return this;
	}
	
	public boolean isMap() {
		return map;
	}
	
	public BulletRayResult setMap(boolean map) {
		this.map = map;
		return this;
	}
	
	public Vector3 getStartRay() {
		return startRay;
	}
	
	public BulletRayResult setStartRay(Vector3 startRay) {
		this.startRay.set(startRay);
		return this;
	}
	
	public Vector3 getEndRay() {
		return endRay;
	}
	
	public BulletRayResult setEndRay(Vector3 endRay) {
		this.endRay.set(endRay);
		return this;
	}
}
