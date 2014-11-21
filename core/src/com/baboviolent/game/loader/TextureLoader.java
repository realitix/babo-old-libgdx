package com.baboviolent.game.loader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class TextureLoader {
    
    /**
     * Renvoie un tableau contenant le nom de toutes les textures
     */ 
    static public Array<String> listTextureFolder() {
        Array<String> textures = new Array<String>();
        
        String dir;
        if (Gdx.app.getType() == ApplicationType.Android) dir = BaboViolentGame.PATH_TEXTURE_GROUND;
        else dir = BaboViolentGame.PATH_TEXTURE_GROUND_DESKTOP;
	    
	    FileHandle[] files = Gdx.files.internal(dir).list();
        for(FileHandle file: files) {
            textures.add(file.nameWithoutExtension());
        }
        
        return textures;
    }
    
    /**
     * Renvoie un tableau contenant le nom de toutes les textures dans une map
     */ 
    static public Array<String> listTextureMap(final Map map) {
        Array<String> textures = new Array<String>();
        
	    for(int i = 0; i < map.getCells().size; i++) {
	        String type = map.getCells().get(i).getType();
	        if( !textures.contains(type, false) ) {
	            textures.add(type);
	        }
	    }
        
        return textures;
    }
    
    /**
     * Charge toutes les textures du sol
     */ 
    static public ObjectMap<String, Texture> getGroundTextures() {
	    return TextureLoader.getGroundTextures(TextureLoader.listTextureFolder());
    }
    
    /**
     * Charge toutes les textures du sol passées en paramètre
     */ 
    static public ObjectMap<String, Texture> getGroundTextures(Array<String> toLoad) {
    	AssetManager manager = new AssetManager();
        String p = BaboViolentGame.PATH_TEXTURE_GROUND;
	    ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
	    
	    // On charge les textures
	    for( int i = 0; i < toLoad.size; i++ ) {
	        manager.load(p+toLoad.get(i)+".png", Texture.class);
	    }
	    manager.update();
	    manager.finishLoading();
	    
	    // On insère les texture dans l'array
	    for( int i = 0; i < toLoad.size; i++ ) {
	        textures.put(
	            toLoad.get(i),
	            manager.get(p+toLoad.get(i)+".png", Texture.class));
	    }
	    
	    return textures;
    }
    
    /**
     * Charge tous les material du sol
     */ 
    static public ObjectMap<String, Material> getGroundMaterials() {
        return TextureLoader.getGroundMaterials(TextureLoader.listTextureFolder());
	}
	
	/**
     * Charge tous les material de la map
     */ 
	static public ObjectMap<String, Material> getGroundMaterialsFromMap(final Map map) {
	    return TextureLoader.getGroundMaterials(TextureLoader.listTextureMap(map));
	}
    
    /**
     * Charge tous les material du sol passés en paramètre
     */ 
    static public ObjectMap<String, Material> getGroundMaterials(Array<String> toLoad) {
        ObjectMap<String, Texture> textures = TextureLoader.getGroundTextures(toLoad);
        ObjectMap<String, Material> materials = new ObjectMap<String, Material>();
        
        for (ObjectMap.Entry<String, Texture> e : textures.entries()) {
	        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, e.value);
	        Material material = new Material(textureAttribute);
	        materials.put(e.key, material);
        }
        
	    return materials;
	}
	
	/**
     * Charge tous les modèles du sol
     */ 
    static public ObjectMap<String, Model> getGroundModels() {
        return TextureLoader.getGroundModels(TextureLoader.listTextureFolder());
	}
	
	/**
     * Charge tous les modèles du sol passés en paramètre
     */ 
    static public ObjectMap<String, Model> getGroundModels(Array<String> toLoad) {
        ObjectMap<String, Material> materials = TextureLoader.getGroundMaterials(toLoad);
        ObjectMap<String, Model> models = new ObjectMap<String, Model>();
        ModelBuilder mb = new ModelBuilder();
        float s = BaboViolentGame.SIZE_MAP_CELL;
        
        for (ObjectMap.Entry<String, Material> e : materials.entries()) {
            models.put(e.key, mb.createRect(
                0, 0 ,0,
                s, 0, 0,
		        s, 0, s,
		        0, 0, s,
		        0, 1, 0,
		        e.value,
		        Usage.Position | Usage.Normal | Usage.TextureCoordinates
            ));
        }
        
	    return models;
	}
}