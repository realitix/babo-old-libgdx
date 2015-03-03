package com.baboviolent.game.bullet.instance.map.shader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration;
import com.baboviolent.game.gdx.texture.BaboTextureBinder;
import com.baboviolent.game.loader.BaboAssetManager;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapShader implements Shader {
	public static final int MAX_DIRLIGHTS = 2;
	public static final int MAX_POINTLIGHTS = 10;
	
	private int quality;
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
	private int u_cameraPosition;
	
	// Lights
	private int u_dirLights0color;
	private int u_dirLights0direction;
	private int u_dirLights1color;
	private int u_pointLights0color;
	private int u_pointLights0position;
	private int u_pointLights0intensity;
	private int u_pointLights1color;
	private int u_numCurrPointLights;
	private int u_numCurrDirectionalLights;
	
	private int dirLightsLoc;
	private int dirLightsColorOffset;
	private int dirLightsDirectionOffset;
	private int dirLightsSize;
	private int pointLightsLoc;
	private int pointLightsColorOffset;
	private int pointLightsPositionOffset;
	private int pointLightsIntensityOffset;
	private int pointLightsSize;
	
	
	private TextureAtlas diffuseAtlas;
	// On a pas besoin de l'atlas pour les autres car ce sont les meme uv que diffuse
	private TextureDescriptor<Texture> normalDescriptor;
	private TextureDescriptor<Texture> specularityDescriptor;
	private TextureDescriptor<Texture> perlinNoise;
	
	private Matrix4 textureUvs = new Matrix4();
	
	public MapShader(int quality) {
		this.quality = quality;
		diffuseAtlas = BaboAssetManager.getAtlas("mapDiffuse");
		normalDescriptor = new TextureDescriptor<Texture>(BaboAssetManager.getAtlas("mapNormal").getTextures().first());
		specularityDescriptor = new TextureDescriptor<Texture>(BaboAssetManager.getAtlas("mapSpecularity").getTextures().first());
		perlinNoise = new TextureDescriptor<Texture>(new Texture("data/texture/other/perlin_noise.png"));
		updateUvs();
	}
	
	public void updateUvs() {
		String s1 = "pavement";
		String s2 = "grass";
				
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
	
	private String createPrefix() {
		String prefix = "#define MIN "+Configuration.MIN+"\n";
		prefix += "#define MED "+Configuration.MED+"\n";
		prefix += "#define MAX "+Configuration.MAX+"\n";
		prefix += "#define QUALITY "+Configuration.getQuality(quality)+"\n\n";
		return prefix;
	}
	
	@Override
	public void init() {
		String p = BaboViolentGame.PATH_SHADERS;
		String prefix = createPrefix();
        String vert = Gdx.files.internal(p+"/map.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/map.fragment.glsl").readString();
        program = new ShaderProgram(prefix+vert, prefix+frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        u_projTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_diffuseAtlas = program.getUniformLocation("u_diffuseAtlas");
        u_normalAtlas = program.getUniformLocation("u_normalAtlas");
        u_specularityAtlas = program.getUniformLocation("u_specularityAtlas");
        u_alphaMap = program.getUniformLocation("u_alphaMap");
        u_tillSize = program.getUniformLocation("u_tillSize");
        u_mapSize = program.getUniformLocation("u_mapSize");
        u_textureUvs = program.getUniformLocation("u_textureUvs");
        u_shadowMapProjViewTrans = program.getUniformLocation("u_shadowMapProjViewTrans");
        u_shadowTexture = program.getUniformLocation("u_shadowTexture");
        u_shadowPCFOffset = program.getUniformLocation("u_shadowPCFOffset");
        u_cameraPosition = program.getUniformLocation("u_cameraPosition");
        
        // Lights
        u_dirLights0color = program.getUniformLocation("u_dirLights[0].color");
    	u_dirLights0direction = program.getUniformLocation("u_dirLights[0].direction");
    	u_dirLights1color = program.getUniformLocation("u_dirLights[1].color");
    	u_pointLights0color = program.getUniformLocation("u_pointLights[0].color");
    	u_pointLights0position = program.getUniformLocation("u_pointLights[0].position");
    	u_pointLights0intensity = program.getUniformLocation("u_pointLights[0].intensity");
    	u_pointLights1color = program.getUniformLocation("u_pointLights[1].color");
    	u_numCurrDirectionalLights = program.getUniformLocation("u_numCurrDirectionalLights");
    	u_numCurrPointLights = program.getUniformLocation("u_numCurrPointLights");
    	
    	dirLightsLoc = u_dirLights0color;
		dirLightsColorOffset = u_dirLights0color - dirLightsLoc;
		dirLightsDirectionOffset = u_dirLights0direction - dirLightsLoc;
		dirLightsSize = u_dirLights1color - dirLightsLoc;
		if (dirLightsSize < 0) dirLightsSize = 0;

		pointLightsLoc = u_pointLights0color;
		pointLightsColorOffset = u_pointLights0color - pointLightsLoc;
		pointLightsPositionOffset = u_pointLights0position - pointLightsLoc;
		pointLightsIntensityOffset = u_pointLights0intensity - pointLightsLoc;
		pointLightsSize = u_pointLights1color - pointLightsLoc;
		if (pointLightsSize < 0) pointLightsSize = 0;
	}	

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		
		program.begin();
		program.setUniformMatrix(u_projTrans, camera.combined);
		program.setUniformf(u_tillSize, 0.025f, 0.025f);
		program.setUniformf(u_mapSize, GroundMesh.MAP_SIZE.x, GroundMesh.MAP_SIZE.y);
		program.setUniformMatrix(u_textureUvs, textureUvs);
		program.setUniformf(u_cameraPosition, camera.position);
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
		program.setUniformi(u_normalAtlas, context.textureBinder.bind(normalDescriptor));
		program.setUniformi(u_specularityAtlas, context.textureBinder.bind(specularityDescriptor));
		//program.setUniformi(u_ambientAtlas, context.textureBinder.bind(ambientAtlas));
		program.setUniformi(u_alphaMap, context.textureBinder.bind(perlinNoise));
		
		bindLights(renderable);
		
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
	
	protected void bindLights (final Renderable renderable) {
		final Environment e = renderable.environment;
		final Array<DirectionalLight> dirs = e.directionalLights;
		final Array<PointLight> points = e.pointLights;
		int nbDirectionalLights = 0;
		int nbPointLights = 0;
		
		// Directional lights
		for( int i = 0; i < dirs.size; i++ ) {
			if( i >= MAX_DIRLIGHTS )
				continue;
			
			DirectionalLight l = dirs.get(i);
			int idx = dirLightsLoc + i * dirLightsSize;
			program.setUniformf(idx + dirLightsColorOffset, l.color.r, l.color.g, l.color.b);
			program.setUniformf(idx + dirLightsDirectionOffset, l.direction);
			nbDirectionalLights++;
		}
		
		// Point lights
		for( int i = 0; i < points.size; i++ ) {
			if( i >= MAX_POINTLIGHTS )
				continue;
			
			PointLight l = points.get(i);
			int idx = pointLightsLoc + i * pointLightsSize;
			program.setUniformf(idx + pointLightsColorOffset, l.color.r, l.color.g, l.color.b);
			program.setUniformf(idx + pointLightsPositionOffset, l.position);
			program.setUniformf(idx + pointLightsIntensityOffset, l.intensity);
			nbPointLights++;
		}
		
		program.setUniformi(u_numCurrDirectionalLights, nbDirectionalLights);
		program.setUniformi(u_numCurrPointLights, nbPointLights);
		
		// Shadow
		if( Configuration.Video.enableShadow && e.shadowMap != null ) {
			BaboTextureBinder tb = (BaboTextureBinder) context.textureBinder;
			program.setUniformMatrix(u_shadowMapProjViewTrans, e.shadowMap.getProjViewTrans());
			program.setUniformi(u_shadowTexture, tb.bind(e.shadowMap.getDepthMap(), true));
			program.setUniformf(u_shadowPCFOffset, 1.f / (float)(2f * e.shadowMap.getDepthMap().texture.getWidth()));
		}
	}
}
