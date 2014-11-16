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
	private int width; // Nombre de cellules en largeur
	private int height; // Nombre de cellules en hauteur
	private float sizeCell = 20; // Taille du coté d'une cellule
	
	public static Map load(String mapname) {
		FileHandle file = Gdx.files.internal("myfile.txt");
		String jsonMap = file.readString();
		Json json = new Json();
		Map map = json.fromJson(Map.class, jsonMap);
		return map;
	}
	
	public BulletEntity generateEntity() {
		Model model = new Model();
		
		
		BulletEntity instance = new BulletEntity();
		return instance;
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
	
}
