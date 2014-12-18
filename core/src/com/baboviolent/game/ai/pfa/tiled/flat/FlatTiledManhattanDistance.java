package com.baboviolent.game.ai.pfa.tiled.flat;

import com.badlogic.gdx.ai.pfa.Heuristic;

/** A Manhattan distance heuristic for a {@link TiledGraph}. It simply calculates the Manhattan distance between two given
 * tiles.
 * 
 * @param <N> Type of node, either flat or hierarchical, extending the {@link TiledNode} class
 * 
 * @author davebaol */
public class FlatTiledManhattanDistance implements Heuristic<FlatTiledNode> {

	public FlatTiledManhattanDistance () {
	}

	@Override
	public float estimate (FlatTiledNode node, FlatTiledNode endNode) {
		return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
	}
}
