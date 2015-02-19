package com.baboviolent.game.bullet.instance.map.shader;

import com.baboviolent.game.BaboViolentGame;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class GroundMesh2 extends Mesh {
	public final static int POSITION_COMPONENTS = 3; // x y z
	public final static int TEXTURE_COMPONENTS = 2; // u v
	public final static int TOTAL_COMPONENTS = POSITION_COMPONENTS + 
											   TEXTURE_COMPONENTS;
	public final static int PRIMITIVE_SIZE = 3 * TOTAL_COMPONENTS;
	
	
	
	public GroundMesh2() {
		super(false, 6, 0,
				new VertexAttribute(Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE));
		
		
		int widthMap = 100;
		int heightMap = 100;
		this.setVertices(new float[] {
				0, 0, 0, 1, 0, // Bas droite
				widthMap, 0, heightMap, 0, 1, // Haut gauche
                widthMap, 0, 0, 0, 0, // Bas gauche
                0, 0, 0, 1, 0, // Bas droite
                0, 0, heightMap, 1, 1, // Haut droite
                widthMap, 0, heightMap, 0, 1 // Haut gauche
                });
	}
}
