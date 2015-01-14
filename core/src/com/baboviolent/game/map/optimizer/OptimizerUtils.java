package com.baboviolent.game.map.optimizer;

import java.io.IOException;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class OptimizerUtils {
	
	public static void generateTextures(Map map) {
		Array<Cell> cells = map.getCells();
		ObjectMap<String, Pixmap> textures = new ObjectMap<String, Pixmap>();
		PixmapGenerator generator = new PixmapGenerator();
		for( int i = 0; i < cells.size; i++ ) {
			if( cells.get(i).getOptimizeType() != 0 ) {
				Cell c = cells.get(i);
				String textureName = getOptimizedFileName(c);
				
				if( !textures.containsKey(textureName) ) {
					textures.put(
						textureName,
						generator.generate(
							c.getTextureName(),
							c.getTexture2(),
							c.getOptimizeType()
						));
				}
			}
		}
		
		// Write png files
		for (ObjectMap.Entry<String, Pixmap> e : textures.entries()) {
			if( e.value == null ) {
				continue;
			}
			
			FileHandle file = Gdx.files.external(BaboViolentGame.PATH_TEXTURE_EXTERNAL_OPTIMIZED+e.key+".png");
			PNG writer = new PNG((int)(e.value.getWidth() * e.value.getHeight() * 1.5f));
			try {
				writer.setFlipY(false);
				try {
					writer.write(file, e.value);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
			finally {
				writer.dispose();
			}
        }
		
	}
	
	public static String getOptimizedFileName(Cell c) {
		return c.getTextureName()+"_"+
				c.getTexture2()+"_"+
				c.getOptimizeType();
	}
	
	public static Array<Cell> findNearCells(Cell c, Array<Cell> cells) {
		Array<Cell> result = new Array<Cell>(); 
		int i;
		float s = BaboViolentGame.SIZE_MAP_CELL;
		
		// Cellule droite
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( p1.z == p2.z && (p1.x + s) == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule bas-droite
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( (p1.z - s) == p2.z && (p1.x + s) == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule bas
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( (p1.z - s) == p2.z && p1.x == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule bas-gauche
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( (p1.z - s) == p2.z && (p1.x - s) == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule gauche
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( p1.z == p2.z && (p1.x - s) == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule haut-gauche
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( (p1.z + s) == p2.z && (p1.x - s) == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule haut
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( (p1.z + s) == p2.z && p1.x == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		// Cellule haut-droite
		for( i = 0; i < cells.size; i++ ) {
			Vector3 p1 = c.getPosition();
			Vector3 p2 = cells.get(i).getPosition();
			if( (p1.z + s) == p2.z && (p1.x + s) == p2.x ) {
				result.add(cells.get(i));
			}
		}
		
		return result;
	}
	
	public static int align3(int s1, int s2, int s3) {
		if( sideIn(s1, s2, s3, 
				MapOptimizer.SIDE_RIGHT, 
				MapOptimizer.SIDE_TOPRIGHT,
				MapOptimizer.SIDE_BOTTOMRIGHT) ) {
			return MapOptimizer.SIDE_RIGHT;
		}
		
		if( sideIn(s1, s2, s3, 
				MapOptimizer.SIDE_BOTTOM, 
				MapOptimizer.SIDE_BOTTOMLEFT,
				MapOptimizer.SIDE_BOTTOMRIGHT) ) {
			return MapOptimizer.SIDE_BOTTOM;
		}
		
		if( sideIn(s1, s2, s3, 
				MapOptimizer.SIDE_LEFT, 
				MapOptimizer.SIDE_TOPLEFT,
				MapOptimizer.SIDE_BOTTOMLEFT) ) {
			return MapOptimizer.SIDE_LEFT;
		}
		
		if( sideIn(s1, s2, s3, 
				MapOptimizer.SIDE_TOP, 
				MapOptimizer.SIDE_TOPLEFT,
				MapOptimizer.SIDE_TOPRIGHT) ) {
			return MapOptimizer.SIDE_TOP;
		}
		
		return -1;
	}
	
	public static boolean sideIn(int s1, int s2, int s3, int t1, int t2, int t3) {
		if( s1 != t1 && s1 != t2 && s1 != t3 )
			return false;
		if( s2 != t1 && s2 != t2 && s2 != t3 )
			return false;
		if( s3 != t1 && s3 != t2 && s3 != t3 )
			return false;
		return true;
	}
}
