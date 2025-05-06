/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.claytablet.relational;

import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.MultiLevelAdat;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 *
 * @author dpkap
 */
public class ATR_Mixed extends adatToRelations{
    HashMap<Adat, Integer> Adatpriority = new HashMap<>() ;
    String output = "\n";

    public ATR_Mixed(Adat A) {
        super(A);
    }
    
    
    public String createMixed(HashMap<Adat, String> mixedlist, MultiLevelAdat mla , AdatAttributeRelationships [] analysis_property, AdatRelationships [] arArray){     
        int priority = -1;
        Multimap<Adat, Adat> tree = mla.getAdatcomplex(); 
        for (Map.Entry<Adat, String> entry : mixedlist.entrySet()){
            if(entry.getValue().contains("complex")){                
                Collection<Adat> children = tree.get(entry.getKey());
                ++priority;
                if(Adatpriority.containsKey(entry.getKey())){
                    int localPriority = Adatpriority.get(entry.getKey());
                    // Adatpriority.replace(entry.getKey(), localPriority, localPriority+1);
                    for(Map.Entry<Adat, Integer> e : Adatpriority.entrySet()){
                       if( e.getValue()==localPriority+1 || e.getValue()==localPriority)
                            Adatpriority.replace(e.getKey(), e.getValue(), e.getValue()+1);
                    }
                    priority = localPriority-1;
                }
                for(Adat i:children){
                    Adatpriority.put(i, priority );
                }
                Adatpriority.put(entry.getKey(),  ++priority);                
            }
        }
        HashMap <Adat, Integer> sortedAdatpriority = Adatpriority.entrySet().stream()
                .sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                        (e1,e2)->e1, LinkedHashMap::new));
        
        for(Map.Entry<Adat, Integer> me : sortedAdatpriority.entrySet()){
            Adat a = me.getKey();            
            switch (mixedlist.get(a)){
                case "atomic": 
                {   adatToRelations arl = new adatToRelations(a);
                    output = output + "\n"+ arl.createRelation(a) + arl.createRelationships(a,arArray)
                                        + arl.createAnalysisProperty(a,analysis_property)+ arl.createDependent(a, arArray);
                    break;
                }
                case "containment": {
                    ATR_Containment arl = new ATR_Containment(a);
                    output = output + "\n"+ arl.createForContent(mla, a, analysis_property, arArray);
                    break;
                }
                case "derived1":
                {
                    ATR_Derived arl = new ATR_Derived(a);
                    output = output + "\n"+arl.createDerivedCase1(a, mla, analysis_property, arArray);
                    break;
                }
                case "derived2":
                {
                    ATR_Derived arl = new ATR_Derived(a);
                    output = output + "\n"+arl.createDerivedCase2(a, mla, analysis_property, arArray);
                    break;
                }
                case "complex1":
                {
                    ATR_complex arl = new ATR_complex(a);
                 //   output = output + "\n"+arl.createComplexCase1(a, mla, analysis_property, arArray);
                    break;
                }
                case "complex2":
                {
                    ATR_complex arl = new ATR_complex(a);
                    //output = output + "\n"+arl.createComplexCase2(a, mla, analysis_property, arArray);
                    output= output+ "\n"+ createRelation(a) + "\n" + createRelationships(a,arArray) 
                            + "\n" + createAnalysisProperty(a, analysis_property)
                            +"\n" + createDependent(a, arArray); 
                    Collection<Adat> children = tree.get(a);
                    for (Adat i : children){ 
                        if(!tree.containsKey(i)) {                      
                            output = output + "\n" + "alter table " + a.getName() + " add "+ addColumn(i.getName()+"_key\tvarchar(50) PRIMARY KEY")+  " ;";
                            output = output + "\n" + "alter table " + a.getName() + " add foreign key ("+ i.getName() +  "_key) references "+ 
                                   i.getName() + "(" + i.getName() + "_key);";
                        }
                    }
                    break;
                }
            }
        }
        return output;
    }
    
}
