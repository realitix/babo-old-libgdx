package com.baboviolent.game.bullet.instance.map;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class ZoneTreeConstructor {
	private Array<Node> nodes;
	
	public ZoneTreeConstructor(Array<Node> nodes) {
		this.nodes = nodes;
	}
	
	public Zone generateRootZone() {
		return initZone();
	}
	
	private Zone initZone() {
		float mapWidth = getMapWidth();
		float mapHeight = getMapHeight();
		float offsetMapX = getBottomRight().x;
		float offsetMapY = getBottomRight().y;
		int nbZoneX = (int) Math.ceil(mapWidth/Zone.ZONE_SIZE);
		int nbZoneY = (int) Math.ceil(mapHeight/Zone.ZONE_SIZE);
		
		Array<Zone> zones = new Array<Zone>(nbZoneX*nbZoneY);
		float s = BaboViolentGame.SIZE_MAP_CELL;
		Vector3 min = new Vector3(offsetMapX, 0, offsetMapY);
		Vector3 max = new Vector3(offsetMapX + s, 0, offsetMapY + s);
		
		// On cree les zones
		for( int i = 0; i < nbZoneX; i++ ) {
			for( int j = 0; j < nbZoneY; j++ ) {
				BoundingBox bb = new BoundingBox(
						min.cpy().add(Zone.ZONE_SIZE*i, 0, Zone.ZONE_SIZE*j),
						max.cpy().add(Zone.ZONE_SIZE*i, 0, Zone.ZONE_SIZE*j)
						);
				zones.add(new Zone().setBoundingBox(bb));
			}
		}
		
		// On ajoute les noeuds dans les zones
		for( Zone z : zones ) {
			for( Node n : nodes ) {
				if( z.in(n) ) {
					z.add(n);
				}
			}
		}
		
		return computeTreeBoundingBox(getZoneTree(zones));
	}
	
	private Zone computeTreeBoundingBox(Zone root) {
		root.computeBoundingBox();
		return root;
	}
	
	/**
	 * Renvoie le zone root en ayant cree l'arbre
	 * On cree une nouvelle branche a chaque fois qu'un etage
	 * depasse Zone.NB_ZONE_MAX
	 */
	private Zone getZoneTree(Array<Zone> zones) {
		Zone root = new Zone();
		
		Array<Zone> treeZones = getZonesRecursively(zones);
		for( Zone z : treeZones ) {
			root.add(z);
		}
		
		return root;
	}
	
	private Array<Zone> getZonesRecursively(Array<Zone> zones) {
		if( zones.size <= Zone.NB_ZONE_MAX ) {
			return zones;
		}
		
		Array<Zone> results = new Array<Zone>((int) Math.ceil(zones.size/Zone.NB_ZONE_MAX));
		Zone currentZone = null;
		for( int i = 0; i < zones.size; i++ ) {
			if( i%Zone.NB_ZONE_MAX == 0 ) {
				currentZone = new Zone();
				results.add(currentZone);
			}
			
			currentZone.add(zones.get(i));
		}
		
		return getZonesRecursively(results);
	}
	
	private float getMapWidth() {
		Vector2 l = getBottomLeft();
		Vector2 r = getBottomRight();
		return Math.abs(l.x - r.x);
	}
	
	private float getMapHeight() {
		Vector2 t = getTopLeft();
		Vector2 b = getBottomLeft();
		return Math.abs(t.y - b.y);
	}
	
	private Vector2 getBottomRight() {
		float r = 9999999, b = 9999999;
		for( Node n : nodes ) {
			if( n.translation.x < r ) r = n.translation.x;
			if( n.translation.z < b ) b = n.translation.z;
		}
		return new Vector2(r,b);
	}
	
	private Vector2 getBottomLeft() {
		float l = -9999999, b = 9999999;
		for( Node n : nodes ) {
			if( n.translation.x > l ) l = n.translation.x;
			if( n.translation.z < b ) b = n.translation.z;
		}
		return new Vector2(l,b);
	}
	
	private Vector2 getTopLeft() {
		float l = -9999999, t = -9999999;
		for( Node n : nodes ) {
			if( n.translation.x > l ) l = n.translation.x;
			if( n.translation.z > t ) t = n.translation.z;
		}
		return new Vector2(l,t);
	}
	
	private Vector2 getTopRight() {
		float r = 9999999, t = -9999999;
		for( Node n : nodes ) {
			if( n.translation.x < r ) r = n.translation.x;
			if( n.translation.z > t ) t = n.translation.z;
		}
		return new Vector2(r,t);
	}
}
