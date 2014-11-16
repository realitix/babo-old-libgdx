package com.baboviolent.game.loader;

import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class MapLoader {
	
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
	
	public static Map load(String mapname) {
		String mapjson = "";
		Json json = new Json();
		Map map = json.fromJson(Map.class, mapjson);
		return map;
	}
}
