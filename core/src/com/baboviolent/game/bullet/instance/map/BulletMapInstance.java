package com.baboviolent.game.bullet.instance.map;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
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
	
	public BulletMapInstance (Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		super(model, constructionInfo);
	}
	
	@Override
	public void init() {
		super.init();
		filteredNodes = new Array<Node>(nodes.size);
		radius = BaboViolentGame.SIZE_MAP_CELL;
		rootZone = new ZoneTreeConstructor(nodes).generateRootZone();
	}
	
	/**
	 * Fonction appele par le modelbatch qui collecte les renderables
	 * On utilise un systeme de zone afin de ne pas tout parcourir pour economiser du temps
	 */
	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		if( camera != null ) {
			getRenderablesWithFilter(renderables, pool);
		}
		else {
			for (Node node : nodes) {
				getRenderables(node, renderables, pool);
			}
		}
	}
	
	public void getRenderablesWithFilter (Array<Renderable> renderables, Pool<Renderable> pool) {
		Vector3 cameraPosition = camera.position.cpy();
		cameraPosition.y = 0;
		filteredNodes.clear();
		rootZone.getNodesAt(cameraPosition, filteredNodes);
		for (Node node : filteredNodes) {
			getRenderables(node, renderables, pool);
		}
	}
	
	@Override
    protected void getRenderablesWithFrustrum (Node node, 
    		Array<Renderable> renderables, Pool<Renderable> pool) {
		tmp.set(node.translation);
		if (camera.frustum.sphereInFrustum(tmp, radius) && 
			node.parts.size > 0) {
    		for (NodePart nodePart : node.parts) {
    			if (nodePart.enabled) renderables.add(getRenderable(
    					pool.obtain(), node, nodePart));
    		}
    	}
	}
}