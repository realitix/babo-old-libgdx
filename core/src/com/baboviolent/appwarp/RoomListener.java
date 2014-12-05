package com.baboviolent.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class RoomListener implements RoomRequestListener {
	
	private WarpController controller;
	
	public RoomListener(WarpController controller) {
		this.controller = controller;
	}
	
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		if(event.getResult() == WarpResponseResultCode.SUCCESS) {
			controller.onGetLiveRoomInfo(event.getJoinedUsers());
		}
		else {
			controller.onGetLiveRoomInfo(null);
		}
	}

	public void onJoinRoomDone(RoomEvent event) {
		controller.onJoinRoomDone(event);
	}

	public void onLeaveRoomDone(RoomEvent arg0) {
		
	}

	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
	}

	public void onSubscribeRoomDone(RoomEvent event) {
		if(event.getResult() == WarpResponseResultCode.SUCCESS) {
			controller.onRoomSubscribed(event.getData().getId());
		}
		else {
			controller.onRoomSubscribed(null);
		}
	}

	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		
	}

	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
	}

	@Override
	public void onLockPropertiesDone (byte result) {
		
	}

	@Override
	public void onUnlockPropertiesDone (byte arg0) {
		
	}

}
