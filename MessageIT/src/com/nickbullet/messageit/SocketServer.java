package com.nickbullet.messageit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
 
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
import org.json.JSONException;
import org.json.JSONObject;
 
import com.google.common.collect.Maps;

@ServerEndpoint("/chat")
public class SocketServer {
	
	public String timeCount="";
    public UserCounter nameCount = new UserCounter();
    
	ArrayList<String> list = new ArrayList<String>();
	// ��������� ���� �������� ������
    private static final Set<Session> sessions = Collections
            .synchronizedSet(new HashSet<Session>());
 
    // ������������ ����� id ������ � ������
    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();
 
    private JSONUtils jsonUtils = new JSONUtils();
 
    // ��������� ���������� �������
    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = Maps.newHashMap();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] nameval = param.split("=");
                map.put(nameval[0], nameval[1]);
            }
        }
        return map;
    }
 
    /**
     * ���������� ��� �������� ������ ����������
     * */
    @OnOpen
    public void onOpen(Session session) {
 
        System.out.println(session.getId() + " has opened a connection");
 
        Map<String, String> queryParams = getQueryMap(session.getQueryString());
 
        String name = "";
 
        if (queryParams.containsKey("name")) {
 
            // ��������� ����� ������� ����� ��������� �������
            name = queryParams.get("name");
            try {
                name = URLDecoder.decode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
 
            // ��������� ������������ ����� id ������ � ������
            nameSessionPair.put(session.getId(), name);
        }
 
        // ���������� ������ � ������ ������
        sessions.add(session);
 
        try {
            // �������� id ������ ������� ������� ������ �������������
            session.getBasicRemote().sendText(
                    jsonUtils.getClientDetailsJson(session.getId(),
                            "Your session details"));
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        // ����������� ���� �������� � ����� ��������
        sendMessageToAll(session.getId(), name, " � ����!", true,
                false, null);
        
        //nameCount.addName(name);
    }
 
    /**
     * ����� ���������� ����� ���������� ��������� �� ������ �������
     * 
     * @param message
     *            JSON ��������� �� �������
     * */
    @OnMessage
    public void onMessage(String message, Session session) {
 
        System.out.println("Message from " + session.getId() + ": " + message);
 
        String msg = null;
 
        // ������� JSON � ��������� ���������
        try {
            JSONObject jObj = new JSONObject(message);
            msg = jObj.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        // �������� ��������� ���� ��������
        sendMessageToAll(session.getId(), nameSessionPair.get(session.getId()),
                msg, false, false, getSysTime(timeCount));
    }
 
    /**
     * ����� ���������� ����� ������ �������
     * */
    @OnClose
    public void onClose(Session session) {
 
        System.out.println("Session " + session.getId() + " has ended");
 
        // Getting the client name that exited
        String name = nameSessionPair.get(session.getId());
 
        // removing the session from sessions list
        sessions.remove(session);
 
        // Notifying all the clients about person exit
        sendMessageToAll(session.getId(), name, " ����� �� ����!", false,
                true, null);
        
        //nameCount.removeName(name);
    }
 
    /**
     * ����� ��� �������� ��������� ���� ��������
     * 
     * @param sessionId
     * @param message
     *            ��������� ��� �������� ���� ��������
     * @param isNewClient
     *            ���� ��� ����������� ��� ��������� � ��� ��� ����� � ���
     * @param isExit
     *            ���� ��� ����������� ��� ��������� � ��� ��� ����� �� ����
     * */
    private void sendMessageToAll(String sessionId, String name,
            String message, boolean isNewClient, boolean isExit, String time) {
 
        // �������� ��������� �� ���� �������
        for (Session s : sessions) {
            String json = null;
 
            // �������� ��� ��������� � ��� ��� ����� ������ �������������
            if (isNewClient) {
                json = jsonUtils.getNewClientJson(sessionId, name, message,
                        sessions.size());
 
            } else if (isExit) {
                // �������� ���� ������ ����� �� ����
                json = jsonUtils.getClientExitJson(sessionId, name, message,
                        sessions.size());
            } else {
                // ��������� � ����
                json = jsonUtils
                        .getSendAllMessageJson(sessionId, name, message, time);
            }
 
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                s.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + s.getId() + ", "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // ��������� �������� �������
    public String getSysTime(String time)
    {
    	Date now = new Date();
    	DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    	timeCount = formatter.format(now);
        return timeCount;
    }
    
    /*public void userCounter(String name, String state)
    {
    	switch (state){
    		case "add" : list.add(name); break;
    		case "remove" : list.remove(name); break;
    	}
        System.out.println("List: " + list.toString());
    }*/
}
