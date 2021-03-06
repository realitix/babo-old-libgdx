package com.baboviolent.game.bullet.instance.map;

import java.util.Arrays;
import java.util.Collections;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.baboviolent.game.bullet.instance.map.shader.GroundMesh;
import com.baboviolent.game.bullet.instance.map.zone.Zone;
import com.baboviolent.game.bullet.instance.map.zone.ZoneTreeConstructor;
import com.baboviolent.game.gdx.shader.MapShader;
import com.baboviolent.game.loader.BaboAssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
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
	private MapShader mapShaderMax;
	private MapShader mapShaderMed;
	private MapShader mapShaderMin;
	private Material groundMaterial;
	
	private float yCamera;
	
	public BulletMapInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, constructionInfo);
	}
	
	@Override
	public void init() {
		super.init();
		
		filteredNodes = new Array<Node>(nodes.size);
		visibleNodes = new Array<Node>(nodes.size);
		radius = BaboViolentGame.SIZE_MAP_CELL;
		rootZone = new ZoneTreeConstructor(nodes).generateRootZone();
		groundMesh = new GroundMesh();
		
		TextureAtlas d = BaboAssetManager.getAtlas("mapDiffuse");
		TextureAtlas n = BaboAssetManager.getAtlas("mapNormal");
		TextureAtlas s = BaboAssetManager.getAtlas("mapSpecular");
		
		// Region useless
		TextureAttribute diffuseAttribute = new TextureAttribute(
        		TextureAttribute.Diffuse,
        		d.getRegions().first());
        TextureAttribute normalAttribute = new TextureAttribute(
        		TextureAttribute.Normal,
        		n.getRegions().first());
        TextureAttribute specularAttribute = new TextureAttribute(
        		TextureAttribute.Specular,
        		s.getRegions().first());
        
        groundMaterial = new Material(diffuseAttribute,
        								 normalAttribute,
        								 specularAttribute);
        
		mapShaderMin = new MapShader(Configuration.MIN);
		mapShaderMin.init();
		mapShaderMin.updateUvs(d);
		
		mapShaderMed = new MapShader(Configuration.MED);
		mapShaderMed.init();
		mapShaderMed.updateUvs(d);
		
		mapShaderMax = new MapShader(Configuration.MAX);
		mapShaderMax.init();
		mapShaderMax.updateUvs(d);
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
		if( yCamera != camera.position.y ) {
			yCamera = camera.position.y;
			groundMesh.updateVertices(camera);
		}
		
		Renderable renderable = pool.obtain();
        renderable.mesh = groundMesh;
        renderable.meshPartOffset = 0;
        renderable.meshPartSize = 6;
        renderable.primitiveType = GL20.GL_TRIANGLES;
        renderable.material = groundMaterial;
        renderable.environment = null;
        renderable.worldTransform.setToTranslation(
        		camera.position.x - (float)GroundMesh.WIDTH / 2f, 0,
        		camera.position.z - (float)GroundMesh.HEIGHT / 2f);
        renderable.shader = getShaderFromConfiguration();
        renderable.userData = this.userData;
        
        renderables.add(renderable);
	}
	
	private Shader getShaderFromConfiguration() {
		if( Configuration.Video.mapShaderQuality == Configuration.MAX )
			return mapShaderMax;
		if( Configuration.Video.mapShaderQuality == Configuration.MED )
			return mapShaderMed;
		return mapShaderMin;
	}
}