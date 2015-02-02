package com.baboviolent.game.effect.decal;


import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.camera.BaboCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class Cursor {
	private Decal cursor;
	private Decal cursorHit;
	private Vector3 tmpV3 = new Vector3();
	private final BaboCamera camera;
	private long lastHit;
	private boolean hit;
	private long timeHit = 3000;
	private float rHit;
	private float gHit;
	private float bHit;
	private float aHit;
	
	public Cursor(BaboCamera camera) {
		this.camera = camera;
		
		float size = 1f;
		cursor = Decal.newDecal(size, size, new TextureRegion(
        		new Texture(Gdx.files.internal(BaboViolentGame.PATH_TEXTURE_OTHERS+"Cross01.png"))),
        		true);
		cursorHit = Decal.newDecal(size, size, new TextureRegion(
        		new Texture(Gdx.files.internal(BaboViolentGame.PATH_TEXTURE_OTHERS+"CrossHit.png"))),
        		true);
		
        cursor.setRotation(Vector3.Y, Vector3.Y);
        cursorHit.setRotation(Vector3.Y, Vector3.Y);
        
        rHit = 1;
    	gHit = 0.2f;
    	bHit = 0.2f;
    	aHit = 1;
    	cursorHit.setColor(rHit, gHit, bHit, aHit);
	}
	
	public void hit() {
		hit = true;
		lastHit = TimeUtils.millis();
		aHit = 1;
		cursorHit.setColor(rHit, gHit, bHit, aHit);
	}
	
	public void update() {
    	tmpV3.set(camera.getTarget());
    	tmpV3.y = BaboViolentGame.SIZE_MAP_CELL;
    	cursor.setPosition(tmpV3);
    	
    	if( hit ) {
    		long t = TimeUtils.timeSinceMillis(lastHit);
    		if( t >= timeHit ) {
    			hit = false;
    		}
    		
    		cursorHit.setPosition(tmpV3);
    		aHit = 1f - (float)t/(float)timeHit;
    		cursorHit.setColor(rHit, gHit, bHit, aHit);
    	}
	}
	
	public void render(DecalBatch decalBatch) {
		update();
		decalBatch.add(cursor);
		
		if( hit ) {
			decalBatch.add(cursorHit);
		}
		decalBatch.flush();
	}
}
