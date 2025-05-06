package com.claytablet.relational;

import com.claytablet.tological.Pan;
/**
 *
 * @author dpkap
 */
public class panToRelations {
    Pan P;
    
    public panToRelations(Pan P) {
        this.P = P;
    }    
    public String createRelation(Pan p){
        if (p.getAttribte().isEmpty()){
            return "\n";
        }
        else{
            String output = "\ncreate table if not exists Dim_"+p.getName() +" (";
            for(String it:  p.getAttribte().keySet())            
            {
                output = output + "\n" +addColumn(it)+",";
                if ((p.getAttribte().get(it)).equals("no_update")){
                    output = output + "\n"+ addColumn(it+"_TMSP")+",";
                }
            }           
            output = output + "\n"+ createAddSurrogateKey(p.getName()) + "\t varchar(50) PRIMARY KEY\n)";            
            return output;      
        }
    }
    public String insertRelationAtrr(Pan p){
        String output="\ncreate table if not exists PANAttribute_Attribute (\nbase_attribute char(100),\ncomposed_attribute char(100),\nPRIMARY KEY (base_attribute, composed_attribute)\n);)";
        
        if (!p.getAttrAttr().isEmpty()){
                for(String attr1:  p.getAttrAttr().keySet())            
                {
                    output = output + "\ninsert into PANAttribute_Attribute (PANAttribute_Attribute) VALUES (" +addColumn(attr1)+"),(";
                    for(String  attr2: p.getAttrAttr().get(attr1)){
                        output = output + addColumn(attr2)+");";
                    }
                }
            }
       return output;
    }         
    public String addColumn(String Adatattribute) {
        if (Adatattribute.contains("_TMSP"))
            return Adatattribute+ "\t DATETIME" ;
        else
            return Adatattribute+ "\t varchar(50)";
    }    
    public String createAddSurrogateKey(String Panname) {
            return Panname + "_SK";
    }
   
    
}
