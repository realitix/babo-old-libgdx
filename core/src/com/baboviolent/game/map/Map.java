package com.baboviolent.game.map;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.loader.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Map
 * 
 * Une map est définie par des celulles et des objets
 * Les champs width et height indiquent le nombre de cellules en largeur et en hauteur
 * 
 * Comment générer le modèle de la map ?
 * On créé un model vide.
 * 
 * Tout d'abord, on génère le sol:
 * On génère un mesh et mesh part représentant une cellule
 * On parcourt les cellules et pour chaque cellule:
 * 1 - on ajoute le "material" au modèle si non existant dans le modèle
 * 2 - On créer un nouveau node et nodepart référencant le material et le meshpart et on applique la translation
 * 
 * Ensuite on génère les objets:
 * Pour chaque objets:
 * 1 - Si le modèle n'est pas déjà chargé, on le charge et on copie "material" et "mesh" dans le modèle source
 * 2 - On créer un nouveau node référencant le mesh part et on applique les transformation dans l'objet
 * 
 * @author realitix
 *
 */
public class Map {
	public static final String TYPE_ERASER = "type_eraser";
	public static final String TYPE_GROUND = "type_ground";
	public static final String TYPE_WALL = "type_wall";
	public static final String TYPE_OBJECT = "type_object";
	
	private String name;
	private String author;
	private int version;
	private Array<Cell> cells = new Array<Cell>();
	private Array<MapObject> objects = new Array<MapObject>();
	
	
	/**
	 * Créer une instance à partir du nom de la map
	*/
	static public BulletInstance loadInstance (String mapname) {
        return Map.loadInstance(Map.loadModel(mapname));
	}
	
	/**
	 * Créer une instance à partir d'une map'
	*/
	static public BulletInstance loadInstance (Map map) {
        return Map.loadInstance(Map.loadModel(map));
	}
	
	/**
	 * Créer une instance à partir d'un modèle'
	*/
	static public BulletInstance loadInstance (Model model) {
        btBvhTriangleMeshShape shape = new btBvhTriangleMeshShape(model.meshParts);
        btRigidBody.btRigidBodyConstructionInfo constructionInfo = 
            new btRigidBody.btRigidBodyConstructionInfo(0, null, shape);
            
        return new BulletInstance(model, constructionInfo);
	}
	
	/**
	 * Créer un modèle à partir du nom
	*/
	static public Model loadModel (String mapname) {
        return Map.loadModel(Map.load(mapname));
	}
	
	/**
	 * Transforme la map en modèle
	 */ 
	static public Model loadModel(Map map) {
	    // Chargement des textures du sol
	    ObjectMap<String, Material> materials = TextureLoader.getGroundMaterialsFromMap(map);
	    
	    // Création du mesh cellule
		MeshBuilder meshBuilder = new MeshBuilder();
		meshBuilder.begin(Usage.Position | Usage.Normal, GL20.GL_TRIANGLES);
		float s = BaboViolentGame.SIZE_MAP_CELL;
		meshBuilder.rect(
		    new Vector3(0, 0, 0),
		    new Vector3(s, 0, 0),
		    new Vector3(s, 0, s),
		    new Vector3(0, 0, s),
		    new Vector3(0, 1, 0)
		);
		MeshPart cellMeshPart = meshBuilder.getMeshPart();
        meshBuilder.end();
		
		// Création du model avec un modelbuilder et ajout de toutes les cellules
		ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        
        Array<Cell> cells = map.getCells();
        for(int i = 0; i < map.getCells().size; i++) {
            Node node = modelBuilder.node();
            node.id = "cell"+i;
            node.translation.set(cells.get(i).getPosition());
            modelBuilder.part(
            	cellMeshPart, 
            	materials.get(cells.get(i).getTextureName())
            );
        }
        
        Model model = modelBuilder.end();
        
        // Fusion des objets dans le modèle
        ObjectMap<String, ModelData> modelDatas = BaboModelLoader.getModelDatasFromMap(map);
        for (ObjectMap.Entry<String, ModelData> d : modelDatas.entries()) {
            Utils.mergeModelDataToModel(d.value, model);
        }
        
        return model;
	}
	
	/**
	 * Charge la map
	 **/
	static public Map load(String mapname) {
		FileHandle file = Gdx.files.internal(BaboViolentGame.PATH_MAPS+mapname+".json");
		String jsonMap = file.readString();
		Json json = new Json();
		Map map = json.fromJson(Map.class, jsonMap);
		return map;
	}
	
	/**
	 * Sauvegarde la map
	 **/
	public static void save(Map map, String mapname) {
		//FileHandle file = Gdx.files.internal(BaboViolentGame.PATH_MAPS+mapname+".json");
		FileHandle file = Gdx.files.external(""+mapname+".json");
		Json json = new Json();
		json.setOutputType(OutputType.json);
		json.toJson(map, file);
	}

	public String getName() {
		return name;
	}

	public Map setName(String name) {
		this.name = name;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public Map setAuthor(String author) {
		this.author = author;
		return this;
	}

	public int getVersion() {
		return version;
	}

	public Map setVersion(int version) {
		this.version = version;
		return this;
	}
	
	public Map addCell(Cell cell) {
		// Test que la cellule n'est pas déjà dedans avant de l'ajouter
		for(int i = 0; i < cells.size; i++) {
			if(cells.get(i).equals(cell)) {
				return this;
			}
		}
		
		// Si la position est la même, on écrase la cellule du dessous
		for(int i = 0; i < cells.size; i++) {
			if(cells.get(i).equalsPosition(cell)) {
				cells.set(i, cell);
				return this;
			}
		}
		
		this.cells.add(cell);
		return this;
	}
	
	public Array<Cell> getCells() {
	    return cells;
	}
	
	public Map removeCell(Vector3 position) {
		for( int i = 0; i < cells.size; i++) {
			if(cells.get(i).getPosition().equals(position)) {
				cells.removeIndex(i);
			}
		}
		return this;
	}
	
	public Map addObject(MapObject m) {
		this.objects.add(m);
		return this;
	}
	
	public Array<MapObject> getObjects() {
	    return objects;
	}
	
	public Map removeObject(Vector3 position) {
		for( int i = 0; i < objects.size; i++) {
			if(objects.get(i).getPosition().equals(position)) {
				objects.removeIndex(i);
			}
		}
		return this;
	}
}
