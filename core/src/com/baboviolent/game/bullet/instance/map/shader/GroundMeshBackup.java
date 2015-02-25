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

public class GroundMeshBackup extends Mesh {
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
	
	private TextureAtlas atlas;
	//private ObjectMap<Node, NodeLinker> links;
	
	
	public GroundMeshBackup(int maxNodes) {
		//int maxVertices = maxNodes * 2 * 3;
		super(false, maxNodes * 2 * 3, 0,
				new VertexAttribute(Usage.Position, POSITION_COMPONENTS, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, TEXTURE_COMPONENTS, ShaderProgram.TEXCOORD_ATTRIBUTE)
				/*new VertexAttribute(Usage.Generic, RANGE_COMPONENTS, MapShader.RANGE_ATTRIBUTE)*/);
		
		int maxVertices = maxNodes * 2 * 3;
		verts = new float[maxVertices * TOTAL_COMPONENTS];
		id = 0;
		atlas = new TextureAtlas("data/texture/ground/atlas/ground.atlas");
		//links = new ObjectMap<Node, NodeLinker>( maxNodes );
	}
	
	// On enregistre seulement les noeuds differents
	public void generateLinks(Array<Node> nodes) {
		float s = BaboViolentGame.SIZE_MAP_CELL;
		
		for( Node n1 : nodes ) {
			Node left = null, topLeft = null, 
				 top = null, topRight = null,
				 right = null, bottomRight = null,
				 bottom = null, bottomLeft = null;
			Vector3 t1 = n1.translation;
			
			for( Node n2 : nodes ) {
				// On autorise seulement un sol visible et different
				NodePart nodePart = n2.parts.get(0);
				if( !nodePart.enabled || 
					nodePart.meshPart.numVertices != 6 ||
					n2.id.equals(n1.id)
					)
					continue;
				
				Vector3 t2 = n2.translation;
				
				if( t2.y == t1.y && t2.x == t1.x + s ) // Left
					left = n2;
				if( t2.y == t1.y + s && t2.x == t1.x + s ) // TopLeft
					topLeft = n2;
				if( t2.y == t1.y + s && t2.x == t1.x ) // Top
					top = n2;
				if( t2.y == t1.y + s && t2.x == t1.x - s ) // TopRight
					topRight = n2;
				if( t2.y == t1.y && t2.x == t1.x - s ) // Right
					right = n2;
				if( t2.y == t1.y - s && t2.x == t1.x - s ) // BottomRight
					bottomRight = n2;
				if( t2.y == t1.y - s && t2.x == t1.x ) // Bottom
					bottom = n2;
				if( t2.y == t1.y - s && t2.x == t1.x + s ) // BottomLeft
					bottomLeft = n2;
			}
			
			/*links.put(n1, new NodeLinker()
					.setLeft(left)
					.setTopLeft(topLeft)
					.setTop(top)
					.setTopRight(topRight)
					.setRight(right)
					.setBottomRight(bottomRight)
					.setBottom(bottom)
					.setBottomLeft(bottomLeft));*/
		}
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
