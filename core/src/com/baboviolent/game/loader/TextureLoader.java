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

public class TextureLoader {
    
    /**
     * Renvoie un tableau contenant le nom de toutes les textures de sol
     */ 
    static public Array<String> listTextureGroundFolder() {
        Array<String> textures = new Array<String>();	    
	    FileHandle[] files = Gdx.files.internal(BaboViolentGame.path(BaboViolentGame.PATH_TEXTURE_GROUND)).list();
        for(FileHandle file: files) {
            textures.add(file.nameWithoutExtension());
        }
        textures.sort();
        
        return textures;
    }
    
    /**
     * Renvoie un tableau contenant le nom de toutes les textures de mur
     */ 
    static public Array<String> listTextureWallFolder() {
        Array<String> textures = new Array<String>();	    
	    FileHandle[] files = Gdx.files.internal(BaboViolentGame.path(BaboViolentGame.PATH_TEXTURE_WALL)).list();
        for(FileHandle file: files) {
            textures.add(file.nameWithoutExtension());
        }
        textures.sort();
        
        return textures;
    }
    
    /**
     * Renvoie un tableau contenant le nom de toutes les textures dans une map
     */ 
    static public Array<String> listTextureMap(final Map map) {
        Array<String> textures = new Array<String>();
        
	    for(int i = 0; i < map.getCells().size; i++) {
	        String type = map.getCells().get(i).getTextureName();
	        if( !textures.contains(type, false) ) {
	            textures.add(type);
	        }
	    }
	    textures.sort();
        
        return textures;
    }
    
    /**
     * Charge toutes les textures du sol
     */ 
    static public ObjectMap<String, Texture> getGroundTextures() {
	    return TextureLoader.getGroundTextures(TextureLoader.listTextureGroundFolder());
    }
    
    /**
     * Charge toutes les textures des murs
     */ 
    static public ObjectMap<String, Texture> getWallTextures() {
	    return TextureLoader.getWallTextures(TextureLoader.listTextureWallFolder());
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
	        manager.update();
		    manager.finishLoading();
		    textures.put(
		            toLoad.get(i),
		            manager.get(p+toLoad.get(i)+".png", Texture.class));
	    }
	    
	    return textures;
    }
    
    /**
     * Charge toutes les textures du sol passées en paramètre
     */ 
    static public ObjectMap<String, Texture> getWallTextures(Array<String> toLoad) {
    	AssetManager manager = new AssetManager();
        String p = BaboViolentGame.PATH_TEXTURE_WALL;
	    ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
	    
	    // On charge les textures
	    for( int i = 0; i < toLoad.size; i++ ) {
	        manager.load(p+toLoad.get(i)+".png", Texture.class);
	        manager.update();
		    manager.finishLoading();
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
        return TextureLoader.getGroundMaterials(TextureLoader.listTextureGroundFolder());
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
     * Charge tous les material de mur passés en paramètre
     */ 
    static public ObjectMap<String, Material> getWallMaterials(Array<String> toLoad) {
        ObjectMap<String, Texture> textures = TextureLoader.getWallTextures(toLoad);
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
        return TextureLoader.getGroundModels(TextureLoader.listTextureGroundFolder());
	}
    
    /**
     * Charge tous les modèles de mur
     */ 
    static public ObjectMap<String, Model> getWallModels() {
        return TextureLoader.getWallModels(TextureLoader.listTextureWallFolder());
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
                0, 0, s,
		        s, 0, s,
		        s, 0, 0,
		        0, 1, 0,
		        e.value,
		        //new Material(ColorAttribute.createDiffuse(Color.GREEN)),
		        Usage.Position | Usage.Normal | Usage.TextureCoordinates
				//		Usage.Position | Usage.Normal
            ));
        }
        
	    return models;
	}
    
    /**
     * Charge tous les modèles de mur passés en paramètre
     */ 
    static public ObjectMap<String, Model> getWallModels(Array<String> toLoad) {
        ObjectMap<String, Material> materials = TextureLoader.getWallMaterials(toLoad);
        ObjectMap<String, Model> models = new ObjectMap<String, Model>();
        ModelBuilder mb = new ModelBuilder();
        float s = BaboViolentGame.SIZE_MAP_CELL;
        
        for (ObjectMap.Entry<String, Material> e : materials.entries()) {
        	Model box =  mb.createBox(s, s, s, e.value, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        	// On déplace le cub pour le mettre au niveau du sol
        	box.meshes.get(0).transform(new Matrix4(new Vector3(s/2, s/2, s/2), new Quaternion(), new Vector3(1,1,1)));
            models.put(e.key, box);
        }
        
	    return models;
	}
}