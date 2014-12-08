package com.baboviolent.appwarp;

public interface WarpListener {
	
	public void onWaitingStarted(String message);
	
	public void onError(String message);
	
	public void onGameStarted(String message);
	
	public void onGameFinished(int code, boolean isRemote);
	
	public void onActionGameUpdateReceived(String type, String value, String username);
	
	public void onUserJoinedRoom(String username);
}
