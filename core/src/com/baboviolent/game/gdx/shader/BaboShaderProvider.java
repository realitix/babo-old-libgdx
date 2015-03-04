package com.baboviolent.game.gdx.shader;

import com.baboviolent.game.Configuration;
import com.baboviolent.game.gdx.attribute.ShaderAttribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;

public class BaboShaderProvider implements ShaderProvider {
	protected Array<Shader> shaders = new Array<Shader>();
	private Shader baboShader = null;

	@Override
	public Shader getShader (Renderable renderable) {
		Shader suggestedShader = renderable.shader;
		if (suggestedShader != null && suggestedShader.canRender(renderable)) return suggestedShader;
		
		if( renderable.material.has(ShaderAttribute.Type) &&
			((ShaderAttribute) renderable.material.get(ShaderAttribute.Type)).shader != ShaderAttribute.DEFAULT) {
			if( baboShader == null ) {
				baboShader = createBaboShader();
				baboShader.init();
			}
			return baboShader;
		}
		else {
			for (Shader s : shaders) {
				if (s.canRender(renderable)) return s;
			}
			Shader shader = createDefaultShader(renderable);
			shader.init();
			shaders.add(shader);
			return shader;
		}
	}
	
	private Shader createDefaultShader (final Renderable renderable) {
		return new DefaultShader(renderable, new DefaultShader.Config());
	}
	
	private Shader createBaboShader () {
		return new BaboShader(Configuration.Video.mapShaderQuality);
	}
	
	@Override
	public void dispose () {
		for (Shader shader : shaders) {
			shader.dispose();
		}
		shaders.clear();
	}
}
