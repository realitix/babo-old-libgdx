package com.baboviolent.game.map;

import com.badlogic.gdx.math.Vector3;

public class Cell {	
	private Vector3 position;
	private String type;
	private String textureName;
	private String texture2;
	
	/*
	 * Si n'est pas egal a zero, le type d'optimisation
	 * Si c'est un cote simple, un coin...
	 */
	private int optimizeType;

	private int angle;
	
	public int getAngle() {
		return angle;
	}

	public Cell setAngle(int angle) {
		this.angle = angle%360;
		return this;
	}

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
	
	public int getOptimizeType() {
		return optimizeType;
	}

	public Cell setOptimizeType(int optimizeType) {
		this.optimizeType = optimizeType;
		return this;
	}
	
	public String getTexture2() {
		return texture2;
	}

	public Cell setTexture2(String textureOptimize) {
		this.texture2 = textureOptimize;
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
