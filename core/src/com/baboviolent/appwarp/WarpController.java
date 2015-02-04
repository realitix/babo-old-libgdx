package com.baboviolent.appwarp;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
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
    public static final String ACTION_DEAD = "h";
    public static final String ACTION_SYNCHRONIZATION = "i";

	private static WarpController instance;
	
	private boolean showLog = true;
	
	private final String apiKey = "d80bf388930379954bd9f212a8b17d9521098d1ad2d46745ad407acd6199d6aa";
	private final String secretKey = "2e03ffea1db4a17831ea817fdb4fbabdc36190254ec4181be554137bdab9a8b1";
	
	private WarpClient warpClient;
	
	private String localUser;
	private String roomId;
	
	private boolean isConnected = false;
	boolean isUDPEnabled = false;
	boolean udp = false;
	private long latency;
	private long lastTimeSent;
	
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
		warpClient.addUpdateRequestListener(new UpdateListener(this));
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
	public void sendSynchronization(Vector3 position, Vector3 target, Vector3 direction, Vector3 velocity, boolean shoot){
		sendAction(ACTION_SYNCHRONIZATION, synchronizationToString(position, target, direction, velocity, shoot));
	}
	
	public void sendDirection(Vector3 direction){
		sendAction(ACTION_DIRECTION, vector3ToString(direction));
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
	
	public void sendDead(String killer){
		sendAction(ACTION_DEAD, killer);
	}
	
	public void sendAction(String type, String value){
		sendGameUpdate(type+SEPARATOR+value);
	}
	
	public void sendGameUpdate(String msg){
		if(isConnected){
			String message = Long.toString(latency)+SEPARATOR+localUser+SEPARATOR+msg;
			if( isUDPEnabled ) {
				warpClient.sendUDPUpdatePeers(message.getBytes());
			}
			else {
				warpClient.sendUpdatePeers(message.getBytes());
			}
			lastTimeSent = TimeUtils.millis();
		}
	}
	
	/**
	 * Fonctions de receptions
	 */
	public void onGameUpdateReceived(String message){
        String[] datas = message.split(SEPARATOR);
        long latency = Long.parseLong(datas[0]);
        String username = datas[1];
		String action = datas[2];
		String value = datas[3];
		
		if( !localUser.equals(username) ) {
	    	if( action.equals(ACTION_DIRECTION) ) {
	    		warpListener.onDirectionReceived(username, stringToVector3(value));
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
	    	else if( action.equals(ACTION_DEAD) ) {
	    		warpListener.onDeadReceived(username, value);
		    }
	    	else if( action.equals(ACTION_SYNCHRONIZATION) ) {
	    		Vector3 position = new Vector3();
	    		Vector3 target = new Vector3();
	    		Vector3 direction = new Vector3();
	    		Vector3 velocity = new Vector3();
	    		boolean shoot;
	    		shoot = stringToSynchronization(value, position, target, direction, velocity);
	    		warpListener.onSynchronizationReceived(username, position, target, direction, velocity, shoot);
		    }
		}
	}
	
	private String synchronizationToString(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4, boolean b) {
		String result = "";
		String s = "!";
		result += vector3ToString(v1)+s;
		result += vector3ToString(v2)+s;
		result += vector3ToString(v3)+s;
		result += vector3ToString(v4)+s;
		result += (b)?"1":"0";
		return result;
	}
	
	private boolean stringToSynchronization(String s, Vector3 position, Vector3 target, Vector3 direction, Vector3 velocity) {
		String[] ps = s.split("!");
		position.set(stringToVector3(ps[0]));
		target.set(stringToVector3(ps[1]));
		direction.set(stringToVector3(ps[2]));
		velocity.set(stringToVector3(ps[3]));
		return (ps[4].equals("1")) ? true : false;
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
		if(status) {
			if(udp) {
				warpClient.initUDP();
			}
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
	
	@SuppressWarnings("unused")
	public void onGetLiveRoomInfo(String[] liveUsers) {
		log("onGetLiveRoomInfo: "+liveUsers.length);
		if(liveUsers != null) {
			if(liveUsers.length == 2) {
				startGame(liveUsers);	
			}
			else{
				waitForOtherUser();
			}
		}
		else {
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
	
	public void onSendUpdateDone(byte arg0) {
		latency = TimeUtils.timeSinceMillis(lastTimeSent);
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
