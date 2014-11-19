package com.baboviolent.game.map;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletInstance;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
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
	private String name;
	private String author;
	private int version;
	private Array<Cell> cells = new Array<Cell>();
	
	public static Map load(String mapname) {
		FileHandle file = Gdx.files.internal("myfile.txt");
		String jsonMap = file.readString();
		Json json = new Json();
		Map map = json.fromJson(Map.class, jsonMap);
		return map;
	}
	
	public BulletInstance generateInstance() {
	    // Chargement des textures du sol
	    ObjectMap<String, Material> materials = TextureLoader.getGroundMaterialsFromMap(this);
	    
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
        
        for(int i = 0; i < cells.size; i++) {
            Node node = modelBuilder.node();
            node.id = "cell"+i;
            node.translation.set(cells.get(i).getPosition());
            modelBuilder.part(
            	cellMeshPart, 
            	materials.get(cells.get(i).getType())
            );
        }
        
        Model mapModel = modelBuilder.end();
		
		return BulletInstance.createMap(mapModel);
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
		this.cells.add(cell);
		return this;
	}
	
	public Array<Cell> getCells() {
	    return cells;
	}
}
