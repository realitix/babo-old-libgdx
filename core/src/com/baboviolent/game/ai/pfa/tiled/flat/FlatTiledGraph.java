/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.baboviolent.game.ai.pfa.tiled.flat;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector3;

public class FlatTiledGraph extends DefaultIndexedGraph<FlatTiledNode> implements IndexedGraph<FlatTiledNode> {
	public boolean diagonal;
	public FlatTiledNode startNode;

	public FlatTiledGraph (Map map) {
		super(map.getCells().size);
		this.diagonal = false;
		this.startNode = null;
		
		init(map);
	}
	
	public void init (Map map) {
		for( int i = 0; i < map.getCells().size; i++ ) {
			nodes.add(new FlatTiledNode(
				i,
				(int) map.getCells().get(i).getPosition().x,
				(int) map.getCells().get(i).getPosition().z,
				map.getCells().get(i).getType(),
				4));
		}

		// Each node has up to 4 neighbors
		for( int i = 0; i < map.getCells().size; i++ ) {
			float sizeConnection = (float) (BaboViolentGame.SIZE_MAP_CELL);
			for( int j = 0; j < map.getCells().size; j++ ) {
				Cell c1 = map.getCells().get(i);
				Cell c2 = map.getCells().get(j);
				
				if( c1.getPosition().sub(c2.getPosition()).len() <= sizeConnection  && 
					c1.getType().equals(Map.TYPE_GROUND) &&
					c2.getType().equals(Map.TYPE_GROUND) &&
					!c1.equals(c2)) {
					FlatTiledNode f1 = getNode(i);
					FlatTiledNode f2 = getNode(j);
					f1.getConnections().add(new FlatTiledConnection(this, f1, f2));
				}
			}
		}
	}

	public FlatTiledNode getNode (int index) {
		for( int i = 0; i < nodes.size; i++) {
			if( nodes.get(i).getIndex() == index ) {
				return nodes.get(i);
			}
		}
		return null;
	}
	
	public FlatTiledNode getCloserGroundNode (Vector3 position) {
		FlatTiledNode closer = null;
		float minDistance = 99999999;
		Vector3 tmp = new Vector3();
		
		for( int i = 0; i < nodes.size; i++) {
			if( nodes.get(i).type.equals(Map.TYPE_GROUND) ) {
				tmp.set(nodes.get(i).x, 0, nodes.get(i).y);
				float distance = position.dst2(tmp);
				if( distance < minDistance ) {
					minDistance = distance;
					closer = nodes.get(i);
				}
			}
		}
		
		return closer;
	}
}
