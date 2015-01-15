package com.baboviolent.game.map.optimizer;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class PixmapGenerator {

	private ObjectMap<String, Pixmap> cache = new ObjectMap<String, Pixmap>();
	
	private Pixmap loadPixmap(String s) {
		if( cache.containsKey(s) ) {
			return cache.get(s);
		}
		
		return new Pixmap(Gdx.files.internal(BaboViolentGame.PATH_TEXTURE_GROUND+s+".png"));
	}
	
	public Pixmap generate(String s1, String s2, int type) {
		Pixmap p1 = loadPixmap(s1);
		Pixmap p2 = loadPixmap(s2);
		
		if( p1.getWidth() != p2.getWidth() || 
			p1.getHeight() != p2.getHeight() || 
			!p1.getFormat().equals(p2.getFormat()) ) {
			System.out.println("les deux images ne sont pas du meme format: "+p1.getFormat());
			return null;
		}
		
		int height = p1.getHeight();
		int width = p1.getWidth();
		
		Pixmap result = new Pixmap(width, height, Pixmap.Format.RGB888);
		
		switch( type ) {
			case MapOptimizer.TYPE_ONESIDE:
				generateOneSide(result, p1, p2);
				break;
			case MapOptimizer.TYPE_TWOSIDE:
				generateTwoSide(result, p1, p2);
				break;
			case MapOptimizer.TYPE_CORNER:
				generateCorner(result, p1, p2);
				break;
		}
		
		return result;
	}
	
	private void generateOneSide(Pixmap result, Pixmap p1, Pixmap p2) {
		int width = result.getWidth();
		int height = result.getHeight();
		
		Color c1 = new Color();
		Color c2 = new Color();
		float t = 0;
		for( int j = 0; j < height; j++) {
			for( int i = 0; i < width; i++) {
				c1.set(p1.getPixel(i, j));
				c2.set(p2.getPixel(i, j));
				result.setColor(c1.lerp(c2, t));
				result.drawPixel(i, j);
			}
			t = ((float)j/(float)height)/2f;
		}
	}
	
	private void generateTwoSide(Pixmap result, Pixmap p1, Pixmap p2) {
		int width = result.getWidth();
		int height = result.getHeight();
		
		Color c1 = new Color();
		Color c2 = new Color();
		float t = 0.5f;
		for( int j = 0; j < height; j++) {
			for( int i = 0; i < width; i++) {
				c1.set(p1.getPixel(i, j));
				c2.set(p2.getPixel(i, j));
				result.setColor(c1.lerp(c2, t));
				result.drawPixel(i, j);
			}
			if( j < height/2 ) {
				t = (1 - (float)j/(float)height*2f)/2f;
			}
			else {
				t = ((float)j/(float)height*2f - 1)/2f;
			}
		}
	}
	
	private void generateCorner(Pixmap result, Pixmap p1, Pixmap p2) {
		int width = result.getWidth();
		int height = result.getHeight();
		
		Color c1 = new Color();
		Color c2 = new Color();
		float t = 0.5f;
		// On va gerer par rapport a la distance initiale
		Vector2 origin = new Vector2();
		Vector2 current = new Vector2();
		float distance;
		for( int j = 0; j < height; j++) {
			for( int i = 0; i < width; i++) {
				current.set(i, j);
				distance = current.cpy().sub(origin).len();
				t = distance/(float)width/2f;
				c1.set(p1.getPixel(i, j));
				c2.set(p2.getPixel(i, j));
				result.setColor(c1.lerp(c2, t));
				result.drawPixel(i, j);
			}
		}
	}
}
