package com.baboviolent.game.camera;

import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Interpolation;
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
	private final Vector3 desiredOffset = new Vector3(0, 1000, 0);
	/** Vecteur temporaire */
	private Vector3 tmp = new Vector3();
	private boolean loaded;
	private Vector3 target = new Vector3();
	private Vector3 startLook = new Vector3(); // Position initiale a cibler
	private Vector3 endLook = new Vector3(); // Position finale a cibler
	private float animationStep;
	private Interpolation interpolation;
	private float animationSpeed = 0.03f;
	
	public ChaseCamera2() {
		super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.far = 10000;
		this.near = 10;
		this.up.set(0,0,1);
		interpolation = new Interpolation.PowOut(3);
		loaded = false;
	}
	
	public void init(Babo babo) {
	    this.babo = babo;
	    this.babo.getInstance().transform.getTranslation(tmp);
	    position.set(tmp.x, desiredOffset.y, tmp.z + desiredOffset.z);
	    loaded = true;
	    update();
	}
	
	@Override
	public void update() {
		if( !loaded )
			return;
		
		this.up.set(0,0,1);
		
		if( animationStep >= 1 )
			return;
		
		tmp.set(startLook);
		tmp.interpolate(endLook, animationStep, interpolation);
		position.set(tmp.x, desiredOffset.y, tmp.z + desiredOffset.z);
		tmp.set(position);
		tmp.y = 0;
	    lookAt(tmp);
		animationStep += animationSpeed;
		super.update();		
	}
	
	public void setTarget(Vector3 t) {
		target.set(t);
		animationStep = animationSpeed;
		//babo.getInstance().transform.getTranslation(startLook);
		startLook.set(position);
		babo.getInstance().transform.getTranslation(tmp);
		endLook.set(tmp.lerp(target, 0.43f));
	}
}

