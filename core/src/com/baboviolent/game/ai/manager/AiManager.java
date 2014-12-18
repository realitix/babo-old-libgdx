package com.baboviolent.game.ai.manager;

import com.baboviolent.game.ai.AiBabo;
import com.baboviolent.game.ai.pfa.tiled.flat.BaboPathGenerator;
import com.baboviolent.game.ai.pfa.tiled.flat.FlatTiledGraph;
import com.baboviolent.game.ai.pfa.tiled.flat.FlatTiledManhattanDistance;
import com.baboviolent.game.ai.pfa.tiled.flat.FlatTiledNode;
import com.baboviolent.game.ai.pfa.tiled.flat.FlatTiledSmoothableGraphPath;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class AiManager {
	private Array<Babo> babos;
	private BaboPathGenerator pathGenerator;
	
	public AiManager(Array<Babo> babos, Map map) {
		this.babos = babos;
		pathGenerator = new BaboPathGenerator(map);
	}
	
	public void test(AiBabo ai1, Babo ai2) {
		// On test le suivi de chemin
		Array<Vector3> waypoints = pathGenerator.getPath(ai1.getPosition(), ai2.getPosition());
        LinePath<Vector3> linePath = new LinePath<Vector3>(waypoints, true);
        FollowPath<Vector3, LinePathParam> followPathSB = new FollowPath<Vector3, LinePathParam>(ai1, linePath) //
			// Setters below are only useful to arrive at the end of an open path
			.setTimeToTarget(1f) //
			.setArrivalTolerance(1f) //
			.setDecelerationRadius(0)
			.setPredictionTime(0)
			.setPathOffset(10);
        
		ai1.setSteeringBehavior(followPathSB);
	}
}
