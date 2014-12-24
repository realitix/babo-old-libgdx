package com.baboviolent.game.loader;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;

public class BaboModelLoader {
	public static final String MODEL_EXTENSION = ".g3dj";
   
    /**
     * Renvoie un tableau contenant le nom de tous les modèles
     */ 
    static public Array<String> listModelFolder() {
    	Array<String> models = new Array<String>();	
    	
    	for( int i = 0; i < Constant.models.length; i++) {
    		models.add(Constant.models[i]);
    	}
    	
    	models.sort();
        
        return models;
    }
    
    /**
     * Renvoie un tableau contenant le nom de tous les modèles dans une map
     */ 
    static public Array<String> listModelMap(final Map map) {
        Array<String> models = new Array<String>();
        
	    for(int i = 0; i < map.getObjects().size; i++) {
	        String type = map.getObjects().get(i).getType();
	        if( !models.contains(type, false) ) {
	            models.add(type);
	        }
	    }
        
        return models;
    }
    
    /**
     * Charge tous les modèles
     */ 
    static public ObjectMap<String, Model> getModels() {
       return BaboModelLoader.getModels(BaboModelLoader.listModelFolder());
    }
    
    /**
     * Charge le modèle en paramètre
     */ 
    static public Model getModel(String toLoad) {
    	Array<String> a = new Array<String>();
    	a.add(toLoad);
       return getModels(a).get(toLoad);
    }
    
    /**
     * Charge les modèles passés en paramètre
     */ 
    static public ObjectMap<String, Model> getModels(Array<String> toLoad) {
        ObjectMap<String, Model> models = new ObjectMap<String, Model>();
        ObjectMap<String, ModelData> modelDatas = getModelDatas(toLoad);
        
        for (ObjectMap.Entry<String, ModelData> m : modelDatas.entries()) {
	        models.put(m.key, new Model(m.value));
        }
        
        return models;
    }
    
    /**
     * Charge tous les modèles
     */ 
    static public ObjectMap<String, ModelData> getModelDatas() {
	    return BaboModelLoader.getModelDatas(BaboModelLoader.listModelFolder());
    }
    
    /**
     * Charge tous les modèles d'une map'
     */ 
    static public ObjectMap<String, ModelData> getModelDatasFromMap(Map map) {
	    return BaboModelLoader.getModelDatas(BaboModelLoader.listModelMap(map));
    }
    
    /**
     * Charge le modèle en paramètre
     */ 
    static public ModelData getModelDatas(String toLoad) {
    	Array<String> a = new Array<String>();
    	a.add(toLoad);
	    return getModelDatas(a).get(toLoad);
    }
    
    /**
     * Charge tous les modèles passés en paramètre
     */ 
    static public ObjectMap<String, ModelData> getModelDatas(Array<String> toLoad) {
    	ModelLoader loader = new G3dModelLoader(new JsonReader());
        String p = BaboViolentGame.PATH_MODELS;
	    ObjectMap<String, ModelData> models = new ObjectMap<String, ModelData>();
	    
	    // On charge les modèles
	    for( int i = 0; i < toLoad.size; i++ ) {
	        models.put(
	            toLoad.get(i),
	            loader.loadModelData(Gdx.files.internal(p+toLoad.get(i)+MODEL_EXTENSION))
	        );
	    }
	    
	    return models;
    }
    
}