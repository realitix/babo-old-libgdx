package com.baboviolent.game.gdx.batch;

import com.baboviolent.game.bullet.instance.BulletInstance;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;

public class BaboModelBatch extends ModelBatch {

	public BaboModelBatch() {
		super();
	}

	public BaboModelBatch(FileHandle vertexShader, FileHandle fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	public BaboModelBatch(RenderableSorter sorter) {
		super(sorter);
	}

	public BaboModelBatch(RenderContext context, RenderableSorter sorter) {
		super(context, sorter);
	}

	public BaboModelBatch(RenderContext context, ShaderProvider shaderProvider,
			RenderableSorter sorter) {
		super(context, shaderProvider, sorter);
	}

	public BaboModelBatch(RenderContext context, ShaderProvider shaderProvider) {
		super(context, shaderProvider);
	}

	public BaboModelBatch(RenderContext context) {
		super(context);
	}

	public BaboModelBatch(ShaderProvider shaderProvider, RenderableSorter sorter) {
		super(shaderProvider, sorter);
	}

	public BaboModelBatch(ShaderProvider shaderProvider) {
		super(shaderProvider);
	}

	public BaboModelBatch(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	public void renderForShadow(Array<BulletInstance> renderableProviders) {
		for (final BulletInstance renderableProvider : renderableProviders)
			renderForShadow(renderableProvider);
	}
	
	public void renderForShadow(final BulletInstance renderableProvider) {
		final int offset = renderables.size;
		renderableProvider.getRenderablesForShadow(renderables, renderablesPool);
		for (int i = offset; i < renderables.size; i++) {
			Renderable renderable = renderables.get(i);
			renderable.shader = shaderProvider.getShader(renderable);
		}
	}
}
