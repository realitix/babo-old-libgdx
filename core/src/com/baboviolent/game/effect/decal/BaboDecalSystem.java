package com.baboviolent.game.effect.decal;

import com.baboviolent.game.camera.BaboCamera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;

public class BaboDecalSystem {	
	private BaboCamera camera;
	private Cursor cursor;
	private Blood blood;
	
	public BaboDecalSystem(BaboCamera camera) {
		this.camera = camera;
		initSystem();
	}
	
	private void initSystem() {
		cursor = new Cursor(camera);
		blood = new Blood();
	}
	
	public void update() {
		cursor.update();
	}
	
	public void render(DecalBatch decalBatch) {
		blood.render(decalBatch);
	}
	
	public void renderCursor(DecalBatch decalBatch) {
		cursor.render(decalBatch);
	}
	
	public void cursorHit() {
		cursor.hit();
	}
	
	public void generateBlood(Vector3 position, float damage) {
		blood.generateBlood(position, damage);
	}
}
