package com.baboviolent.game.ai.pfa.tiled.flat;

import com.baboviolent.game.map.Map;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BaboPathGenerator {
	FlatTiledGraph worldMap;
	FlatTiledSmoothableGraphPath path;
	FlatTiledManhattanDistance heuristic;
	IndexedAStarPathFinder<FlatTiledNode> pathFinder;
	PathSmoother<FlatTiledNode, Vector2> pathSmoother;
	
	public BaboPathGenerator(Map map) {
		worldMap = new FlatTiledGraph(map);
		path = new FlatTiledSmoothableGraphPath();
		heuristic = new FlatTiledManhattanDistance();
		pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(worldMap, true);
	}
	
	public Array<Vector3> getPath(Vector3 begin, Vector3 end) {
		Array<Vector3> resultPath = new Array<Vector3>();
		pathFinder.searchNodePath(
			worldMap.getCloserGroundNode(begin),
			worldMap.getCloserGroundNode(end),
			heuristic,
			path);
		
		if( pathFinder.metrics != null ) {
			for( int i = 0; i < path.nodes.size; i++ ) {
				FlatTiledNode node = path.nodes.get(i);
				resultPath.add(new Vector3(node.x, 0, node.y));
			}
		}
		return resultPath;
	}
	
}
