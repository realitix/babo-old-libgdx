package com.baboviolent.game.bullet.instance.map.shader;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class GroundMesh extends Mesh {
	public final static int POSITION_COMPONENTS = 3; // x y z
	public final static int TEXTURE_COMPONENTS = 2; // u v
	public final static int TOTAL_COMPONENTS = POSITION_COMPONENTS + 
											   TEXTURE_COMPONENTS;
	public final static int PRIMITIVE_SIZE = 3 * TOTAL_COMPONENTS;
	
	public static int HEIGHT;
	public static int WIDTH;
	public static Vector2 MAP_SIZE = new Vector2(96, 96);
	
	public GroundMesh() {
		super(false, 6, 0,
				new VertexAttribute(Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE));
	}
	
	public void updateVertices(Camera camera) {
		Vector2 bl = getXZWorld(camera, 0, 0);
		Vector2 tr = getXZWorld(camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		WIDTH = (int) Math.ceil(Math.abs(bl.x - tr.x));
		HEIGHT = (int) Math.ceil(Math.abs(bl.y - tr.y));
		this.setVertices(new float[] {
				0, 0, 0, 1, 0, // Bas droite
				WIDTH, 0, HEIGHT, 0, 1, // Haut gauche
				WIDTH, 0, 0, 0, 0, // Bas gauche
                0, 0, 0, 1, 0, // Bas droite
                0, 0, HEIGHT, 1, 1, // Haut droite
                WIDTH, 0, HEIGHT, 0, 1 // Haut gauche
                });
	}
	
	private Vector2 getXZWorld(Camera camera, int screenX, int screenY) {
		Vector3 position = new Vector3();
		Ray ray = camera.getPickRay(screenX, screenY);
        final float distance = -ray.origin.y / ray.direction.y;
        position.set(ray.direction).scl(distance).add(ray.origin);
        return new Vector2(position.x, position.z);
	}
}
