package com.baboviolent.appwarp;

import java.util.HashMap;
import java.util.Hashtable;

import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;

public class NotificationListener implements NotifyListener {

	private WarpController controller;
	
	public NotificationListener(WarpController controller) {
		this.controller = controller;
	}
	
	public void onChatReceived(ChatEvent event) {
		
	}

	public void onRoomCreated(RoomData arg0) {
		
	}

	public void onRoomDestroyed(RoomData arg0) {
		
	}

	public void onUpdatePeersReceived(UpdateEvent event) {
		controller.onGameUpdateReceived(new String(event.getUpdate()));
	}

	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		
	}

	public void onUserJoinedRoom(RoomData data, String username) {
		controller.onUserJoinedRoom(data.getId(), username);
	}

	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		
	}

	public void onUserLeftRoom(RoomData roomData, String userName) {
		controller.onUserLeftRoom(roomData.getId(), userName);
	}

	@Override
	public void onGameStarted (String arg0, String arg1, String arg2) {
		
	}
	
	@Override
	public void onGameStopped (String arg0, String arg1) {
		
	}

	@Override
	public void onMoveCompleted (MoveEvent me) {
		
	}

	@Override
	public void onPrivateChatReceived (String arg0, String arg1) {
		
	}

	@Override
	public void onUserChangeRoomProperty (RoomData roomData, String userName, HashMap<String, Object> properties, HashMap<String, String> lockProperties) {
		int code = Integer.parseInt(properties.get("result").toString());
		controller.onResultUpdateReceived(userName, code);
	}

	@Override
	public void onUserPaused (String arg0, boolean arg1, String arg2) {
		
	}

	@Override
	public void onUserResumed (String arg0, boolean arg1, String arg2) {
		
	}
	
}
