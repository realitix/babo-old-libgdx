package com.baboviolent.game.loader;

import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class MapAssetLoader {
    private String textureGroundPath = "data/texture/ground/";
	private Map map;
	private AssetManager assetManager;
	
	public MapAssetLoader(Map map) {
	    this.map = map;
	    assetManager = new AssetManager();
	}
	
	private ObjectMap<String, Material> loadGroundMaterials() {
	    Array<String> texturesToLoad = getTexturesToLoad();
	    ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
	    ObjectMap<String, Material> materials = new ObjectMap<String, Material>();
	    
	    // On charge les textures
	    for( int i = 0; i < texturesToLoad.size; i++ ) {
	        manager.load(textureGroundPath+texturesToLoad.get(i)+".png", Texture.class);
	    }
	    manager.update();
	    manager.finishLoading();
	    
	    // On charge les material avec les textures
	    for( int i = 0; i < texturesToLoad.size; i++ ) {
	        // On charge la texture
	        Texture = manager.get(textureGroundPath+texturesToLoad.get(i)+".png", Texture.class);
	        
	        // On insert la texture dans un Attribut
	        TextureAttribute textureAttribute = = new TextureAttribute(TextureAttribute.Diffuse, texture);
	        
	        // On insert l'attribut dans un material
	        Material material = new Material(textureAttribute);
	        
	        // On inset le material dans la liste
	        materials.put(texturesToLoad.get(i), material);
	    }
	    
	    return materials;
	}
	
	private Array<String> getTexturesToLoad() {
	    Array<String> texturesToLoad = new Array<String>();
	    for(int i = 0; i < map.getCells().size; i++) {
	        String type = map.getCells().get(i).getType();
	        if( !texturesToLoad.contains(type) ) {
	            texturesToLoad.add(type);
	        }
	    }
	    
	    return texturesToLoad;
	}
	
	public ObjectMap<String, Texture> getTextures() {
	    return textures;
	}
	
	public void test() {
		Map map = new Map()
			.setAuthor("Jean-seb")
			.setName("test map")
			.setVersion(1)
			.addCell(new Cell().setType("sable").setPosition(new Vector3(5, 4, 3)))
			.addCell(new Cell().setType("herbe"))
			;
		
		Json json = new Json();
		String text = json.toJson(map, Object.class);
		System.out.println(json.prettyPrint(text));
		
	}
}
