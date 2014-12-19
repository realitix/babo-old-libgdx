package com.baboviolent.appwarp;

import com.badlogic.gdx.Gdx;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class ConnectionListener implements ConnectionRequestListener {

	WarpController controller;
	
	public ConnectionListener(WarpController controller){
		this.controller = controller;
	}
	
	@Override
	public void onConnectDone(ConnectEvent e) {
		if(e.getResult() == WarpResponseResultCode.SUCCESS){
			controller.onConnectDone(true);
		}else{
			Gdx.app.error("Babo", "Impossible de se connecter, code: "+e.getResult()+" raison: "+e.getReasonCode());
			controller.onConnectDone(false);
		}
	}
	
	@Override
	public void onDisconnectDone(ConnectEvent e) {
		
	}

	@Override
	public void onInitUDPDone (byte result) {
		if(result == WarpResponseResultCode.SUCCESS){
			controller.isUDPEnabled = true;
		}
		else {
			Gdx.app.error("Babo", "UDP non disponible, on passe en TCP");
		}
	}

}
