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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MapOptimizer {
	// Transition sur un cote
	public static final int TYPE_ONESIDE = 1;
	
	private Map map;
	
	public MapOptimizer(Map map) {
		this.map = map;
	}
	
	public void optimize() {
		optimizeCells();
		generateTextures();
	}
	
	/**
	 * Detecte les cellules a modifier
	 * Ce sont les cellules adjacentes devant etre transitionne
	 * pour un meilleur rendu visuel
	 */	
	private void optimizeCells() {
		Array<Cell> cells = map.getCells();
		for( int i = 0; i < cells.size; i++ ) {
			for( int j = 0; j < cells.size; j++ ) {
				int type = mustOptimized(cells.get(i), cells.get(j) );
				if( type != 0 ) {
					cells.get(i)
						.setOptimizeType(type)
						.setTexture2(cells.get(j).getTextureName());
				}
			}
		}
	}
	
	private int mustOptimized(Cell c1, Cell c2) {
		// Si une des cellules est un mur, on ne fait rien
		if( c1.getType().equals(Map.TYPE_WALL) || c2.getType().equals(Map.TYPE_WALL) ) {
			return 0;
		}
		
		// Cellules cote a cote
		if( c1.getPosition().dst(c2.getPosition()) == BaboViolentGame.SIZE_MAP_CELL ) {
			// Textures differentes
			if( !c1.getTextureName().equals(c2.getTextureName()) ) {
				return TYPE_ONESIDE;
			}
		}
		
		return 0;
	}
	
	private void generateTextures() {
		Array<Cell> cells = map.getCells();
		ObjectMap<String, Pixmap> textures = new ObjectMap<String, Pixmap>();
		PixmapGenerator generator = new PixmapGenerator();
		for( int i = 0; i < cells.size; i++ ) {
			if( cells.get(i).getOptimizeType() != 0 ) {
				Cell c = cells.get(i);
				String textureName = c.getTextureName()+"_"+
						c.getTexture2()+"_"+
						c.getOptimizeType();
				
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
}
