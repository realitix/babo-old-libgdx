package com.baboviolent.game.bullet.instance.map.shader;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapShader2 implements Shader {
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	private int u_projTrans;
	private int u_worldTrans;
	private int u_diffuseTexture;
	private int u_alphaMap;
	private int u_tillSize;
	private int u_texture0UV;
	private int u_texture1UV;
	private int u_shadowMapProjViewTrans;
	private int u_shadowTexture;
	private int u_shadowPCFOffset;
	private TextureAtlas atlas;
	private TextureDescriptor<Texture> perlinNoise;
	
	public MapShader2() {
		atlas = new TextureAtlas("data/texture/ground/atlas/ground.atlas");
		perlinNoise = new TextureDescriptor<Texture>(new Texture("data/texture/other/perlin_noise.png"));
	}
	
	@Override
	public void init() {
		String p = BaboViolentGame.PATH_SHADERS;
        String vert = Gdx.files.internal(p+"/map2.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/map2.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        u_projTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_diffuseTexture = program.getUniformLocation("u_diffuseTexture");
        u_alphaMap = program.getUniformLocation("u_alphaMap");
        u_tillSize = program.getUniformLocation("u_tillSize");
        u_texture0UV = program.getUniformLocation("u_texture0UV");
        u_texture1UV = program.getUniformLocation("u_texture1UV");
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
		program.setUniformf(u_tillSize, 0.02f, 0.02f);
		String s1 = "city_1";
		String s2 = "city_2";
		program.setUniformf(u_texture0UV,
				atlas.findRegion(s1).getU2(),
				atlas.findRegion(s1).getV2(),
				atlas.findRegion(s1).getU(),
				atlas.findRegion(s1).getV());
		program.setUniformf(u_texture1UV,
				atlas.findRegion(s2).getU2(),
				atlas.findRegion(s2).getV2(),
				atlas.findRegion(s2).getU(),
				atlas.findRegion(s2).getV());
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
		program.setUniformi(u_alphaMap, context.textureBinder.bind(perlinNoise));
		
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
