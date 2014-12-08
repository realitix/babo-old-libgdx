package com.baboviolent.game.mode;

import com.baboviolent.appwarp.WarpController;
import com.baboviolent.appwarp.WarpListener;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DeathMatchMultiplayerMode extends DeathMatchMode implements WarpListener {
	private WarpController wc;
	
	public DeathMatchMultiplayerMode(final String mapName) {
		super(mapName);
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
        wc.sendActionGameUpdate(WarpController.ACTION_DIRECTION, angle);
    }
    
    protected Babo initBabo(String username) {
    	return super.initBabo(username);
    }

    public void onWaitingStarted(String message) {
        
    }
	
	public void onError(String message) {
	    
	}
	
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
	
	public void onGameFinished(int code, boolean isRemote) {
	    
	}
	
	public void onActionGameUpdateReceived(String type, String value, String username) {
		System.out.println("Recu. Angle: "+value);
		if( type.equals(WarpController.ACTION_DIRECTION) ) {
			for( int i = 0; i < babos.size; i++ ) {
				if( babos.get(i).getUsername().equals(username) ) {
					float angle = Float.parseFloat(value);
					Vector2 tmp = new Vector2(1, 0).rotate(angle).nor();
					Vector3 dir = new Vector3(tmp.x, 0, tmp.y);
					if( angle < 0 ) {
						dir.set(0, 0, 0);
					}
					babos.get(i).setDirection(dir);
				}
			}
		}
	}
}