package com.baboviolent.game.map;

import com.badlogic.gdx.math.Vector3;

public class Cell {	
	private Vector3 position;
	private String type;
	private String textureName;
	
	public Cell() {
		position = new Vector3();
	}
	
	public Vector3 getPosition() {
		return position.cpy();
	}
	
	public Cell setPosition(Vector3 position) {
		this.position.set(position);
		return this;
	}
	
	public String getType() {
		return type;
	}
	
	public Cell setType(String type) {
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
