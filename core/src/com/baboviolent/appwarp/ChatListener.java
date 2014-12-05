package com.baboviolent.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.listener.ChatRequestListener;

public class ChatListener implements ChatRequestListener{
	
	WarpController controller;
	
	public ChatListener(WarpController controller) {
		this.controller = controller;
	}

	public void onSendChatDone(byte result) {
		if(result==WarpResponseResultCode.SUCCESS){
			controller.onSendChatDone(true);
		}else{
			controller.onSendChatDone(false);
		}
	}

	@Override
	public void onSendPrivateChatDone (byte arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
