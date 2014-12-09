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
	
	public DeathMatchMultiplayerMode(final String mapName) {
		super(mapName);
		System.out.println("test to string "+new Vector3(2, 3, 4));
		nbIa = 0;
		super.init();
		wc = WarpController.getInstance();
		wc.setListener(this);
		wc.startApp(player.getUsername());
    }
    
    public void onSetPlayerDirection(Vector3 direction) {
    	super.onSetPlayerDirection(direction);
    	
    	String angle = "-1"; // Pas de déplacement
    	if( !direction.isZero() ) {
	    	angle = Float.toString(new Vector2(direction.x, direction.z).angle());
    	}
    	
    	System.out.println("Envoie. Angle: "+angle);
        wc.sendAction(WarpController.ACTION_DIRECTION, angle);
    }
    
    protected Babo initBabo(String username) {
    	return super.initBabo(username);
    }
    
    public void update() {
    	super.update();
    	updatePlayerStopMoving();
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
				babos.get(i).getInstance().transform.setToTranslation(position);
			}
		}
	}
}