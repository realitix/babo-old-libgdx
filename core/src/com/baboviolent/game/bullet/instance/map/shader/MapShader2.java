package com.baboviolent.game.bullet.instance.map.shader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.gdx.texture.BaboTextureBinder;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapShader2 implements Shader {
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	private int u_projTrans;
	private int u_worldTrans;
	private int u_diffuseTexture;
	private int u_alphaMap;
	private int u_mapSize;
	private int u_tillSize;
	private int u_texture0UV;
	private int u_texture1UV;
	private int u_shadowMapProjViewTrans;
	private int u_shadowTexture;
	private int u_shadowPCFOffset;
	private int u_center;
	private int u_screenSize;
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
        u_mapSize = program.getUniformLocation("u_mapSize");
        u_texture0UV = program.getUniformLocation("u_texture0UV");
        u_texture1UV = program.getUniformLocation("u_texture1UV");
        u_shadowMapProjViewTrans = program.getUniformLocation("u_shadowMapProjViewTrans");
        u_shadowTexture = program.getUniformLocation("u_shadowTexture");
        u_shadowPCFOffset = program.getUniformLocation("u_shadowPCFOffset");
        
        u_center = program.getUniformLocation("u_center");
        u_screenSize = program.getUniformLocation("u_screenSize");
	}	

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		
		program.begin();
		program.setUniformMatrix(u_projTrans, camera.combined);
		program.setUniformf(u_tillSize, 0.02f, 0.02f);
		program.setUniformf(u_mapSize, GroundMesh2.MAP_SIZE.x, GroundMesh2.MAP_SIZE.y);
		program.setUniformf(u_center, camera.position.x, camera.position.z);
		
		Vector3 sToW1 = camera.unproject(new Vector3(0,0,0));
		Vector3 sToW2 = camera.unproject(new Vector3(Gdx.graphics.getWidth(),0,Gdx.graphics.getHeight()));
		
		program.setUniformf(u_screenSize, Math.abs(sToW1.x - sToW2.x)/2f, Math.abs(sToW1.z - sToW2.z)/2f);
		
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
			BaboTextureBinder tb = (BaboTextureBinder) context.textureBinder;
			program.setUniformMatrix(u_shadowMapProjViewTrans, e.shadowMap.getProjViewTrans());
			program.setUniformi(u_shadowTexture, tb.bind(e.shadowMap.getDepthMap(), true));
			program.setUniformf(u_shadowPCFOffset, 1.f / (float)(2f * e.shadowMap.getDepthMap().texture.getWidth()));
		}
		
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
