package com.baboviolent.game.map.optimizer;

import java.io.IOException;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MapOptimizer {
	// Cote en fonction de l'id dans le tableau
	public static final int SIDE_RIGHT = 0;
	public static final int SIDE_BOTTOMRIGHT = 1;
	public static final int SIDE_BOTTOM = 2;
	public static final int SIDE_BOTTOMLEFT = 3;
	public static final int SIDE_LEFT = 4;
	public static final int SIDE_TOPLEFT = 5;
	public static final int SIDE_TOP = 6;
	public static final int SIDE_TOPRIGHT = 7;
		
	// Transition sur un cote
	public static final int TYPE_ONESIDE = 1;
	// Transition sur deux cote
	public static final int TYPE_TWOSIDE = 2;
	// Transition sur un coin
	public static final int TYPE_CORNER = 3;
	
	private Map map;
	
	public MapOptimizer(Map map) {
		this.map = map;
	}
	
	public void optimize() {
		optimizeCells();
		OptimizerUtils.generateTextures(map);
	}
	
	/**
	 * Detecte les cellules a modifier
	 * Ce sont les cellules adjacentes devant etre transitionne
	 * pour un meilleur rendu visuel
	 */	
	private void optimizeCells() {
		Array<Cell> cells = map.getCells();
		for( int i = 0; i < cells.size; i++ ) {
			Array<Cell> cellsNear = OptimizerUtils.findNearCells(cells.get(i), cells);
			optimizeCell(cells.get(i), cellsNear);
		}
	}
	
	/**
	 * Modifie la cellule en fonction des cellules adjacentes
	 * Les cellules adjacentes sont classe a partir de la droite et
	 * dans le sens des aiguilles
	 * S'il y a moins de 8 cellules adjacentes, c'est une extremite de la map donc
	 * un mur, on passe.
	 * De plus, on ne traite pas les murs
	 * @param c1 La cellule a analyse
	 * @param cs Les cellules adjacentes (8 max)
	 */
	private void optimizeCell(Cell c1, Array<Cell> cs) {
		if( c1.getType().equals(Map.TYPE_WALL) || cs.size < 8 ) {
			return;
		}
		
		// On commence par determiner le nombre de cellules differentes
		// Un mur est considere comme identique puisqu'il n'y a pas de traitement
		Array<Integer> differents = new Array<Integer>();
		for( int i = 0; i < cs.size; i++ ) {
			if( !cs.get(i).getType().equals(Map.TYPE_WALL) && 
				!cs.get(i).getTextureName().equals(c1.getTextureName()) ) {
				differents.add(i);
			}
		}
		
		int id = 0;
		switch( differents.size ) {
			case 1:
				id = differents.get(0);
				optimizeOneSide(c1, cs.get(id), id);
				break;
			case 2:
				id = OptimizerUtils.align2(differents.get(0), differents.get(1));
				if( id >= 0 ) optimizeOneSide(c1, cs.get(id), id);
				break;
			case 3:
				id = OptimizerUtils.align3(differents.get(0), differents.get(1), differents.get(2));
				if( id >= 0 ) { optimizeOneSide(c1, cs.get(id), id); }
				else {
					id = OptimizerUtils.corner3(differents.get(0), differents.get(1), differents.get(2));
					if( id >= 0 ) { optimizeCorner(c1, cs.get(id), id); }
				}
				
				break;
			case 5:
				id = OptimizerUtils.corner5(
						differents.get(0), differents.get(1), differents.get(2),
						differents.get(3), differents.get(4));
				if( id >= 0 ) { optimizeCorner(c1, cs.get(id), id); }
				break;
			case 6:
				id = OptimizerUtils.align6(
						differents.get(0), differents.get(1), differents.get(2),
						differents.get(3), differents.get(4), differents.get(5));
				if( id >= 0 ) optimizeTwoSide(c1, cs.get(id), id);
				break;
		}
	}
	
	/**
	 * @param c1 La cellule a traite
	 * @param c2 La cellule differente
	 * @param side L'emplacement de la cellule differente
	 */
	private void optimizeOneSide(Cell c1, Cell c2, int side) {
		if( side%2 == 0 ) {
			c1
				.setOptimizeType(TYPE_ONESIDE)
				.setTexture2(c2.getTextureName())
				.setAngle((side/2)*90 + 180);
		}
	}
	
	/**
	 * @param c1 La cellule a traite
	 * @param c2 La cellule differente
	 * @param side L'emplacement de la cellule differente
	 */
	private void optimizeTwoSide(Cell c1, Cell c2, int side) {
		if( side%2 == 0 ) {
			c1
				.setOptimizeType(TYPE_TWOSIDE)
				.setTexture2(c2.getTextureName())
				.setAngle((side/2)*90 + 180);
		}
	}
	
	/**
	 * @param c1 La cellule a traite
	 * @param c2 La cellule differente
	 * @param side L'emplacement de la cellule differente
	 */
	private void optimizeCorner(Cell c1, Cell c2, int side) {
		c1
			.setOptimizeType(TYPE_CORNER)
			.setTexture2(c2.getTextureName())
			.setAngle((side/2)*90 + 180);
	}
}
