package com.baboviolent.appwarp;

import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONObject;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.shephertz.app42.gaming.multiplayer.client.Constants;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

public class WarpController {
    public static final String SEPARATOR = "#";
    
    // TYPES ACTIONS
    public static final String ACTION_DIRECTION = "a";
    public static final String ACTION_TARGET = "c";
    public static final String ACTION_STOP_TARGET = "d";
    public static final String ACTION_SHOOT = "e";
    public static final String ACTION_STOP_SHOOT = "f";
    public static final String ACTION_POSITION = "g";

	private static WarpController instance;
	
	private boolean showLog = true;
	
	private final String apiKey = "14a611b4b3075972be364a7270d9b69a5d2b24898ac483e32d4dc72b2df039ef";
	private final String secretKey = "55216a9a165b08d93f9390435c9be4739888d971a17170591979e5837f618059";
	
	private WarpClient warpClient;
	
	private String localUser;
	private String roomId;
	
	private boolean isConnected = false;
	boolean isUDPEnabled = false;
	
	private WarpListener warpListener ;
	
	private int STATE;
	
	// Game state constants
	public static final int WAITING = 1;
	public static final int STARTED = 2;
	public static final int COMPLETED = 3;
	public static final int FINISHED = 4;
	
	// Game completed constants
	public static final int GAME_WIN = 5;
	public static final int GAME_LOOSE = 6;
	public static final int ENEMY_LEFT = 7;
	
	public WarpController() {
		initAppwarp();
		warpClient.addConnectionRequestListener(new ConnectionListener(this));
		warpClient.addChatRequestListener(new ChatListener(this));
		warpClient.addZoneRequestListener(new ZoneListener(this));
		warpClient.addRoomRequestListener(new RoomListener(this));
		warpClient.addNotificationListener(new NotificationListener(this));
	}
	
	public static WarpController getInstance(){
		if(instance == null){
			instance = new WarpController();
		}
		return instance;
	}
	
	public void startApp(String localUser){
		this.localUser = localUser;
		warpClient.connectWithUserName(localUser);
	}
	
	public void setListener(WarpListener listener) {
		this.warpListener = listener;
	}
	
	public void stopApp() {
		if(isConnected){
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
		}
		warpClient.disconnect();
	}
	
	private void initAppwarp(){
		try {
			WarpClient.initialize(apiKey, secretKey);
			warpClient = WarpClient.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***********
	 * HELPER
	 */
	
	/**
	 * Fonctions d'envoies
	 */
	public void sendDirection(float angle){
		sendAction(ACTION_DIRECTION, Float.toString(angle));
	}
	
	public void sendPosition(Vector3 position){
		sendAction(ACTION_POSITION, vector3ToString(position));
	}
	
	public void sendTarget(Vector3 target){
		sendAction(ACTION_TARGET, vector3ToString(target));
	}
	
	public void sendShoot(boolean shoot){
		sendAction(ACTION_SHOOT, (shoot)?"1":"0");
	}
	
	public void sendAction(String type, String value){
		sendGameUpdate(type+SEPARATOR+value);
	}
	
	public void sendGameUpdate(String msg){
		if(isConnected){
			warpClient.sendUDPUpdatePeers((localUser+SEPARATOR+msg).getBytes());
		}
	}
	
	/**
	 * Fonctions de receptions
	 */
	public void onGameUpdateReceived(String message){
        String[] datas = message.split(SEPARATOR);
		String username = datas[0];
		String action = datas[1];
		String value = datas[2];
		
		if( !localUser.equals(username) ) {
	    	if( action.equals(ACTION_DIRECTION) ) {
	    		warpListener.onDirectionReceived(username, Float.parseFloat(value));
	    	}
	    	else if( action.equals(ACTION_TARGET) ) {
	    		warpListener.onTargetReceived(username, stringToVector3(value));
	    	}
	    	else if( action.equals(ACTION_POSITION) ) {
	    		warpListener.onPositionReceived(username, stringToVector3(value));
		    }
	    	else if( action.equals(ACTION_SHOOT) ) {
	    		warpListener.onShootReceived(username, (value.equals("1")) ? true : false);
		    }
		}
	}
	
	private String vector3ToString(Vector3 v) {
		return v.x+":"+v.y+":"+v.z;
	}
	
	private Vector3 stringToVector3(String s) {
		String[] ps = s.split(":");
		return new Vector3(
				Float.parseFloat(ps[0]),
				Float.parseFloat(ps[1]), 
				Float.parseFloat(ps[2]));
	}
	
	public void updateResult(int code, String msg){
		if(isConnected){
			STATE = COMPLETED;
			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put("result", code);
			warpClient.lockProperties(properties);
		}
	}
	
	public void onConnectDone(boolean status){
		log("onConnectDone: "+status);
		if(status){
			warpClient.initUDP();
			warpClient.joinRoomInRange(1, 1, false);
		}else{
			isConnected = false;
			handleError();
		}
	}
	
	public void onDisconnectDone(boolean status){
		
	}
	
	public void onRoomCreated(String roomId){
		if(roomId!=null){
			warpClient.joinRoom(roomId);
		}else{
			handleError();
		}
	}
	
	public void onJoinRoomDone(RoomEvent event){
		log("onJoinRoomDone: "+event.getResult());
		if( event.getResult() == WarpResponseResultCode.SUCCESS ) {
			this.roomId = event.getData().getId();
			warpClient.subscribeRoom(roomId);
		}
		else if( event.getResult() == WarpResponseResultCode.RESOURCE_NOT_FOUND ) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("result", "");
			warpClient.createRoom("superjumper", "shephertz", 2, data);
		}
		else {
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onRoomSubscribed(String roomId){
		log("onSubscribeRoomDone: "+roomId);
		if(roomId!=null){
			isConnected = true;
			warpClient.getLiveRoomInfo(roomId);
		}else{
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onGetLiveRoomInfo(String[] liveUsers){
		log("onGetLiveRoomInfo: "+liveUsers.length);
		if(liveUsers!=null){
			if(liveUsers.length==2){
				startGame(liveUsers);	
			}else{
				waitForOtherUser();
			}
		}else{
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onUserJoinedRoom(String roomId, String userName){
		/*
		 * if room id is same and username is different then start the game
		 */
		if( !localUser.equals(userName) ) {
			startGame(new String[] {userName});
		}
	}

	public void onSendChatDone(boolean status){
		log("onSendChatDone: "+status);
	}
	
	
	
	public void onResultUpdateReceived(String userName, int code){
		if(localUser.equals(userName)==false){
			STATE = FINISHED;
			warpListener.onGameFinished(code, true);
		}else{
			warpListener.onGameFinished(code, false);
		}
	}
	
	public void onUserLeftRoom(String roomId, String userName){
		log("onUserLeftRoom "+userName+" in room "+roomId);
		if(STATE==STARTED && !localUser.equals(userName)){// Game Started and other user left the room
			warpListener.onGameFinished(ENEMY_LEFT, true);
		}
	}
	
	public int getState(){
		return this.STATE;
	}
	
	private void log(String message){
		if(showLog){
			System.out.println(message);
		}
	}
	
	private void startGame(String[] usernames){
		STATE = STARTED;
		warpListener.onGameStarted(usernames);
	}
	
	private void waitForOtherUser(){
		STATE = WAITING;
		warpListener.onWaitingStarted("Waiting for other user");
	}
	
	private void handleError(){
		if(roomId!=null && roomId.length()>0){
			warpClient.deleteRoom(roomId);
		}
		disconnect();
	}
	
	public void handleLeave(){
		if(isConnected){
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
			if(STATE!=STARTED){
				warpClient.deleteRoom(roomId);
			}
			warpClient.disconnect();
		}
	}
	
	private void disconnect(){
		warpClient.removeConnectionRequestListener(new ConnectionListener(this));
		warpClient.removeChatRequestListener(new ChatListener(this));
		warpClient.removeZoneRequestListener(new ZoneListener(this));
		warpClient.removeRoomRequestListener(new RoomListener(this));
		warpClient.removeNotificationListener(new NotificationListener(this));
		warpClient.disconnect();
	}
}
