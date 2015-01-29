package com.baboviolent.game.effect.decal;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Blood {
	public static final int MAX_BLOOD = 500;
	
	private TextureAtlas bloodAtlas;
	private int bloodIndex;
	private Decal[] bloods;
	
	public Blood() {
		bloods = new Decal[MAX_BLOOD];
		bloodIndex = 0;
		String p = BaboViolentGame.PATH_PARTICLES;
		bloodAtlas = new TextureAtlas(p+"blood/atlas/blood.atlas");
	}
	
	public void render(DecalBatch decalBatch) {
		for( Decal blood : bloods ) {
			if( blood != null ) {
				decalBatch.add(blood);
			}
		}
	}
	
	/**
	 * Genere le sang
	 * @param position
	 * @param damage Compris entre 0 et 100 (la vie d'un babo)
	 */
	public void generateBlood(Vector3 position, float damage) {
		Vector2 tmpV2 = new Vector2();
		Vector3 pos = new Vector3();
		for( int i=0; i < damage/5; i++) {
			float maxDistance = damage/50f;
			float distanceX = MathUtils.random(-maxDistance, maxDistance);
			float distanceZ = MathUtils.random(-maxDistance, maxDistance);
			float distance = tmpV2.set(distanceX, distanceZ).len();
			float maxSize = 10f-(distance)/2.5f;
			float size = MathUtils.random(0.2f, maxSize);
			pos.set(position).add(distanceX,0,distanceZ);
			pos.y = 0;
			
			Decal blood = Decal.newDecal(size, size, bloodAtlas.getRegions().random(), true);
			blood.setPosition(pos);
			blood.setRotation(Vector3.Y, Vector3.Y);
			blood.setColor(MathUtils.random(0.25f, 0.5f), 0, 0, MathUtils.random(0.5f,1));
			
			if( bloodIndex >= MAX_BLOOD ) {
				bloodIndex = 0;
			}
			
			bloods[bloodIndex] = blood;
			bloodIndex++;
		}
	}
}
