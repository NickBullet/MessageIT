package com.nickbullet.messageit;

import java.util.ArrayList;

public class UserCounter {

	ArrayList<String> list = new ArrayList<String>();
	
	public void addName(String name)
    {
    	list.add(name);
        //System.out.println("List: " + list.toString());
    	
    	String un = "";
    	for (String item : list) {
    		un = un + item + " ";
    	}
    	System.out.println("List: " + un);
    }
	
	public void removeName(String name)
    {
    	list.remove(name);
        //System.out.println("List: " + list.toString());
    }
}
