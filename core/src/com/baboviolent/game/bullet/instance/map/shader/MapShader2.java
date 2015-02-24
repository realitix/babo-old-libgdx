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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapShader2 implements Shader {
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	private int u_projTrans;
	private int u_worldTrans;
	private int u_diffuseAtlas;
	private int u_normalAtlas;
	private int u_specularityAtlas;
	private int u_alphaMap;
	private int u_mapSize;
	private int u_tillSize;
	private int u_textureUvs;
	private int u_shadowMapProjViewTrans;
	private int u_shadowTexture;
	private int u_shadowPCFOffset;
	private int u_lightDirection;
	
	private TextureAtlas diffuseAtlas;
	// On a pas besoin de l'tals pour les autres car ce sont les meme uv que diffuse
	private TextureDescriptor<Texture> normalAtlas;
	private TextureDescriptor<Texture> ambientAtlas;
	private TextureDescriptor<Texture> specularityAtlas;
	private TextureDescriptor<Texture> perlinNoise;
	
	private Matrix4 textureUvs = new Matrix4();
	
	public MapShader2() {
		diffuseAtlas = new TextureAtlas("data/atlas/map/diffuse.atlas");
		normalAtlas = new TextureDescriptor<Texture>(new Texture("data/atlas/map/normal.png"));
		ambientAtlas = new TextureDescriptor<Texture>(new Texture("data/atlas/map/ambient.png"));
		specularityAtlas = new TextureDescriptor<Texture>(new Texture("data/atlas/map/specularity.png"));
		perlinNoise = new TextureDescriptor<Texture>(new Texture("data/texture/other/perlin_noise.png"));
		updateUvs();
	}
	
	public void updateUvs() {
		String s1 = "grass";
		String s2 = "pavement";
				
		// Texture 1
		textureUvs.val[Matrix4.M00] = diffuseAtlas.findRegion(s1).getU2();
		textureUvs.val[Matrix4.M10] = diffuseAtlas.findRegion(s1).getU();
		textureUvs.val[Matrix4.M20] = diffuseAtlas.findRegion(s1).getV2();
		textureUvs.val[Matrix4.M30] = diffuseAtlas.findRegion(s1).getV();
		
		// Texture 2
		textureUvs.val[Matrix4.M01] = diffuseAtlas.findRegion(s2).getU2();
		textureUvs.val[Matrix4.M11] = diffuseAtlas.findRegion(s2).getU();
		textureUvs.val[Matrix4.M21] = diffuseAtlas.findRegion(s2).getV2();
		textureUvs.val[Matrix4.M31] = diffuseAtlas.findRegion(s2).getV();
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
        u_diffuseAtlas = program.getUniformLocation("u_diffuseAtlas");
        u_normalAtlas = program.getUniformLocation("u_normalAtlas");
        u_specularityAtlas = program.getUniformLocation("u_specularityAtlas");
        u_alphaMap = program.getUniformLocation("u_alphaMap");
        u_tillSize = program.getUniformLocation("u_tillSize");
        u_lightDirection = program.getUniformLocation("u_lightDirection");
        u_mapSize = program.getUniformLocation("u_mapSize");
        u_textureUvs = program.getUniformLocation("u_textureUvs");
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
		program.setUniformf(u_tillSize, 0.025f, 0.025f);
		program.setUniformf(u_mapSize, GroundMesh2.MAP_SIZE.x, GroundMesh2.MAP_SIZE.y);
		program.setUniformMatrix(u_textureUvs, textureUvs);
	}

	@Override
	public void render(Renderable renderable) {
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);
		context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		context.setDepthMask(true);
		
		TextureDescriptor<Texture> t = new TextureDescriptor<Texture>(diffuseAtlas.getRegions().get(0).getTexture());
		
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		program.setUniformi(u_diffuseAtlas, context.textureBinder.bind(t));
		program.setUniformi(u_normalAtlas, context.textureBinder.bind(normalAtlas));
		program.setUniformi(u_specularityAtlas, context.textureBinder.bind(specularityAtlas));
		program.setUniformi(u_alphaMap, context.textureBinder.bind(perlinNoise));
		
		Environment e = renderable.environment;
		program.setUniformf(u_lightDirection, e.directionalLights.get(0).direction);
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
