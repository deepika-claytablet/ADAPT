package com.claytablet.relational;

import com.google.common.collect.Multimap;
import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.Pan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class adatToRelations{
    
    Adat A;
    
    public adatToRelations(Adat A){
        this.A =A;
    }
    public String createRelation(Adat a){
        String output = "create table "+a.getName() +" (";
        //adding attributes and ADAT key
        if (a.getAttr_DataKind().isEmpty()){
            output = output + "\n"+ addAdatKey(a.getName()) + "\t" + "varchar(50)" + "\n)";  
        }
        else{
            for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
                String key = it.getKey();
                String value = it.getValue();
                output = output + "\n" +addColumn(key) + "\t" + value + ",";   
            }
            output = output + "\n"+ addAdatKey(a.getName()) + "\t" + "varchar(50)" + "\n)";  
        }
        return output;
    }
    public String createRelationships(Adat a, AdatAttributeRelationships [] mla, AdatRelationships [] ar){
        // mapping is-analyzed-by relationship
        String output="";
        for(AdatRelationships i : ar){
            Multimap<Adat, Pan> isAnalyzedBy = i.getAnalyzed();
             for(Pan p: isAnalyzedBy.get(a)){ 
                output = output + "\n"+addColumnSurrogateKey(a.getName(),p.getName());             
            }
        }
        //determining analysis property
        output = output + "\n"+ "\ncreate table if not exists analysis_property (\nAdat varchar(50), \nAttribute varchar(50), "
                + "\nPan varchar(50), \nis_Additive boolean, \ncardinality int, \nApplicability boolean, \nPRIMARY KEY (Adat, Attribute, Pan)\n); ";
        for(AdatAttributeRelationships j : mla){
            if(j == null) break;
            HashMap<ArrayList,Boolean> isAdditive = j.getIsAdditive();
            HashMap<ArrayList,Integer> cardinality = j.getCardinality();
            HashMap<ArrayList,Boolean> applicability = j.getApplicability();
            Set<ArrayList> key = isAdditive.keySet();
            Iterator<ArrayList> iterator = key.iterator();  
            ArrayList next = iterator.next();   
            String temp;
            temp = determineAdditive(next.get(0).toString(),next.get(1).toString(), next.get(2).toString(), Boolean.valueOf(isAdditive.values().toString().replaceAll("[\\[\\](){}]","")) , Integer.valueOf(cardinality.values().toString().replaceAll("[\\[\\](){}]","")) , Boolean.valueOf(applicability.values().toString().replaceAll("[\\[\\](){}]","")));
            output = output + "\n"+ temp;
        }
        //mapping dependent relationship
        output = output + "\n"+ "\ncreate table if not exists dependentAdat (\nAdat_dependee varchar(50), \nAdat_dependent varchar(50), "
                + "\nPRIMARY KEY (Adat_dependee, Adat_dependent)\n); ";
        String insert =" " ;
        for(AdatRelationships m: ar){
            Multimap<Adat, Adat> dependent = m.getDependent(); 
            for(Adat a1: dependent.values()){
                insert = insert + "\n" + "insert into dependentAdat (Adat_dependee, Adat_dependent) values ('" + a.getName() + "','" 
                        + a1.getName() + "');";
            }
        }
        //returing the entire output string        
        return output + insert;  
    }
    public String addColumn(String Adatattribute){
        return Adatattribute ;
    }
    public String addAdatKey(String name){
        return name + "_Key";
    }
    public String addColumnSurrogateKey (String tableName, String referenceName) {
        String output="";
        output = output + "alter table " + tableName + " add "+ addColumn(referenceName+"_SK\tvarchar(50)")+  " ;";
        output = output + "\n"+ "alter table " + tableName + " add foreign key ("+ referenceName +  "_SK) references "+ 
            referenceName + "(" + referenceName + "_SK);";
        return output;
    }
    public String determineAdditive (String AdatName, String adAttr, String panName, Boolean additive, Integer value, Boolean applyTuple) {
        String insert;                                     
        insert = """                 
                 insert into analysis_property (Adat, Attribute, Pan, is_Additive,cardinality,Applicability) values ('"""+ AdatName 
                    + "', '"+ adAttr+ "', '" + panName + "', "+ additive + ", "+ value + ", "+ applyTuple +");";
        return insert;
    }
}
