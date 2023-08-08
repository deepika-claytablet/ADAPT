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
            String output = "\ncreate table if not exists "+p.getName() +" (";
            for(String it:  p.getAttribte().keySet())            
            {
                output = output + "\n" +addColumn(it)+",";
                if ((p.getAttribte().get(it)).equals("no_update")){
                    output = output + "\n"+ addColumn(it+"_TMSP");
                }
            }
            output = output + "\n"+ createAddSurrogateKey(p.getName()) + "\t varchar(50)\n)";
            return output;      
        }
      }
    public String addColumn(String Adatattribute) {
        if (Adatattribute.contains("_TMSP"))
            return Adatattribute+ "\t TIMESTAMP," ;
        else
            return Adatattribute+ "\t varchar(50)";
    }    
    public String createAddSurrogateKey(String Panname) {
            return Panname + "_SK";
    }
   
    
}
