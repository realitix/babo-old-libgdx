package com.baboviolent.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.listener.UpdateRequestListener;

public class UpdateListener  implements UpdateRequestListener {

	private WarpController controller;
	
	public UpdateListener(WarpController controller) {
		this.controller = controller;
	}
	
	@Override
	public void onSendPrivateUpdateDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendUpdateDone(byte arg0) {
		controller.onSendUpdateDone(arg0);
	}

}
