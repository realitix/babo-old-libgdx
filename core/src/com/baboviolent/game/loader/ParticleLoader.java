package com.baboviolent.game.loader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ParticleLoader {
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
    	String p = BaboViolentGame.path(BaboViolentGame.PATH_PARTICLES);
        Array<String> particles = new Array<String>();	    
	    FileHandle[] files = Gdx.files.internal(p).list();
        for(FileHandle file: files) {
            particles.add(file.nameWithoutExtension());
        }
        
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
    	String p = BaboViolentGame.path(BaboViolentGame.PATH_PARTICULES);
    	AssetManager manager = new AssetManager();
	    ObjectMap<String, PoolParticle> particles = new ObjectMap<String, PoolParticle>();
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(ParticleSystem.get().getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        assets.setLoader(ParticleEffect.class, loader);
	    
	    // On charge les particules
	    for( int i = 0; i < toLoad.size; i++ ) {
	        manager.load(p+toLoad.get(i)+".pfx", ParticleEffect.class, loadParam);
		    manager.finishLoading();
		    ParticleEffect effect = manager.get(p+toLoad.get(i)+".pfx");
		    particles.put(toLoad.get(i), new PoolParticle(effect));
	    }
	    
	    return particles;
    }
}