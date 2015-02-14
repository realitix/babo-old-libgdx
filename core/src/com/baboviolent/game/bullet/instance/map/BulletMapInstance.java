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
			/*for (Node node : nodes) {
				getRenderables(node, renderables, pool);
			}*/
			getRenderablesWithFilter(renderables, pool);
		}
		else {
			for (Node node : nodes) {
				getRenderables(node, renderables, pool);
			}
		}
	}
	
	public void getRenderablesWithFilter(Array<Renderable> renderables, Pool<Renderable> pool) {
		filteredNodes.clear();
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
    		for (NodePart nodePart : node.parts) {
    			if (nodePart.enabled) renderables.add(getRenderable(
    					pool.obtain(), node, nodePart));
    		}
    	}
	}
}