package com.nickbullet.messageit;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	
	// флаги дл€ определени€ запроса на стороне клиента
    private static final String FLAG_SELF = "self", FLAG_NEW = "new",
            FLAG_MESSAGE = "message", FLAG_EXIT = "exit";
 
	ArrayList<String> list = new ArrayList<>();
    
    public JSONUtils() {
    }
 
    /**
     *  огда клиент нуждаетс€ в детал€х сессии
     * */
    public String getClientDetailsJson(String sessionId, String message) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_SELF);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
 
    /**
     * ”ведомление всех клиентов о новом клиенте
     * */
    public String getNewClientJson(String sessionId, String name,
            String message, int onlineCount) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_NEW);
            jObj.put("name", name);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("onlineCount", onlineCount);
            
            //userCounter(name, "add");
            
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
 
    /**
     *  огда клиент разрывает соединение
     * */
    public String getClientExitJson(String sessionId, String name,
            String message, int onlineCount) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_EXIT);
            jObj.put("name", name);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("onlineCount", onlineCount);
            
            //userCounter(name, "remove");
            
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
 
    /**
     *  огда сообщение необходимо отослать всем клиентам
     * */
    public String getSendAllMessageJson(String sessionId, String fromName,
            String message, String time) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", sessionId);
            jObj.put("name", fromName);
            jObj.put("message", message);
            jObj.put("time", time);
 
            json = jObj.toString();
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
    
    /*public void userCounter(String name, String state)
    {
    	switch (state){
    		case "add" : list.add(name); break;
    		case "remove" : list.remove(name); break;
    	}
    	String un = "";
    	for (String item : list) {
    		un = un + item + " ";
    	}
    	System.out.println("List: " + un);
    }*/
}
