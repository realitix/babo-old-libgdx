package com.baboviolent.game.bullet.instance.map;

import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

/**
 * Classe utilise pour le zoning
 * Plutot que de parcourir toutes les cellules,
 * On va utiliser des zones recursives afin de minimiser au maximum les appelles
 * Les zones sont generees a la creation de l'instance
 * Algo:
 * 1 - Les zones contenant les cellules doivent faire la taille de 1/4 du nombre
 * 		de cellules visibles afin d'avoir toujours 4 zones a recupere
 * 2 - 
 */
public class Zone {
	public static final float ZONE_SIZE = 10; // Taille de la map dans chaque zone
	public static final float NB_ZONE_MAX = 10; // Nombre zones max imbriquees
	private BoundingBox boundingBox;
	private Array<Node> nodes = new Array<Node>();
	private Zone parent;
	private Array<Zone> children = new Array<Zone>();
	
	public Zone() {
	}
	
	public void getNodesAt(Vector3 position, Array<Node> out) {
		if( children.size == 0 ) {
			out.addAll(nodes);
		}
		
		for( Zone zone: children ) {
			if( zone.in(position) ) {
				zone.getNodesAt(position, out);
			}
		}
	}
	
	public Zone setBoundingBox(BoundingBox bb) {
		this.boundingBox = bb;
		return this;
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	public void computeBoundingBox() {
		// On calcul en premier lieu celui des enfants puis le notre
		for( Zone zone: children ) {
			zone.computeBoundingBox();
		}
		
		// On etend notre bounding box
		// Si elle n'existe pas, on initialise avec celle de l'enfant
		for( Zone zone: children ) {
			if( boundingBox == null ) boundingBox = new BoundingBox(zone.getBoundingBox());
			else boundingBox.ext(zone.getBoundingBox());
		}
	}
	
	public void add(Zone zone) {
		children.add(zone);
		zone.setParent(this);
	}
	
	public void add(Node node) {
		nodes.add(node);
	}
	
	public void setParent(Zone parent) {
		this.parent = parent;
	}
	
	public boolean in(Node node) {
		return in(node.translation);
	}
	
	public boolean in(Vector3 target) {
		if( boundingBox.contains(target) ) {
			return true;
		}
		return false;
	}
}
