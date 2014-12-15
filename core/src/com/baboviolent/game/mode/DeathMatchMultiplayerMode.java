package com.baboviolent.game.mode;

import com.baboviolent.appwarp.WarpController;
import com.baboviolent.appwarp.WarpListener;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DeathMatchMultiplayerMode extends DeathMatchMode implements WarpListener {
	private WarpController wc;
	private boolean playerPositionSent = false;
	private Vector3 lastTarget;
	
	public DeathMatchMultiplayerMode(final String mapName) {
		super(mapName);
		lastTarget = new Vector3();
		nbIa = 0;
    }
	
	public void init() {
		super.init();
		wc = WarpController.getInstance();
		wc.setListener(this);
		wc.startApp(player.getUsername());
	}
    
    public void onSetPlayerDirection(Vector3 direction) {
    	super.onSetPlayerDirection(direction);
    	
    	float angle = -1f; // Pas de déplacement
    	if( !direction.isZero() ) {
	    	angle = new Vector2(direction.x, direction.z).angle();
    	}
    	
        wc.sendDirection(angle);
    }
    
    @Override
    public void onStartShoot() {
    	super.onStartShoot();
    	wc.sendShoot(true);
    }
    
    @Override
    public void onStopShoot() {
    	super.onStopShoot();
    	wc.sendShoot(false);
    }
    
    protected Babo initBabo(String username) {
    	return super.initBabo(username);
    }
    
    public void update() {
    	super.update();
    	updatePlayerStopMoving();
    	updatePlayerTarget();
    }
    
    /**
     * Quand le joueur ne bouge plus, on envoie sa position aux autres pour se synchronizer
     */
    private void updatePlayerStopMoving() {
    	if( !player.isMoving() && !playerPositionSent ) {
    		System.out.println("Envoie de la position");
    		wc.sendPosition(player.getInstance().transform.getTranslation(tmpV));
    		playerPositionSent = true;
    	}
    	
    	if( player.isMoving() ) {
    		playerPositionSent = false;
    	}
    }
    
    /**
     * Quand le joueur change sa cible (Bouge la souris)
     * On envoie l'info
     */
    private void updatePlayerTarget() {
    	Vector3 currentTarget = super.getTarget();
    	if( !lastTarget.equals(currentTarget) ) {
    		lastTarget.set(currentTarget);
    		wc.sendTarget(currentTarget);
    	}
    }

    @Override
    public void onWaitingStarted(String message) {
        
    }
	
    @Override
	public void onError(String message) {
	    
	}
	
	@Override
	public void onGameStarted(final String[] usernames) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				for( String username:usernames ) {
			    	if( !username.equals(player.getUsername()) ) {
			    		initBabo(username);
			    	}
			    }
			}
		});
	}
	
	@Override
	public void onGameFinished(int code, boolean isRemote) {
	    
	}

	@Override
	public void onDirectionReceived(String username, float angle) {
		for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getUsername().equals(username) ) {
				Vector2 tmp = new Vector2(1, 0).rotate(angle).nor();
				Vector3 dir = new Vector3(tmp.x, 0, tmp.y);
				if( angle < 0 ) {
					dir.set(0, 0, 0);
				}
				babos.get(i).setDirection(dir);
			}
		}
	}

	@Override
	public void onPositionReceived(String username, Vector3 position) {
		for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getUsername().equals(username) ) {
				System.out.println("Mis a jour de la position");
				babos.get(i).teleport(position);
			}
		}
	}
	
	@Override
	public void onTargetReceived(String username, Vector3 target) {
		for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getUsername().equals(username) ) {
				babos.get(i).setTarget(target);
			}
		}
	}

	@Override
	public void onShootReceived(String username, boolean shoot) {
		for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getUsername().equals(username) ) {
				if( shoot ) {
					babos.get(i).shoot();
				}
				else {
					babos.get(i).stopShoot();
				}
			}
		}
	}
	
	@Override
	public void onDeadReceived(String username, String killer) {
		for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getUsername().equals(username) ) {
				babos.get(i).explode();
			}
		}
	}
}