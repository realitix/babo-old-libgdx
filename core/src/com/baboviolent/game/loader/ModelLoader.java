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

public class ModelLoader {
    
    /**
     * Renvoie un tableau contenant le nom de tous les modèles
     */ 
    static public Array<String> listModelFolder() {
        Array<String> models = new Array<String>();
        
        String dir;
        if (Gdx.app.getType() == ApplicationType.Android) dir = BaboViolentGame.PATH_MODEL;
        else dir = BaboViolentGame.PATH_MODEL_DESKTOP;
	    
	    FileHandle[] files = Gdx.files.internal(dir).list();
        for(FileHandle file: files) {
            models.add(file.nameWithoutExtension());
        }
        
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
       return ModelLoader.getModel(ModelLoader.listModelFolder());
    }
    
    /**
     * Charge les modèles passés en paramètre
     */ 
    static public ObjectMap<String, Model> getModels(Array<String> toLoad) {
        ObjectMap<String, Model> models = new ObjectMap<String, Model>;
	    ObjectMap<String, ModelData> modelDatas = ModelLoader.getModelDatas(toLoad);
        
        for (ObjectMap.Entry<String, ModelData> d : modelDatas.entries()) {
            models.put(d.key, new Model(d.value));
        }
        
        return models;
    }
    
    /**
     * Charge tous les modèles
     */ 
    static public ObjectMap<String, ModelData> getModelDatas() {
	    return ModelLoader.getModelDatas(ModelLoader.listModelFolder());
    }
    
    /**
     * Charge tous les modèles d'une map'
     */ 
    static public ObjectMap<String, ModelData> getModelDatasFromMap(Map map) {
	    return ModelLoader.getModelDatas(ModelLoader.listModelMap(map));
    }
    
    /**
     * Charge tous les modèles passés en paramètre
     */ 
    static public ObjectMap<String, ModelData> getModelDatas(Array<String> toLoad) {
    	ModelLoader loader = new G3dModelLoader();
        model = loader.loadModel(Gdx.files.internal("data/ship.obj"));
        String p = BaboViolentGame.PATH_MODEL;
	    ObjectMap<String, ModelData> models = new ObjectMap<String, ModelData>();
	    
	    // On charge les modèles
	    for( int i = 0; i < toLoad.size; i++ ) {
	        models.put(
	            toLoad.get(i),
	            loader.loadModelData(new FileHandle(toLoad.get(i)+".j3dj"))
	        );
	    }
	    
	    return models;
    }
    
}