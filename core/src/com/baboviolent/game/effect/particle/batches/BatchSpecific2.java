package com.baboviolent.game.effect.particle.batches;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class BatchSpecific2 extends BaboParticleBatch {

	public BatchSpecific2(Camera camera, Texture texture) {
		super(camera, texture);
	}
	
	@Override
	protected Renderable allocRenderable() {
		Renderable renderable = super.allocRenderable();
		renderable.material = null;
		renderable.material = new Material(	new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 1f),
				new DepthTestAttribute(GL20.GL_LEQUAL, true),
				TextureAttribute.createDiffuse(texture));
		
		return renderable;
	}
}
