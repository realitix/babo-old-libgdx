package com.baboviolent.game.bullet.instance.map;

import java.util.Arrays;
import java.util.Collections;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.baboviolent.game.bullet.instance.map.shader.GroundMesh;
import com.baboviolent.game.bullet.instance.map.shader.MapShader;
import com.baboviolent.game.bullet.instance.map.zone.Zone;
import com.baboviolent.game.bullet.instance.map.zone.ZoneTreeConstructor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class BulletMapInstance extends BulletInstance implements Disposable {

	private Zone rootZone;
	private Array<Node> filteredNodes;
	private Array<Node> visibleNodes;
	private GroundMesh groundMesh;
	private MapShader mapShader;
	
	public BulletMapInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, constructionInfo);
	}
	
	@Override
	public void init() {
		super.init();
		mapShader = new MapShader();
		mapShader.init();
		filteredNodes = new Array<Node>(nodes.size);
		visibleNodes = new Array<Node>(nodes.size);
		radius = BaboViolentGame.SIZE_MAP_CELL;
		rootZone = new ZoneTreeConstructor(nodes).generateRootZone();
		groundMesh = createGroundMesh();
		
	}
	
	public GroundMesh createGroundMesh() {
		int maxTriangles = this.nodes.size * 2;
		int maxVertex = maxTriangles * 3;

	   return new GroundMesh(maxVertex);
	}
	
	
	/**
	 * Fonction appele par le modelbatch qui collecte les renderables
	 * On utilise un systeme de zone afin de ne pas tout parcourir pour economiser du temps
	 */
	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		filteredNodes.clear();
		visibleNodes.clear();
		getRenderablesWithFilter(renderables, pool);
		
		// On a rempli les noeuds visibles, on les charge dans le mesh
		groundMesh.batchNodes(visibleNodes);
		
		// On ajoute le ground renderable au modelbatch
		getGroundRenderable(renderables, pool);
	}
	
	private void getGroundRenderable(Array<Renderable> renderables, Pool<Renderable> pool) {
		Renderable renderable = pool.obtain();
        renderable.mesh = groundMesh;
        renderable.meshPartOffset = 0;
        renderable.meshPartSize = groundMesh.getVertexCount();
        renderable.primitiveType = GL20.GL_TRIANGLES;
        renderable.material = new Material();
        renderable.environment = null;
        renderable.worldTransform.idt();
        renderable.shader = mapShader;
        
        renderables.add(renderable);
	}
	
	public void getRenderablesWithFilter(Array<Renderable> renderables, Pool<Renderable> pool) {
		rootZone.getNodesInCamera(camera, filteredNodes);
		for (Node node : filteredNodes) {
			getRenderables(node, renderables, pool);
		}
	}
	
	/**
	 * @TODO creer un shader dedie a la map
	 * Ce shader doit rendre la map en une fois plutot que de parcourir les cellules une par une
	 * De plus, cela permmetra de faire des effets de fondu entre les textures
	 * Il faudra un seul mesh dedie contenant les extremites de la map visible
	 * et un shader a qui on envoie les coordonnes des cellules dans un tableau
	 * Actuellement, sur mon mobile il y a 126 noeuds a afficher donc 126 pass dans le shader
	 */
	@Override
    protected void getRenderablesWithFrustrum (Node node, 
    		Array<Renderable> renderables, Pool<Renderable> pool) {
		tmp.set(node.translation);
		if (camera.frustum.sphereInFrustum(tmp, radius) && 
			node.parts.size > 0) {
			// Il n'y a qu'un node part par cellule
    		NodePart nodePart = node.parts.get(0);
			if( nodePart.enabled ) { 
				// Si le sol
				if( nodePart.meshPart.numVertices == 6 )
					visibleNodes.add(node);
				else
					renderables.add(getRenderable(pool.obtain(), node, nodePart));
			}
    	}
	}
}