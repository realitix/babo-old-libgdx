package com.baboviolent.game.map;

import com.badlogic.gdx.math.Vector3;

public class Cell {
	public static final int TYPE_WALL = 1;
	public static final int TYPE_GROUND = 2;
	
	private Vector3 position;
	private int type;
	private String textureName;
	
	public Vector3 getPosition() {
		return position;
	}
	
	public Cell setPosition(Vector3 position) {
		this.position = position;
		return this;
	}
	
	public int getType() {
		return type;
	}
	
	public Cell setType(int type) {
		this.type = type;
		return this;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	public Cell setTextureName(String t) {
		this.textureName = t;
		return this;
	}
	
	public boolean equals(Cell other) {
		if(this.position.equals(other.getPosition()) && type == other.getType() && textureName == other.getTextureName())
			return true;
		return false;
	}
	
	public boolean equalsPosition(Cell other) {
		if(this.position.equals(other.getPosition()) )
			return true;
		return false;
	}
}
