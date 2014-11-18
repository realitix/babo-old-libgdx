package com.baboviolent.game.map;

import com.baboviolent.game.bullet.BulletEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

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
	private float sizeCell = 20; // Taille du coté d'une cellule
	
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
        Mesh cellMesh = meshBuilder.end();
		
		// Création du model avec un modelbuilder et ajout de toutes les cellules
		ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        
        for(int i = 0; i < cells.length; i++) {
            Node node = modelBuilder.node();
            node.id = "cell"+cells[i].getNumber();
            node.translation = cells[i].getPosition().cpy();
            modelBuilder.part(
            	cellMesh.parts.get(0), 
            	materials.get(cells[i].getType());
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
