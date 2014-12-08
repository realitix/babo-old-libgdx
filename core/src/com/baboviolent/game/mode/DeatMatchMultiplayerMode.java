package com.baboviolent.game.mode;

public class DeathMatchMultiplayerMode extends DeathMatchMode implements WarpListener {
	
	public DeathMatchMultiplayerMode(final String mapName) {
		super(mapName);
		nbIa = 0;
		super.init();
		WarpController
		    .getInstance()
		    .setListener(this)
		    .startApp(player.getUsername());
    }
    
    public void onSetPlayerDirection(Vector3 direction) {
    	super.onSetPlayerDirection(direction);
    	
    	String angle = "-1"; // Pas de déplacement
    	if( !direction.isZero() ) {
	    	angle = Float.toString(new Vector2(direction.x, direction.z).angle());
    	}
    	
        WarpController
        	.getInstance()
        	.sendActionGameUpdate(WarpController.ACTION_DIRECTION, angle);
    }

    public void onWaitingStarted(String message) {
        
    }
	
	public void onError(String message) {
	    
	}
	
	public void onGameStarted(String[] usernames) {
	    for( String username:usernames ) {
	    	if( !username.equals() ) {
	    		super.initBabo(username);
	    	}
	    }
	}
	
	public void onGameFinished(int code, boolean isRemote) {
	    
	}
	
	public void onActionGameUpdateReceived(String type, String value, String username) {
		if( type.equals(WarpController.ACTION_DIRECTION) ) {
			for( int i = 0; i < babos.size; i++ ) {
				if( babos.get(i).getUsername().equals(username) ) {
					float angle = Float.parseFloat(value);
					Vector2 dir = new Vector2(1, 0).rotate(angle);
					babos.get(i).setDirection(new Vector3(dir.x, 0, dir.y));
				}
			}
		}
	}
}