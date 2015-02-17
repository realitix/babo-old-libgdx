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
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.utils.Array;

public class GroundMesh extends Mesh {
	private float[] verts;
	private int id;
	private int vertexCount;
	
	public final static int POSITION_COMPONENTS = 3; // x y z
	public final static int TEXTURE_COMPONENTS = 2; // u v
	public final static int RANGE_COMPONENTS = 1; // range
	public final static int TOTAL_COMPONENTS = POSITION_COMPONENTS + 
											   TEXTURE_COMPONENTS +
											   RANGE_COMPONENTS;
	public final static int PRIMITIVE_SIZE = 3 * TOTAL_COMPONENTS;
	
	private float s = BaboViolentGame.SIZE_MAP_CELL/2f;
	private int texture0;
	
	private TextureAtlas atlas;
	
	
	public GroundMesh(int maxVertices) {
		super(false, maxVertices, 0,
				new VertexAttribute(Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE),
				new VertexAttribute(Usage.Generic, RANGE_COMPONENTS, MapShader.RANGE_ATTRIBUTE));
		
		verts = new float[maxVertices * TOTAL_COMPONENTS];
		id = 0;
		atlas = new TextureAtlas("data/texture/ground/atlas/ground.atlas");
	}
	
	public void batchNodes(Array<Node> nodes) {
		id = 0;

		for( Node n : nodes ) {
			batchNode(n);
		}
		vertexCount = (id/TOTAL_COMPONENTS);
		setVertices(verts, 0, id);
	}
	
	private void batchNode(Node n) {
		float range = 0;
		/*
		 *  First triangle
		 */
		TextureRegion r = atlas.findRegion(n.id);
		
		// Top left
	    verts[id++] = n.translation.x + s;
	    verts[id++] = n.translation.y;
	    verts[id++] = n.translation.z + s;
	    verts[id++] = r.getU();
	    verts[id++] = r.getV2();
	    verts[id++] = range;
	    
	    // Bottom left
	    verts[id++] = n.translation.x + s;
	    verts[id++] = n.translation.y;
	    verts[id++] = n.translation.z - s;
	    verts[id++] = r.getU();
	    verts[id++] = r.getV();
	    verts[id++] = range;
	    
	    // Bottom right
	    verts[id++] = n.translation.x - s;
	    verts[id++] = n.translation.y;
	    verts[id++] = n.translation.z - s;
	    verts[id++] = r.getU2();
	    verts[id++] = r.getV();
	    verts[id++] = range;
	    
	    
	    /*
		 *  Second triangle
		 */
	    
	    // Top right
	    verts[id++] = n.translation.x - s;
	    verts[id++] = n.translation.y;
	    verts[id++] = n.translation.z + s;
	    verts[id++] = r.getU2();
	    verts[id++] = r.getV2();
	    verts[id++] = range;
	    
	    // Top left
	    verts[id++] = n.translation.x + s;
	    verts[id++] = n.translation.y;
	    verts[id++] = n.translation.z + s;
	    verts[id++] = r.getU();
	    verts[id++] = r.getV2();
	    verts[id++] = range;
	    
	    // Bottom right
	    verts[id++] = n.translation.x - s;
	    verts[id++] = n.translation.y;
	    verts[id++] = n.translation.z - s;
	    verts[id++] = r.getU2();
	    verts[id++] = r.getV();
	    verts[id++] = range;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
}
