package com.nickbullet.messageit;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	
	// ����� ��� ����������� ������� �� ������� �������
    private static final String FLAG_SELF = "self", FLAG_NEW = "new",
            FLAG_MESSAGE = "message", FLAG_EXIT = "exit";
 
	ArrayList<String> list = new ArrayList<>();
    
    public JSONUtils() {
    }
 
    /**
     * ����� ������ ��������� � ������� ������
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
     * ����������� ���� �������� � ����� �������
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
     * ����� ������ ��������� ����������
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
     * ����� ��������� ���������� �������� ���� ��������
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
