package com.baboviolent.game.loader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.particle.PoolParticle;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ParticleLoader {
	public static final String PARTICLE_EXTENSION = ".pfx";
	
    /**
     * Initialise le système de particule
     */ 
    static public ParticleSystem init(Camera c) {
	    ParticleSystem particleSystem = ParticleSystem.get();
		BillboardParticleBatch b = new BillboardParticleBatch();
		b.setCamera(c);
		particleSystem.add(b);
		return particleSystem;
    }
    
    /**
     * Renvoie un tableau contenant le nom de toutes les particules
     */ 
    static public Array<String> listParticleFolder() {
    	Array<String> particles = new Array<String>();
    	
    	for( int i = 0; i < Constant.particles.length; i++) {
    		particles.add(Constant.particles[i]);
    	}
    	particles.sort();
        
        return particles;
    }
    
    /**
     * Charge toutes les particules
     */ 
    static public ObjectMap<String, PoolParticle> getParticles() {
	    return getParticles(listParticleFolder());
    }
    
    /**
     * Charge toutes les particules passées en paramètre
     */ 
    static public ObjectMap<String, PoolParticle> getParticles(Array<String> toLoad) {
    	String p = BaboViolentGame.PATH_PARTICLES;
    	AssetManager manager = new AssetManager();
	    ObjectMap<String, PoolParticle> particles = new ObjectMap<String, PoolParticle>();
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(ParticleSystem.get().getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        manager.setLoader(ParticleEffect.class, loader);
	    
	    // On charge les particules
	    for( int i = 0; i < toLoad.size; i++ ) {
	        manager.load(p+toLoad.get(i)+PARTICLE_EXTENSION, ParticleEffect.class, loadParam);
		    manager.finishLoading();
		    ParticleEffect effect = manager.get(p+toLoad.get(i)+PARTICLE_EXTENSION);
		    particles.put(toLoad.get(i), new PoolParticle(effect));
	    }
	    
	    return particles;
    }
}