package com.baboviolent.game.bullet.instance.map;

import java.util.Arrays;
import java.util.Collections;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.baboviolent.game.bullet.instance.map.shader.GroundMeshBackup;
import com.baboviolent.game.bullet.instance.map.shader.GroundMesh;
import com.baboviolent.game.bullet.instance.map.shader.MapShader;
import com.baboviolent.game.bullet.instance.map.shader.MapShader2;
import com.baboviolent.game.bullet.instance.map.zone.Zone;
import com.baboviolent.game.bullet.instance.map.zone.ZoneTreeConstructor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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
	private MapShader2 mapShader;
	private Material groundMaterial;
	
	public BulletMapInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, constructionInfo);
	}
	
	@Override
	public void init() {
		super.init();
		mapShader = new MapShader2();
		mapShader.init();
		filteredNodes = new Array<Node>(nodes.size);
		visibleNodes = new Array<Node>(nodes.size);
		radius = BaboViolentGame.SIZE_MAP_CELL;
		rootZone = new ZoneTreeConstructor(nodes).generateRootZone();
		groundMesh = new GroundMesh();
		groundMaterial = new Material(new TextureAttribute(
        		TextureAttribute.Diffuse,
        		new Texture("data/texture/ground/atlas/ground.png")));
	}
	
	public GroundMeshBackup createGroundMesh() {
	   return new GroundMeshBackup(nodes.size);
	}
	
	
	/**
	 * Fonction appele par le modelbatch qui collecte les renderables
	 * On utilise un systeme de zone afin de ne pas tout parcourir pour economiser du temps
	 */
	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		filteredNodes.clear();
		visibleNodes.clear();
		getRenderablesWithFilter(renderables, pool, false);

		// On ajoute le ground renderable au modelbatch
		getGroundRenderable(renderables, pool);
	}
	
	// On renvoie seulement les murs
	public void getRenderablesForShadow (Array<Renderable> renderables, Pool<Renderable> pool) {
		getRenderablesWithFilter(renderables, pool, true);
	}
	
	public void getRenderablesWithFilter(Array<Renderable> renderables, Pool<Renderable> pool, boolean onlyWall) {
		rootZone.getNodesInCamera(camera, filteredNodes);
		for (Node node : filteredNodes) {
			getRenderables(node, renderables, pool, onlyWall);
		}
	}
	
    protected void getRenderables(Node node, Array<Renderable> renderables, Pool<Renderable> pool, boolean onlyWall) {
    	if( camera == null ) {
    		super.getRenderables(node, renderables, pool);
    	}
    	else {
    		getRenderablesWithFrustrum(node, renderables, pool, onlyWall);
    	}
	}
	
    protected void getRenderablesWithFrustrum (Node node, 
    		Array<Renderable> renderables, Pool<Renderable> pool, boolean onlyWall) {
		tmp.set(node.translation);
		if (camera.frustum.sphereInFrustum(tmp, radius) && 
			node.parts.size > 0) {
			// Il n'y a qu'un node part par cellule
    		NodePart nodePart = node.parts.get(0);
			if( nodePart.enabled ) { 
				// Si le sol
				if( !onlyWall && nodePart.meshPart.numVertices == 6 )
					visibleNodes.add(node);
				else
					renderables.add(getRenderable(pool.obtain(), node, nodePart));
			}
    	}
	}
	
	private void getGroundRenderable(Array<Renderable> renderables, Pool<Renderable> pool) {
		Renderable renderable = pool.obtain();
        renderable.mesh = groundMesh;
        renderable.meshPartOffset = 0;
        renderable.meshPartSize = 6;
        renderable.primitiveType = GL20.GL_TRIANGLES;
        renderable.material = groundMaterial;
        renderable.environment = null;
        renderable.worldTransform.setToTranslation(
        		camera.position.x - GroundMesh.WIDTH / 2, 0,
        		camera.position.z - GroundMesh.HEIGHT / 2);
        renderable.shader = mapShader;
        renderable.userData = this.userData;
        
        renderables.add(renderable);
	}
}