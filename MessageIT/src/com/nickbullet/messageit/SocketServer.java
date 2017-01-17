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
	// получение всех активных сессий
    private static final Set<Session> sessions = Collections
            .synchronizedSet(new HashSet<Session>());
 
    // соответствие между id сессии и именем
    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();
 
    private JSONUtils jsonUtils = new JSONUtils();
 
    // получение параметров запроса
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
     * ¬ызываетс€ при создании соккет соединени€
     * */
    @OnOpen
    public void onOpen(Session session) {
 
        System.out.println(session.getId() + " has opened a connection");
 
        Map<String, String> queryParams = getQueryMap(session.getQueryString());
 
        String name = "";
 
        if (queryParams.containsKey("name")) {
 
            // ѕолучение имени клиента через параметры запроса
            name = queryParams.get("name");
            try {
                name = URLDecoder.decode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
 
            // ѕолучение соответстви€ между id сессии и именем
            nameSessionPair.put(session.getId(), name);
        }
 
        // ƒобавление сессии в список сессий
        sessions.add(session);
 
        try {
            // ќтправка id сессии клиенту который только присоединилс€
            session.getBasicRemote().sendText(
                    jsonUtils.getClientDetailsJson(session.getId(),
                            "Your session details"));
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        // ”ведомление всем клиентам о новом вошедшем
        sendMessageToAll(session.getId(), name, " в чате!", true,
                false, null);
        
        //nameCount.addName(name);
    }
 
    /**
     * метод вызываетс€ когда получаетс€ сообщение от любого клиента
     * 
     * @param message
     *            JSON сообщение от клиента
     * */
    @OnMessage
    public void onMessage(String message, Session session) {
 
        System.out.println("Message from " + session.getId() + ": " + message);
 
        String msg = null;
 
        // ѕарсинг JSON и получение сообщени€
        try {
            JSONObject jObj = new JSONObject(message);
            msg = jObj.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        // ќтправка сообщени€ всем клиентам
        sendMessageToAll(session.getId(), nameSessionPair.get(session.getId()),
                msg, false, false, getSysTime(timeCount));
    }
 
    /**
     * ћетод вызываетс€ когда сесси€ закрыта
     * */
    @OnClose
    public void onClose(Session session) {
 
        System.out.println("Session " + session.getId() + " has ended");
 
        // Getting the client name that exited
        String name = nameSessionPair.get(session.getId());
 
        // removing the session from sessions list
        sessions.remove(session);
 
        // Notifying all the clients about person exit
        sendMessageToAll(session.getId(), name, " вышел из чата!", false,
                true, null);
        
        //nameCount.removeName(name);
    }
 
    /**
     * ћетод дл€ отправки сообщений всем клиентам
     * 
     * @param sessionId
     * @param message
     *            сообщение дл€ отправки всем клиентам
     * @param isNewClient
     *            флаг дл€ определени€ что сообщение о том что вошли в чат
     * @param isExit
     *            флаг дл€ определени€ что сообщение о том что вышли из чата
     * */
    private void sendMessageToAll(String sessionId, String name,
            String message, boolean isNewClient, boolean isExit, String time) {
 
        // –ассылка сообщений по всем сесси€м
        for (Session s : sessions) {
            String json = null;
 
            // ѕроверка что сообщение о том что новый клиент присоединилс€
            if (isNewClient) {
                json = jsonUtils.getNewClientJson(sessionId, name, message,
                        sessions.size());
 
            } else if (isExit) {
                // ѕроверка если клиент вышел из чата
                json = jsonUtils.getClientExitJson(sessionId, name, message,
                        sessions.size());
            } else {
                // —ообщение в чате
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
    
    // ѕолучение текущего времени
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
