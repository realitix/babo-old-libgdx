package com.baboviolent.game.gdx.shader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration;
import com.baboviolent.game.bullet.instance.map.shader.GroundMesh;
import com.baboviolent.game.gdx.texture.BaboTextureBinder;
import com.baboviolent.game.loader.BaboAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BaboShader implements Shader {
	public static final int MAX_DIRLIGHTS = 2;
	public static final int MAX_POINTLIGHTS = 10;
	
	private int quality;
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	private int u_projTrans;
	private int u_worldTrans;
	private int u_normalMatrix;
	private int u_diffuseAtlas;
	private int u_normalAtlas;
	private int u_specularAtlas;
	private int u_shadowMapProjViewTrans;
	private int u_shadowTexture;
	private int u_shadowPCFOffset;
	private int u_cameraPosition;
	private int u_diffuseUVTransform;
	
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
	
	private Matrix3 normalMatrix = new Matrix3();
	
	public BaboShader(int quality) {
		this.quality = quality;
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
        String vert = Gdx.files.internal(p+"/babo.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/babo.fragment.glsl").readString();
        program = new ShaderProgram(prefix+vert, prefix+frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        u_projTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_normalMatrix = program.getUniformLocation("u_normalMatrix");
        u_diffuseAtlas = program.getUniformLocation("u_diffuseAtlas");
        u_normalAtlas = program.getUniformLocation("u_normalAtlas");
        u_specularAtlas = program.getUniformLocation("u_specularAtlas");
        u_shadowMapProjViewTrans = program.getUniformLocation("u_shadowMapProjViewTrans");
        u_shadowTexture = program.getUniformLocation("u_shadowTexture");
        u_shadowPCFOffset = program.getUniformLocation("u_shadowPCFOffset");
        u_cameraPosition = program.getUniformLocation("u_cameraPosition");
        u_diffuseUVTransform = program.getUniformLocation("u_diffuseUVTransform");
        
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
		program.setUniformf(u_cameraPosition, camera.position);
	}

	@Override
	public void render(Renderable renderable) {
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);
		context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		context.setDepthMask(true);
		
		TextureAttribute ta = (TextureAttribute)renderable.material.get(TextureAttribute.Diffuse);
		TextureDescriptor<Texture> d = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription;
		TextureDescriptor<Texture> n = ((TextureAttribute)renderable.material.get(TextureAttribute.Normal)).textureDescription;
		TextureDescriptor<Texture> s = ((TextureAttribute)renderable.material.get(TextureAttribute.Specular)).textureDescription;
		
		normalMatrix.set(renderable.worldTransform).inv().transpose();
		program.setUniformf(u_diffuseUVTransform, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		program.setUniformMatrix(u_normalMatrix, normalMatrix);
		program.setUniformi(u_diffuseAtlas, context.textureBinder.bind(d));
		program.setUniformi(u_normalAtlas, context.textureBinder.bind(n));
		program.setUniformi(u_specularAtlas, context.textureBinder.bind(s));

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
		return true;
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
