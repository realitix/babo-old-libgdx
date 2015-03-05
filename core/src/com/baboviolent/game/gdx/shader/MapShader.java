package com.baboviolent.game.gdx.shader;

import com.baboviolent.game.bullet.instance.map.shader.GroundMesh;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Matrix4;

public class MapShader extends BaboShader {
	
	private int u_alphaMap;
	private int u_mapSize;
	private int u_tillSize;
	private int u_textureUvs;
	
	private Matrix4 textureUvs = new Matrix4();
	
	private TextureDescriptor<Texture> perlinNoise;
	
	public MapShader(int quality) {
		super(quality);
		perlinNoise = new TextureDescriptor<Texture>(new Texture("data/texture/other/perlin_noise.png"));
	}

	public void updateUvs(TextureAtlas atlas) {
		String s1 = "pavement";
		String s2 = "grass";
				
		// Texture 1
		textureUvs.val[Matrix4.M00] = atlas.findRegion(s1).getU2();
		textureUvs.val[Matrix4.M10] = atlas.findRegion(s1).getU();
		textureUvs.val[Matrix4.M20] = atlas.findRegion(s1).getV2();
		textureUvs.val[Matrix4.M30] = atlas.findRegion(s1).getV();
		
		// Texture 2
		textureUvs.val[Matrix4.M01] = atlas.findRegion(s2).getU2();
		textureUvs.val[Matrix4.M11] = atlas.findRegion(s2).getU();
		textureUvs.val[Matrix4.M21] = atlas.findRegion(s2).getV2();
		textureUvs.val[Matrix4.M31] = atlas.findRegion(s2).getV();
	}
	
	@Override
	public void init() {
		init("map");
	}
	
	@Override
	public void init(String n) {
		super.init(n);
		
		u_alphaMap = program.getUniformLocation("u_alphaMap");
        u_tillSize = program.getUniformLocation("u_tillSize");
        u_mapSize = program.getUniformLocation("u_mapSize");
        u_textureUvs = program.getUniformLocation("u_textureUvs");
	}
	
	@Override
	public void begin(Camera camera, RenderContext context) {
		super.begin(camera, context);
		program.setUniformf(u_tillSize, 0.025f, 0.025f);
		program.setUniformf(u_mapSize, GroundMesh.MAP_SIZE.x, GroundMesh.MAP_SIZE.y);
		program.setUniformMatrix(u_textureUvs, textureUvs);
	}
	
	@Override
	public void render(Renderable renderable) {
		super.render(renderable);
		program.setUniformi(u_alphaMap, context.textureBinder.bind(perlinNoise));
	}
	
	@Override
	public boolean canRender(Renderable instance) {
		if( instance.userData.equals("map") ) {
			return true;
		}
		return false;
	}
}
