package com.baboviolent.game.bullet.instance.map.shader;

import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

public class NodeLinker {
	
	public static final int LEFT = 0;
	public static final int TOPLEFT = 1;
	public static final int TOP = 2;
	public static final int TOPRIGHT = 3;
	public static final int RIGHT = 4;
	public static final int BOTTOMRIGHT = 5;
	public static final int BOTTOM = 6;
	public static final int BOTTOMLEFT = 7;
	
	public Array<Node> nodes = new Array<Node>(8);
	
	public NodeLinker setLeft(Node n) {
		nodes.set(LEFT, n);
		return this;
	}
	
	public NodeLinker setTopLeft(Node n) {
		nodes.set(TOPLEFT, n);
		return this;
	}
	
	public NodeLinker setTop(Node n) {
		nodes.set(TOP, n);
		return this;
	}
	
	public NodeLinker setTopRight(Node n) {
		nodes.set(TOPRIGHT, n);
		return this;
	}
	
	public NodeLinker setRight(Node n) {
		nodes.set(RIGHT, n);
		return this;
	}
	
	public NodeLinker setBottomRight(Node n) {
		nodes.set(BOTTOMRIGHT, n);
		return this;
	}
	
	public NodeLinker setBottom(Node n) {
		nodes.set(BOTTOM, n);
		return this;
	}
	
	public NodeLinker setBottomLeft(Node n) {
		nodes.set(BOTTOMLEFT, n);
		return this;
	}
}
