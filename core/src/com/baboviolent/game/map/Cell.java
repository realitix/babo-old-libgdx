package com.baboviolent.game.map;

import com.badlogic.gdx.math.Vector3;

public class Cell {
	private int number;
	private String type;
	
	public int getNumber() {
		return number;
	}
	public Cell setNumber(int number) {
		this.number = number;
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
