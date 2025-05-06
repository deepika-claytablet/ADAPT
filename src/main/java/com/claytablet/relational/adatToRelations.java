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
    public static boolean analysis_property = true;
    public static boolean dependentAdat = true;
    
    public adatToRelations(Adat A){
        this.A =A;
    }
    public String createRelation(Adat a){
        String output = "create table "+a.getName() +" (";
        //adding attributes and ADAT key
        if (a.getAttr_DataKind().isEmpty()){
            output = output + "\n"+ addAdatKey(a.getName()) + "\t" + "varchar(50) PRIMARY KEY" + "\n)";  
        }
        else{
            for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
                String key = it.getKey();
                String value = it.getValue();
                output = output + "\n" +addColumn(key) + "\t" + value + ",";   
            }
            output = output + "\n"+ addAdatKey(a.getName()) + "\t" + "varchar(50) PRIMARY KEY" + "\n)";  
        }
        return output;
    }
    public String createDependent(Adat a,AdatRelationships [] arArray){
         //mapping dependent relationship
        String output="";
        if(dependentAdat){
            output = output + "\n"+ "\ncreate table dependentAdat (\nAdat_dependee varchar(50), \nAdat_dependent varchar(50), "
                + "\nPRIMARY KEY (Adat_dependee, Adat_dependent)\n); ";
            dependentAdat = false;
        }
        String insert =" " ;
        for(AdatRelationships m: arArray){
            if(m!=null){
                Multimap<Adat, Adat> dependent = m.getDependent(); 
                for(Adat a1: dependent.values()){
                    insert = insert + "\n" + "insert into dependentAdat (Adat_dependee, Adat_dependent) values ('" + a.getName() + "','" 
                            + a1.getName() + "');";
                }
            }
        }
        return output + insert;
    }
    public String createAnalysisProperty(Adat a, AdatAttributeRelationships [] mla){
        //determining analysis property
        String output="";String AdatName = a.getName();
        if(analysis_property){
            output = output + "\n"+ "\ncreate table analysis_property (\nAdat varchar(50), \nAttribute varchar(50), "
                + "\nPan varchar(50), \nis_Additive boolean, \ncardinality int, \nApplicability boolean, \nPRIMARY KEY (Adat, Attribute, Pan)\n); ";
            analysis_property =false;
        }
        
        for(AdatAttributeRelationships j : mla){
            if(j == null) break;
            HashMap<ArrayList,Boolean> isAdditive = j.getIsAdditive();
            HashMap<ArrayList,String> cardinality = j.getCardinality();
            HashMap<ArrayList,Boolean> applicability = j.getApplicability();
            Set<ArrayList> key = isAdditive.keySet();
            Iterator<ArrayList> iterator = key.iterator();  
            ArrayList next = iterator.next();   
            
            if(AdatName.equalsIgnoreCase(next.get(0).toString())){
                String temp ;
                temp = determineAdditive(next.get(0).toString(),next.get(1).toString(), 
                        next.get(2).toString(), 
                        Boolean.valueOf(isAdditive.values().toString().replaceAll("[\\[\\](){}]","")) , 
                        cardinality.values().toString().replaceAll("[\\[\\](){}]","") , 
                        Boolean.valueOf(applicability.values().toString().replaceAll("[\\[\\](){}]","")));
                output = output + "\n"+ temp;
            }
        }
        return output;
    }
    public String createRelationships(Adat a, AdatRelationships [] arArray){
        // mapping is-analyzed-by relationship
        String output="";String AdatName = a.getName();
            for(AdatRelationships i : arArray){
                if(i!=null){
                Multimap<Adat, Pan> isAnalyzedBy = i.getAnalyzed();
                for(Pan p: isAnalyzedBy.get(a)){             
                    output = output + "\n"+addColumnSurrogateKey(a.getName(),p.getName());
                }
                }
            }
     
        return output ;  
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
        output = output + "\n"+ "alter table " + tableName + " add foreign key ("+ referenceName +  "_SK) references Dim_"+ 
            referenceName + "(" + referenceName + "_SK);";
        return output;
    }
    public String determineAdditive (String AdatName, String adAttr, String panName, Boolean additive, String value, Boolean applyTuple) {
        String insert;                                     
        insert = """                 
                 insert into analysis_property (Adat, Attribute, Pan, is_Additive,cardinality,Applicability) values ('"""+ AdatName 
                    + "', '"+ adAttr+ "', '" + panName + "', "+ additive + ", "+ value + ", "+ applyTuple +");";
        return insert;
    }
}
