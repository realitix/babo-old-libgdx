package com.baboviolent.game.bullet.instance.map.shader;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapShader implements Shader {
	public final static String RANGE_ATTRIBUTE = "a_range";
	
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	private int u_projTrans;
	private int u_worldTrans;
	private int u_diffuseTexture;
	private int u_shadowMapProjViewTrans;
	private int u_shadowTexture;
	private int u_shadowPCFOffset;
	private int i;
	
	@Override
	public void init() {
		String p = BaboViolentGame.PATH_SHADERS;
        String vert = Gdx.files.internal(p+"/map.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/map.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        u_projTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_diffuseTexture = program.getUniformLocation("u_diffuseTexture");
        u_shadowMapProjViewTrans = program.getUniformLocation("u_shadowMapProjViewTrans");
        u_shadowTexture = program.getUniformLocation("u_shadowTexture");
        u_shadowPCFOffset = program.getUniformLocation("u_shadowPCFOffset");
	}	

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		
		program.begin();
		program.setUniformMatrix(u_projTrans, camera.combined);
	}

	@Override
	public void render(Renderable renderable) {
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);
		context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		context.setDepthMask(true);
		
		TextureDescriptor<Texture> t = ((TextureAttribute)(renderable.material
				.get(TextureAttribute.Diffuse))).textureDescription;
		
		
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		program.setUniformi(u_diffuseTexture, context.textureBinder.bind(t));
		
		Environment e = renderable.environment;
		if( e.shadowMap != null ) {
			program.setUniformMatrix(u_shadowMapProjViewTrans, e.shadowMap.getProjViewTrans());
			program.setUniformi(u_shadowTexture, context.textureBinder.bind(e.shadowMap.getDepthMap()));
			program.setUniformf(u_shadowPCFOffset, 1.f / (float)(2f * e.shadowMap.getDepthMap().texture.getWidth()));
		}
		
		
		/*set(u_shadowMapProjViewTrans, lights.shadowMap.getProjViewTrans());
		set(u_shadowTexture, lights.shadowMap.getDepthMap());
		set(u_shadowPCFOffset, 1.f / (float)(2f * lights.shadowMap.getDepthMap().texture.getWidth()));*/
		
		renderable.mesh.render(program,
		renderable.primitiveType,
		renderable.meshPartOffset,
		renderable.meshPartSize);
	}

	@Override
	public void end() {
		program.end();
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		if( instance.userData.equals("map") ) {
			return true;
		}
		return false;
	}
	
	@Override
	public void dispose() {
		program.dispose();
	}
}
