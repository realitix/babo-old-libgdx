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
     * Renvoie un tableau contenant le nom de toutes les textures en flnctione du type
     */ 
    static public Array<String> listTextureFolder(String type) {
    	String p;
    	if( type == Map.TYPE_GROUND )
    		p = BaboViolentGame.path(BaboViolentGame.PATH_TEXTURE_GROUND);
    	if( type == Map.TYPE_WALL )
    		p = BaboViolentGame.path(BaboViolentGame.PATH_TEXTURE_WALL);
    		
        Array<String> textures = new Array<String>();	    
	    FileHandle[] files = Gdx.files.internal(p).list();
        for(FileHandle file: files) {
            textures.add(file.nameWithoutExtension());
        }
        textures.sort();
        
        return textures;
    }
    
    /**
     * Renvoie un tableau contenant le nom des textures dans une map en fonction du type
     */ 
    static public Array<String> listTextureMap(final Map map, String type) {
        Array<String> textures = new Array<String>();
        
	    for(int i = 0; i < map.getCells().size; i++) {
	        String textureName = map.getCells().get(i).getTextureName();
	        if( !textures.contains(textureName, false) && map.getCells().get(i).getType() == type ) {
	            textures.add(textureName);
	        }
	    }
	    textures.sort();
        
        return textures;
    }
    
    /**
     * Charge toutes les textures en fonction du paramètre
     */ 
    static public ObjectMap<String, Texture> getTextures(String type) {
	    return getTextures(listTextureFolder(type), type);
    }
    
    /**
     * Charge toutes les textures passées en paramètre
     */ 
    static public ObjectMap<String, Texture> getTextures(Array<String> toLoad, String type) {
    	String p;
    	if( type == Map.TYPE_GROUND )
    		p = BaboViolentGame.path(BaboViolentGame.PATH_TEXTURE_GROUND);
    	if( type == Map.TYPE_WALL )
    		p = BaboViolentGame.path(BaboViolentGame.PATH_TEXTURE_WALL);
    	
    	AssetManager manager = new AssetManager();
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
     * Charge tous les material du type passé en paramètre
     */ 
    static public ObjectMap<String, Material> getMaterials(String type) {
        return getMaterials(listTextureFolder(type));
	}
	
	/**
     * Charge tous les material de la map
     */ 
	static public ObjectMap<String, Material> getMaterialsFromMap(final Map map) {
		ObjectMap<String, Material> materials = new ObjectMap<String, Material>();
		materials.putAll(getMaterials(listTextureMap(map, Map.TYPE_GROUND), Map.TYPE_GROUND));
		materials.putAll(getMaterials(listTextureMap(map, Map.TYPE_WALL), Map.TYPE_WALL));
	    return materials;
	}
    
    /**
     * Charge tous les material du type passés en paramètre
     */ 
    static public ObjectMap<String, Material> getMaterials(Array<String> toLoad, String type) {
        ObjectMap<String, Texture> textures = getTextures(toLoad, type);
        ObjectMap<String, Material> materials = new ObjectMap<String, Material>();
        
        for (ObjectMap.Entry<String, Texture> e : textures.entries()) {
	        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, e.value);
	        Material material = new Material(textureAttribute);
	        materials.put(e.key, material);
        }
        
	    return materials;
	}
	
	/**
     * Charge tous les modèles du type passé en paramètre
     */ 
    static public ObjectMap<String, Model> getModels(String type) {
        return getModels(listTextureFolder(type), type);
	}
	
	/**
     * Charge les modèles du type passés en paramètre
     */ 
    static public ObjectMap<String, Model> getGroundModels(Array<String> toLoad, String type) {
        ObjectMap<String, Material> materials = getMaterials(toLoad, type);
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
     * Charge tous les modèles du sol passés en paramètre
     */ 
    static public ObjectMap<String, Model> getGroundModels(Array<String> toLoad) {
        ObjectMap<String, Material> materials = TextureLoader.getGroundMaterials(toLoad);
        ObjectMap<String, Model> models = new ObjectMap<String, Model>();
        ModelBuilder mb = new ModelBuilder();
        float s = BaboViolentGame.SIZE_MAP_CELL;
        
        for (ObjectMap.Entry<String, Material> e : materials.entries()) {
        	Model m;
        	if( type == Map.TYPE_GROUND ) {
        		m = mb.createRect(0,0,0,0,0,s,s,0,s,s,0,0,0,1,0,e.value,Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        	}
        	if( type == Map.TYPE_WALL) {
        		m =  mb.createBox(s, s, s, e.value, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        		// On déplace le cub pour le mettre au niveau du sol
        		m.meshes.get(0).transform(new Matrix4(new Vector3(s/2, s/2, s/2), new Quaternion(), new Vector3(1,1,1)));
        	}
            models.put(e.key, m);
        }
        
	    return models;
	}
}