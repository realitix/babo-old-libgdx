package com.baboviolent.game.camera;

import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * A Camera that tends to chase an object in a natural way
 */
public class ChaseCamera2 extends PerspectiveCamera {
	/** Babo a suivre */
	private Babo babo;
	/** Distance par rapport à l'objet */ 
	private final Vector3 desiredOffset = new Vector3(0, 1000, -300);
	/** Vecteur temporaire */
	private Vector3 tmp = new Vector3();
	private boolean loaded;
	
	public ChaseCamera2(Babo babo) {
		super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.far = 10000;
		this.near = 10;
		loaded = false;
		init(babo);
		update();
	}
	
	public void init(Babo babo) {
	    this.babo = babo;
	    this.babo.getInstance().transform.getTranslation(tmp);
	    position.set(tmp.x, tmp.y + desiredOffset.y, tmp.z + desiredOffset.z);
	    loaded = true;
	}
	
	@Override
	public void update() {
		if( !loaded )
			return;
		
		float delta = Gdx.graphics.getDeltaTime();
		Matrix4 transform = babo.getInstance().transform;
	    transform.getTranslation(tmp);
	    //lookAt(tmp.lerp(babo.getTarget(), 0.5f));
	    lookAt(tmp);
	    position.set(tmp.x, tmp.y + desiredOffset.y, tmp.z + desiredOffset.z);
		super.update();		
	}
}

