package com.baboviolent.game.gdx.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Cette classe va permettre de ne binder aucune texture pendant le jeu
 *
 */
public class BaboTextureBinder implements  TextureBinder {

	public final static int OFFSET = 1;
	public final static int MAX_GLES_UNITS = 32;
	
	private final static ObjectMap<GLTexture, Integer> caches = new ObjectMap<GLTexture, Integer>(MAX_GLES_UNITS);
	private final static TextureDescriptor<GLTexture> tempDesc = new TextureDescriptor();
	private static int count = OFFSET;
	
	@Override
	public void begin() {
		// Static donc vide
	}

	@Override
	public void end() {
		// Static donc vide
	}
	
	// STATIC
	
	public final static int bindStatic(final TextureDescriptor textureDescriptor) {
		final GLTexture texture = textureDescriptor.texture;
		if( !caches.containsKey(texture) ) {
			if( count >= MAX_GLES_UNITS ) {
				throw new GdxRuntimeException("Can't bind more than "+MAX_GLES_UNITS+" textures");
			}
			
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + count);
			texture.bind(count);
			texture.unsafeSetWrap(textureDescriptor.uWrap, textureDescriptor.vWrap);
			texture.unsafeSetFilter(textureDescriptor.minFilter, textureDescriptor.magFilter);
			caches.put(texture, count);
			count++;
		}
		return caches.get(texture);
	}
	
	// NON STATIC
	@Override
	public final int bind(final TextureDescriptor textureDescriptor) {
		return bindStatic(textureDescriptor);
	}
	
	@Override
	public final int bind (final GLTexture texture) {
		tempDesc.set(texture, null, null, null, null);
		return bind(tempDesc);
	}

	public final int bind(
			Texture texture,
			TextureFilter minFilter,
			TextureFilter magFilter,
			TextureWrap uWrap,
			TextureWrap vWrap) {
		tempDesc.set(texture, minFilter, magFilter, uWrap, vWrap);
		return bind(tempDesc);
	}

	@Override
	public int getBindCount() {
		return 0;
	}

	@Override
	public int getReuseCount() {
		return 0;
	}

	@Override
	public void resetCounts() {
	}

}
