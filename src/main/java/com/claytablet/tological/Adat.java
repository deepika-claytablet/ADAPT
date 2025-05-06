package com.claytablet.tological;

import java.util.HashMap;

/**
 *
 * @author dpkap
 */
public class Adat {
    String name;
   // String dataKind;
    String nature;
    HashMap<String,String> attr_DataKind;
    
    Adat(){
        
    }
    
    Adat(String name, String nature, HashMap<String,String> attr_DataKind){
        this.name = name;
       // this.dataKind = dk;
        this.nature = nature;
        for (int i = 0 ; i < attr_DataKind.size(); i++)
            this.attr_DataKind = attr_DataKind;
    }
    public String getName(){
        return this.name;
    }
    public HashMap<String, String> getAttr_DataKind(){
        return this.attr_DataKind;
    }
    public String getNature(){
        return this.nature;
    }
}
