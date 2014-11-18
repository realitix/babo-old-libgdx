package com.baboviolent.game.map;

import com.badlogic.gdx.math.Vector3;

public class Cell {
	private Vector3 position;
	private String type;
	
	public Vector3 getPosition() {
		return position;
	}
	public Cell setPosition(Vector3 position) {
		this.position = position;
		return this;
	}
	public String getType() {
		return type;
	}
	public Cell setType(String type) {
		this.type = type;
		return this;
	}
}
