package com.baboviolent.game.mode;

import com.baboviolent.appwarp.WarpController;
import com.baboviolent.appwarp.WarpListener;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class DeathMatchMultiplayerMode extends DeathMatchMode implements WarpListener {
	private WarpController wc;
	private Vector3 lastTarget;
	private long lastSynchronization = 0;
	private final long synchronizationInterval = 1000;
	private long lastTargetSent = 0;
	private final long targetInterval = 1000;
	
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
        wc.sendDirection(direction);
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
    
    private Babo initEnnemy(String username) {
    	return super.initBabo(username, false).setManualDeath(true);
    }
    
    public void update() {
    	super.update();
    	updateSynchonization();
    	updatePlayerTarget();
    }
    
    /**
     * On envoie les donnees de synchro regulirement
     */
    private void updateSynchonization() {
    	if( TimeUtils.millis() - lastSynchronization > synchronizationInterval ) {
    		lastSynchronization = TimeUtils.millis();
    		Vector3 position = player.getPosition();
    		Vector3 target = player.getTarget();
    		Vector3 direction = player.getDirection();
    		Vector3 velocity = player.getLinearVelocity();
    		boolean shoot = player.getShoot();
    		wc.sendSynchronization(position, target, direction, velocity, shoot);
    	}
    }
    
    /**
     * Quand le joueur change sa cible (Bouge la souris)
     * On envoie l'info
     */
    private void updatePlayerTarget() {
    	if( TimeUtils.millis() - lastTargetSent > targetInterval ) {
    		lastTargetSent = TimeUtils.millis();
	    	Vector3 currentTarget = super.getTarget();
	    	if( !lastTarget.equals(currentTarget) ) {
	    		lastTarget.set(currentTarget);
	    		wc.sendTarget(currentTarget);
	    	}
    	}
    }
    
    @Override
    protected void updateBabos() {
    	super.updateBabos();
    	if( player.getState() == Babo.STATE_EXPLODE ) {
    		wc.sendDead(player.getLastShooter().getUsername());
    	}
    }
    
    private Babo getBaboFromUsername(String username) {
    	for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getUsername().equals(username) ) {
				return babos.get(i);
			}
    	}
    	return null;
    }

    @Override
    public void onWaitingStarted(String message) {
        
    }
	
    @Override
	public void onError(String message) {
	    System.out.println("Erreur AppWarp: "+message);
	}
	
	@Override
	public void onGameStarted(final String[] usernames) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				for( String username:usernames ) {
			    	if( !username.equals(player.getUsername()) ) {
			    		initEnnemy(username).appear(new Vector3());
			    	}
			    }
			}
		});
	}
	
	@Override
	public void onGameFinished(int code, boolean isRemote) {
	    
	}

	@Override
	public void onDirectionReceived(String username, Vector3 direction) {
		getBaboFromUsername(username).setDirection(direction);
	}

	@Override
	public void onPositionReceived(String username, Vector3 position) {
		getBaboFromUsername(username).teleport(position);
	}
	
	@Override
	public void onTargetReceived(String username, Vector3 target) {
		getBaboFromUsername(username).setTarget(target);
	}

	@Override
	public void onShootReceived(String username, boolean shoot) {
		getBaboFromUsername(username).setShoot(shoot);
	}
	
	@Override
	public void onDeadReceived(String username, String killer) {
		getBaboFromUsername(username).explode();
	}

	@Override
	public void onSynchronizationReceived(String username, Vector3 position, Vector3 target, Vector3 direction, Vector3 velocity, boolean shoot) {
		getBaboFromUsername(username)
			.teleport(position)
			.setTarget(target)
			.setDirection(direction)
			.setLinearVelocity(velocity)
			.setShoot(shoot);
	}
}