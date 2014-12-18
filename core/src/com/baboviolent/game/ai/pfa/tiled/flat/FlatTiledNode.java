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

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

/** A node for a {@link FlatTiledGraph}.
*/
public class FlatTiledNode implements IndexedNode<FlatTiledNode> {
	private final int index;
	
	/** The x coordinate of this tile */
	public final int x;

	/** The y coordinate of this tile */
	public final int y;

	/** The type of this tile, see {@link #TILE_EMPTY}, {@link #TILE_FLOOR} and {@link #TILE_WALL} */
	public final String type;
	
	protected Array<Connection<FlatTiledNode>> connections;

	@Override
	public int getIndex () {
		return index;
	}
	
	public FlatTiledNode (int index, int x, int y, String type, int connectionCapacity) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.type = type;
		this.connections = new Array<Connection<FlatTiledNode>>(connectionCapacity);
	}

	public Array<Connection<FlatTiledNode>> getConnections () {
		return this.connections;
	}
}
