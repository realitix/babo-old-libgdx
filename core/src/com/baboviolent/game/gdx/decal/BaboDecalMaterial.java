package com.baboviolent.game.gdx.decal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;

public class BaboDecalMaterial {
	public static final int NO_BLEND = -1;
	protected TextureRegion textureRegion;
	protected int srcBlendFactor;
	protected int dstBlendFactor;
	private final TextureDescriptor<GLTexture> tempDesc = new TextureDescriptor();

	/** Binds the material's texture to the OpenGL context and changes the glBlendFunc to the values used by it. */
	public final int set (RenderContext context) {
		Texture t = textureRegion.getTexture();
		tempDesc.set(t, t.getMinFilter(), t.getMagFilter(), t.getUWrap(), t.getVWrap());
		final int number = context.textureBinder.bind(tempDesc);
		if (!isOpaque()) {
			Gdx.gl.glBlendFunc(srcBlendFactor, dstBlendFactor);
		}
		return number;
	}

	/** @return true if the material is completely opaque, false if it is not and therefor requires blending */
	public boolean isOpaque () {
		return srcBlendFactor == NO_BLEND;
	}

	public int getSrcBlendFactor () {
		return srcBlendFactor;
	}

	public int getDstBlendFactor () {
		return dstBlendFactor;
	}

	@Override
	public boolean equals (Object o) {
		if (o == null) return false;

		BaboDecalMaterial material = (BaboDecalMaterial)o;

		return dstBlendFactor == material.dstBlendFactor && srcBlendFactor == material.srcBlendFactor
			&& textureRegion.getTexture() == material.textureRegion.getTexture();

	}

	@Override
	public int hashCode () {
		int result = textureRegion.getTexture() != null ? textureRegion.getTexture().hashCode() : 0;
		result = 31 * result + srcBlendFactor;
		result = 31 * result + dstBlendFactor;
		return result;
	}
}
