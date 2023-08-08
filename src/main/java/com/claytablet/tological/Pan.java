package com.claytablet.tological;

import java.util.HashMap;

/**
 *
 * @author dpkap
 */
public class Pan{
    String name;
    HashMap<String,String> attr_changeType;
    
    Pan(String name, HashMap<String, String> attr_changeType){
        this.name = name;
        this.attr_changeType = attr_changeType;
    }   
    public String getName(){
        return this.name;
    }
    public HashMap<String,String> getAttribte(){
        if (this.attr_changeType ==null)
            return new HashMap<>();
        return this.attr_changeType;
    }
  
}
