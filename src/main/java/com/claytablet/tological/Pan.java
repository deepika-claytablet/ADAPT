package com.claytablet.tological;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.HashMap;

/**
 *
 * @author dpkap
 */
public class Pan{
    String name;
    HashMap<String,String> attr_changeType;
    Multimap<String,String> attr_attr;
    
    Pan(String name, HashMap<String, String> attr_changeType, Multimap<String,String> attr_attr){
        this.name = name;
        this.attr_changeType = attr_changeType;
        this.attr_attr = attr_attr;
    }   
    public String getName(){
        return this.name;
    }
    public HashMap<String,String> getAttribte(){
        if (this.attr_changeType ==null)
            return new HashMap<>();
        return this.attr_changeType;
    }
     public Multimap<String,String> getAttrAttr(){
        if (this.attr_attr ==null)          
            return  null;
        return this.attr_attr;
    }
    public Pan getPan(String name){
        return this;
    }
}
