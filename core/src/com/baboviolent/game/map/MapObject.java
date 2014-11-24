package com.baboviolent.game.map;

import com.badlogic.gdx.math.Vector3;

public class MapObject {
	private Vector3 position;
	private float rotation = 0; // Rotation en degrée
	private String type; // Nom de l'objet
	
	public Vector3 getPosition() {
		return position;
	}
	
	public MapObject setPosition(Vector3 position) {
		this.position = position;
		return this;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public MapObject setRotation(float rotation) {
		this.rotation = rotation;
		return this;
	}
	
	public String getType() {
		return type;
	}
	
	public MapObject setType(String type) {
		this.type = type;
		return this;
	}
}
