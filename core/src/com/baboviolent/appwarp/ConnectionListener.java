package com.baboviolent.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class ConnectionListener implements ConnectionRequestListener {

	WarpController controller;
	
	public ConnectionListener(WarpController controller){
		this.controller = controller;
	}
	
	public void onConnectDone(ConnectEvent e) {
		if(e.getResult() == WarpResponseResultCode.SUCCESS){
			controller.onConnectDone(true);
		}else{
			controller.onConnectDone(false);
		}
	}

	public void onDisconnectDone(ConnectEvent e) {
		
	}

	@Override
	public void onInitUDPDone (byte result) {
		if(result == WarpResponseResultCode.SUCCESS){
			controller.isUDPEnabled = true;
		}
	}

}
