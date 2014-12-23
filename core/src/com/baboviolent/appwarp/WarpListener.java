package com.baboviolent.appwarp;

import com.badlogic.gdx.math.Vector3;

public interface WarpListener {
	
	public void onWaitingStarted(String message);
	
	public void onError(String message);
	
	public void onGameStarted(String[] usernamese);
	
	public void onGameFinished(int code, boolean isRemote);
	
	public void onDirectionReceived(String username, Vector3 direction);
	
	public void onPositionReceived(String username, Vector3 position);
	
	public void onTargetReceived(String username, Vector3 target);
	
	public void onShootReceived(String username, boolean shoot);
	
	public void onDeadReceived(String username, String killer);
	
	public void onSynchronizationReceived(String username, Vector3 position, Vector3 target, Vector3 direction, Vector3 velocity, boolean shoot);
}
